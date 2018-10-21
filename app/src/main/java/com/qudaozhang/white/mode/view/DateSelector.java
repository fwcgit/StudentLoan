package com.qudaozhang.white.mode.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.qudaozhang.white.MyApplication;
import com.qudaozhang.white.R;
import com.qudaozhang.white.interfaces.WellCallBack;
import com.qudaozhang.white.mode.view.wheel.NumericWheelAdapter;
import com.qudaozhang.white.mode.view.wheel.OnWheelChangedListener;
import com.qudaozhang.white.mode.view.wheel.WheelView;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

@TargetApi(19)
public class DateSelector extends PopupWindow implements OnClickListener {
	private Activity mContext;
	private View mMenuView;
	private ViewFlipper viewfipper;
	private Button btn_submit, btn_cancel;
	private DateNumericAdapter yearAdapter;
	private DateNumericAdapter monthAdapter;
	private DateNumericAdapter dayAdapter;
	private WheelView wvYear, wvMonth, wvDay;
	private String[] dateType;
	private Calendar calendar;
	WellCallBack wellCallBack;
	int yearIndex, montnIndex, dayIndex, yearNow, monthNow, dayNow;
	int yearSelect, monthSelect;
	private OnWheelChangedListener yearListener, monthListener, dayListener;
	String yearStr, monthStr, dayStr;
	@SuppressWarnings("unused")
	private MyApplication app;
	RelativeLayout llDateSelector;
	
	@SuppressLint("InflateParams")
	public DateSelector(Activity context, WellCallBack wellCallBack) {
		super(context);
		this.mContext = context;
		app = (MyApplication) this.mContext.getApplicationContext();
		this.wellCallBack = wellCallBack;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.selector_date, null);
		viewfipper = new ViewFlipper(context);
		viewfipper.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		calendar = Calendar.getInstance();
		//
		llDateSelector = (RelativeLayout) mMenuView.findViewById(R.id.llDateSelector);
		wvYear = (WheelView) mMenuView.findViewById(R.id.year);
		wvMonth = (WheelView) mMenuView.findViewById(R.id.month);
		wvDay = (WheelView) mMenuView.findViewById(R.id.day);
		viewLayout(wvYear);
		viewLayout(wvMonth);
		viewLayout(wvDay);
		
