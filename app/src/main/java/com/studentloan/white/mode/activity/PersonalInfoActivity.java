package com.studentloan.white.mode.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fuiou.pay.util.IdcardUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.megvii.idcardquality.IDCardQualityLicenseManager;
import com.megvii.licensemanager.Manager;
import com.megvii.livenessdetection.LivenessLicenseManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.studentloan.white.MyApplication;
import com.studentloan.white.MyContacts;
import com.studentloan.white.R;
import com.studentloan.white.Util;
import com.studentloan.white.interfaces.DialogCallback;
import com.studentloan.white.net.CallServer;
import com.studentloan.white.net.CustomDataRequest;
import com.studentloan.white.net.HttpListener;
import com.studentloan.white.net.NoHttpRequest;
import com.studentloan.white.net.ServerInterface;
import com.studentloan.white.net.data.BooleanResponse;
import com.studentloan.white.net.data.Identification;
import com.studentloan.white.net.data.IdentificationResponse;
import com.studentloan.white.net.data.IntegerResponse;
import com.studentloan.white.net.data.Shenghe;
import com.studentloan.white.net.data.ShengheResponse;
import com.studentloan.white.utils.ConUtil;
import com.studentloan.white.utils.DialogUtils;
import com.studentloan.white.utils.FileBase64;
import com.studentloan.white.utils.SdUtil;
import com.yolanda.nohttp.FileBinary;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@EActivity(R.layout.activity_personal_info_layout)
public class PersonalInfoActivity extends BaseActivity {
	private static int FACE_REQUEST_CODE = 0x001888;
	private static final int INTO_IDCARDSCAN_PAGE = 100;

	private Dialog dialog;

	@ViewById
	ImageView faceImg,IDCardFontImg,IDCardBackImg;
	@ViewById
	EditText nickNameEt ,idEt,cityEt,addressEt,eduBkgEt;
	@ViewById
	View eduBkgLayout,cityLayout;

	boolean isIDSdkAuth,isFaceSdkAuth,idCardExist,isIDCardValid;

	String delta;

	private Map<String, byte[]> mImage;

	private String mDelta;
	private String bestPath;
	private String image1Path;
	private String image2Path;
	private String envPath;


	@Override
	@AfterViews
	public void initViews() {
		super.initViews();

		getUserInfo();

		setTitleText("个人信息");

		showInfo();

		isIDCardValid = true;

	}



	@Override
	protected void onResume() {
		super.onResume();

		getShengheState();
	}

	@Override
	public void rightTv(TextView tv) {
		tv.setVisibility(View.VISIBLE);
		tv.setText("保存");

		getUserInfo();

		if(userInfo.identification != null){
			tv.setVisibility(View.GONE);
			addressEt.setEnabled(false);
			eduBkgLayout.setEnabled(false);
			cityLayout.setEnabled(false);
		}
	}

	@Override
	public void rightTvClick(TextView tv) {

		if(userInfo.shengYuShenFenRenZhengCiShu <= 0){
			showToast("身份证认证次数已达上限！");
			return;
		}


		submitBtn();
	}

	private void showInfo(){
		if(null != getUserInfo()){

			if(null != userInfo.account){
				nickNameEt.setText(userInfo.account.name);
				idEt.setText(userInfo.account.idCard);
				if(null != userInfo.identification){
					eduBkgEt.setText(userInfo.identification.eduBkg);
					cityEt.setText(userInfo.identification.city);
					addressEt.setText(userInfo.identification.address);
				}
			}

			if(null != userInfo.idImageUrl){
				if(!TextUtils.isEmpty(userInfo.idImageUrl.frontFaceUrl)){
					ImageLoader.getInstance().displayImage(MyContacts.BASE_URL+userInfo.idImageUrl.frontFaceUrl,faceImg);
				}

				if(!TextUtils.isEmpty(userInfo.idImageUrl.frontIdCardUrl)){
					ImageLoader.getInstance().displayImage(MyContacts.BASE_URL+userInfo.idImageUrl.frontIdCardUrl,IDCardFontImg);
				}

				if(!TextUtils.isEmpty(userInfo.idImageUrl.backIdCardUrl)){
					ImageLoader.getInstance().displayImage(MyContacts.BASE_URL+userInfo.idImageUrl.backIdCardUrl,IDCardBackImg);
				}

			}
		}
	}

