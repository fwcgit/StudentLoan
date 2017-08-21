package com.studentloan.white.receive;

import com.studentloan.white.utils.LogUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class SMSBroadcast extends BroadcastReceiver {
	
	public interface SmsReceive{
		public void smscode(String code);
	}
	
	private SmsReceive smsReceive;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
			LogUtils.logDug("android.provider.Telephony.SMS_RECEIVED");
		}else if(intent.getAction().equals("android.provider.Telephony.SMS_DELIVER")){
			LogUtils.logDug("android.provider.Telephony.SMS_DELIVER");
		}
	}

	public SmsReceive getSmsReceive() {
		return smsReceive;
	}

	public void setSmsReceive(SmsReceive smsReceive) {
		this.smsReceive = smsReceive;
	}

	@Override
	public IBinder peekService(Context myContext, Intent service) {
			LogUtils.logDug("peekService");
		return super.peekService(myContext, service);
	}

	public SMSBroadcast() {
		super();
		LogUtils.logDug("SMSBroadcast");
	}
	
	
	
}
