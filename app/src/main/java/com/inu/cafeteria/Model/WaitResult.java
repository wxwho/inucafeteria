package com.inu.cafeteria.Model;

/**
 * Created by GaYoon on 2017-08-09.
 */

public class WaitResult {

    private String num1;
    private String num2;
    private String num3;
    private String code;


    public WaitResult(String num1, String num2, String num3, String code) {
        this.num1 = num1;
        this.num2 = num2;
        this.num3 = num3;
        this.code = code;
    }

    public String getNum1() {
        return num1;
    }

    public void setNum1(String num1) {
        this.num1 = num1;
    }

    public String getNum2() {
        return num2;
    }

    public void setNum2(String num2) {
        this.num2 = num2;
    }

    public String getNum3() {
        return num3;
    }

    public void setNum3(String num3) {
        this.num3 = num3;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
