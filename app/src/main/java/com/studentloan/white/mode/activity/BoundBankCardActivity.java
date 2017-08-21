package com.studentloan.white.mode.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.studentloan.white.MyApplication;
import com.studentloan.white.R;
import com.studentloan.white.interfaces.OnRefreshHeadListener;
import com.studentloan.white.mode.view.OnDelListener;
import com.studentloan.white.mode.view.PullUpView;
import com.studentloan.white.mode.view.TouchDelView;
import com.studentloan.white.net.HttpListener;
import com.studentloan.white.net.ServerInterface;
import com.studentloan.white.net.data.BankCard;
import com.studentloan.white.net.data.BooleanResponse;
import com.studentloan.white.net.data.GetBankCardResponse;
import com.yolanda.nohttp.rest.Response;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

/**
 * Created by fu on 2017/6/29.
 * 我的银行卡
 */

@EActivity(R.layout.activity_bound_bankcard_layout)
public class BoundBankCardActivity extends BaseActivity implements OnRefreshHeadListener{

    @ViewById
    public PullUpView pullUpView;

    @ViewById
    public ListView listView;

    private MyAdapter adapter;

    private List<BankCard> list;


    @Override
    @AfterViews
    public void initViews() {
        super.initViews();

        setTitleText("我的银行卡");

        getUserInfo();

        pullUpView.setOnRefreshHeadListener(this);

        listView.setAdapter(adapter = new MyAdapter());

    }

    @Override
    protected void onResume() {
        super.onResume();

        pullUpView.startDownRefresh();//自动下拉刷新
    }

    @Override
    public void headRefresh() {
        //下拉刷新调用
        getBankCardList();
    }

    @Override
    public void rightImg(ImageView rightImg) {
       rightImg.setVisibility(View.VISIBLE);
       rightImg.setImageResource(R.drawable.icon_bank_add);
    }

    @Override
    public void rightClick(ImageView rightImg) {
        if(list == null || list.size()+1 < 4){
            com.studentloan.white.mode.activity.AddBankCardActivity_.intent(this).
                    flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).start();
        }else{
            showToast("已添加最大卡数");
        }

    }

    private class MyAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder h = null;
            if(null == convertView){
                h = new Holder();
                convertView = LayoutInflater.from(BoundBankCardActivity.this).inflate(R.layout.item_bank_card_layout,null);
                h.bankBgLayout = (LinearLayout) convertView.findViewById(R.id.bankBgLayout);
                h.bankNameTv = (TextView) convertView.findViewById(R.id.bankNameTv);
                h.bankCardNumTv = (TextView) convertView.findViewById(R.id.bankCardNumTv);
                h.setCardTv = (TextView) convertView.findViewById(R.id.setCardTv);
                h.setCardImg = (ImageView) convertView.findViewById(R.id.setCardImg);

                h.delView = (TouchDelView) convertView.findViewById(R.id.delView);
                h.delBtn = (Button) convertView.findViewById(R.id.delBtn);

                convertView.setTag(h);
            }else{
                h = (Holder) convertView.getTag();
            }

            final BankCard bc = list.get(position);
            h.bankNameTv.setText(bc.bankName);
            h.bankCardNumTv.setText("**** **** **** "+ bc.bankCardNum.substring(bc.bankCardNum.length()-4,bc.bankCardNum.length()));


            if(bc.isPrimary == 1){
                h.bankBgLayout.setBackgroundResource(R.drawable.bank_main_card);
                h.setCardTv.setText("主卡");
                h.setCardImg.setImageResource(R.drawable.icon_bank_select);
            }else{
                h.bankBgLayout.setBackgroundResource(R.drawable.bank_vice_card);
                h.setCardTv.setText("副卡");
                h.setCardImg.setImageResource(R.drawable.icon_bank_normal);
            }

            h.setCardImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(bc.isPrimary != 1){
                        setBankCardMain(bc.cardId);
                    }
                }
            });

            h.delView.setSlideViewRight(h.delBtn,false);

            h.delView.setDelListener(new OnDelListener() {
                @Override
                public void invokingUi(boolean b) {
                    bc.isDel = b;
                }
            });

            if(bc.isPrimary == 1){
                h.delView.setIntercept(false);
            }else{
                h.delView.setIntercept(true);
            }


            if(null != bc.isDel && bc.isDel){
                h.delView.sligeDel();
            }else{
                if(h.delView.isDel()){
                    h.delView.sligeGoneDel();
                }
            }

            h.delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delBankCard(bc.cardId,bc.isPrimary);
                }
            });

            return convertView;
        }
    }

    /***
     * 获取所有银行银行卡
     */
    private void getBankCardList(){
        String formatUrl = String.format(ServerInterface.GET_BANK_CARD_LIST,userInfo.account.cellphone);
        requestGet(formatUrl.hashCode(), formatUrl, GetBankCardResponse.class, new HttpListener<GetBankCardResponse>() {
            @Override
            public void onSucceed(int what, Response<GetBankCardResponse> response) {

                pullUpView.pullShow();

                if(response.isSucceed() && response.get() != null){
                    BoundBankCardActivity.this.list = response.get().result;
                    if(null != adapter){
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<GetBankCardResponse> response) {
                pullUpView.pullShow();
            }
        },false);
    }

    private void delBankCard(int cardId,int par){

        if(par == 1){
            showToast("主卡不能删除！");
            return;
        }

        String formatUrl = String.format(ServerInterface.DEL_BANK_CARD,userInfo.account.cellphone,cardId,userInfo.token);
        requestDel(formatUrl.hashCode(), null, formatUrl, BooleanResponse.class, new HttpListener<BooleanResponse>() {
            @Override
            public void onSucceed(int what, Response<BooleanResponse> response) {
                if(response.isSucceed() && response.get() != null){
                    if(response.get().result){
                        pullUpView.startDownRefresh();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<BooleanResponse> response) {

            }
        },true);
    }

    private void setBankCardMain(final int cardId){

        MyApplication.mainActivity.getHuankuan(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if(msg.arg1 == 1){
                    showToast("还有未完成的借款!");
                }else{
                    if(msg.arg1 == 0){
                        switchBankCardMain(cardId);
                    }
                }
                return false;
            }
        });



    }

    private void switchBankCardMain(int cardId){
        String formarUrl = String.format(ServerInterface.SET_BANK_CARD_MAIN,userInfo.account.cellphone,cardId,userInfo.token);
        requestPut(formarUrl.hashCode(), null, formarUrl, BooleanResponse.class, new HttpListener<BooleanResponse>() {
            @Override
            public void onSucceed(int what, Response<BooleanResponse> response) {
                if(response.isSucceed() && response.get() != null){
                    if(response.get().result){
                        pullUpView.startDownRefresh();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<BooleanResponse> response) {

            }
        },true);
    }

    private class Holder{
        TextView bankNameTv,bankCardNumTv,setCardTv;
        ImageView setCardImg;
        LinearLayout bankBgLayout;
        TouchDelView delView;
        Button delBtn;
    }
}
