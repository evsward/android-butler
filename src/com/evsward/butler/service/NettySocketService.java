package com.evsward.butler.service;

import java.net.InetSocketAddress;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.evsward.butler.entities.netty.UpstreamMessage;
import com.evsward.butler.entities.protobuf.HIMessage.HIAckMessage;
import com.evsward.butler.entities.protobuf.HIMessage.HIReqMessage;
import com.evsward.butler.util.ByteUtils;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.LogUtil;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * netty service
 * 
 * @Date Jun 16, 2015
 * @author liuBin
 */
public class NettySocketService extends Service {

	private static final String TAG = "NettySocketService";
	public static final int READ_TIME_OUT = 10;

	private final IBinder nettyBinder = new NettyBinder();
	public String SERVER_ADDRESS;
	private LocalBroadcastManager mLocalBroadcastManager;
	private ClientBootstrap bootstrap = null;
	private List<Channel> channels = new ArrayList<Channel>();
	private BlockingQueue<Channel> pool = new LinkedBlockingDeque<Channel>();
	private HINettyAsyncTask HINettyAsyncTask;
	private int channelNum = 2;

	// 心跳标志
	// private final AtomicBoolean heartBeatFlag = new AtomicBoolean(true);

	@Override
	public void onCreate() {
		super.onCreate();
		SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
		SERVER_ADDRESS = pref.getString("ServerIP", "");
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return nettyBinder;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// heartBeatFlag.set(false);
		closeChannels();
	}

	public class NettyBinder extends Binder {

		public void closeLink() {
			onDestroy();
		}

		public boolean sendMsg(int msgID, String jsonReq) {
			openChannels(1);
			Channel channel = getChannel();
			if (channel == null) {
				return false;
			}
			LogUtil.i(TAG, ">>>>发送socket连接请求，channelID=" + channel.getId() + "，消息号：" + msgID + "，消息体：" + jsonReq);
			HIReqMessage.Builder req = HIReqMessage.newBuilder();
			req.setJsonReqMsg(jsonReq);
			byte[] body = req.build().toByteArray();
			ChannelBuffer buffer = ChannelBuffers.buffer(ByteOrder.LITTLE_ENDIAN, body.length + 12);
			buffer.writeInt(msgID);// command
			buffer.writeInt(body.length);// bodyLength
			buffer.writeInt(1);// check
			buffer.writeBytes(body);// bodyContent
			channel.write(ChannelBuffers.wrappedBuffer(buffer));
			// 启动心跳服务，绑定到当前channel上
			heartBeat(channel, msgID, jsonReq);
			return true;
		}
	}

	public void heartBeat(Channel channel, int cmdId, String jsonReq) {
		ExecutorService worker = Executors.newSingleThreadExecutor();
		worker.execute(new HeartBeat(channel, cmdId, jsonReq));
	}

	class HeartBeat implements Runnable {
		public HeartBeat(Channel ch, int cmdId, String jsonReq) {
			this.ch = ch;
			this.cmdId = cmdId;
			this.jsonReq = jsonReq;
		}

		private Channel ch;
		private int cmdId;
		private String jsonReq;

		@Override
		public void run() {
			Thread.currentThread().setName("NettySocketService client HeartBeat Thread");
			while (true) {
				try {
					if (cmdId == 503) {
						Thread.sleep(Const.HeartBeatConst.SeatInfo_HeartBeat_Time);
					} else {
						Thread.sleep(Const.HeartBeatConst.Default_HeartBeat_Time);
					}
				} catch (InterruptedException e) {
				}
				HIReqMessage.Builder req = HIReqMessage.newBuilder();
				// req.setJsonReqMsg(heartBeatMsg);
				req.setJsonReqMsg(jsonReq);
				byte[] body = req.build().toByteArray();
				ChannelBuffer buffer = ChannelBuffers.buffer(ByteOrder.LITTLE_ENDIAN, body.length + 12);
				// buffer.writeInt(Const.Req_HeartBeat_256);// command
				buffer.writeInt(cmdId);
				buffer.writeInt(body.length);// bodyLength
				buffer.writeInt(1);// check
				buffer.writeBytes(body);// bodyContent
				if (ch != null && ch.isConnected() && ch.isWritable()) {
					ch.write(ChannelBuffers.wrappedBuffer(buffer));
				} else {
					LogUtil.i(TAG, "---------NettySocketService.heartBeat msgId=" + cmdId + " stop---------");
					return;
				}
				LogUtil.i(TAG, "---------NettySocketService.heartBeat channelId="+ch.getId()+" ,cmdId="+cmdId+"---------");
			}
		}
	}

