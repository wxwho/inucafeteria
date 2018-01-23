package com.inu.cafeteria.Model;

/**
 * Created by GaYoon on 2017-08-08.
 */

public class LoginData {

    private String sno;
    private String pw;
    private String device;
    private String auto;

    public LoginData(String sno, String pw, String device, String auto) {
        this.sno = sno;
        this.pw = pw;
        this.device = device;
        this.auto = auto;
    }

    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getAuto() {
        return auto;
    }

    public void setAuto(String auto) {
        this.auto = auto;
    }

}
