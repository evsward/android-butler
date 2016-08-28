package com.evsward.butler.service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.Time;
import android.widget.Toast;

import com.evsward.butler.activity.MyApplication;
import com.evsward.butler.entities.AckNewSeatInfoList.NewSeatInfo;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.LogUtil;
import com.evsward.butler.R;

public class PrintUtil {

	private static final int PRINT_PORT = Const.PRINT_PORT;
	private static final String TAG = "PrintService";

	// 清除字体放大指令
	private static final byte[] FD_FONT = new byte[3];
	private static final byte[] FONT_S = new byte[3];
	private static final byte[] FONT_Large_Model = new byte[3];
	private static final byte[] FONT_Normal_Model = new byte[3];
	private static final byte[] FONT_B = new byte[3];
	private static final byte[] FONT_Un_B = new byte[3];

	private static final byte[] CLEAR_FONT = new byte[3];
	private static final byte[] INIT_PRINTER = new byte[2];
	private static final byte[] NEWLINE = new byte[2]; // 换行
	private static final byte[] CUT = new byte[2]; // 完全切纸
	private static final byte[] PRINT_MOVE_PAPER = new byte[3];// 打印并进纸
	private static final byte[] SET_LINE_H = new byte[3];// 设置换行量
	private static final byte[] MOVE_PAPER = new byte[3];// 进纸
	private static final byte[] MOVE_PAPER_SMALL = new byte[3];
	private static byte[] FONT_T = new byte[3];

	private static final String strSeparator = "------------------------------------------------\r\n";

	private static Socket socket = null;
	private static OutputStream outstream = null;
	private static OutputStreamWriter outwriter = null;

	/**
	 * Logo打印
	 * 
	 * @param resID
	 *            logo 资源
	 * @param outstreamTmp
	 *            输出流
	 * @param outwriterTmp
	 *            输出流
	 */
	private static void PrintImage(final int resID, OutputStream outstreamTmp, OutputStreamWriter outwriterTmp) {
		if (outstreamTmp == null || outwriterTmp == null)
			return;
		try {
			// 打印Image
			Bitmap bmp = BitmapFactory.decodeResource(MyApplication.getContext().getResources(), resID);

			int pixelColor;

			// ESC * m nL nH 点阵图
			byte[] escBmp = new byte[] { 0x1B, 0x2A, 0x00, 0x00, 0x00 };

			escBmp[2] = (byte) 0x21;

			// nL, nH
			escBmp[3] = (byte) (bmp.getWidth() % 256);
			escBmp[4] = (byte) (bmp.getWidth() / 256);

			byte[] data = new byte[3];
			data[0] = (byte) 0x00;
			data[1] = (byte) 0x00;
			data[2] = (byte) 0x00; // 重置参数

			// 每行进行打印
			for (int i = 0; i < bmp.getHeight() / 24 + 1; i++) {
				outstreamTmp.write(escBmp);

				for (int j = 0; j < bmp.getWidth(); j++) {
					for (int k = 0; k < 24; k++) {
						if (((i * 24) + k) < bmp.getHeight()) {
							pixelColor = bmp.getPixel(j, (i * 24) + k);
							if (pixelColor != -1) {
								data[k / 8] += (byte) (128 >> (k % 8));
							}
						}
					}

					outstreamTmp.write(data);
					outwriterTmp.flush();

					// 重置参数
					data[0] = (byte) 0x00;
					data[1] = (byte) 0x00;
					data[2] = (byte) 0x00;
				}

				// 换行
				outstreamTmp.write(NEWLINE);
				outwriterTmp.flush();
			}
		} catch (IOException e) {
			LogUtil.se(TAG, e);
		}
	}

