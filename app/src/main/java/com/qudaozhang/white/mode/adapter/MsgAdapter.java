package com.qudaozhang.white.mode.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qudaozhang.white.R;
import com.qudaozhang.white.net.data.Message;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("InflateParams")
public class MsgAdapter extends BaseAdapter {
	private Context context;
	private List<Message> msgList = new ArrayList<Message>();
	public MsgAdapter(Context context) {
		super();
		this.context = context;
	}

	@Override
	public int getCount() {
		return msgList == null ? 0 : msgList.size();
	}

	@Override
	public Object getItem(int position) {
		return msgList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder h = null;
		if(convertView == null){
			h = new Holder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_msg_layout, null);
			h.dateTv = (TextView) convertView.findViewById(R.id.dateTv);
			h.timeTv = (TextView) convertView.findViewById(R.id.timeTv);
			h.contentTv = (TextView) convertView.findViewById(R.id.contentTv);
			convertView.setTag(h);
		}else{
			h = (Holder) convertView.getTag();
		}
		Message msg = msgList.get(position);
		h.dateTv.setText(msg.createAt);
		h.contentTv.setText(msg.content);
		return convertView;
	}
	
	public class Holder{
		TextView dateTv,timeTv,contentTv;
	}

	public void setMsgList(List<Message> msgList, boolean ref) {
		if(ref){
			this.msgList = msgList;
		}else{
			this.msgList.addAll(msgList);
		}
		
		notifyDataSetChanged();
	}
	
	

}
