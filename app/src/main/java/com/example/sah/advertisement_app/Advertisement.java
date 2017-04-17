package com.example.sah.advertisement_app;


import org.json.JSONObject;

public class Advertisement {


        String key,value;

        public Advertisement(String jsonObject) {

            value = jsonObject;
        }

        public Advertisement() {
        }

//        public String getKey() {
//            return key;
//        }
//
//        public void setKey(String k) {
//            key = k;
//        }

        public String getValue() {
            return value;
        }

        public void setValue(String string) {
            value = string;
        }



}