	/**
	 * 注册时打印
	 * 
	 * @param strSerial
	 *            序列号
	 * @param strName
	 *            会员姓名
	 * @param bMale
	 *            会员性别
	 */
	public static void registPrint(final String strSerial, final String strName, final String bMale) {
		new Thread(new Runnable() {
			public void run() {
				try {
					LogUtil.i(TAG, "【会员小条】打印开始：卡号=" + strSerial + " 姓名=" + strName + " 性别=" + bMale);
					connect();
					outstream.write(INIT_PRINTER); // 初始化打印机
					outstream.write(CLEAR_FONT);
					outstream.write(FONT_S);// 字体放大
					// outstream.write(FONT_B);// 字体加粗
					outwriter.flush();// 关键,很重要,不然指令一次性输出,后面指令覆盖前面指令,导致取消放大指令无效

					outstream.write(SET_LINE_H); // 设置换行量
					outwriter.flush();

					// 打印Text
					FONT_T[2] = 0;
					outstream.write(FONT_T);
					outwriter.write("        " + strSerial + "\r\n");

					FONT_T[2] = 16;
					outstream.write(FONT_T);
					outwriter.write("        " + strName + "(" + bMale + ")" + "\r\n");
					outwriter.flush();

					// 下面指令为打印完成后自动走纸
					outstream.write(PRINT_MOVE_PAPER);
					// 走纸
					outstream.write(MOVE_PAPER);
					outstream.write(MOVE_PAPER);
					// 切纸
					outstream.write(CUT);

					outwriter.flush();
					outstream.flush();

					disConnect();
				} catch (Exception e) {
					LogUtil.se(TAG, e);
				}
			}
		}).start();
	}

	/**
	 * 会员报名，查询：打印比赛座位信息
	 * 
	 * @param matchName
	 * @param matchTime
	 * @param playerName
	 * @param cardNO
	 * @param tableNO
	 * @param seatNO
	 * @param gender
	 */
	public static void printSeatInfo(final String matchName, final String matchTime, final String playerName, final String cardNO, final int tableNO,
			final int seatNO, final String gender) {
		new Thread(new Runnable() {
			public void run() {
				try {
					LogUtil.i(TAG, "【比赛座位信息】打印开始：卡号=" + cardNO + " 姓名=" + playerName + " 性别=" + gender);
					connect();
					for (int i = 0; i < 2; i++) {
						outstream.write(INIT_PRINTER); // 初始化打印机
						outstream.write(CLEAR_FONT);
						outstream.write(SET_LINE_H); // 设置换行量
						outwriter.flush();

						// 打印Image
						PrintImage(R.drawable.cpt_logo, outstream, outwriter);

						// 打印Text
						outstream.write(FONT_B);// 字体加粗
						outwriter.write("比赛名称： " + matchName + "\r\n");
						outstream.write(MOVE_PAPER_SMALL);
						outwriter.flush();

						outstream.write(FONT_Un_B);
						outstream.write(CLEAR_FONT);
						outwriter.flush();

						outwriter.write(strSeparator);
						outwriter.write("比赛时间： " + matchTime + "\r\n");
						outwriter.write(strSeparator);

						outwriter.write("姓名：     " + playerName + "(" + gender + ")" + "\r\n");

						outstream.write(MOVE_PAPER_SMALL);
						outwriter.flush();

						outwriter.write("卡号：     " + cardNO + "\r\n");

						outstream.write(MOVE_PAPER_SMALL);
						outwriter.flush();

						outwriter.write(strSeparator);
						outwriter.flush();

						outstream.write(FONT_B);// 字体加粗
						outwriter.write("桌号：     " + tableNO + "                      座位号： " + seatNO + "\r\n");
						outwriter.flush();

						outstream.write(FONT_Un_B);
						outstream.write(CLEAR_FONT);
						outwriter.write(strSeparator);
						outwriter.flush();

						Time nowTime = new Time();
						nowTime.setToNow();
						outwriter.write(nowTime.format("%Y-%m-%d %H:%M:%S") + "\r\n");
						outwriter.flush();

						// 下面指令为打印完成后自动走纸
						outstream.write(PRINT_MOVE_PAPER);
						// 走纸
						outstream.write(MOVE_PAPER);
						outstream.write(MOVE_PAPER);
						// 切纸
						outstream.write(CUT);

						outwriter.flush();
						outstream.flush();
					}

					disConnect();
				} catch (IOException e) {
					LogUtil.se(TAG, e);
				}
			}
		}).start();
	}

