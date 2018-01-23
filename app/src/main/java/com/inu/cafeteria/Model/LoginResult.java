package com.inu.cafeteria.Model;


import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class LoginResult implements Serializable {

    public LoginResult() {

    }

    public LoginResult(DataLogin login, DataStu stu_info, ArrayList<DataCode> code) {
        this.login = login;
        this.stu_info = stu_info;
        this.code = code;
    }

    private DataLogin login;
    private DataStu stu_info;
    private ArrayList<DataCode> code;

    public ArrayList<DataCode> getCode() {
        return code;
    }
    public DataLogin getLogin() {return login;}
    public DataStu getStu_info() {return stu_info;}

    public void setLogin(DataLogin login) {
        this.login = login;
    }
    public void setStu_info(DataStu stu_info) {
        this.stu_info = stu_info;
    }
    public void setCode(ArrayList<DataCode> code) {
        this.code = code;
    }

    @SuppressWarnings("serial")
    public class DataLogin implements Serializable {
        private String dtoken;
        private String barcode;

        public String getDtoken() {return dtoken;}
        public String getBarcode() {return barcode;}

        public void setDtoken(String dtoken) {
            this.dtoken = dtoken;
        }
        public void setBarcode(String barcode) {
            this.barcode = barcode;
        }
    }


    @SuppressWarnings("serial")
    public class DataStu implements Serializable {

        private String stu_num;
        private String name;
        private String dep;
        private String stat;

        public String getStu_num() {
            return stu_num;
        }
        public String getName() {
            return name;
        }
        public String getDep() {
            return dep;
        }
        public String getStat() {
            return stat;
        }

        public void setStu_num(String stu_num) {
            this.stu_num = stu_num;
        }
        public void setName(String name) {
            this.name = name;
        }
        public void setDep(String dep) {
            this.dep = dep;
        }
        public void setStat(String stat) {
            this.stat = stat;
        }
    }

    @SuppressWarnings("serial")
    public class DataCode implements Serializable {
        private String name;
        private String code;
        private String img;

        public String getName() {
            return name;
        }
        public String getCode() {
            return code;
        }
        public String getImg() {
            return img;
        }

        public void setName(String name) {
            this.name = name;
        }
        public void setCode(String code) {
            this.code = code;
        }
        public void setImg(String img) {
            this.img = img;
        }
    }



}
