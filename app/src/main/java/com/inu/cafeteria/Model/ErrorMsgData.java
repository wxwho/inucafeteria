package com.inu.cafeteria.Model;

/**
 * Created by GaYoon on 2017-08-18.
 */

public class ErrorMsgData {

    private String sno;
    private String msg;

    public ErrorMsgData(String sno, String msg) {
        this.sno = sno;
        this.msg = msg;
    }

    public String getSno() {return sno;}
    public String getMsg() {return msg;}
    public void setSno(String sno) {this.sno = sno;}
    public void setMsg(String msg) {this.msg = msg;}
}
