package com.inu.cafeteria.Model;

/**
 * Created by ksj on 2017. 8. 9..
 */

public class ActiveBarcodeData {
    private int flag;
    private String device;
    private String barcode;

    public ActiveBarcodeData() {
    }

    public ActiveBarcodeData(int flag, String device, String barcode) {
        this.flag = flag;
        this.device = device;
        this.barcode = barcode;
    }

    public String getDevice() {
        return device;
    }

    public String getBarcode() {
        return barcode;
    }

    public int getFlag() {
        return flag;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }


}
