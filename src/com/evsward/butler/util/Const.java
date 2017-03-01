package com.evsward.butler.util;

import com.evsward.butler.activity.MyApplication;

import android.provider.Settings.Secure;

/**
 * 公共静态常量类
 * 
 * @Date Apr 2, 2015
 * @author liuwb.edward
 */
public class Const {
	// 安卓设备唯一标示
	public static String ANDROID_ID = Secure.getString(MyApplication.getContext().getContentResolver(), Secure.ANDROID_ID);
	// 版本信息
	private static final String VERSION = "1.2";
	// 系统种类
	public static final int HI = 1;
	// 倒计时前注标识位
	public static final int countdownBeforeChip = 11;
	// 日期格式
	public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	// 日期格式
	public static final String DATE_PATTERN1 = "yyyy-MM-dd_HH点";
	// 日期格式
	public static final String TIME_PATTERN = "aa hh:mm";
	// http请求头
	public static final String HTTP = "http://";
	// 默认服务器访问端口
	public static final String SERVER_PORT = ":7979/Hawaii-core-server";// :8080/HIserver
	// 默认打印机访问端口
	public static final int PRINT_PORT = 9100;
	// 默认socket访问端口
	public static final int SOCKET_PORT = 22399;
	// 本地图片地址
	public static final String HIGHLEVLE_AVATAR_FOLDER = "/butler/avatar";
	// 本地座位缩略图地址
	public static final String SEAT_AVATAR_FOLDER = "/butler/seat_avatar";
	// 本地广告图片地址
	public static final String AD_IMAGE_FOLDER = "/butler/ad";
	// 本地输出日志地址
	public static final String LOCAL_LOG_FOLDER = "/butler/logs";

	public interface HeartBeatConst {
		// 默认心跳间隔30秒
		public static final int Default_HeartBeat_Time = 30*1000;
		// 比赛管理--座位信息，心跳间隔300秒
		public static final int SeatInfo_HeartBeat_Time = 2*60*1000;
	}

