package com.studentloan.white.mode.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.studentloan.white.R;
import com.studentloan.white.net.data.Borrow;
import com.studentloan.white.utils.ConvertUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressLint("InflateParams")
public class HistoryNotesAdapter extends RecyclerView.Adapter<HistoryNotesAdapter.Holder> {

	private Context context;
	private List<Borrow> list = new ArrayList<Borrow>();
	private OnItemClicklistener listerener;

	public interface OnItemClicklistener{
		void onItemClick(View view,int position);
		void onItemLongClick(View view,int position);
	}

	public HistoryNotesAdapter(Context context) {
		super();
		this.context = context;
	}

	public void setOnItemClickListener(OnItemClicklistener listener){
		this.listerener = listener;
	}

	public Borrow getItem(int position){
		return list.get(position);
	}

	@Override
	public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

		Holder holder = new Holder(LayoutInflater.from(context).inflate(R.layout.item_history_notes_layout,parent,false));

		return holder;
	}

	@Override
	public void onBindViewHolder(final Holder h, final int position) {

		if(listerener != null){
			h.view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					listerener.onItemClick(h.view,position);
				}
			});

			h.view.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					listerener.onItemLongClick(h.view,position);
					return false;
				}
			});
		}

		Borrow borrow = list.get(position);
		h.hyPriceTv.setText(borrow.huanKuanJinE+" 元");
		h.jkPriceTv.setText("借款金额："+borrow.jieKuanJinE+" 元");
		h.jkDateTv.setText("借款日期："+ ConvertUtils.dateTimeToStr(new Date(borrow.jieKuanRiQi),"yyyy年MM月dd日"));

		h.sjhkDateTv.setVisibility(View.GONE);
		h.hkDateTv.setVisibility(View.VISIBLE);

		if(borrow.huanKuanDeadline != null){
			h.hkDateTv.setText("应还款日期："+ConvertUtils.dateTimeToStr(new Date(borrow.huanKuanDeadline),"yyyy年MM月dd日"));
		}

		if(borrow.jieKuanZhuangTai < 0){

			h.statusTv.setText("借款失败");
			h.hkDateTv.setVisibility(View.GONE);
			h.sjhkDateTv.setVisibility(View.GONE);
			h.statusTv.setTextColor(0xffff0000);

		}else if(borrow.jieKuanZhuangTai == 0 || borrow.jieKuanZhuangTai == 1){

			h.statusTv.setText("等待放款");
			h.hkDateTv.setVisibility(View.GONE);
			h.statusTv.setTextColor(0xffa2a2a2);

		}else if(borrow.jieKuanZhuangTai == 2){
			h.hyPriceTv.setText(borrow.yingHuanKuanJinE+" 元");
			h.statusTv.setText("立即还款>");
			h.statusTv.setTextColor(0xff27aa29);

		}else if(borrow.jieKuanZhuangTai == 3){
			//h.hyPriceTv.setText(borrow.+" 元");
			h.statusTv.setText("已还清");
			h.statusTv.setTextColor(0xffa2a2a2);
			h.sjhkDateTv.setVisibility(View.VISIBLE);
			h.sjhkDateTv.setText("实际还款日期："+ConvertUtils.dateTimeToStr(new Date(borrow.huanKuanRiQi),"yyyy年MM月dd日"));

		}
	}

	@Override
	public int getItemCount() {
		return list.size();
	}


	
	public class Holder extends RecyclerView.ViewHolder{
		View view;
		TextView hyPriceTv,jkPriceTv,jkDateTv,hkDateTv,sjhkDateTv,statusTv;

		public Holder(View view) {
			super(view);

			this.view = view;

			hyPriceTv = (TextView) view.findViewById(R.id.hyPriceTv);
			jkPriceTv = (TextView) view.findViewById(R.id.jkPriceTv);
			jkDateTv = (TextView) view.findViewById(R.id.jkDateTv);
			hkDateTv = (TextView) view.findViewById(R.id.hkDateTv);
			sjhkDateTv = (TextView) view.findViewById(R.id.sjhkDateTv);
			statusTv = (TextView) view.findViewById(R.id.statusTv);
		}
	}
	
	public void setHistoryList(List<Borrow> list,boolean ref) {
		if(ref){
			this.list = list;
		}else{
			this.list.addAll(list);
		}
		
		notifyDataSetChanged();
	}
}