	/**
	 * 爆桌打印，连接一次，直到打完所有
	 * 
	 * @param strMatchName
	 * @param strTime
	 * @param burstSeatInfoList
	 */
	private static void BurstTable(final String strMatchName, final String strTime, List<NewSeatInfo> burstSeatInfoList) {
		for (int i = 0; i < burstSeatInfoList.size(); i++) {
			NewSeatInfo seatInfo = burstSeatInfoList.get(i);
			LogUtil.i(TAG, "爆桌打印中：玩家【" + seatInfo.getMemName() + "】座位号【" + seatInfo.getSeatNO() + "】桌号【" + seatInfo.getTableNO() + "】");
			try {
				outstream.write(INIT_PRINTER); // 初始化打印机
				outstream.write(CLEAR_FONT);
				outstream.write(SET_LINE_H); // 设置换行量
				outwriter.flush();

				// 打印Image
				PrintImage(R.drawable.cpt_logo, outstream, outwriter);

				// 打印Text
				outstream.write(FONT_B);// 字体加粗
				outwriter.write("比赛名称： " + strMatchName + "----Breaking Tables" + "\r\n");
				outstream.write(MOVE_PAPER_SMALL);
				outwriter.flush();

				outstream.write(FONT_Un_B);
				outstream.write(CLEAR_FONT);
				outwriter.flush();

				outwriter.write(strSeparator);
				outwriter.write("比赛时间： " + strTime + "\r\n");
				outstream.write(MOVE_PAPER_SMALL);
				outwriter.flush();

				if (seatInfo.getMemSex() == 1) {
					outwriter.write("姓名：     " + seatInfo.getMemName() + "(" + "M" + ")" + "\r\n");
				} else {
					outwriter.write("姓名：     " + seatInfo.getMemName() + "(" + "F" + ")" + "\r\n");
				}
				outstream.write(MOVE_PAPER_SMALL);
				outwriter.flush();

				outwriter.write("卡号：     " + seatInfo.getCardNO() + "\r\n");
				outstream.write(MOVE_PAPER_SMALL);
				outwriter.flush();

				outwriter.write(strSeparator);
				outwriter.flush();

				outstream.write(FONT_B);// 字体加粗
				outwriter.write("桌号：     " + seatInfo.getTableNO() + "                      座位号： " + seatInfo.getSeatNO() + "\r\n");
				outwriter.flush();

				outstream.write(FONT_Un_B);
				outstream.write(CLEAR_FONT);
				outwriter.write(strSeparator);
				outwriter.flush();

				Time nowTime = new Time();
				nowTime.setToNow();
				outwriter.write(nowTime.format("%Y-%m-%d %H:%M:%S") + "\r\n");
				outwriter.flush();

				// 下面指令为打印完成后自动走纸
				outstream.write(PRINT_MOVE_PAPER);
				// 走纸
				outstream.write(MOVE_PAPER);
				outstream.write(MOVE_PAPER);
				// 切纸
				outstream.write(CUT);

				outwriter.flush();
				outstream.flush();

			} catch (IOException e) {
				LogUtil.se(TAG, e);
			}
		}
	}

	/**
	 * 爆桌-批量打印
	 * 
	 * @param strMatchName
	 *            比赛名称
	 * @param strTime
	 *            时间
	 * @param strName
	 *            玩家名
	 * @param strCardID
	 *            卡号
	 * @param nTableID
	 *            桌号
	 * @param nChairID
	 *            座位号
	 * @param bMale
	 *            男女
	 */
	public static void printBurstTable(final String strMatchName, final String strTime, final List<NewSeatInfo> burstSeatInfoList) {
		new Thread(new Runnable() {
			public void run() {
				connect();
				BurstTable(strMatchName, strTime, burstSeatInfoList);
				disConnect();
			}
		}).start();
	}

