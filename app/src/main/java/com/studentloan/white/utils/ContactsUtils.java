package com.studentloan.white.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.studentloan.white.MyApplication;
import com.studentloan.white.mode.data.ContactsInfo;
import com.studentloan.white.net.NoHttpRequest;
import com.studentloan.white.net.ServerInterface;
import com.studentloan.white.net.data.BaseResponse;
import com.studentloan.white.net.data.Contacts;

import java.util.ArrayList;
import java.util.List;

/***
 * 联系人
 * @author fu
 *
 */
public class ContactsUtils {
	private Context ctx;
	private Thread thread;
	private List<ContactsInfo> contactsInfos = new ArrayList<ContactsInfo>();
	private IContactsCallBack callBack;

	public ContactsUtils(Context ctx, IContactsCallBack callBack) {
		super();
		this.ctx = ctx;
		this.callBack = callBack;
	}
	
	public void readContacts(){
		
		contactsInfos.clear();
		
		if(thread != null){
			thread = null;
		}
		thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				if(ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
					Cursor cur = ctx.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
					if(null == cur){
						if(callBack != null){
							callBack.handComplate();
						}
						return;
					}
					int indexId = cur.getColumnIndex(ContactsContract.Contacts._ID);
					int indexName = cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
					if(cur != null && cur.moveToFirst()){
						do{
							String idStr = cur.getString(indexId);
							String nameStr = cur.getString(indexName);
							String phoneStr = "";
							int phoneNumber = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
							if(phoneNumber > 0){

								Cursor phoneCur = ctx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
										ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = " + idStr, null, null);
								if(null != phoneCur && phoneCur.moveToNext()){
									do{
										if(TextUtils.isEmpty(phoneStr)){
											phoneStr = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
										}else{
											phoneStr+="|";
											phoneStr += phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

										}
									}while(phoneCur.moveToNext());

									phoneCur.close();
								}


							}
							
							phoneStr = phoneStr.replaceAll(" ", "");
							ContactsInfo contactsInfo = new ContactsInfo();
							contactsInfo.name = nameStr;
							contactsInfo.phone = phoneStr;
							contactsInfos.add(contactsInfo);
							
							LogUtils.logDug("id="+idStr+"---->name="+nameStr+"----->"+"phone="+phoneStr);
						}while(cur.moveToNext());
						
						cur.close();
					}
				}
				
				thread = null;
				if(callBack != null){
					callBack.handComplate();
				}
			}
		});
		
		thread.start();

	}
	
	public void upContacts(){
//		contactsInfos.clear(); 
//		for (int i = 0; i < 10; i++) {
//			ContactsInfo contactsInfo = new ContactsInfo();
//			contactsInfo.name = "张"+i;
//			contactsInfo.phone = "1316711836"+i;
//			contactsInfos.add(contactsInfo);
//		}
		if(contactsInfos.isEmpty())return;
		List<Contacts> list = new ArrayList<Contacts>();
		for (ContactsInfo info: contactsInfos) {
			list.add(new Contacts(info.name,info.phone));
		}

		String bodyJson = new Gson().toJson(list);
		String urlFormat = String.format(ServerInterface.INSERT_CONTACTS,
				MyApplication.getInstance().userInfo.account.cellphone,MyApplication.getInstance().userInfo.token);

		NoHttpRequest.getInstance().requestPostBody(MyApplication.mainActivity, urlFormat.hashCode(),
				null, bodyJson,urlFormat, BaseResponse.class, null, false);
	}
	


	public void upTempContacts(){

	}
	
	public boolean isConactsEmp(){
		return contactsInfos.isEmpty();
	}
	
	public interface IContactsCallBack{
		public void handComplate();
	}
	
	
}
