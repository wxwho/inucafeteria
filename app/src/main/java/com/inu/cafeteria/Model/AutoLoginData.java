package com.inu.cafeteria.Model;

/**
 * Created by GaYoon on 2017-08-01.
 */

public class AutoLoginData {
    private String dtoken;
    private String sno;

    public String getDtoken() {
        return dtoken;
    }

    public void setDtoken(String dtoken) {
        this.dtoken = dtoken;
    }

    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public AutoLoginData(String dtoken, String sno) {
        this.dtoken = dtoken;
        this.sno = sno;
    }
}
