package com.qudaozhang.white.net.data;

/**
 * Created by fu on 2017/8/1.
 */

public class JieKuanFeiYong {

//    成员	类型	备注
//    jinE	Integer	借款金额
//    feiYong	Float	借款费用
//    daoZhangJinE	Integer	借款天数
//    tianShu	Integer	借款天数
//    liXi	Float	利息
//    yingHuanJinE	Float	应还金额

//    {
//        "module": "borrows-getJieKuanFeiYongDetail",
//            "errorCode": 0,
//            "errorMsg": "",
//            "prompt": false,
//            "result": {
//        "jinE": 900,
//                "feiYong": 202.5,
//                "daoZhangJinE": 697.5,
//                "tianShu": 15,
//                "yingHuanJinE": 859,
//                "shouXuFei": 4
//    }
//    }

    public int jinE;
    public float feiYong;
    public float daoZhangJinE;
    public int tianShu;
    public float liXi;
    public float yingHuanJinE;
    public double shouXuFei;
    public double zongZhiFuFeiYong;
}
