package com.studentloan.white.mode.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.studentloan.white.R;
import com.studentloan.white.interfaces.DialogCallback;
import com.studentloan.white.net.HttpListener;
import com.studentloan.white.net.ServerInterface;
import com.studentloan.white.net.data.BooleanResponse;
import com.studentloan.white.net.data.Contact;
import com.studentloan.white.net.data.EmergencyContactInfo;
import com.studentloan.white.net.data.EmergencyInfo;
import com.studentloan.white.utils.ContactsUtils;
import com.studentloan.white.utils.DialogUtils;
import com.yolanda.nohttp.rest.Response;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/***
 * 添加联系人
 * 
 * @author fu
 *
 */
@TargetApi(23)
@EActivity(R.layout.activity_add_contacts_layout)
public class AddContactsActivity extends BaseActivity implements ContactsUtils.IContactsCallBack {
	public static final int PERMISSION_CONTACTS_REQUEST_CODE = 0x221;
	
	@ViewById
	TextView relationTv,lsRelationTv,hyRelationTv;
	@ViewById
	EditText sibNameEt,sibPhoneEt,lsSibNameEt,lsSibPhoneEt,hyName1Et,hyPhone1Et;

	@ViewById
	Button submitBtn;
	
	private final int  CONTACTS_REQUEST = 1;
	
	boolean isChange = true;
	
	int seletcType = 0;
	
