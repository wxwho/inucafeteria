package com.inu.cafeteria.Model;

/**
 * Created by GaYoon on 2017-08-09.
 */

public class WaitData {

    private String fcmtoken;

    public WaitData(String fcmtoken) {
        this.fcmtoken = fcmtoken;
    }

    public String getFcmtoken() {
        return fcmtoken;
    }

    public void setFcmtoken(String fcmtoken) {
        this.fcmtoken = fcmtoken;
    }
}
