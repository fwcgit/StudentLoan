package com.studentloan.white.mode.activity;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.studentloan.white.R;
import com.studentloan.white.net.HttpListener;
import com.studentloan.white.net.ServerInterface;
import com.studentloan.white.net.data.Borrow;
import com.studentloan.white.net.data.BorrowResponse;
import com.studentloan.white.utils.ConvertUtils;
import com.yolanda.nohttp.rest.Response;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.Date;

@EActivity(R.layout.activity_progress_query_layout)
public class ProgressQueryActivity extends BaseActivity {
	@ViewById
	TextView step1DetailTv, step2DetailTv, step3DetailTv, step1Tv, step2Tv, step3Tv;
	@ViewById
	View step1Line, step2Line, mStep1Line, mStep2Line;
	@ViewById
	TextView jkPriceTv, jkDayTv, costPriceTv, dateTv;
	@ViewById
	TextView shTypeTv, shTimeTv;
	@ViewById
	TextView mStep1DetailTv, mStep2DetailTv, mStep3DetailTv, mStep1Tv, mStep2Tv, mStep3Tv;
	@ViewById
	LinearLayout materialLayout, fkLayout;

	@AfterViews
	public void initViews() {
		super.initViews();

		setTitleText("进度查询");
		getUserInfo();

		materialLayout.setVisibility(View.GONE);
		fkLayout.setVisibility(View.GONE);

		getData();

		fkLayout.setVisibility(View.GONE);
		materialLayout.setVisibility(View.VISIBLE);


		if (userInfo.verificationResult == 0) {
			if (userInfo.submit == 0) {
				mStep1DetailTv.setText("");
				mStep1DetailTv.setTextColor((0xffcccccc));
				mStep1Tv.setBackgroundResource(R.drawable.shape_circle_hui_bg);
				mStep1Line.setBackgroundColor(0xffcccccc);

				mStep2DetailTv.setText("");
				mStep2DetailTv.setTextColor(0xffcccccc);
				mStep2Tv.setBackgroundResource(R.drawable.shape_circle_hui_bg);
				mStep2Line.setBackgroundColor(0xffcccccc);

				mStep3DetailTv.setText("");
				mStep3DetailTv.setTextColor(0xffcccccc);
				mStep3Tv.setBackgroundResource(R.drawable.shape_circle_hui_bg);

				if (userInfo.submitTime != null) {
					shTimeTv.setText(ConvertUtils.dateTimeToStr(new Date(userInfo.submitTime), "yyyy-MM-dd"));

					mStep1DetailTv.setText("提交申请");
					mStep1DetailTv.setTextColor(ContextCompat.getColor(ProgressQueryActivity.this, R.color.bg));
					mStep1Tv.setBackgroundResource(R.drawable.shape_circle_bg);
					mStep1Line.setBackgroundColor(ContextCompat.getColor(ProgressQueryActivity.this, R.color.bg));

					mStep2DetailTv.setText("智能审核");
					mStep2DetailTv.setTextColor(ContextCompat.getColor(ProgressQueryActivity.this, R.color.bg));
					mStep2Tv.setBackgroundResource(R.drawable.shape_circle_bg);
					mStep2Line.setBackgroundColor(ContextCompat.getColor(ProgressQueryActivity.this, R.color.bg));

					mStep3DetailTv.setText("审核未通过");
					mStep3DetailTv.setTextColor(ContextCompat.getColor(ProgressQueryActivity.this, R.color.bg));
					mStep3Tv.setBackgroundResource(R.drawable.shape_circle_bg);
				}

			} else if (userInfo.submit == 1) {

				shTimeTv.setText(ConvertUtils.dateTimeToStr(new Date(userInfo.submitTime), "yyyy-MM-dd"));

				mStep1DetailTv.setText("提交申请");
				mStep1DetailTv.setTextColor(ContextCompat.getColor(ProgressQueryActivity.this, R.color.bg));
				mStep1Tv.setBackgroundResource(R.drawable.shape_circle_bg);
				mStep1Line.setBackgroundColor(ContextCompat.getColor(ProgressQueryActivity.this, R.color.bg));

				mStep2DetailTv.setText("等待审核");
				mStep2DetailTv.setTextColor(ContextCompat.getColor(ProgressQueryActivity.this, R.color.bg));
				mStep2Tv.setBackgroundResource(R.drawable.shape_circle_bg);
				mStep2Line.setBackgroundColor(0xffcccccc);

				mStep3DetailTv.setText("");
				mStep3DetailTv.setTextColor(0xffcccccc);
				mStep3Tv.setBackgroundResource(R.drawable.shape_circle_hui_bg);
			}
		} else if (userInfo.verificationResult == 1) {

			shTimeTv.setText(ConvertUtils.dateTimeToStr(new Date(userInfo.verificationDateTime), "yyyy-MM-dd"));


			mStep1DetailTv.setText("提交申请");
			mStep1DetailTv.setTextColor(ContextCompat.getColor(ProgressQueryActivity.this, R.color.bg));
			mStep1Tv.setBackgroundResource(R.drawable.shape_circle_bg);
			mStep1Line.setBackgroundColor(ContextCompat.getColor(ProgressQueryActivity.this, R.color.bg));

			mStep2DetailTv.setText("等待人工审核");
			mStep2DetailTv.setTextColor(ContextCompat.getColor(ProgressQueryActivity.this, R.color.bg));
			mStep2Tv.setBackgroundResource(R.drawable.shape_circle_bg);
			mStep2Line.setBackgroundColor(ContextCompat.getColor(ProgressQueryActivity.this, R.color.bg));

			mStep3DetailTv.setText("审核通过");
			mStep3DetailTv.setTextColor(ContextCompat.getColor(ProgressQueryActivity.this, R.color.bg));
			mStep3Tv.setBackgroundResource(R.drawable.shape_circle_bg);
		}

	}

