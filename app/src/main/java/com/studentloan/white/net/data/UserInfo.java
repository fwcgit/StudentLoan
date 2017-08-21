package com.studentloan.white.net.data;

public class UserInfo {

//	submit	Integer	是否提交个人资料, 06-16
//	passVerification	Integer	是否通过了个人资料审核, 06-16 1:通过
//	frozen	Integer	是否冻结, 06-23 1:帐号冻结
//	blackList	Integer	是否在黑名单中, 06-23, 1:在黑名单中
//	backUrl	String	富有支付后台通知URL 06-24
//	latestVersion	String	最新版本号 07-05
//	isLatest	Boolean	是否是最新版 07-05
//	foreceUpdate	Boolean	是否需要强制升级 07-05
//	downloadUrl	String	android需要，如果需要强制升级，app下载地址 07-05
//	shengYuShenFenRenZhengCiShu	Integer	剩余调用「上传个人认证信息」接口次数 07-17
//	shengYuWangYinRenZhengCiShu	Integer	剩余「网银认证」次数 07-17
//	wangYinRenZhengJieGuo	Integer	「网银认证」结果 07-17

	public Account account;
	public Identification identification;
	public WhiteCollarInfo whiteCollar;
	public StudentInfo student;
	public FreeWorkerInfo freeWorker;
	public EmergencyContactInfo emergencyContact;
	public MoreInfo moreInfo;
	public long xueXinVeriTime;
	public long wangYinVeriTime;
	public Long yunYingShangVeriTime;
	public String headImageUrl;
	public IdImageUrl idImageUrl;


	public int submit;
	public Long submitTime;
	public int submitCiShu;
	public int verificationResult;
	public Long verificationDateTime;
	public int verificationUnPassTimes;
	public int frozen;
	public int blackList;
	public String backUrl;
	public int jiangLiJinE;
	public String token;
	public String latestVersion;
	public boolean isLatest;
	public boolean foreceUpdate;
	public String downloadUrl;
	public int shengYuShenFenRenZhengCiShu;
	public int shengYuWangYinRenZhengCiShu;
	public int wangYinRenZhengJieGuo;
	public long serverTime;

}