	@Click
	public void faceImg(){

		if(userInfo.shengYuShenFenRenZhengCiShu <= 0 ){
			showToast("你的个人信息已超最大认证次数.无法使用");
			return;
		}

		if(idCardExist){
			showToast("身份证已经存在！");
			return;
		}

		if(TextUtils.isEmpty(idEt.getText().toString())){
			showToast("请先认证身份证！");
			return;
		}

		if(isFaceSdkAuth){
			startActivityForResult(new Intent(PersonalInfoActivity.this,LivenessActivity.class),FACE_REQUEST_CODE);
		}else{
			authFaceSdk(2);
		}

	}

	@Click
	public void eduBkgLayout(){

		DialogUtils.getInstance().showSelectList(this,
				new String[]{"博士","硕士","本科","大专","中专","高中","初中","初中以下","其它"}, new DialogCallback() {
			@Override
			public void typeStr(String type) {
				eduBkgEt.setText(type);
			}
		});
	}

	@Click
	public void cityLayout(){
		com.studentloan.white.mode.activity.SelectCityActivity_.intent(this).startForResult(SelectCityActivity.REQUEST_CODE);
	}


	public void submitBtn(){
		String name = nickNameEt.getText().toString();
		String idCard = idEt.getText().toString();
		String city = cityEt.getText().toString();
		String address = addressEt.getText().toString();
		String edu = eduBkgEt.getText().toString();

		if(TextUtils.isEmpty(name)){
			showToast("姓名不能为空");
			return;
		}

		if(TextUtils.isEmpty(idCard)){
			showToast("身份证号不能为空");
			return;
		}

		int age = IdcardUtils.getAgeByIdCard(idCard);
		if(age < 18 || age > 36 ){
			showToast("年龄不符合!");
			return;
		}

		if(!isIDCardValid){
			showToast("身份证错误!");
			return;
		}

		if(TextUtils.isEmpty(edu)){
			showToast("学历不能为空");
			return;
		}

		if(TextUtils.isEmpty(city)){
			showToast("所在城市不能为空");
			return;
		}

		if(TextUtils.isEmpty(address)){
			showToast("详细地址不能为空");
			return;
		}

		File faceFile = new File(bestPath);

		File idFontFile = SdUtil.getFile("idcard_font.png",false);
		File idBackFile = SdUtil.getFile("idcard_back.png",false);

		if(!faceFile.exists()){
			showToast("请进行人脸识别");
			return;
		}

		if(!idFontFile.exists()){
			showToast("请进行身份证正面识别");
			return;
		}

		if(!idBackFile.exists()){
			showToast("请进行身份证背面识别");
			return;
		}

		//比对
		imageVerify();



	}