	public void getData() {
		String formatUrl = String.format(ServerInterface.BORROW_PROGRESS, userInfo.account.cellphone, userInfo.token);
		requestGet(formatUrl.hashCode(), formatUrl, BorrowResponse.class, new HttpListener<BorrowResponse>() {
			@Override
			public void onSucceed(int what, Response<BorrowResponse> response) {
				if (response.isSucceed() && response.get() != null) {
					Borrow borrow = response.get().result;
					if (null != borrow) {
						fkLayout.setVisibility(View.VISIBLE);
						materialLayout.setVisibility(View.GONE);

						jkPriceTv.setText(borrow.jieKuanJinE + "元");
						jkDayTv.setText(borrow.jieKuanTianShu + "天");
						costPriceTv.setText(borrow.jieKuanFeiYong + "元");
						dateTv.setText(ConvertUtils.dateTimeToStr(new Date(borrow.jieKuanRiQi), "yyyy年MM月dd日"));

						if (borrow.jieKuanZhuangTai < 0) {
							step1DetailTv.setText("申请成功");
							step1DetailTv.setTextColor(ContextCompat.getColor(ProgressQueryActivity.this, R.color.bg));
							step1Tv.setBackgroundResource(R.drawable.shape_circle_bg);
							step1Line.setBackgroundResource(R.color.bg);

							step2DetailTv.setText("等待放款");
							step2DetailTv.setTextColor(ContextCompat.getColor(ProgressQueryActivity.this, R.color.bg));
							step2Tv.setBackgroundResource(R.drawable.shape_circle_bg);
							step2Line.setBackgroundResource(R.color.bg);

							step3DetailTv.setText("租赁失败");
							step3DetailTv.setTextColor(ContextCompat.getColor(ProgressQueryActivity.this, R.color.bg));
							step3Tv.setBackgroundResource(R.drawable.shape_circle_bg);
						} else if (borrow.jieKuanZhuangTai == 0) {
							step1DetailTv.setText("申请成功");
							step1DetailTv.setTextColor(ContextCompat.getColor(ProgressQueryActivity.this, R.color.bg));
							step1Tv.setBackgroundResource(R.drawable.shape_circle_bg);
							step1Line.setBackgroundColor(0xffcccccc);

							step2DetailTv.setText("");
							step2DetailTv.setTextColor(0xffcccccc);
							step2Tv.setBackgroundResource(R.drawable.shape_circle_hui_bg);
							step2Line.setBackgroundColor(0xffcccccc);

							step3DetailTv.setText("");
							step3DetailTv.setTextColor(0xffcccccc);
							step3Tv.setBackgroundResource(R.drawable.shape_circle_hui_bg);
						} else if (borrow.jieKuanZhuangTai == 1) {

							step1DetailTv.setText("申请成功");
							step1DetailTv.setTextColor(ContextCompat.getColor(ProgressQueryActivity.this, R.color.bg));
							step1Tv.setBackgroundResource(R.drawable.shape_circle_bg);
							step1Line.setBackgroundResource(R.color.bg);

							step2DetailTv.setText("等待放款");
							step2DetailTv.setTextColor(ContextCompat.getColor(ProgressQueryActivity.this, R.color.bg));
							step2Tv.setBackgroundResource(R.drawable.shape_circle_bg);
							step2Line.setBackgroundColor(0xffcccccc);

							step3DetailTv.setText("");
							step3DetailTv.setTextColor(0xffcccccc);
							step3Tv.setBackgroundResource(R.drawable.shape_circle_hui_bg);
						} else if (borrow.jieKuanZhuangTai == 2 || borrow.jieKuanZhuangTai == 3) {
							step1DetailTv.setText("申请成功");
							step1DetailTv.setTextColor(ContextCompat.getColor(ProgressQueryActivity.this, R.color.bg));
							step1Tv.setBackgroundResource(R.drawable.shape_circle_bg);
							step1Line.setBackgroundResource(R.color.bg);

							step2DetailTv.setText("等待放款");
							step2DetailTv.setTextColor(ContextCompat.getColor(ProgressQueryActivity.this, R.color.bg));
							step2Tv.setBackgroundResource(R.drawable.shape_circle_bg);
							step2Line.setBackgroundResource(R.color.bg);

							step3DetailTv.setText("租赁成功");
							step3DetailTv.setTextColor(ContextCompat.getColor(ProgressQueryActivity.this, R.color.bg));
							step3Tv.setBackgroundResource(R.drawable.shape_circle_bg);
						}


					} else {

					}
				} else {

				}
			}

			@Override
			public void onFailed(int what, Response<BorrowResponse> response) {

			}
		}, true);
	}
}
