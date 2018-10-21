package com.qudaozhang.white.mode.activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qudaozhang.white.MyContacts;
import com.qudaozhang.white.R;
import com.qudaozhang.white.interfaces.DialogCallback;
import com.qudaozhang.white.mode.view.RoundImageView;
import com.qudaozhang.white.net.HttpListener;
import com.qudaozhang.white.net.ServerInterface;
import com.qudaozhang.white.net.data.BooleanResponse;
import com.qudaozhang.white.net.data.HeadImage;
import com.qudaozhang.white.net.data.StringResponse;
import com.qudaozhang.white.utils.DialogUtils;
import com.qudaozhang.white.utils.FileBase64;
import com.qudaozhang.white.utils.ImageUtils;
import com.qudaozhang.white.utils.SdUtil;
import com.yolanda.nohttp.rest.Response;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.Date;
import java.util.List;

@EActivity(R.layout.activity_account_info_layout)
public class AccountInfoActivity extends BaseActivity {
	private static final int RESULT_CAMARE_IMAGE = 0x001;
	private static final int RESULT_PHOTO_IMAGE = 0x002;
	private static final String PATH_DOCUMENT = "document";

	@ViewById
	TextView userNameTv,accountTv;

	@ViewById
	RoundImageView headImg;

	String imgTempName;

	@Override
	@AfterViews
	public void initViews() {
		super.initViews();
		setTitleText("账户信息");
	}

	@Override
	protected void onResume() {
		super.onResume();

		if(null != getUserInfo()){
			userNameTv.setText(userInfo.account.name);
			accountTv.setText(userInfo.account.cellphone);

			if(!TextUtils.isEmpty(userInfo.headImageUrl)){
				ImageLoader.getInstance().displayImage(MyContacts.BASE_URL+userInfo.headImageUrl, headImg);
			}
		}

	}

	@Click
	public void exitBtn() {
        exitData();
	}

	@Click
	public void changePwdLayout(){
		com.qudaozhang.white.mode.activity.ChangePasswordActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
	}

	@Click
	public void headImg() {
		DialogUtils.getInstance().showSelectPicture(this, new DialogCallback() {

			@Override
			public void camareClick() {

				imgTempName = new Date().getTime()+".png";

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 调用android自带的照相机
				intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);

				File file = SdUtil.getFilePath(imgTempName);
				int currentapiVersion = android.os.Build.VERSION.SDK_INT;
				if (currentapiVersion<24){
					Uri uri = Uri.fromFile(file);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
					startActivityForResult(intent, RESULT_CAMARE_IMAGE);
				}else {
					ContentValues contentValues = new ContentValues(1);
					contentValues.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
					Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
					startActivityForResult(intent, RESULT_CAMARE_IMAGE);
				}

			}

			@Override
			public void picClick() {

				Intent in = new Intent();
				/* 开启Pictures画面Type设定为image */
				in.setType("image/*");
				/* 使用Intent.ACTION_GET_CONTENT这个Action */
				in.setAction(Intent.ACTION_GET_CONTENT);
				/* 取得相片后返回本画面 */
				startActivityForResult(in, RESULT_PHOTO_IMAGE);
			}

		},-1);
	}

	@SuppressWarnings("unused")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case RESULT_CAMARE_IMAGE:
				File file = SdUtil.getFilePath(imgTempName);
				File oldFile = SdUtil.getFilePath("headCopy.png");
				if(file.exists()){

					if(oldFile.exists()){
						oldFile.delete();
					}

					Bitmap headBmp = ImageUtils.getimage(file.getAbsolutePath());
					headImg.setImageBitmap(headBmp);

					ImageUtils.SavePicInLocal(headBmp,"headCopy.png");

					file.delete();

					modifyHead();
				}

				break;
			case RESULT_PHOTO_IMAGE:
				if (data != null) {
					Uri uri = data.getData();
					ContentResolver cr = getContentResolver();
					String degree = "";
					if (isDownloadsDocument(uri)) {
						String wholeID = getDocumentId(data.getData());
						String id = wholeID.split(":")[1];
						String[] column = { MediaStore.Images.Media.DATA,
								MediaStore.Images.Media.ORIENTATION };
						String sel = MediaStore.Images.Media._ID + "=?";
						Cursor cursor = getContentResolver().query(
								MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel,
								new String[] { id }, null);
						int columnIndex1 = cursor.getColumnIndex(column[1]);
						if (cursor.moveToFirst()) {
							degree = cursor.getString(columnIndex1);
						}
					}

					try {

						Bitmap bitmap = ImageUtils.compressImageFromFile(cr, uri);

						if(null != bitmap){
							File oldFile2 = SdUtil.getFile(imgTempName, false);

							if(oldFile2.exists()){
								oldFile2.delete();
							}

							ImageUtils.SavePicInLocal(ImageUtils.comp(bitmap),"headCopy.png");

							modifyHead();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public static boolean isDownloadsDocument(Uri uri) {
		if(uri.getAuthority() != null){
			Log.e("coument", uri.getAuthority());
		}
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	public static String getDocumentId(Uri documentUri) {
		final List<String> paths = documentUri.getPathSegments();
		if (paths.size() < 2) {
			throw new IllegalArgumentException("Not a document: " + documentUri);
		}
		if (!PATH_DOCUMENT.equals(paths.get(0))) {
			throw new IllegalArgumentException("Not a document: " + documentUri);
		}
		return paths.get(1);
	}


	private void modifyHead(){


		File headFile = SdUtil.getFile("headCopy.png",false);
		String urlFormat = String.format(ServerInterface.MODIFY_HEAD,userInfo.account.cellphone,userInfo.token);
		HeadImage headImage = new HeadImage();
		headImage.image = FileBase64.FileToBase64(headFile);
		headImage.pictureFormat = "png";

		String jsonBody = new Gson().toJson(headImage);

		requestPostBody(urlFormat.hashCode(), null, jsonBody,urlFormat, StringResponse.class, new HttpListener<StringResponse>() {

			@Override
			public void onSucceed(int what, Response<StringResponse> response) {
				if(response.isSucceed() && response.get() != null){
					if(response.get().errorCode.equals("0")){
						userInfo.headImageUrl = response.get().result;
					}
				}
			}

			@Override
			public void onFailed(int what, Response<StringResponse> response) {

			}


		}, true);


	}

	private void exitData(){
        String formatUrl = String.format(ServerInterface.EXIT,userInfo.account.cellphone,userInfo.token);

        requestPost(formatUrl.hashCode(),null,formatUrl, BooleanResponse.class,new HttpListener<BooleanResponse>(){
            @Override
            public void onSucceed(int what, Response<BooleanResponse> response) {
                if(response.isSucceed() && response.get() != null){
                    if(response.get().result){

						app.reLogin();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<BooleanResponse> response) {

            }
        },true);

	}

}