	private void openChannels(int channelNum) {
		this.channelNum = channelNum;
		this.HINettyAsyncTask = new HINettyAsyncTask();
		this.HINettyAsyncTask.executeOnExecutor(Executors.newCachedThreadPool(), new Void[0]);
		LogUtil.d(TAG, "---------NettySocketService.channel开启，服务开启---------");
	}

	private void addChannel(Channel channel) {
		channels.add(channel);
		pool.offer(channel);
	}

	private Channel getChannel() {
		try {
			return pool.poll(READ_TIME_OUT + 1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			LogUtil.e(TAG, e.getMessage());
			return null;
		}
	}

	private void closeChannels() {
		Channel ch = null;
		while (channels.size() > 0) {
			ch = channels.remove(0);
			if (ch != null) {
				Channels.close(ch);
			}
		}
		if (this.bootstrap != null) {
			this.bootstrap.releaseExternalResources();
		}
		LogUtil.d(TAG, "---------NettySocketService.channel关闭，服务关闭---------");
	}

	private class HINettyAsyncTask extends AsyncTask<Void, Void, Void> {
		public HINettyAsyncTask() {

		}

		@Override
		protected Void doInBackground(Void... params) {
			ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool(), channelNum);
			bootstrap = new ClientBootstrap(factory);

			// Set up the event pipeline factory.
			bootstrap.setOption("connectTimeoutMillis", 15000);
			bootstrap.setOption("connectResponseTimeoutMillis", 15000);
			// bootstrap.setOption("allIdleTime", 0);
			bootstrap.setOption("sendBufferSize", 1048576);
			bootstrap.setOption("receiveBufferSize", 1048576);
			bootstrap.setOption("tcpNoDelay", false);

			// Configure the event pipeline factory.
			bootstrap.setPipelineFactory(new HINettyServiceClientPipelineFactory());
			// 初始化所有channel连接
			for (int i = 0; i < channelNum; i++) {
				bootstrap.connect(new InetSocketAddress(SERVER_ADDRESS, Const.SOCKET_PORT));
			}
			return null;
		}
	}

	private class HINettyServiceClientPipelineFactory implements ChannelPipelineFactory {

		@Override
		public ChannelPipeline getPipeline() throws Exception {
			ChannelPipeline p = Channels.pipeline();
			p.addLast("frameDecoder", new HINettyServiceFrameDecoder());
			p.addLast("handler", new HINettyServiceClientHandler(NettySocketService.this));
			return p;
		}
	}

	private class HINettyServiceClientHandler extends SimpleChannelUpstreamHandler {

		private NettySocketService socketService;

		public HINettyServiceClientHandler(NettySocketService socketService) {
			this.socketService = socketService;
		}
		
		@Override
		public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)throws Exception{
			LogUtil.i("HINettyServiceClientHandler", "channel is closed channelId="+e.getChannel().getId());
			super.channelClosed(ctx, e);
		}