	/**
	 * 平衡
	 * 
	 * @param strMatchName
	 *            比赛名称
	 * @param tTime
	 *            时间
	 * @param strName
	 *            玩家名
	 * @param strCardID
	 *            卡号
	 * @param nTableID
	 *            桌号
	 * @param nChairID
	 *            座位号
	 * @param bMale
	 *            男女
	 */
	public static void printBalancePlayer(final String strMatchName, final String strTime, final String strName, final String strCardID, final int nTableID,
			final int nChairID, final Boolean bMale) {
		new Thread(new Runnable() {
			public void run() {
				try {
					connect();
					outstream.write(INIT_PRINTER); // 初始化打印机
					outstream.write(CLEAR_FONT);
					outstream.write(SET_LINE_H); // 设置换行量
					outwriter.flush();

					// 打印Image
					PrintImage(R.drawable.cpt_logo, outstream, outwriter); // cpt_logo

					// 打印Text
					outstream.write(FONT_B);// 字体加粗
					outwriter.write("比赛名称： " + strMatchName + "----Balancing Tables" + "\r\n");
					outstream.write(MOVE_PAPER_SMALL);
					outwriter.flush();

					outstream.write(FONT_Un_B);
					outstream.write(CLEAR_FONT);
					outwriter.flush();

					outwriter.write(strSeparator);
					outwriter.write("比赛时间： " + strTime + "\r\n");
					outstream.write(MOVE_PAPER_SMALL);
					outwriter.flush();

					if (bMale == true) {
						outwriter.write("姓名：     " + strName + "(" + "M" + ")" + "\r\n");
					} else {
						outwriter.write("姓名：     " + strName + "(" + "F" + ")" + "\r\n");
					}

					outstream.write(MOVE_PAPER_SMALL);
					outwriter.flush();

					outwriter.write("卡号：     " + strCardID + "\r\n");
					outstream.write(MOVE_PAPER_SMALL);
					outwriter.flush();

					outwriter.write(strSeparator);
					outwriter.flush();

					outstream.write(FONT_B);// 字体加粗
					outwriter.write("桌号：     " + nTableID + "                      座位号： " + nChairID + "\r\n");
					outwriter.flush();

					outstream.write(FONT_Un_B);
					outstream.write(CLEAR_FONT);
					outwriter.write(strSeparator);
					outwriter.flush();

					Time nowTime = new Time();
					nowTime.setToNow();
					outwriter.write(nowTime.format("%Y-%m-%d %H:%M:%S") + "\r\n");
					outwriter.flush();

					// 下面指令为打印完成后自动走纸
					outstream.write(PRINT_MOVE_PAPER);
					// 走纸
					outstream.write(MOVE_PAPER);
					outstream.write(MOVE_PAPER);
					// 切纸
					outstream.write(CUT);

					outwriter.flush();
					outstream.flush();
					disConnect();
				} catch (IOException e) {
					LogUtil.se(TAG, e);
				}
			}
		}).start();
	}

	/**
	 * 奖池
	 * 
	 * @param strMatchName
	 *            比赛名称
	 * @param tTime
	 *            时间
	 * @param strName
	 *            玩家名
	 * @param strCardID
	 *            卡号
	 * @param nTableID
	 *            桌号
	 * @param nChairID
	 *            座位号
	 * @param bMale
	 *            男女
	 * @param strPlayerID
	 *            玩家id
	 * @param nPrize
	 *            奖金
	 * @param nPosition
	 *            位置
	 */
	public static void printChips(final String strMatchName, final String strTime, final String strName, final String strCardID, final int nTableID,
			final int nChairID, final Boolean bMale, final String strPlayerID, final int nPrize, final int nPosition) {
		new Thread(new Runnable() {
			public void run() {
				try {
					connect();
					outstream.write(INIT_PRINTER); // 初始化打印机
					outstream.write(CLEAR_FONT);
					outstream.write(SET_LINE_H); // 设置换行量
					outwriter.flush();

					// 打印Image
					PrintImage(R.drawable.cpt_logo, outstream, outwriter);

					// 打印Text
					outstream.write(FONT_B);// 字体加粗
					outwriter.write("比赛名称： " + strMatchName + "\r\n");
					outstream.write(MOVE_PAPER_SMALL);
					outwriter.flush();

					outstream.write(FONT_Un_B);
					outstream.write(CLEAR_FONT);
					outwriter.flush();

					outwriter.write(strSeparator);
					outwriter.write("比赛时间： " + strTime + "\r\n");
					outstream.write(MOVE_PAPER_SMALL);
					outwriter.flush();

					if (bMale == true) {
						outwriter.write("姓名：     " + strName + "(" + "M" + ")" + "\r\n");
					} else {
						outwriter.write("姓名：     " + strName + "(" + "F" + ")" + "\r\n");
					}
					outstream.write(MOVE_PAPER_SMALL);
					outwriter.flush();

					outwriter.write("卡号：     " + strCardID + "\r\n");
					outstream.write(MOVE_PAPER_SMALL);
					outwriter.flush();

					outwriter.write("证件号：   " + strPlayerID + "\r\n");
					outstream.write(MOVE_PAPER_SMALL);
					outwriter.flush();

					outwriter.write(strSeparator);
					outwriter.flush();

					outstream.write(FONT_B);// 字体加粗
					String strTableID = formatNum(nTableID, 12);
					String strPrize = formatNum(nPrize, 12);
					outwriter.write("桌号： " + strTableID + "                座位号： " + nChairID + "\r\n");
					outwriter.write("奖金： " + strPrize + "                名次：   " + nPosition + "\r\n");
					outwriter.flush();

					outstream.write(FONT_Un_B);
					outstream.write(CLEAR_FONT);
					outwriter.write(strSeparator);
					outwriter.flush();

					Time nowTime = new Time();
					nowTime.setToNow();
					outwriter.write(nowTime.format("%Y-%m-%d %H:%M:%S") + "\r\n");
					outwriter.flush();

					// 下面指令为打印完成后自动走纸
					outstream.write(PRINT_MOVE_PAPER);
					// 走纸
					outstream.write(MOVE_PAPER);
					outstream.write(MOVE_PAPER);
					// 切纸
					outstream.write(CUT);

					outwriter.flush();
					outstream.flush();

					disConnect();
				} catch (IOException e) {
					LogUtil.se(TAG, e);
				}
			}
		}).start();
	}