	private void submitData(){

		String name = nickNameEt.getText().toString();
		String idCard = idEt.getText().toString();
		String city = cityEt.getText().toString();
		String address = addressEt.getText().toString();
		String edu = eduBkgEt.getText().toString();


		File faceFile = new File(bestPath);
		File envFaceFile = new File(envPath);
		File action1FaceFile = new File(image1Path);
		File action2FaceFile = new File(image2Path);

		File idFontFile = SdUtil.getFile("idcard_font.png",false);
		File idBackFile = SdUtil.getFile("idcard_back.png",false);


		final Identification id = new Identification();
		id.frontIdCard = FileBase64.FileToBase64(idFontFile);
		id.backIdCard = FileBase64.FileToBase64(idBackFile);
		id.frontFace = FileBase64.FileToBase64(faceFile);
		id.otherImage1 = FileBase64.FileToBase64(envFaceFile);
		id.otherImage2 = FileBase64.FileToBase64(action1FaceFile);
		id.otherImage3 = FileBase64.FileToBase64(action2FaceFile);
		id.pictureFormat ="png";
		id.idCard = idCard;
		id.name = name;
		id.city = city;
		id.address = address;
		id.eduBkg = edu;

		String json = new Gson().toJson(id);

		try {
			FileOutputStream fos = new FileOutputStream(SdUtil.getFile("ident.dat",false));
			fos.write(json.getBytes());
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


		String urlFormat = String.format(ServerInterface.MODIFY_USER_INFO,app.userInfo.account.cellphone,userInfo.token);
		requestPostBody(urlFormat.hashCode(), null, json, urlFormat, IdentificationResponse.class, new HttpListener<IdentificationResponse>() {
			@Override
			public void onSucceed(int what, Response<IdentificationResponse> response) {
				if(response.isSucceed() && response.get() != null){
					if(response.get().errorCode.equals("0")){

						userInfo.identification = id;

						userInfo.account.name = id.name;
						userInfo.account.idCard = id.idCard;

						userInfo.idImageUrl = response.get().result;

						showToast("提交成功");

						finish();

					}else{

						showToast(response.get().errorMsg);
					}
				}
			}

			@Override
			public void onFailed(int what, Response<IdentificationResponse> response) {

			}


		},true);
	}

	@Click
	public void IDCardFontImg(){
		if(userInfo.shengYuShenFenRenZhengCiShu <= 0){
			showToast("身份证认证次数已达上限！");
			return;
		}
		if(isIDSdkAuth){
			Intent intent = new Intent(this, IDCardScanActivity.class);
			intent.putExtra("side", 0);
			intent.putExtra("isvertical", false);
			startActivityForResult(intent, INTO_IDCARDSCAN_PAGE);
		}else{
			authIDSdk(0);
		}
	}

	@Click
	public void IDCardBackImg(){
		if(isIDSdkAuth){
			Intent intent = new Intent(this, IDCardScanActivity.class);
			intent.putExtra("side", 1);
			intent.putExtra("isvertical", false);
			startActivityForResult(intent, INTO_IDCARDSCAN_PAGE);
		}else{
			authIDSdk(1);
		}
	}
	public void authIDSdk(final int type){

		showLoadingDialog();

		new Thread(new Runnable() {
			@Override
			public void run() {
				Manager manager = new Manager(PersonalInfoActivity.this);
				IDCardQualityLicenseManager idCardLicenseManager = new IDCardQualityLicenseManager(
						PersonalInfoActivity.this);
				manager.registerLicenseManager(idCardLicenseManager);
				manager.takeLicenseFromNetwork(ConUtil.getUUIDString(PersonalInfoActivity.this));

				if (idCardLicenseManager.checkCachedLicense() > 0)
					isIDSdkAuth = true;
					cancelDialog();
					if(type == 0){
						Intent intent = new Intent(PersonalInfoActivity.this, IDCardScanActivity.class);
						intent.putExtra("side", type);
						intent.putExtra("isvertical", false);
						startActivityForResult(intent, INTO_IDCARDSCAN_PAGE);
					}else if(type == 1){
						Intent intent = new Intent(PersonalInfoActivity.this, IDCardScanActivity.class);
						intent.putExtra("side", type);
						intent.putExtra("isvertical", false);
						startActivityForResult(intent, INTO_IDCARDSCAN_PAGE);
					}
				else
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(PersonalInfoActivity.this,"联网授权失败！请检查网络或找服务商",Toast.LENGTH_SHORT).show();
							cancelDialog();
						}
					});

				isIDSdkAuth = false;
			}
		}).start();
	}
	public void authFaceSdk(final int type){
		showLoadingDialog();
		new Thread(new Runnable() {
			@Override
			public void run() {

				Manager manager = new Manager(PersonalInfoActivity.this);
				LivenessLicenseManager licenseManager = new LivenessLicenseManager(PersonalInfoActivity.this);
				manager.registerLicenseManager(licenseManager);
				manager.takeLicenseFromNetwork(ConUtil.getUUIDString(PersonalInfoActivity.this));

				if(manager.checkCachedLicense().size() > 0){
					isFaceSdkAuth = true;
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if(type == 2){
								startActivityForResult(new Intent(PersonalInfoActivity.this,LivenessActivity.class),FACE_REQUEST_CODE);
							}
							cancelDialog();
						}
					});
				}else{
					isFaceSdkAuth = false;
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(PersonalInfoActivity.this,"联网授权失败！请检查网络或找服务商",Toast.LENGTH_SHORT).show();
							cancelDialog();
						}
					});
				}

			}
		}).start();
	}



	private void showLoadingDialog(){
		dialog = new Dialog(this, R.style.custom_dialog);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				return true;
			}
		});

		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.width = MyApplication.getInstance().widthPixels;
		lp.height = MyApplication.getInstance().heightPixels;

		dialog.setContentView(R.layout.dialog_loading_layout);
		((TextView)dialog.findViewById(R.id.loadingTv)).setText("正在授权,请稍后...");
		dialog.show();
	}


	private void showLoadingDialog(String hint){
		dialog = new Dialog(this, R.style.custom_dialog);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				return true;
			}
		});

		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.width = MyApplication.getInstance().widthPixels;
		lp.height = MyApplication.getInstance().heightPixels;

		dialog.setContentView(R.layout.dialog_loading_layout);
		((TextView)dialog.findViewById(R.id.loadingTv)).setText(hint);
		dialog.show();
	}

	private void cancelDialog(){
		if(null != dialog && dialog.isShowing()){
			dialog.dismiss();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK){
			if(requestCode == FACE_REQUEST_CODE){

				//LivenessData livenessData = (LivenessData) data.getSerializableExtra("result");

				loadData(data);

			}else if(requestCode == INTO_IDCARDSCAN_PAGE){
				int mIDCardSide = data.getIntExtra("side", 0);

				if(mIDCardSide == 0){
					byte[] portraitImgData = data.getByteArrayExtra(
							"portraitImg");
					Bitmap img = BitmapFactory.decodeByteArray(portraitImgData, 0,
							portraitImgData.length);
					IDCardFontImg.setImageBitmap(img);

					saveIdCardFontToFile(img);

					OCRImg();

				}else{
					byte[] idcardImgData = data.getByteArrayExtra("idcardImg");
					Bitmap idcardBmp = BitmapFactory.decodeByteArray(idcardImgData, 0,
							idcardImgData.length);
					IDCardBackImg.setImageBitmap(idcardBmp);

					saveIdCardBackToFile(idcardBmp);
				}

			}else if(requestCode == SelectCityActivity.REQUEST_CODE){
				cityEt.setText(data.getStringExtra("name"));
				cityEt.setTag(data.getStringExtra("code"));
			}

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	public void OCRImg(){
		File file = SdUtil.getFile("idcard_font.png",false);
		if(!file.exists()) return;

		Map<String, String> params = new HashMap<String, String>();
		params.put("api_key", "mJovDXnYtCDFc0AX-HRwzv366IGC3a8g");
		params.put("api_secret", "EV6vuhRapp4OiGLzGVX7qDpf-PIlKl96");


		Request<String> request = new CustomDataRequest<String>("https://api.faceid.com/faceid/v1/ocridcard", RequestMethod.POST,null);
		request.add(params);
		request.add("image", new FileBinary(file));

		CallServer.getRequestInstance().add(this, 0x0098, request, new HttpListener<String>() {
			@Override
			public void onSucceed(int what, Response<String> response) {
					if(response.isSucceed()){
						if(response.get() != null){
							try {
								JSONObject jsonObject = new JSONObject(response.get());

								if(jsonObject.has("id_card_number")){
									String idNumber = jsonObject.getString("id_card_number");
									int age = IdcardUtils.getAgeByIdCard(idNumber);
									if(age < 18 || age > 36 ){
										isIDCardValid = false;
										showToast("年龄不符合!");
										return;
									}
									isIDCardExist(idNumber);
									idEt.setText(idNumber);
								}

								isIDCardValid = true;

								if(jsonObject.has("name")){
									String name = jsonObject.getString("name");
									nickNameEt.setText(name);
								}

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
			}

			@Override
			public void onFailed(int what, Response<String> response) {

			}


		}, MyApplication.mainActivity, true);

	}

	private void saveIdCardFontToFile(Bitmap bitmap){
		File outfile = SdUtil.getFile(new Date().getTime()+".png",false);
		File file = SdUtil.getFile("idcard_font.png",false);
		if(file.exists()) file.delete();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(outfile);
			bitmap.compress(Bitmap.CompressFormat.PNG,100,fos);
			fos.flush();
			fos.close();

			outfile.renameTo(file);

			OCRImg();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			if(null != fos) try {
				fos.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
			if(null != fos) try {
				fos.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}finally {
			if(null != fos) try {
				fos.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void saveIdCardBackToFile(Bitmap bitmap){
		File outfile = SdUtil.getFile(new Date().getTime()+".png",false);
		File file = SdUtil.getFile("idcard_back.png",false);
		if(file.exists()) file.delete();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(outfile);
			bitmap.compress(Bitmap.CompressFormat.PNG,100,fos);
			fos.flush();
			fos.close();

			outfile.renameTo(file);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			if(null != fos) try {
				fos.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
			if(null != fos) try {
				fos.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}finally {
			if(null != fos) try {
				fos.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void isIDCardExist(String id){
		String formatUrl = String.format(ServerInterface.IDCARD_EXIST,id);
		requestGet(formatUrl.hashCode(), formatUrl, BooleanResponse.class, new HttpListener<BooleanResponse>() {
			@Override
			public void onSucceed(int what, Response<BooleanResponse> response) {
					if(response.isSucceed() && response.get() != null){
						idCardExist = response.get().result;
					}
			}

			@Override
			public void onFailed(int what, Response<BooleanResponse> response) {

			}
		},true);
	}


	/**
	 * 调用Verify2.0方法
	 * 默认字段是有源比对，使用无源比对参考官网后台文档调整
	 */
	public void imageVerify() {
		String name = nickNameEt.getText().toString();
		String idCard = idEt.getText().toString();

		RequestParams requestParams = new RequestParams();
		requestParams.put("idcard_name", name);
		requestParams.put("idcard_number", idCard);
		requestParams.put("delta", mDelta);
		requestParams.put("api_key", "mJovDXnYtCDFc0AX-HRwzv366IGC3a8g");
		requestParams.put("api_secret", "EV6vuhRapp4OiGLzGVX7qDpf-PIlKl96");
		requestParams.put("comparison_type", 1 + "");
		requestParams.put("face_image_type", "meglive");
//            for (Map.Entry<String, byte[]> entry : mImage.entrySet()) {
//                requestParams.put(entry.getKey(),
//                        new ByteArrayInputStream(entry.getValue()));
//            }
		try {
			requestParams.put("image_best", new File(bestPath));
			requestParams.put("image_env", new File(envPath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		showLoadingDialog("人脸身份核实中...");

		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		String url = "https://api.megvii.com/faceid/v2/verify";
		asyncHttpClient.post(url, requestParams,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int i, Header[] headers, byte[] bytes) {
						String success = new String(bytes);
						Log.e("TAG", "成功信息：" + success);

						cancelDialog();

						JsonObject jo = (JsonObject) new JsonParser().parse(success);
						if(jo.has("result_faceid")){
							JsonObject result_faceid = (JsonObject) new JsonParser().parse(jo.get("result_faceid").toString());
							float confidence =  0f;
							float val = 0f;
							if(result_faceid.has("confidence")){
								confidence = result_faceid.get("confidence").getAsFloat();
							}

							if(result_faceid.has("thresholds")){
								JsonObject thresholds = (JsonObject) new JsonParser().parse(result_faceid.get("thresholds").toString());
								if(thresholds.has("1e-4")){
									val = thresholds.get("1e-4").getAsFloat();
								}
							}

							if(confidence < val){
								showToast("人脸身份识别失败，请确认人脸识别和身份证是同一个人！");
								notifacionShengfenCount();
							}else{
								notifacionShengfenCount();
								submitData();
							}
						}
					}

					@Override
					public void onFailure(int i, Header[] headers,
										  byte[] bytes, Throwable throwable) {

						String error = new String(bytes);
						Log.e("TAG", "失败信息：" + error);

						showToast("人脸身份识别失败");
						// 请求失败

						cancelDialog();
					}
				});
	}

	private void loadData(Intent intent) {
		LivenessData livenessData = (LivenessData) intent.getSerializableExtra("result");
		boolean isSuccess = livenessData.getResID() == R.string.verify_success;
		if (isSuccess) {

			mDelta = livenessData.getDelta();
			mImage = livenessData.getImages();

			byte[] image_best = mImage.get("image_best");
			byte[] image_env = mImage.get("image_env");
			byte[] image_action1 = mImage.get("image_action1");//
			byte[] image_action2 = mImage.get("image_action2");
//          N张动作图根据需求自取，对应actionN
//          byte[] image_action1 = images.get("image_action1);
			bestPath = Util.saveJPGFile(this, image_best, "image_best");      //保存图片
			envPath = Util.saveJPGFile(this, image_env, "image_env");
			image1Path = Util.saveJPGFile(this, image_action1, "image_action1");
			image2Path = Util.saveJPGFile(this, image_action2, "image_action2");

			faceImg.setImageBitmap(BitmapFactory.decodeByteArray(image_best,0,image_best.length));

		} else {

			int resID = livenessData.getResID();
			showToast(resID);

		}


	}

	public void notifacionShengfenCount(){
		String formatUrl = String.format(ServerInterface.ADD_ID_TIMES,
				MyApplication.getInstance().userInfo.account.cellphone,
				MyApplication.getInstance().userInfo.token);
		requestPost(formatUrl.hashCode(), null, formatUrl, IntegerResponse.class, new HttpListener<IntegerResponse>() {
			@Override
			public void onSucceed(int what, Response<IntegerResponse> response) {
				if(response.isSucceed() && response.get() != null){
					//userInfo.shengYuShenFenRenZhengCiShu = response.get().result;
					MyApplication.mainActivity.getShengheState();

				}
			}

			@Override
			public void onFailed(int what, Response<IntegerResponse> response) {

			}
		},true);
	}


	public void getShengheState(){
		if(MyApplication.getInstance().userInfo == null) return;

		String formartUrl = String.format(ServerInterface.PASS_VERIFICATION,MyApplication.getInstance().userInfo.account.cellphone);

		NoHttpRequest.getInstance().requestGet(this, formartUrl.hashCode(), formartUrl, ShengheResponse.class, new HttpListener<ShengheResponse>() {
			@Override
			public void onSucceed(int what, Response<ShengheResponse> response) {
				if(response.isSucceed() && response.get() != null){

					if(response.get().result != null){
						Shenghe sh = response.get().result;

						MyApplication.getInstance().userInfo.verificationResult = sh.verificationResult;
						MyApplication.getInstance().userInfo.verificationDateTime = sh.verificationDateTime;
						MyApplication.getInstance().userInfo.submit = sh.submit;
						MyApplication.getInstance().userInfo.submitTime = sh.submitTime;
						MyApplication.getInstance().userInfo.submitCiShu = sh.submitCiShu;
						MyApplication.getInstance().userInfo.blackList = sh.blackList;
						MyApplication.getInstance().userInfo.frozen = sh.frozen;
						MyApplication.getInstance().userInfo.yunYingShangVeriTime = sh.yunYingShangVeriTime;
						MyApplication.getInstance().userInfo.shengYuShenFenRenZhengCiShu = sh.shengYuShenFenRenZhengCiShu;
						MyApplication.getInstance().userInfo.shengYuWangYinRenZhengCiShu = sh.shengYuWangYinRenZhengCiShu;

					}

				}
			}

			@Override
			public void onFailed(int what, Response<ShengheResponse> response) {

			}
		},true);
	}

}
