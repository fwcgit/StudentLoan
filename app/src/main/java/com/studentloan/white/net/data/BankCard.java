package com.studentloan.white.net.data;

/**
 * Created by fu on 2017/6/29.
 */

public class BankCard {
//    cardId	Integer	银行卡ID
//    bankCardNum	String	卡号
//    bankName	String	银行名称
//    isPrimary	Integer	是否是主卡
//    boundCellphone	String	帐号绑定的银行卡 06-13新增

    public int cardId;
    public String bankCardNum;
    public String bankName;
    public int isPrimary;
    public String boundCellphone;

    public Boolean isDel;
    public boolean isSelect;
}