	ContactsUtils contactsUtils;
	@Override
	@AfterViews
	public void initViews() {
		super.initViews();

		getUserInfo();

		setTitleText("联系人");
		
		contactsUtils = new ContactsUtils(this, this);

		if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
			requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_CONTACTS_REQUEST_CODE);
		}else{

		}

		if(null != userInfo.emergencyContact){
			//直系亲属
			sibNameEt.setText(userInfo.emergencyContact.directRelative.name);
			sibPhoneEt.setText(userInfo.emergencyContact.directRelative.cellphone);
			relationTv.setText(userInfo.emergencyContact.directRelative.relationship);
			//老师
			lsSibNameEt.setText(userInfo.emergencyContact.teacher.name);
			lsSibPhoneEt.setText(userInfo.emergencyContact.teacher.cellphone);
			lsRelationTv.setText(userInfo.emergencyContact.teacher.relationship);
			//好友
			hyName1Et.setText(userInfo.emergencyContact.friend.name);
			hyPhone1Et.setText(userInfo.emergencyContact.friend.cellphone);
			hyRelationTv.setText(userInfo.emergencyContact.friend.relationship);
		}

		contactsUtils.readContacts();

	}

	@Override
	public void rightTv(TextView tv) {
		tv.setVisibility(View.VISIBLE);
		tv.setText("保存");
	}

	@Override
	public void rightTvClick(TextView tv) {
		submit();
	}


	@Click
	public void selectImg1(){
		seletcType = 1;
		Intent intent = new Intent(Intent.ACTION_PICK,android.provider.ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(intent, 1);
	}
	@Click
	public void selectImg2(){
		seletcType = 2;
		Intent intent = new Intent(Intent.ACTION_PICK,android.provider.ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(intent, 1);
	}
	@Click
	public void selectImg3(){
		seletcType = 3;
		Intent intent = new Intent(Intent.ACTION_PICK,android.provider.ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(intent, 1);
	}
	
	@Click
	public void relationLayout(){
		if(!isChange) return;
		DialogUtils.getInstance().showSelectList(this, new String[]{"父亲","母亲"}, new DialogCallback() {
			@Override
			public void typeStr(String type) {
				relationTv.setText(type);
			}
		});
	}

	@Click
	public void lsRelationLayout(){
		if(!isChange) return;
		DialogUtils.getInstance().showSelectList(this, new String[]{"媳妇","领导","朋友","亲戚"}, new DialogCallback() {
			@Override
			public void typeStr(String type) {
				lsRelationTv.setText(type);
			}
		});
	}

	@Click
	public void hyRelationLayout(){
		if(!isChange) return;
		DialogUtils.getInstance().showSelectList(this, new String[]{"朋友","同事","同学","媳妇"}, new DialogCallback() {
			@Override
			public void typeStr(String type) {
				hyRelationTv.setText(type);
			}
		});
	}


	public void submit(){
		if(contactsUtils.isConactsEmp()){
			showToast("访问通讯录授权拒绝,请到授权管理打开，再次授权！");
			return;
		}
		String sibName = sibNameEt.getText().toString();
		String sibPhone = sibPhoneEt.getText().toString();
		String sibRelation = relationTv.getText().toString();
		String lsName = lsSibNameEt.getText().toString();
		String lsPhone = lsSibPhoneEt.getText().toString();
		String lsRelation = lsRelationTv.getText().toString();
		String hyRelation = hyRelationTv.getText().toString();
		String hyName = hyName1Et.getText().toString();
		String hyPhone = hyPhone1Et.getText().toString();


		if(sibRelation.isEmpty()){
			showToast("请选择直系关系!");
			return;
		}

		if(TextUtils.isEmpty(sibName)){
			showToast("直系亲属姓名不能为空!");
			return;
		}

		if(TextUtils.isEmpty(sibPhone)){
			showToast("直系亲属手机号不能为空!");
			return;
		}

		if(lsRelation.isEmpty()){
			showToast("请选择老师关系!");
			return;
		}

		if(TextUtils.isEmpty(lsName)){
			showToast("老师姓名不能为空!");
			return;
		}

		if(TextUtils.isEmpty(lsPhone)){
			showToast("老师手机号不能为空!");
			return;
		}

		if(hyRelation.isEmpty()){
			showToast("请选择好友关系!");
			return;
		}

		if(TextUtils.isEmpty(hyName)){
			showToast("好友姓名不能为空!");
			return;
		}

		if(TextUtils.isEmpty(hyPhone)){
			showToast("好友手机号不能为空!");
			return;
		}


		if(!app.isTelPhone(sibPhone)){
			showToast("直系亲属手机号格式错误!");
			return;
		}

		if(!app.isTelPhone(lsPhone)){
			showToast("老师手机号格式错误!");
			return;
		}

		if(!app.isTelPhone(hyPhone)){
			showToast("好友手机号格式错误!");
			return;
		}


		final EmergencyInfo ei = new EmergencyInfo();
		ei.directRelative = new Contact(sibName,sibPhone,sibRelation);
		ei.teacher = new Contact(lsName,lsPhone,lsRelation);
		ei.friend = new Contact(hyName,hyPhone,hyRelation);

		String urlFormat = String.format(ServerInterface.MODIFY_CONTACTS,userInfo.account.cellphone,userInfo.token);

		String bodyJson = new Gson().toJson(ei);

		requestPostBody(urlFormat.hashCode(), null, bodyJson,urlFormat, BooleanResponse.class, new HttpListener<BooleanResponse>() {

			@Override
			public void onSucceed(int what, Response<BooleanResponse> response) {
				if(response.isSucceed() && response.get() != null){

					if(response.get().result){
						if( null == userInfo.emergencyContact) userInfo.emergencyContact = new EmergencyContactInfo();

						app.userInfo.emergencyContact.directRelative = ei.directRelative;
						app.userInfo.emergencyContact.teacher = ei.teacher;
						app.userInfo.emergencyContact.friend = ei.friend;

						showToast("添加成功！");
						finish();
					}else{
						showToast(response.get().errorMsg);
					}
				}
			}

			@Override
			public void onFailed(int what, Response<BooleanResponse> response) {

			}

		}, true);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			if(requestCode == CONTACTS_REQUEST){
				Uri uri = data.getData();
		        // 得到ContentResolver对象
		        ContentResolver cr = getContentResolver();
		        // 取得电话本中开始一项的光标
		        Cursor cursor = cr.query(uri, null, null, null, null);
				if(cursor == null){
					return;
				}
		        // 向下移动光标
		        while (cursor.moveToNext()) {
		            // 取得联系人名字
		            int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
		            int phoneIndex = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
		            
		            int hasPhone = cursor.getInt(phoneIndex);
		            String name = cursor.getString(nameFieldColumnIndex);
		            
		            
		            if(hasPhone > 0){
		                String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));           
			            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);

			            while(phone.moveToNext())

			            {

			                String Number = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			                Number = Number.replaceAll(" ", "");
			                Number = Number.replaceAll("-", "");
			                
			                if(seletcType == 1){
			                	sibNameEt.setText(name);
			                	sibPhoneEt.setText(Number);
			                }else if(seletcType == 2){
			                	lsSibNameEt.setText(name);
			                	lsSibPhoneEt.setText(Number);
			                }else if(seletcType == 3){
			                	hyName1Et.setText(name);
			                	hyPhone1Et.setText(Number);
			                }
			                break;

			            }
		            }
		        }
		        

			}
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if(requestCode == PERMISSION_CONTACTS_REQUEST_CODE){
			if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED){
				showToast("访问通讯录授权拒绝,请到授权管理打开，再次授权！");
				finish();
			}else{
				contactsUtils.readContacts();
			}
		}
	}

	@Override
	public void handComplate() {
		contactsUtils.upContacts();
	}
}
