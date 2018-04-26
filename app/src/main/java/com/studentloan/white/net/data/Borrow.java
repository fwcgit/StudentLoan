package com.studentloan.white.net.data;

/**
 * Created by fu on 2017/6/28.
 */

public class Borrow {

//    成员	类型	备注
//    borrowId	Integer	借款ID
//    jieKuanJinE	Integer	借款金额
//    jieKuanTianShu	Integer	借款天数
//    jieKuanRiQi	Long	借款日期
//    jieKuanZhuangTai	Integer	借款状态 0:申请借款, 1:放款成功, -1:放款失败
//    fangKuanShiJian	Long	放款日期,null说明还没放款
//    huanKuanRiQi	Long	还款日期, null说明还没还款
//    huanKuanJinE	Float	实际还款金额,null说明没有还款
//    yingHuanKuanJinE	Float	应该还款金额 17-06-30
//    overdueDays	Integer	逾期天数 17-06-30
//    overdueFee	Float	逾期费用 17-06-30

    public int borrowId;
    public int jieKuanJinE;
    public int jieKuanTianShu;
    public long jieKuanRiQi;
    public int jieKuanZhuangTai;
    public Long fangKuanShiJian;
    public Long huanKuanRiQi;
    public float huanKuanJinE;
    public double yingHuanKuanJinE;
    public int overdueDays;
    public float overdueFee;
    public float jieKuanFeiYong;
    public Long huanKuanDeadline;
    public int xuZu;

}
