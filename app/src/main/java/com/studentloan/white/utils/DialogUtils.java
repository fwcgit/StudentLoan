package com.studentloan.white.utils;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.studentloan.white.MyApplication;
import com.studentloan.white.R;
import com.studentloan.white.interfaces.DialogCallback;
import com.studentloan.white.mode.view.TouchDelView;
import com.studentloan.white.mode.view.wheel.AbstractWheelTextAdapter;
import com.studentloan.white.mode.view.wheel.WheelView;
import com.studentloan.white.net.data.BankCard;
import com.studentloan.white.net.data.JieKuanFeiYong;

import java.util.List;

/***
 * dialog  工具类。
 * @author fu
 *
 */
public class DialogUtils {
	private static DialogUtils dialogUtils;
	String bankdata ="";

	public static DialogUtils getInstance(){
		return dialogUtils == null ? dialogUtils = new DialogUtils() : dialogUtils;
	}
	
	public Dialog createDialog(Context ctx,int resLayout){
		if(ctx == null ) return null;
		Dialog dialog = new Dialog(ctx, R.style.custom_dialog);
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.width = MyApplication.getInstance().widthPixels;
		lp.height = MyApplication.getInstance().heightPixels;
		dialog.setContentView(resLayout);
		return dialog;
	}

	public void showSelectHetong(Context ctx,final DialogCallback callback){
		final Dialog dialog = createDialog(ctx, R.layout.dialog_select_hetong_layout);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);

