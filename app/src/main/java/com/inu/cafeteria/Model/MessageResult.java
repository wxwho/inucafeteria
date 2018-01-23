package com.inu.cafeteria.Model;

/**
 * Created by GaYoon on 2017-08-17.
 */

public class MessageResult {

    private All all;
    private Android android;

    public class All {
        private String title;
        private String message;

        public String getTitle() {
            return title;
        }

        public String getMessage() {
            return message;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public class Android {
        private String title;
        private String message;

        public String getTitle() {
            return title;
        }

        public String getMessage() {
            return message;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public All getAll() {
        return all;
    }

    public Android getAndroid() {
        return android;
    }

    public void setAll(All all) {
        this.all = all;
    }

    public void setAndroid(Android android) {
        this.android = android;
    }
}
