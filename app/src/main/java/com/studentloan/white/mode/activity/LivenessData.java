package com.studentloan.white.mode.activity;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by jianghaozhang on 17/7/11.
 */

public class LivenessData implements Serializable {
    private String delta;
    private Map<String, byte[]> images;
    private int resID;


    public LivenessData(String delta, Map<String, byte[]> images, int resID) {
        this.delta = delta;
        this.images = images;
        this.resID = resID;

    }

    public Map<String, byte[]> getImages() {
        return images;
    }

    public void setImages(Map<String, byte[]> images) {
        this.images = images;
    }

    public int getResID() {
        return resID;
    }

    public void setResID(int resID) {
        this.resID = resID;
    }

    public String getDelta() {
        return delta;
    }

    public void setDelta(String delta) {
        this.delta = delta;
    }


}
