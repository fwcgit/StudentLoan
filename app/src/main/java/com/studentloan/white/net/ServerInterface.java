package com.studentloan.white.net;

/***
 * 服务器接口
 * @author fu
 *
 */
public class ServerInterface {
	/***
	 * 发送短信接口
	 */
	public static final String SEND_SMS = "/sms/app/%s/checkcode/register?platform=android";//
	/***
	 * 发送短信重置密码接口
	 */
	public static final String SEND_SMS_RESET = "/sms/app/%s/checkcode/resetpwd?platform=android";//
	/***
	 * 用户注册接口
	 */
	public static final String USER_REGISTER ="/accounts/app/%s/register?platform=android&checkCode=%s&password=%s&invitedBy=%s";
	
	/***
	 * 忘记密码接口
	 */
	public static final String RESET_PWD = "/accounts/app/%s/resetpwd?platform=android&checkCode=%s&newPwd=%s";
	/***
	 * 修改密码接口
	 */
	public static final String MODIFY_PWD="/accounts/app/%s/password?platform=android&oldPwd=%s&newPwd=%s&token=%s";
	/***
	 * 登陆接口
	 */
	public static final String LOGIN = "/accounts/app/%s/login?platform=android&password=%s&appVersion=%s";

	/***
	 * 上传个人基本信息
	 */
	public static final String MODIFY_USER_INFO = "/accounts/app/%s/identification?platform=android&token=%s";
	/***
	 * 修改用户头像地址接口
	 */
	public static final String MODIFY_HEAD="/accounts/app/%S/headImage?platform=android&token=%s";

	/***
	 * 设置紧急联系人接口
	 */
	public static final String MODIFY_CONTACTS = "/accounts/app/%S/emergency-info?platform=android&token=%s";

	/***
	 * 添加联系人接口
	 */
	public static final String INSERT_CONTACTS = "/accounts/app/%s/contacts?platform=android&token=%s";

	/***
	 * 上传职业-白领信息
	 */
	public static final String WHITE_COLLAR_INFO = "/accounts/app/%s/white-collar-info?platform=android&token=%s";

	/***
	 * 上传更多信息
	 */
	public static final String MORE_INFO = "/accounts/app/%s/more-info?platform=android&token=%s";

	/***
	 * 获取借款限额
	 */
	public static final String BORROW_LIMITS = "/borrows/app/%s/borrow-limits?platform=android&token=%s";

	/***
	 * 获取借款进度
	 */
	public static final String BORROW_PROGRESS = "/borrows/app/%s/borrow-progress?platform=android&token=%s";
	/**
	 * 运营商验证是否完成
	 */
	public static final String PASS_YUNYINGSHANG = "/accounts/app/%s/pass-yunyingshang?platform=android";

	/***
	 * 提交审核
	 */
	public static final String SUBMIT = "/accounts/app/%s/submit?platform=android&token=%s";

	/***
	 * 获取-绑定银行卡验证码
	 */
	public static final String SMS_BIND_BANK_CARD = "/sms/app/%s/checkcode/bind-bank-card?platform=android&token=%s";

	/***
	 *新增银行卡号
	 */
	public static final String ADD_BANKCARD = "/accounts/app/%s/bankcards?platform=android&checkCode=%s&token=%s";

	/***
	 * 查询所有银行卡
	 */
	public static final String GET_BANK_CARD_LIST = "/accounts/app/%s/bankcards?platform=android";

	/***
	 * 删除银行卡
	 */
	public static final String DEL_BANK_CARD = "/accounts/app/%s/bankcards/%s?platform=android&token=%s";

	/***
	 * 设置主卡
	 */
	public static final String SET_BANK_CARD_MAIN = "/accounts/app/%s/bankcards/%s/primary?platform=android&token=%s";

	/***
	 * 查询支付的银行
	 */
	public static final String GET_BANKS_NAME = "/borrows/app/%s/banks?platform=android";

	/***
	 * 查询帐号借款
	 */
	public static final String QUERY_BORROWS = "/borrows/app/%s/borrows?platform=android&page=%s&pagesize=%s";

	/***
	 * 申请借款
	 */
	public static final String REQUEST_LOAN = "/borrows/app/%s/borrows?platform=android&jine=%s&tianshu=%s&token=%s";

	/***
	 * 消息
	 */
	public static final String GET_MSG = "/messages/app/%s?platform=android&page=%s&pageSize=%s";

	/***
	 * 网银认证进度
	 */
	public static final String QUERY_WANG_YIN = "/accounts/app/%s/pass-wang-yin?platform=android";

    /****
     * 身份证是否存在
     */
    public static final String IDCARD_EXIST = "/accounts/app/%s/idcard-exist?platform=android&accountId=%s";

	/***
	 * 借款费用明细
	 */
	public static final String BORROW_MONEY_INFO = "/borrows/app/%s/jie-kuan-fei-yong?platform=android&jinE=%s&tianShu=%s";

	/***
	 * 通知服务器还款信息
	 */
	public static final String NOTIFATION_JIAANGLIJIN_E = "/borrows/app/%s/borrows/%s/huankuan?platform=android&jiangli=%s&token=%s";

	/***
	 * 帐号是否通过了最终的人工审核
	 */
	public static final String PASS_VERIFICATION = "/accounts/app/%s/pass-verification?platform=android";

	/***
	 * 退出登陆
	 */
	public static final String EXIT = "/accounts/app/%s/log-out?platform=android&token=%s";

	/***
	 * 获取商户订单流水号
	 */
	public static final String GET_ORDER_NUMBER = "/borrows/app/%s/ding-dan-liu-shui-hao?platform=android";

	/***
	 * 增加身份认证次数
	 */
	public static final String ADD_ID_TIMES = "/accounts/app/%s/add-id-times?platform=android&token=%s";

	/***
	 * 借款协议(已经有借款记录)
	 */
	public static final String JIE_KUAN_XIE_YI = "/borrows/app/%s/jie-kuan-xie-yi?platform=android&borrowId=%s";

	/***
	 * 借款协议(没有借款记录)
	 */
	public static final String JIE_KUAN_XIE_YI_TWO = "/borrows/app/%s/jie-kuan-xie-yi?platform=android&jieKuanJinE=%s&jieKuanTianShu=%s";

	/***
	 * 学信验证是否完成
	 */
	public static final String XUEXIN_AUTH = "/accounts/app/%s/pass-xue-xin?platform=android";
}
