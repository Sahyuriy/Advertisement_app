package com.example.sah.advertisement_app.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.sah.advertisement_app.AdvAdapter;
import com.example.sah.advertisement_app.MainActivity;
import com.example.sah.advertisement_app.R;

import java.util.ArrayList;

public class FavoritesActivity extends MainActivity {

    private RecyclerView recyclerView;
    private ArrayList<String> items = new ArrayList<>();
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        recyclerView = (RecyclerView) findViewById(R.id.rv_favorites);

        dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        Cursor cursor = database.query(DBHelper.TABLE_ADV, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int adv = cursor.getColumnIndex(DBHelper.KEY_ADV);
            do {
                items.add(String.valueOf(cursor.getString(adv)));
            } while (cursor.moveToNext());
        }
        FavoritesAdapter myAdapter = new FavoritesAdapter(items, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(myAdapter);
    }
}
