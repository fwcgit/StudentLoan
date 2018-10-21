package com.qudaozhang.white.crash;

import java.lang.Thread.UncaughtExceptionHandler;

import com.qudaozhang.white.MyApplication;

import android.os.Looper;
import android.widget.Toast;

public class CrashHandler implements UncaughtExceptionHandler {
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	
	public CrashHandler() {
		this.mDefaultHandler =  Thread.getDefaultUncaughtExceptionHandler();
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if(!handlerError(ex) && mDefaultHandler != null){
			mDefaultHandler.uncaughtException(thread, ex);
		}
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		android.os.Process.killProcess(android.os.Process.myPid()); 
        System.exit(10);
		
	}
	
	public boolean handlerError(Throwable tw){
		if(tw == null){
			return true;
		}
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Looper.prepare();  
				Toast.makeText(MyApplication.getInstance().getApplicationContext(), "很抱歉,程序异常退出！", Toast.LENGTH_SHORT).show();
				Looper.loop();    
			}
		}).start();
		
		return false;
	}

}
