package com.qudaozhang.white.net.data;

/**
 * Created by fu on 2017/8/6.
 */

public class Shenghe {
//    —-verificationResult: Integer, (审核结果)
//            —-verificationDateTime: Long(时间戳，可能null)
//—-submit: Integer,
//            —-submitTime: Long(时间戳，可能null)时间戳，可能null
//—-submitCiShu: Integer（提交自动审核次数）

    public int verificationResult;
    public Long verificationDateTime;
    public int submit;
    public Long submitTime;
    public int submitCiShu;
    public int blackList;
    public int frozen;
    public Long yunYingShangVeriTime;
    public int shengYuShenFenRenZhengCiShu;
    public int shengYuWangYinRenZhengCiShu;
}
