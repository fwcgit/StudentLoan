package com.studentloan.white.mode.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.studentloan.white.R;
import com.studentloan.white.db.BaseDataDB;
import com.studentloan.white.db.data.AreaModel;
import com.studentloan.white.interfaces.ILetterViewCallback;
import com.studentloan.white.mode.view.LetterIndexView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EActivity(R.layout.comm_select_layout)
public class SelectCityActivity extends BaseActivity implements ILetterViewCallback {
	public static final int REQUEST_CODE = 0x123;
	@ViewById
	LetterIndexView letterIndexView;
	@ViewById 
	TextView showLetterTv;
	@ViewById
	ListView listView;
	private Handler handler = new Handler();
	
	BaseDataDB datadb = BaseDataDB.getInstance();
	
	List<AreaModel> pList;
	
	MyAdapter adapter;
	
	Map<String , Integer> map;
	@Override
	@AfterViews
	public void initViews() {
		super.initViews();
		
		map = new HashMap<String, Integer>();
		
		setTitleText("选择城市");
		
		letterIndexView.setCallback(this);
		
		
		handlerData();
		
		
		listView.setAdapter(adapter = new MyAdapter());
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				AreaModel areaModel = (AreaModel) adapter.getItem(position);
				com.studentloan.white.mode.activity.SelctCityDetailActivity_.intent(SelectCityActivity.this).
						code(areaModel.getCode()).name(areaModel.getName()).
						startForResult(SelctCityDetailActivity.REQUEST_CODE);

			}
		});
		
	}
	
	private void handlerData(){
		new Thread(new Runnable() {
			public void run() {

				pList = datadb.queryProvinceAll();

				if(null == pList || pList.isEmpty()) return;
				
				Collections.sort(pList, new Comparator<AreaModel>() {

					@Override
					public int compare(AreaModel lhs, AreaModel rhs) {
						if(lhs == null  || rhs == null){
							return 0;
						}
						return lhs.getC().compareTo(rhs.getC());
					}
				});
				
				handType(pList);
				
				createPinyYinIndex();
				
				runOnUiThread(new Runnable() {
					public void run() {
						adapter.notifyDataSetChanged();
					}
				});
				
			}
		}).start();
	}

	@Override
	public void letterCallback(int index, String chat) {
		showLetterTv.setVisibility(View.VISIBLE);
		showLetterTv.setText(chat);
		handler.postDelayed(new Runnable() {
			public void run() {
				showLetterTv.setVisibility(View.GONE);
			}
		}, 300);
		
		if(map.containsKey(chat)){
			listView.setSelection(map.get(chat));
			adapter.notifyDataSetInvalidated();
		}
	}
	
	public class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return pList == null ? 0 : pList.size();
		}

		@Override
		public Object getItem(int position) {
			return pList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder h = null;
			if(convertView == null){
				h = new  Holder();
				convertView = View.inflate(SelectCityActivity.this, R.layout.item_comm_select_layout, null);
				h.nameTv =(TextView) convertView.findViewById(R.id.nameTv);
				h.hintTv = (TextView) convertView.findViewById(R.id.hintTv);
				convertView.setTag(h);
			}else{
				h = (Holder) convertView.getTag();
			}
			AreaModel areaModel = pList.get(position);
			h.nameTv.setText(areaModel.getName());
			h.hintTv.setText(areaModel.getC());
			if(areaModel.isType()){
				h.hintTv.setVisibility(View.VISIBLE);
				map.put(areaModel.getC(), position);
			}else{
				h.hintTv.setVisibility(View.GONE);
			}
			return convertView;
		}
		
	}
	
	class Holder{
		TextView nameTv,hintTv;
	}
	
	private void handType(List<AreaModel> pList){
		String  c = "";
		for (int i = 0; i < pList.size(); i++) {
			AreaModel areaModel = pList.get(i);
			if(areaModel.getC().equals(c)){
				areaModel.setType(false);
			}else{
				areaModel.setType(true);
			}
			c = areaModel.getC();
		}
		
	}
	
	private void createPinyYinIndex(){
		for (int i = 0; i < pList.size(); i++) {
			AreaModel areaModel= pList.get(i);
			if(areaModel.isType()){
				map.put(areaModel.getC(), i);
			}else{
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK && SelctCityDetailActivity.REQUEST_CODE == requestCode){
				Intent dataRes = new Intent();
				dataRes.putExtra("name", data.getStringExtra("name"));
				dataRes.putExtra("code", data.getStringExtra("code"));
				setResult(RESULT_OK, data);
				finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
