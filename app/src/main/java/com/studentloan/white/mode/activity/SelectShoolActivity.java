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
import com.studentloan.white.db.data.UniversityModel;
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
public class SelectShoolActivity extends BaseActivity implements ILetterViewCallback {
	public static final int REQUEST_CODE = 0x1235;
	@ViewById
	LetterIndexView letterIndexView;
	@ViewById 
	TextView showLetterTv;
	@ViewById
	ListView listView;
	private Handler handler = new Handler();
	
	BaseDataDB datadb = new BaseDataDB();
	
	List<UniversityModel> pList;
	
	MyAdapter adapter;
	
	Map<String , Integer> map;
	@Override
	@AfterViews
	public void initViews() {
		super.initViews();
		
		map = new HashMap<String, Integer>();
		
		handlerData();
		
		setTitleText("选择学校");
		
		letterIndexView.setCallback(this);
		

		listView.setAdapter(adapter = new MyAdapter());
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				UniversityModel universityModel = (UniversityModel) adapter.getItem(position);
				Intent data = new Intent();
				data.putExtra("name", universityModel.getName());
				data.putExtra("code", universityModel.getCode());
				SelectShoolActivity.this.setResult(RESULT_OK, data);
				SelectShoolActivity.this.finish();
			}
		});
	}
	
	private void handlerData(){
		new Thread(new Runnable() {
			public void run() {
				
				datadb.init();
				
				pList = datadb.queryUniversAll();
				
				if(pList == null || pList.isEmpty()){
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							finish();
						}
					});

					return;

				}
				
				Collections.sort(pList, new Comparator<UniversityModel>() {

					@Override
					public int compare(UniversityModel lhs, UniversityModel rhs) {
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
				convertView = View.inflate(SelectShoolActivity.this, R.layout.item_comm_select_layout, null);
				h.nameTv =(TextView) convertView.findViewById(R.id.nameTv);
				h.hintTv = (TextView) convertView.findViewById(R.id.hintTv);
				convertView.setTag(h);
			}else{
				h = (Holder) convertView.getTag();
			}
			UniversityModel universityModel= pList.get(position);
			h.nameTv.setText(universityModel.getName());
			h.hintTv.setText(universityModel.getC());
			if(universityModel.isType()){
				h.hintTv.setVisibility(View.VISIBLE);
			}else{
				h.hintTv.setVisibility(View.GONE);
			}
			return convertView;
		}
		
	}
	
	class Holder{
		TextView nameTv,hintTv;
	}
	
	private void handType(List<UniversityModel> pList){
		String  c = "";
		for (int i = 0; i < pList.size(); i++) {
			UniversityModel universityModel = pList.get(i);
			if(universityModel.getC().equals(c)){
				universityModel.setType(false);
			}else{
				universityModel.setType(true);
			}
			c = universityModel.getC();
		}
	}
	
	private void createPinyYinIndex(){
		for (int i = 0; i < pList.size(); i++) {
			UniversityModel universityModel= pList.get(i);
			if(universityModel.isType()){
				map.put(universityModel.getC(), i);
			}else{
			}
		}
	}
	
	
}