		@Override
		public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
			super.channelOpen(ctx, e);
		}

		/**
		 * 连接建立
		 */
		@Override
		public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
			socketService.addChannel(e.getChannel());
			LogUtil.i("HINettyServiceClientHandler", "Add channel, id:" + e.getChannel().getId() + ";LocalAddress:"
					+ e.getChannel().getLocalAddress() + ";RemoteAddress:" + e.getChannel().getRemoteAddress());
			super.channelConnected(ctx, e);
		}

		/**
		 * 接受返回消息
		 */
		@Override
		public void messageReceived(ChannelHandlerContext ctx, final MessageEvent e) {
			UpstreamMessage upMessage = (UpstreamMessage) e.getMessage();
			if (upMessage == null) {
				// Channels.close(channel);
				return;
			} else {
				// 重整序列
				ChannelBuffer cb = ChannelBuffers.wrappedBuffer(ChannelBuffers.LITTLE_ENDIAN, upMessage.getBody().array());
				// 解析出protobuffer对象
				HIAckMessage ack = null;
				try {
					ack = HIAckMessage.parseFrom(cb.array());
				} catch (InvalidProtocolBufferException e1) {
					LogUtil.e(TAG, String.format("service request error..."));
					return;
				}
				String jsonRes = ack.getJsonAckMsg();
				Intent intent = null;
				switch (upMessage.getCmd()) {
				case Const.Ack_Manage_TV_501:
					intent = new Intent(Const.ACK_Manage_TV_501_ACTION);
					intent.putExtra(Const.ACK_Manage_TV_501_ACTION, jsonRes);
					break;
				case Const.Ack_MatchList_502:
					intent = new Intent(Const.ACK_MatchList_502_ACTION);
					intent.putExtra(Const.ACK_MatchList_502_ACTION, jsonRes);
					break;
				case Const.Ack_Mange_Match_503:
					intent = new Intent(Const.ACK_Table_Seat_List_503_ACTION);
					intent.putExtra(Const.ACK_Table_Seat_List_503_ACTION, jsonRes);
					break;
				case Const.Ack_Manage_Process_504:
					intent = new Intent(Const.ACK_Manage_Process_504_ACTION);
					intent.putExtra(Const.ACK_Manage_Process_504_ACTION, jsonRes);
					break;
				case Const.Ack_XLarge_TV_101:
					intent = new Intent(Const.Ack_XLarge_TV_101_ACTION);
					intent.putExtra(Const.Ack_XLarge_TV_101_ACTION, jsonRes);
					break;
				case Const.Ack_XLarge_TV_MatchInfo_151:
					intent = new Intent(Const.Ack_XLarge_TV_MatchInfo_151_ACTION);
					intent.putExtra(Const.Ack_XLarge_TV_MatchInfo_151_ACTION, jsonRes);
					break;
				case Const.Ack_XLarge_TV_MatchList_152:
					intent = new Intent(Const.Ack_XLarge_TV_MatchList_152_ACTION);
					intent.putExtra(Const.Ack_XLarge_TV_MatchList_152_ACTION, jsonRes);
					break;
				case Const.Ack_XLarge_TV_PlayerList_153:
					intent = new Intent(Const.Ack_XLarge_TV_PlayerList_153_ACTION);
					intent.putExtra(Const.Ack_XLarge_TV_PlayerList_153_ACTION, jsonRes);
					break;
				case Const.Ack_XLarge_TV_Enter_154:
					intent = new Intent(Const.Ack_XLarge_TV_Enter_154_ACTION);
					intent.putExtra(Const.Ack_XLarge_TV_Enter_154_ACTION, jsonRes);
					break;
				default:
					break;
				}
				if (intent != null)
					mLocalBroadcastManager.sendBroadcast(intent);
			}
		}

		/**
		 * 异常发生后，关闭channel
		 */
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
			Channel channel = e.getChannel();
			LogUtil.e("HINettyServiceClientHandler", "Unexpected exception from downstream." + e.getCause());
			LogUtil.i("HINettyServiceClientHandler", "Remove channel, id:" + channel.getId() + ";LocalAddress:" + channel.getLocalAddress()
					+ ";RemoteAddress:" + channel.getRemoteAddress());
			Channels.close(channel);
//			channel = null;
		}
	}

	private class HINettyServiceFrameDecoder extends FrameDecoder {

		public HINettyServiceFrameDecoder() {
			super();
		}

		@Override
		protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
			if (buffer.readableBytes() < 4)
				return null;
			int cmd = ByteUtils.getInt(buffer.array(), 0);
			if (buffer.readableBytes() < 8)
				return null;
			int bodyLength = ByteUtils.getInt(buffer.array(), 4);

			if (buffer.readableBytes() < 12)
				return null;
			int check = ByteUtils.getInt(buffer.array(), 8);

			if (buffer.readableBytes() < 12 + bodyLength)
				return null;

			LogUtil.d("HINettyServiceFrameDecoder",
					"buffer length:" + buffer.readableBytes() + " ip:"
							+ ((InetSocketAddress) channel.getRemoteAddress()).getAddress().getHostAddress());

			buffer.readerIndex(buffer.readerIndex() + 12);
			ChannelBuffer body = buffer.readBytes(bodyLength);
			UpstreamMessage message = new UpstreamMessage();
			message.setCmd(cmd);
			message.setBody(body);
			message.setBodyLength(bodyLength);
			message.setIp(((InetSocketAddress) channel.getRemoteAddress()).getAddress().getHostAddress());
			message.setCheck(check);
			return message;
		}

	}
}
