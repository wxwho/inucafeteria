package com.inu.cafeteria.Model;

/**
 * Created by GaYoon on 2017-08-02.
 */

public class ResetNumModel {

    private String fcmtoken;
    private String result;


    public ResetNumModel(String fcmtoken) {
        this.fcmtoken = fcmtoken;
    }

    public String getFcmtoken() {
        return fcmtoken;
    }

    public void setFcmtoken(String fcmtoken) {
        this.fcmtoken = fcmtoken;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