	private static String formatNum(final int num, final int formatNum) {
		String strNum = Integer.toString(num);
		int len = strNum.length();
		if (len >= formatNum)
			return strNum;
		else {
			for (int i = 0; i < formatNum - len; i++) {
				strNum = strNum + " ";
			}
		}
		return strNum;
	}

	private static void connect() {
		try {
			initCode();
			String print_ip = MyApplication.getSharedPreferences().getString("PrinterIP", "");
			socket = new Socket(print_ip, PRINT_PORT);
			outstream = socket.getOutputStream();
			outwriter = new OutputStreamWriter(outstream, "GBK");
		} catch (IOException e) {
			LogUtil.se(TAG, e);
		}
	}

	private static void disConnect() {
		try {
			if (outwriter != null) {
				outwriter.close();
			}
			if (outstream != null) {
				outstream.close();
			}
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			Toast.makeText(MyApplication.getContext(), "打印服务断开失败！", Toast.LENGTH_SHORT).show();
			LogUtil.se(TAG, e);
		}
	}

	private static void initCode() {
		// 设置打印机IP
		FONT_T[0] = 0x1b;
		FONT_T[1] = 0x4d;
		FONT_T[2] = 0x01;

		// 清除字体放大指令
		FD_FONT[0] = 0x1c;
		FD_FONT[1] = 0x21;
		FD_FONT[2] = 4;

		// 字体放大
		FONT_S[0] = 0x1d;
		FONT_S[1] = 0x21;
		FONT_S[2] = 0x12;

		// 宽高加倍
		FONT_Large_Model[0] = 0x1d;
		FONT_Large_Model[1] = 0x21;
		FONT_Large_Model[2] = 0x11;

		// 取消宽高加倍
		FONT_Normal_Model[0] = 0x1d;
		FONT_Normal_Model[1] = 0x21;
		FONT_Normal_Model[2] = 0x00;

		// 字体加粗指令
		FONT_B[0] = 0x1b;
		FONT_B[1] = 0x21;
		FONT_B[2] = 8;

		// 字体取消加粗指令
		FONT_Un_B[0] = 0x1b;
		FONT_Un_B[1] = 0x21;
		FONT_Un_B[2] = 0;

		// 字体纵向放大一倍
		CLEAR_FONT[0] = 0x1c;
		CLEAR_FONT[1] = 0x21;
		CLEAR_FONT[2] = 0;

		// 初始化打印机
		INIT_PRINTER[0] = 0x1b;
		INIT_PRINTER[1] = 0x40;

		// 换行
		NEWLINE[0] = 0x0d;
		NEWLINE[1] = 0x0a;

		// 切纸
		CUT[0] = 0x1B;
		CUT[1] = 0x69;

		PRINT_MOVE_PAPER[0] = 0x1B;
		PRINT_MOVE_PAPER[1] = 0x64;
		PRINT_MOVE_PAPER[2] = 0x7F;

		SET_LINE_H[0] = 0x1B;
		SET_LINE_H[1] = 0x33;
		SET_LINE_H[2] = 0x00;

		MOVE_PAPER[0] = 0x1B;
		MOVE_PAPER[1] = 0x4A;
		MOVE_PAPER[2] = 0x7F;

		MOVE_PAPER_SMALL[0] = 0x1B;
		MOVE_PAPER_SMALL[1] = 0x4A;
		MOVE_PAPER_SMALL[2] = 0x30;
	}

}