		dialog.findViewById(R.id.mmBtn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(callback != null){
					callback.hetong(1);
				}
			}
		});

		dialog.findViewById(R.id.zpBtn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(callback != null){
					callback.hetong(2);
				}
			}
		});

		dialog.findViewById(R.id.cancelBtn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	public void showSelectPicture(Context ctx,final DialogCallback callback,int res){
		final Dialog dialog = createDialog(ctx, R.layout.dialog_select_picture_layout);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		ImageView slImg = (ImageView) dialog.findViewById(R.id.slImg);
		View line = dialog.findViewById(R.id.slView);
		TextView slTv = (TextView) dialog.findViewById(R.id.slTv);
		if(res == -1){
			slImg.setVisibility(View.GONE);
			line.setVisibility(View.GONE);
			slTv.setVisibility(View.GONE);
		}else{
			slImg.setImageResource(res);
		}

		dialog.findViewById(R.id.cameraBtn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(callback != null){
					callback.camareClick();
				}
			}
		});
		
		dialog.findViewById(R.id.picBtn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(callback != null){
					callback.picClick();
				}
			}
		});
		
		dialog.show();
	}


	public void showSelectGive(Context ctx,final DialogCallback callback,int res,String slStr){
		final Dialog dialog = createDialog(ctx, R.layout.dialog_select_give_layout);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		ImageView slImg = (ImageView) dialog.findViewById(R.id.slImg);
		TextView slTv = (TextView) dialog.findViewById(R.id.slTv);

		slTv.setText(slStr);
		slImg.setImageResource(res);

		dialog.findViewById(R.id.cameraBtn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(callback != null){
					callback.camareClick();
				}
			}
		});

		dialog.findViewById(R.id.picBtn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(callback != null){
					callback.picClick();
				}
			}
		});

		dialog.show();
	}

	public void showConfirmIdCard(Context ctx,final DialogCallback callback){
		final Dialog dialog = createDialog(ctx, R.layout.dialog_confirm_idcard_layout);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);

		dialog.findViewById(R.id.cancelBtn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.findViewById(R.id.confirmBtn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(callback != null){
					callback.confirm();
				}
			}
		});

		dialog.show();
	}

	public void showUpdate(Context ctx,final DialogCallback callback){
		final Dialog dialog = createDialog(ctx, R.layout.dialog_upate_layout);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
				return true;
			}
		});
		dialog.findViewById(R.id.cancelBtn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(callback != null){
					callback.cancel();
				}
			}
		});

		dialog.findViewById(R.id.confirmBtn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(callback != null){
					callback.confirm();
				}
			}
		});

		dialog.show();
	}
	
	public void showSelectList(Context ctx,String[] items,final DialogCallback callback){
		final Dialog dialog = createDialog(ctx, R.layout.dialog_select_list_layout);
		final WheelView wheelView = (WheelView) dialog.findViewById(R.id.wheelView);
		final StringAdapter adapter = new StringAdapter(ctx, items);
		wheelView.setViewAdapter(adapter);
		dialog.findViewById(R.id.cancelBtn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.findViewById(R.id.confirmBtn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(callback!= null){
					callback.typeStr(adapter.getItemText(wheelView.getCurrentItem()).toString());
				}
				dialog.dismiss();
			}
		});
		
		dialog.show();
	}
	

	
	public void showPushMsgDialog(Context ctx,String msg,final DialogCallback callback){
		
		final Dialog dialog = createDialog(ctx, R.layout.dialog_push_msg_layout);
		if(dialog == null) return;
		((TextView)dialog.findViewById(R.id.msgTv)).setText(msg);
		dialog.findViewById(R.id.cancelBtn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.findViewById(R.id.confirmBtn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(callback!= null){
					callback.confirm();
				}
				dialog.dismiss();
			}
		});
		
		dialog.show();
	}

	public void showLoanConfirm(final Context ctx, final JieKuanFeiYong jieKuanFeiYong,final DialogCallback callback){
		final Dialog dialog = createDialog(ctx,R.layout.dialog_loan_confirm_layout);
		dialog.findViewById(R.id.cancelBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null != dialog && dialog.isShowing()){
					dialog.dismiss();
				}
			}
		});

		((TextView)dialog.findViewById(R.id.jieKuanEtv)).setText(jieKuanFeiYong.jinE+"元");
		((TextView)dialog.findViewById(R.id.jiekuanCostTv)).setText(jieKuanFeiYong.feiYong+"元");
		((TextView)dialog.findViewById(R.id.daozhangEtv)).setText(jieKuanFeiYong.daoZhangJinE+"元");
		((TextView)dialog.findViewById(R.id.jiekuanDaysTv)).setText(jieKuanFeiYong.tianShu+"天");
		((TextView)dialog.findViewById(R.id.yinhuanEtv)).setText(jieKuanFeiYong.zongZhiFuFeiYong+"元");

		TextView decTv = (TextView) dialog.findViewById(R.id.decTv);
		decTv.setMovementMethod(LinkMovementMethod.getInstance());//必须设置否则无效

		String dec = decTv.getText().toString();

		SpannableString span = new SpannableString(dec);
		span.setSpan(new ForegroundColorSpan(0xff214cfb),dec.indexOf("《"),dec.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		span.setSpan(new TextClick(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if(null != callback){
					callback.xieyi();
				}

			}
		}), dec.indexOf("《"), dec.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		decTv.setText(span);

		dialog.findViewById(R.id.confirmBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null != dialog && dialog.isShowing()){
					dialog.dismiss();
				}

				if(null != callback){
					callback.confirm();
				}
			}
		});

		dialog.show();

	}

	private class TextClick  extends  ClickableSpan implements OnClickListener{
		private final OnClickListener listener;

		public TextClick(OnClickListener listener) {
			this.listener = listener;
		}

		@Override
		public void onClick(View widget) {
			listener.onClick(widget);
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setColor(ds.linkColor);
			ds.setUnderlineText(false);    //去除超链接的下划线
		}
	}

	public void selectBankCard(Context ctx,List<BankCard> list,final DialogCallback callback){

		final Dialog dialog = createDialog(ctx,R.layout.dialog_select_bankcard_layout);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
				return true;
			}
		});
		ListView listView = (ListView) dialog.findViewById(R.id.listView);
		listView.setAdapter(new MyAdapter(list, ctx, new DialogCallback() {
			@Override
			public void typeStr(String type) {
				bankdata = type;
			}
		}));

		dialog.findViewById(R.id.cancelBtn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.findViewById(R.id.confirmBtn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(callback!= null){
					callback.typeStr(bankdata);
				}
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	public void showServiceAmount(Context ctx,int price,int day){
		final Dialog dialog = createDialog(ctx,R.layout.dialog_service_amount_layout);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
				return true;
			}
		});
		dialog.findViewById(R.id.OkBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null != dialog && dialog.isShowing()){
					dialog.dismiss();
				}
			}
		});

		TextView xinxiTv = (TextView) dialog.findViewById(R.id.xinxiTv);
		TextView minjianTv = (TextView) dialog.findViewById(R.id.minjianTv);
		TextView pinTaiTv = (TextView) dialog.findViewById(R.id.pintaiTv);
		TextView totalTv = (TextView) dialog.findViewById(R.id.totalTv);

		int xinxi = 20;
		int minjian = 20;
		int pintai = 20;

		if(price == 600){
			minjian=20;
			pintai= 20;
		}else if(price == 700){
			minjian=25;
			pintai= 25;
		}else if(price == 800){
			minjian=30;
			pintai= 30;
		}else if(price == 900){
			minjian=35;
			pintai= 35;
		}else if(price == 1000){
			minjian=40;
			pintai= 40;
		}

		xinxiTv.setText(xinxi+"元");
		minjianTv.setText(minjian+"元");
		pinTaiTv.setText(pintai+"元");

		float feiyong = 0f;
		feiyong = xinxi+minjian+pintai+feiyong;

		totalTv.setText(String.format("%.2f",feiyong)+"元");

		dialog.show();
	}
	
	/**
	 * Adapter for countries
	 */
	private class StringAdapter extends AbstractWheelTextAdapter {
		String[] items;
		
		
		public StringAdapter(Context context, String[] items) {
			super(context,R.layout.modify_item, NO_RESOURCE);
			this.items = items;
			
			setItemTextResource(R.id.item);
		}


		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		public int getItemsCount() {
			return items.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return items[index];
		}
	}


	private class MyAdapter extends BaseAdapter {

		private List<BankCard> list;
		private Context ctx;
		DialogCallback callback;

		public MyAdapter(List<BankCard> list, Context ctx,DialogCallback callback) {
			this.list = list;
			this.ctx = ctx;
			this.callback= callback;

			for (int i = 0 ;i < list.size() ;i++){
				if(list.get(i).isPrimary == 1){
					list.get(i).isSelect = true;
					break;
				}
			}
		}

		public void selectCard(String num){
			for (int i = 0 ;i < list.size();i++)
			{
				BankCard bc = list.get(i);
				if(bc.bankCardNum.equals(num)){
					bc.isSelect = true;
				}else{
					bc.isSelect =false;
				}
			}
		}


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
		public View getView(final int position, View convertView, ViewGroup parent) {
			Holder h = null;
			if(null == convertView){
				h = new Holder();
				convertView = LayoutInflater.from(ctx).inflate(R.layout.item_bank_card_layout,null);
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

			}else{
				h.bankBgLayout.setBackgroundResource(R.drawable.bank_vice_card);
				h.setCardTv.setText("副卡");

			}

			h.setCardImg.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					selectCard(bc.bankCardNum);

					notifyDataSetChanged();

				}


			});

			if(bc.isSelect){
				if(callback != null){
					callback.typeStr(bc.bankName+","+bc.bankCardNum);
				}
				h.setCardImg.setImageResource(R.drawable.icon_bank_select);
			}else{
				h.setCardImg.setImageResource(R.drawable.icon_bank_normal);
			}

			h.delView.setIntercept(false);

			return convertView;
		}

	}

	private class Holder{
		TextView bankNameTv,bankCardNumTv,setCardTv;
		ImageView setCardImg;
		LinearLayout bankBgLayout;
		TouchDelView delView;
		Button delBtn;
	}

}