		btn_submit = (Button) mMenuView.findViewById(R.id.submit);
		btn_cancel = (Button) mMenuView.findViewById(R.id.cancel);
		btn_submit.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		yearListener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				yearAdapter.setCenId(newValue);
				yearIndex = newValue;
				yearSelect = newValue;
			}
		};
		monthListener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				monthAdapter.setCenId(newValue);
				montnIndex = newValue;
				monthSelect = newValue;
				selectDay(monthSelect, yearSelect);
			}
		};
		dayListener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				dayAdapter.setCenId(newValue);
				dayIndex = newValue;
				dayNow = newValue+1;
			}
		};
		yearNow = calendar.get(Calendar.YEAR);
		monthNow = calendar.get(Calendar.MONTH) + 1;
		dayNow = calendar.get(Calendar.DAY_OF_MONTH) -1 ;
		
		yearStr = "" + yearNow;
		monthStr = monthNow + "";
		dayStr = "" + dayNow;
		
		
		// 年
		dateType = mContext.getResources().getStringArray(R.array.date);
		yearAdapter = new DateNumericAdapter(context, yearNow - (yearNow - 1970),yearNow, 0);
		yearAdapter.setTextType(dateType[0]);
		wvYear.setViewAdapter(yearAdapter);
		if (TextUtils.isEmpty(yearStr)) {
			wvYear.setCurrentItem(0);
			yearAdapter.setCenId(0);
			yearIndex = 0;
		}else {
			int position = 0;
			for (int i = yearNow - (yearNow - 1970); i <= yearNow; i++) {
				if ((i + "").equals(yearStr)) {
					position = i;
					break;
				}
			}
			wvYear.setCurrentItem(position - (yearNow -(yearNow - 1970)));
			yearAdapter.setCenId(position - (yearNow-(yearNow - 1970)));
			yearIndex = Integer.parseInt(yearStr.trim()) - 2005;
		}
		
		wvYear.addChangingListener(yearListener);
		yearSelect = yearNow;
		
		calendar.set(Calendar.MONTH, Integer.parseInt(monthStr)-1);
		monthNow = calendar.get(Calendar.MONTH);
		// 月
		dateType = mContext.getResources().getStringArray(R.array.date);
		monthAdapter = new DateNumericAdapter(context, 1, 12, 0);
		monthAdapter.setTextType(dateType[1]);
		wvMonth.setViewAdapter(monthAdapter);
		if (TextUtils.isEmpty(monthStr)) {
			wvMonth.setCurrentItem(monthNow);
			monthAdapter.setCenId(monthNow);
			montnIndex = monthNow;
		} else {
			int position = 0;
 			for (int i = 1; i <= 12; i++) {
				if ((i + "").equals( monthStr)) {
					position = i;
					break;
				}
			}
			wvMonth.setCurrentItem(position - 1);
			monthAdapter.setCenId(position - 1);
		}
		wvMonth.addChangingListener(monthListener);
		monthSelect = monthNow;
		dayNow = calendar.get(Calendar.DAY_OF_MONTH);
		// selectDay(monthSellect, yearSelect);

		int maxDay = dayInSelectYearAndMonth(monthSelect, yearSelect);
		// 日
		dateType = mContext.getResources().getStringArray(R.array.date);
		dayAdapter = new DateNumericAdapter(mContext, 1, maxDay, 0);
		dayAdapter.setTextType(dateType[2]);
		wvDay.setViewAdapter(dayAdapter);
		if (TextUtils.isEmpty(dayStr)) {
			wvDay.setCurrentItem(dayNow - 1);
			dayAdapter.setCenId(dayNow - 1);
		} else {
			int position = 0;
			for (int i = 1; i <= maxDay; i++) {
				if ((i + "").equals(dayStr)) {
					position = i;
					break;
				}
			}
			dayNow = position;
			wvDay.setCurrentItem(position - 1);
			dayAdapter.setCenId(position - 1);
		}
		wvDay.addChangingListener(dayListener);
		viewfipper.addView(mMenuView);
		viewfipper.setFlipInterval(6000000);
		this.setContentView(viewfipper);
		this.setWidth(LayoutParams.MATCH_PARENT);
		// this.setWidth(LayoutParams.FILL_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		llDateSelector.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				DateSelector.this.dismiss();
			}
		});
		ColorDrawable dw = new ColorDrawable(0x0d000000);
		this.setBackgroundDrawable(dw);
		this.setAnimationStyle(R.style.AlterDialogAnima);
		this.update();

	}

	private int dayInSelectYearAndMonth(int _monthIndexPath, int yearint) {
		if (_monthIndexPath == 0 || _monthIndexPath == 2
				|| _monthIndexPath == 4 || _monthIndexPath == 6
				|| _monthIndexPath == 7 || _monthIndexPath == 9
				|| _monthIndexPath == 11) {
			return 31;
		} else if (_monthIndexPath == 1) {

			if (((yearint % 4 == 0) && (yearint % 100 != 0))
					|| (yearint % 400 == 0)) {
				return 29;
			} else {
				return 28;
			}
		} else {
			return 30;
		}
	}

	private void selectDay(int _monthIndexPath, int yearint) {
		int maxDay = dayInSelectYearAndMonth(_monthIndexPath, yearint);
		// 日
		dateType = mContext.getResources().getStringArray(R.array.date);
		dayAdapter = new DateNumericAdapter(mContext, 1, maxDay, 0);
		dayAdapter.setTextType(dateType[2]);
		wvDay.setViewAdapter(dayAdapter);
		if(maxDay < dayNow){
			dayNow = maxDay;
		}
		wvDay.setCurrentItem(dayNow - 1);
		dayAdapter.setCenId(dayNow - 1);
		wvDay.addChangingListener(dayListener);
	}

	private void viewLayout(WheelView button) {
		LinearLayout.LayoutParams layoutParams = (android.widget.LinearLayout.LayoutParams) button
				.getLayoutParams();
		int width = mContext.getResources().getDisplayMetrics().widthPixels;
		layoutParams.width = (int) (width / 8);
		button.setLayoutParams(layoutParams);
	}

	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		super.showAtLocation(parent, gravity, x, y);
		viewfipper.startFlipping();
	}
	@Override
	public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
		super.showAsDropDown(anchor, xoff, yoff, gravity);
		viewfipper.startFlipping();
	}
	@SuppressLint("SimpleDateFormat")
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.cancel:
			this.dismiss();
			break;
		case R.id.submit:
			String str = yearAdapter.getItemText(wvYear.getCurrentItem()) + "-"
					+ monthAdapter.getItemText(wvMonth.getCurrentItem()) + "-"
					+ dayAdapter.getItemText(wvDay.getCurrentItem());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try {
				Date strDate = sdf.parse(str);
				String selectDate = sdf.format(strDate);
				String currDate = sdf.format(new Date());
				if(currDate.equals(selectDate)){
					Toast.makeText(mContext, "不可选择当前及未来日期", Toast.LENGTH_LONG).show();
					return ;
				}
				
				if(sdf.parse(currDate).getTime() < strDate.getTime()){
					Toast.makeText(mContext, "不可选择未来日期", Toast.LENGTH_LONG).show();
					return ;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			wellCallBack.contentBack(yearAdapter.getItemText(wvYear.getCurrentItem()) + "年"
					+ monthAdapter.getItemText(wvMonth.getCurrentItem()) + "月"
					+ dayAdapter.getItemText(wvDay.getCurrentItem())+"日");
			break;
		}
		this.dismiss();
	}

	/**
	 * Adapter for numeric wheels. Highlights the current value.
	 */
	private class DateNumericAdapter extends NumericWheelAdapter {

		/**
		 * Constructor
		 */
		public DateNumericAdapter(Context context, int minValue, int maxValue,
				int current) {
			super(context, minValue, maxValue);
			setTextSize(17);
		}

		protected void configureTextView(TextView view) {
			super.configureTextView(view);
		}

		public CharSequence getItemText(int index) {
			return super.getItemText(index);
		}
	}
}
