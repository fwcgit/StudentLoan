package com.studentloan.white.utils;

import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by fu on 2017/5/17.
 */

public class FileBase64 {

    public static String FileToBase64(File file){
        if(!file.exists()){
            return "";
        }

        FileInputStream fos = null;
        try {
            fos = new FileInputStream(file);
            byte[] buff = new byte[(int) file.length()];
            fos.read(buff);
            return Base64.encodeToString(buff,Base64.NO_WRAP);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if(null != fos){
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null != fos){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }
}
