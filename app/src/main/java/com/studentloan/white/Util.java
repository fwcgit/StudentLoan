package com.studentloan.white;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.megvii.livenessdetection.bean.FaceIDDataStruct;
import com.studentloan.white.utils.Constant;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

/**
 * 代码参考
 * <p>
 * 这里写了一些代码帮助（仅供参考）
 */
public class Util {

    /**
     * 根据byte数组，生成图片
     */
    public static String saveJPGFile(Context mContext, byte[] data, String key) {
        if (data == null)
            return null;

        File mediaStorageDir = mContext
                .getExternalFilesDir(Constant.cacheImage);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            String jpgFileName = System.currentTimeMillis() + ""
                    + new Random().nextInt(1000000) + "_" + key + ".jpg";
            fos = new FileOutputStream(mediaStorageDir + "/" + jpgFileName);
            bos = new BufferedOutputStream(fos);
            bos.write(data);
            return mediaStorageDir.getAbsolutePath() + "/" + jpgFileName;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 获取活体检测的BestImage和Delta 注意：需要在活体检测成功后调用
     * <p>
     * 如何获取idDataStruct： （从活体检测器中获取） FaceIDDataStruct idDataStruct =
     * detector.getFaceIDDataStruct();
     */
    public void getBestImageAndDelta(FaceIDDataStruct idDataStruct) {
        String delta = idDataStruct.delta; // 获取delta；
        HashMap<String, byte[]> images = (HashMap<String, byte[]>) idDataStruct.images;// 获取所有图片
        for (String key : idDataStruct.images.keySet()) {
            byte[] data = idDataStruct.images.get(key);
            if (key.equals("image_best")) {
                byte[] imageBestData = data;// 这是最好的一张图片
            } else if (key.equals("image_env")) {
                byte[] imageEnvData = data;// 这是一张全景图
            } else {
                // 其余为其他图片，根据需求自取
            }
        }
    }

    /**
     * 如何调用Verify2.0方法
     */
    public void imageVerify(String delta, Map<String, byte[]> mImage) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("idcard_name", "身份证姓名");
        requestParams.put("idcard_number", "身份证号码");
        requestParams.put("delta", delta);
        requestParams.put("api_key", "API_KEY");
        requestParams.put("api_secret", "API_SECRET");

        requestParams.put("comparison_type", 1 + "");
        requestParams.put("face_image_type", "meglive");

        for (Entry<String, byte[]> entry : mImage.entrySet()) {
            requestParams.put(entry.getKey(),
                    new ByteArrayInputStream(entry.getValue()));
        }
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = "https://api.megvii.com/faceid/v2/verify";
        asyncHttpClient.post(url, requestParams,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        String s = new String(bytes);
                        Log.e("TAG", "成功：" + s);

                    }

                    @Override
                    public void onFailure(int i, Header[] headers,
                                          byte[] bytes, Throwable throwable) {
                        String s = new String(bytes);
                        Log.e("TAG", "失败信息：" + s);
                        // 请求失败
                    }
                });
    }

    /**
     * ocridcard接口调用
     */
    public void imageOCR() {
        RequestParams rParams = new RequestParams();
        rParams.put("api_key", "API_KEY");
        rParams.put("api_secret", "API_SECRET");
        try {
            rParams.put("image", new File("imagePath"));// 身份证照片图片地址
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        rParams.put("legality", 1 + "");// 传入1可以判断身份证是否
        // 被编辑/是真实身份证/是复印件/是屏幕翻拍/是临时身份证
        AsyncHttpClient asyncHttpclient = new AsyncHttpClient();
        String url = "https://api.faceid.com/faceid/v1/ocridcard";
        asyncHttpclient.post(url, rParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseByte) {
                String successStr = new String(responseByte);
                try {
                    JSONObject jObject = new JSONObject(successStr);
                    if ("back".equals(jObject.getString("side"))) {
                        // 身份证背后信息
                    } else {
                        // 身份证正面信息
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {
                // 上传失败
            }
        });
    }
}