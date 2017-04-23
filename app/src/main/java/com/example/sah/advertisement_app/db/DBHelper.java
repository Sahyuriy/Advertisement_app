package com.example.sah.advertisement_app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "My_DataBase";
    public static final String TABLE_ADV = "adv";


    public static final String KEY_ID = "_id";
    public static final String KEY_ADV = "advertisement";



    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_ADV + "(" + KEY_ID + " integer primary key,"
                + KEY_ADV + " text" +")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_ADV);

        onCreate(db);
    }
}