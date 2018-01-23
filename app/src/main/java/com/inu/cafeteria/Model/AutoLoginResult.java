package com.inu.cafeteria.Model;

import java.io.Serializable;


@SuppressWarnings("serial")
public class AutoLoginResult implements Serializable {

    public AutoLoginResult() {
    }

    public AutoLoginResult(String barcode, StuInfo stu_info) {
        this.barcode = barcode;
        this.stu_info = stu_info;
    }


    private String barcode;
    private StuInfo stu_info;

    public String getBarcode() {return barcode;}
    public StuInfo getStuInfo() {return stu_info;}

    public void setBarcode(String barcode) {this.barcode = barcode;}
    public void setStuInfo(StuInfo stu_info) {this.stu_info = stu_info;}

    @SuppressWarnings("serial")
    public class StuInfo implements Serializable{
        private String stu_num;
        private String name;
        private String dep;
        private String stat;

        public String getStu_num() {return stu_num;}
        public String getName() {return name;}
        public String getDep() {return dep;}
        public String getStat() {return stat;}

        public void setStu_num(String stu_num) {this.stu_num = stu_num;}
        public void setName(String name) {this.name = name;}
        public void setDep(String dep) {this.dep = dep;}
        public void setStat(String stat) {this.stat = stat;}
    }
}
