package com.inu.cafeteria.Model;

/**
 * Created by GaYoon on 2017-07-28.
 */

public class PushModel {

    private String num;
    private String code;


    public PushModel(String num, String code) {
        this.num = num;
        this.code = code;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


}
