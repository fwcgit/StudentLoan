package com.qudaozhang.white.mode.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.qudaozhang.white.R;
import com.qudaozhang.white.db.BaseDataDB;
import com.qudaozhang.white.db.data.AreaModel;
import com.qudaozhang.white.interfaces.ILetterViewCallback;
import com.qudaozhang.white.mode.view.LetterIndexView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fu on 2016/12/23.
 */

@EActivity(R.layout.comm_select_layout)
public class SelctCityDetailActivity extends BaseActivity implements ILetterViewCallback {
    public static final int REQUEST_CODE = 0x124;
    @ViewById
    LetterIndexView letterIndexView;
    @ViewById
    TextView showLetterTv;
    @ViewById
    ListView listView;
    private Handler handler = new Handler();

    BaseDataDB datadb = BaseDataDB.getInstance();

    List<AreaModel> pList;

    SelctCityDetailActivity.MyAdapter adapter;

    Map<String , Integer> map;

    @Extra
    String code;
    @Extra
    String name;
    @Override
    @AfterViews
    public void initViews() {
        super.initViews();

        setTitleText("选择城市");

        map = new HashMap<String, Integer>();

        letterIndexView.setCallback(this);

        handlerData();

        listView.setAdapter(adapter = new SelctCityDetailActivity.MyAdapter());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AreaModel areaModel = (AreaModel) adapter.getItem(position);
                Intent data = new Intent();
                data.putExtra("name", name+" "+areaModel.getName());
                data.putExtra("code", areaModel.getFcode());
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    private void handlerData(){
        new Thread(new Runnable() {
            public void run() {

                pList = datadb.queryProvince(code);

                Collections.sort(pList, new Comparator<AreaModel>() {

                    @Override
                    public int compare(AreaModel lhs, AreaModel rhs) {
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

    class MyAdapter extends BaseAdapter {

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
            SelctCityDetailActivity.Holder h = null;
            if(convertView == null){
                h = new SelctCityDetailActivity.Holder();
                convertView = View.inflate(SelctCityDetailActivity.this, R.layout.item_comm_select_layout, null);
                h.nameTv =(TextView) convertView.findViewById(R.id.nameTv);
                h.hintTv = (TextView) convertView.findViewById(R.id.hintTv);
                convertView.setTag(h);
            }else{
                h = (SelctCityDetailActivity.Holder) convertView.getTag();
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
}
