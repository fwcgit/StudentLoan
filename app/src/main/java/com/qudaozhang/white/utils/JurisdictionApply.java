package com.qudaozhang.white.utils;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 权限动态申请
 * Created by fu on 2017/5/19.
 */

public class JurisdictionApply{
    private static JurisdictionApply jurisdictionApply;

    private LinkedList<Jurisdiciton> queue = new LinkedList<Jurisdiciton>();
    private List<Jurisdiciton> applyFailLog = new ArrayList<Jurisdiciton>();
    private List<Jurisdiciton> applySuccesslLog = new ArrayList<Jurisdiciton>();
    private List<String> dangerousPermissions = new ArrayList<String>();


    private Activity activity;

    private IJurisdictionListener listener;
    public void setJurisdictionListener(IJurisdictionListener listener){
        this.listener = listener;
    }

    public interface IJurisdictionListener{
        public void applyfinish();
    }

    public class Jurisdiciton{
        public int requestCode;
        public String name;
        public String dec;
    }

    private JurisdictionApply(){

    }

    public void init(Activity activity){
        this.activity = activity;

        queue = new LinkedList<Jurisdiciton>();
        applyFailLog = new ArrayList<Jurisdiciton>();
        applySuccesslLog = new ArrayList<Jurisdiciton>();
        dangerousPermissions = new ArrayList<String>();

        configPermission();

        readManifestJurisdictionList();
    }

    public static JurisdictionApply getInstance(){
        return jurisdictionApply == null ? jurisdictionApply = new JurisdictionApply() : jurisdictionApply;
    }

    public Jurisdiciton getCurrentApplyJurisdiction(){
        return queue.peek();
    }

    private void applyJurisdictionSuccess(){
        if(!queue.isEmpty()){
            applySuccesslLog.add(getCurrentApplyJurisdiction());
            queue.remove();
        }

    }

    private void applyJurisdictionFail(){
        if(!queue.isEmpty()){
            applyFailLog.add(getCurrentApplyJurisdiction());
            queue.remove();
        }

    }

    private void readManifestJurisdictionList(){

        try {
            PackageInfo packageInfo =  activity.getPackageManager().getPackageInfo(activity.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] permissionInfos = packageInfo.requestedPermissions;
            int i = 0;
            for (String str:permissionInfos) {

                if(!str.startsWith("android")){
                    continue;
                }

                Jurisdiciton jurisdiciton = new Jurisdiciton();
                jurisdiciton.name = str;
                jurisdiciton.requestCode = 0x0010+i++;

                if(isNeedApplyJurisdiction(jurisdiciton.name)){
                    if(ContextCompat.checkSelfPermission(activity, jurisdiciton.name) != PackageManager.PERMISSION_GRANTED){

                        queue.add(jurisdiciton);

                        LogUtils.logDug("apply permissionInfos---"+str);
                    }
                }

                LogUtils.logDug("permissionInfos---"+str);

            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void applyJuricdiction(){

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if(null != listener) listener.applyfinish();;
            return;
        }

        Jurisdiciton jurisdiciton = getCurrentApplyJurisdiction();
        if(null == jurisdiciton) {

            createLog();

            if(null != listener) listener.applyfinish();

            return;
        }

        if(ContextCompat.checkSelfPermission(activity, jurisdiciton.name) != PackageManager.PERMISSION_GRANTED){
            activity.requestPermissions(new String[]{jurisdiciton.name},jurisdiciton.requestCode);
        }else{
            applyJurisdictionSuccess();
            applyJuricdiction();
        }


    }

    private void createLog(){
        for (Jurisdiciton jurisdiction : applySuccesslLog) {
            LogUtils.logDug("jurisdiction success = " + jurisdiction.name);
        }

        for (Jurisdiciton jurisdiction : applyFailLog) {
            LogUtils.logDug("jurisdiction fail = " + jurisdiction.name);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Jurisdiciton jurisdiciton = getCurrentApplyJurisdiction();
        if(null == jurisdiciton) return;

        if(requestCode == jurisdiciton.requestCode){
            if (grantResults != null && grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    applyJurisdictionFail();
                } else {
                    applyJurisdictionSuccess();
                }
            } else {
                applyJurisdictionFail();
            }

            applyJuricdiction();
        }
    }


    private void configPermission(){
        //group:android.permission-group.CONTACTS
        dangerousPermissions.add("android.permission.WRITE_CONTACTS");
        dangerousPermissions.add("android.permission.GET_ACCOUNTS");
        dangerousPermissions.add("android.permission.READ_CONTACTS");

        //group:android.permission-group.PHONE
        dangerousPermissions.add("android.permission.READ_CALL_LOG");
        dangerousPermissions.add("android.permission.READ_PHONE_STATE");
        dangerousPermissions.add("android.permission.CALL_PHONE");
        dangerousPermissions.add("android.permission.WRITE_CALL_LOG");
        dangerousPermissions.add("android.permission.USE_SIP");
        dangerousPermissions.add("android.permission.PROCESS_OUTGOING_CALLS");
        dangerousPermissions.add("com.android.voicemail.permission.ADD_VOICEMAIL");

       // group:android.permission-group.CALENDAR
        dangerousPermissions.add("android.permission.READ_CALENDAR");
        dangerousPermissions.add("android.permission.WRITE_CALENDAR");

        //group:android.permission-group.CAMERA
        dangerousPermissions.add("android.permission.CAMERA");

        //group:android.permission-group.SENSORS
        dangerousPermissions.add("android.permission.BODY_SENSORS");

        //group:android.permission-group.LOCATION
        dangerousPermissions.add("android.permission.ACCESS_FINE_LOCATION");
        dangerousPermissions.add("android.permission.ACCESS_COARSE_LOCATION");

       // group:android.permission-group.STORAGE
        dangerousPermissions.add("android.permission.READ_EXTERNAL_STORAGE");
        dangerousPermissions.add("android.permission.WRITE_EXTERNAL_STORAGE");

        //group:android.permission-group.MICROPHONE
        dangerousPermissions.add("android.permission.RECORD_AUDIO");

        //group:android.permission-group.SMS
        dangerousPermissions.add("android.permission.READ_SMS");
        dangerousPermissions.add("android.permission.RECEIVE_WAP_PUSH");
        dangerousPermissions.add("android.permission.RECEIVE_MMS");
        dangerousPermissions.add("android.permission.RECEIVE_SMS");
        dangerousPermissions.add("android.permission.SEND_SMS");
        dangerousPermissions.add("android.permission.READ_CELL_BROADCASTS");
    }

    private boolean isNeedApplyJurisdiction(String name){
        for (String str: dangerousPermissions) {
            if(name.equals(str)){
                return true;
            }
        }

        return false;
    }

}