	// 服务器返回json成功rspCode
	public static final int RspCode_Success = 1;
	// 用于会员报名显示比赛给每条比赛ID赋值
	public static final String MATCH_ID_KEY = "258";
	// socket message cmd
	public static final int Ack_XLarge_TV_101 = (101 | 0x8000000);
	public static final int Ack_XLarge_TV_MatchInfo_151 = (151 | 0x8000000);
	public static final int Ack_XLarge_TV_MatchList_152 = (152 | 0x8000000);
	public static final int Ack_XLarge_TV_PlayerList_153 = (153 | 0x8000000);
	public static final int Ack_XLarge_TV_Enter_154 = (154 | 0x8000000);
	public static final int Ack_HeartBeat_256 = (256 | 0x8000000);
	public static final int Ack_Manage_TV_501 = (501 | 0x8000000);
	public static final int Ack_MatchList_502 = (502 | 0x8000000);
	public static final int Ack_Mange_Match_503 = (503 | 0x8000000);
	public static final int Ack_Manage_Process_504 = (504 | 0x8000000);
	// send socket code
	public static final int Req_XLarge_TV_101 = 101;
	public static final int Req_XLarge_TV_MatchInfo_151 = 151;
	public static final int Req_XLarge_TV_MatchList_152 = 152;
	public static final int Req_XLarge_TV_PlayerList_153 = 153;
	public static final int Req_XLarge_TV_Enter_154 = 154;
	public static final int Req_HeartBeat_256 = 256;
	public static final int Req_Manage_TV_501 = 501;
	public static final int Req_MatchList_502 = 502;
	public static final int Req_Table_Seat_List_503 = 503;
	public static final int Req_Manage_Process_504 = 504;
	// action intent filter
	public static final String Ack_XLarge_TV_101_ACTION = "org.evsward.butler.XLarge_TV_action";
	public static final String Ack_XLarge_TV_MatchInfo_151_ACTION = "org.evsward.butler.XLarge_TV_MatchInfo_action";
	public static final String Ack_XLarge_TV_MatchList_152_ACTION = "org.evsward.butler.XLarge_TV_MatchList_action";
	public static final String Ack_XLarge_TV_PlayerList_153_ACTION = "org.evsward.butler.XLarge_TV_PlayerList_action";
	public static final String Ack_XLarge_TV_Enter_154_ACTION = "org.evsward.butler.XLarge_TV_Enter_action";
	public static final String Ack_HeartBeat_256_ACTION = "org.evsward.butler.HeartBeat_action";
	public static final String ACK_Manage_TV_501_ACTION = "org.evsward.butler.manage_TV_action";
	public static final String ACK_MatchList_502_ACTION = "org.evsward.butler.matchlist_action";
	public static final String ACK_Table_Seat_List_503_ACTION = "org.evsward.butler.table_seat_list_action";
	public static final String ACK_Manage_Process_504_ACTION = "org.evsward.butler.manage_process_action";
	// -------------------------------接口--------------------------------------
	// 检测服务器连接
	public static final String CHECK_SERVER_ADDRESS = "/system/ping/check.do";
	// 检测打印机连接
	public static final String CHECK_PRINTER_ADDRESS = "/SSI/index.htm";
	// 登陆接口
	public static final String METHOD_LOGIN = "/system/login/uuidLogin.do?empUuid=%s&version=" + VERSION;
	// 获取NFC卡号信息
	public static final String METHOD_NFC_GET_CARD_NO = "/member/member/getcardno.do?nfcID=%d";
	// 注册会员
	public static final String METHOD_REGISTER = "/member/member/addMember.do";
	// 刷NFC卡查询会员
	public static final String METHOD_SEARCH_MEMBER_BY_NFC = "/member/member/searchMemNFC.do?nfcID=%d";
	// 模糊查找用户
	public static final String METHOD_SEARCH_MEMBER_BY_INPUT = "/member/member/vagueFindMems.do?vagueParam=%s";
	// 服务器图片地址
	public static final String METHOD_DOWNLOAD_IMAGE_PATH = "/image/memimage/%s";
	// 更新会员信息
	public static final String METHOD_UPDATE_MEMBER_INFO = "/member/member/updateMember.do";
	// 查询单个大屏幕设备信息
	public static final String METHOD_FIND_SCREEN = "/screen/screen/findScreen.do?devImei=%s";
	// 更新单个大屏幕设备信息
	public static final String METHOD_UPDATE_SCREEN = "/screen/screen/updateScreen.do?devImei=%s&devName=%s&pushType=%d&compID=%d&language=%d";
	// 添加会员nfc卡接口
	public static final String METHOD_NEW_MEMBER_NFC = "/system/nfcInfo/addnfc.do?nfcID=%d";
	// 添加管理员nfc卡接口
	public static final String METHOD_NEW_MANAGER_NFC = "/system/nfcInfo/addEmployee.do?empUuid=%s";
	// 新建比赛
	public static final String METHOD_NEW_MATCH = "/competition/competition/createCompetition.do?empUuid=%s&compName=%s&leastPrize=%d&regFee=%d&serviceFee=%d&beginChip=%d&unit=%d&roundTempID=%d&stopRegRank=%d&reEntry=%d&tableType=%d&aword=%d&beginTime=%d&assignSeat=%d&compType=%d";
	// 盲注列表
	public static final String METHOD_MANGZHU_LIST = "/roundTemp/roundTemp/getRoundTempList.do";
	// 会员报名，查询比赛信息
	public static final String METHOD_MEM_ENTER_MATCH = "/memRegComp/memRegComp/memRegCompSearch.do?empUuid=%s&memID=%d";
	// 会员报名，报名比赛
	public static final String METHOD_MEM_REG_MATCH = "/memRegComp/memRegComp/memRegComp.do?empUuid=%s&memID=%d&compID=%d";
	// 会员报名，VIP报名比赛
	public static final String METHOD_VIP_REG_MATCH = "/memRegComp/memRegComp/vipMemRegComp.do?empUuid=%s&memID=%d&compID=%d&tableNO=%d&seatNO=%d";
	// 会员查询，查询与会员相关的比赛信息
	public static final String METHOD_MEM_SEARCH_MATCH = "/compMember/compMember/memCompSearch.do?empUuid=%s&memID=%d";
	// 入场安检，pad端会员查询
	public static final String METHOD_MEM_ENTRANCE_CHECK = "/check/welcome/checkWelcome.do?empUuid=%s&memID=%d";
	// 座位信息
	public static final String METHOD_SEAT_INFO = "";
	// 座位头像
	public static final String METHOD_DOWNLOAD_SEAT_IMAGE = "/image/memimage/small/%s";
	// 广告
	public static final String METHOD_DOWNLOAD_AD_IMAGE = "/image/advertisment/%s";
	// 删除比赛
	public static final String METHOD_DELETE_MATCH = "/competition/competition/delCompetition.do?empUuid=%s&compID=%d";
	// 结束比赛
	public static final String METHOD_FINISH_MATCH = "/competition/competition/endCompetition.do?empUuid=%s&compID=%d";
	// 比赛进阶-查询比赛列表请求
	public static final String METHOD_MATCH_ADVANCED_COMP_LIST = "/compAdvanManage/importComp/getOriginalAndDestCompList.do?empUuid=%s";
	// 比赛进阶-导入请求
	public static final String METHOD_MATCH_ADVANCED = "/compAdvanManage/importComp/importComps.do?empUuid=%s&orignal=%s&destCompID=%d&condition=%d";
	// 开启牌桌
	public static final String METHOD_OPEN_TABLE = "/compManage/seatManage/openCompTable.do?empUuid=%s&compID=%d&openTableNO=%s";
	// 平衡选手
	public static final String METHOD_BALANCE_PLAYER = "/compManage/seatManage/balanceCompTable.do?empUuid=%s&compID=%d&cmID=%d&memID=%d&tableNO=%d&seatNO=%d";
	// 淘汰选手
	public static final String METHOD_ELIMINATE_PLAYER = "/compManage/seatManage/outMember.do?empUuid=%s&compID=%d&tableNO=%d&seatNO=%d&memID=%d";
	// 释放牌桌
	public static final String METHOD_RELEASE_TABLE = "/compManage/seatManage/releaseCompTable.do?empUuid=%s&compID=%d&tableNO=%d";
	// 爆桌
	public static final String METHOD_BURST_TABLE = "/compManage/seatManage/burstCompTable.do?empUuid=%s&compID=%d&tableNO=%d";
	// 位移记录
	public static final String METHOD_SEAT_MOVEMENT_HISTORY = "/Logs/compLog/getMoveSeatLogs.do?compID=%d&empUuid=%s";
	// 座位查询会员
	public static final String METHOD_SEAT_SEARCH_MEMBER = "/member/member/searchMemByMemID.do?memID=%d";
	// 比赛管理-玩家列表
	public static final String METHOD_MATCH_MANAGE_PLAYERS = "/compManage/playerManage/getLivedPlayers.do?empUuid=%s&compID=%d";
	// 请求牌桌资源
	public static final String METHOD_REQ_TABLE_RES = "/cardTable/cardTable/listTables.do";
	// 新建牌桌
	public static final String METHOD_NEW_TABLE = "/cardTable/cardTable/addTables.do?address=%s&tableCount=%d&empUuid=%s";
	// 删除牌桌
	public static final String METHOD_DELETE_PLACE = "/cardTable/cardTable/delTables.do?empUuid=%s";
	// 比赛管理-筹码列表
	public static final String METHOD_MATCH_MANAGE_CHIPS = "/compManage/chipInfoManage/getLivedPlayersChipInfo.do?empUuid=%s&compID=%d";
	// 比赛管理-查询选手筹码
	public static final String METHOD_GET_PLAYER_CHIPS = "/compManage/chipInfoManage/getPlayerChipInfo.do?empUuid=%s&compID=%d&nfcID=%d&cardNO=%s";
	// 比赛管理-修改选手筹码
	public static final String METHOD_MODIFY_PLAYER_CHIPS = "/compManage/chipInfoManage/updatePlayerChipInfo.do?empUuid=%s&compID=%d&mcID=%d&memID=%d&chip=%d";
	// 比赛管理-广告列表
	public static final String METHOD_ADVERTISEMENT_LIST = "/compManage/advertInfoManage/getAllAdvertInfo.do?empUuid=%s&compID=%d";
	// 比赛管理-广告设置
	public static final String METHOD_ADVERTISEMENT_SET = "/compManage/advertInfoManage/updateCompAdvertInfo.do";
	// ---------------------------------------进程管理---------------------------------------
	// 比赛管理-进程-修改计时
	public static final String METHOD_MATCH_PROCESS_SET_COUNTDOWN = "/compManage/procManage/editRunningTime.do?empUuid=%s&compID=%d&curRank=%d&jumpTime=%d";
	// 比赛管理-进程-进一级
	public static final String METHOD_MATCH_PROCESS_FORWARD_ONE_LEVEL = "/compManage/procManage/goForward.do?empUuid=%s&compID=%d&curRank=%d";
	// 比赛管理-进程-退一级
	public static final String METHOD_MATCH_PROCESS_BACK_ONE_LEVEL = "/compManage/procManage/goBack.do?empUuid=%s&compID=%d&curRank=%d";
	// 比赛管理-进程-暂停比赛
	public static final String METHOD_MATCH_PROCESS_PAUSE_MATCH = "/compManage/procManage/pauseCompetition.do?empUuid=%s&compID=%d";
	// 比赛管理-进程-修改选手人数
	public static final String METHOD_MATCH_PROCESS_MODIFY_PEOPLE_NUM = "/compManage/procManage/subPlayer.do?empUuid=%s&compID=%d&subNum=%d";
	// ---------------------------------------奖池管理---------------------------------------
	// 奖池列表
	public static final String METHOD_REWARD_LIST = "/compManage/prizeInfoManage/getAllRunningChipInfo.do?empUuid=%s&compID=%d";
	// 修改奖金（单个选手）
	public static final String METHOD_MODIFY_ONE_PLAYER_REWARD = "/compManage/prizeInfoManage/updateRunningChipInfo.do?empUuid=%s&compID=%d&ranking=%d&memID=%d&awordMoney=%d";
	// 打印奖金（单个选手）
	public static final String METHOD_PRINTINFO_ONE_PLAYER_REWARD = "/compManage/prizeInfoManage/updateRunningChipInfo.do?empUuid=%s&compID=%d&ranking=%d&memID=%d&awordMoney=%d";
	// ---------------------------------------大屏幕管理---------------------------------------
	// 盒子注册
	public static final String METHOD_REGIST_TV_BOX = "/screen/show/registScreenDev.do?devImei=%s";
	// 玩家列表
	public static final String METHOD_TV_PLAYERLIST = "/screen/show/getScreenPlayersInfo.do?compID=%d&devImei=%s";
	// 请求奖励信息
	public static final String METHOD_TV_PRIZE_INFO = "/screen/show/getScreenPrizes.do?compID=%d&devImei=%s";
	// 大屏幕滚动最低玩家数量
	public static int minPlayerEntered = 15;
	// 大屏幕滚动最低奖池名次的数量
	public static int minPrizeListNum = 3;

}
