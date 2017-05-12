package com.example.sah.advertisement_app.db;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.sah.advertisement_app.AdvAdapter;
import com.example.sah.advertisement_app.MainActivity;
import com.example.sah.advertisement_app.R;
import com.example.sah.advertisement_app.auth.AuthActivity;

import java.util.ArrayList;

public class FavoritesActivity extends MainActivity {

    private RecyclerView recyclerView;
    private ArrayList<String> items = new ArrayList<>();
    private DBHelper dbHelper;
    private ImageButton btnClean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.getBackground().setAlpha(125);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.rv_favorites);
        btnClean = (ImageButton) findViewById(R.id.btn_clean) ;

        dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        Cursor cursor = database.query(DBHelper.TABLE_ADV, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int adv = cursor.getColumnIndex(DBHelper.KEY_ADV);
            do {
                items.add(String.valueOf(cursor.getString(adv)));
            } while (cursor.moveToNext());
        }
        FavoritesAdapter myAdapter = new FavoritesAdapter(items, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(myAdapter);
        
        btnClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanDB();
            }
        });
    }

    private void cleanDB() {

        final AlertDialog.Builder cleanDialog = new AlertDialog.Builder(this);
        cleanDialog.setMessage(R.string.dialogFavorites);
        cleanDialog.setPositiveButton(R.string.dialogBtn_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                database.delete(DBHelper.TABLE_ADV, null, null);
                dbHelper.close();
                Intent i = new Intent(getApplicationContext(), FavoritesActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        }).setCancelable(false)
                .setNegativeButton(R.string.dialogBtn_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        cleanDialog.create();
        cleanDialog.show();

    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu){
        super.onPrepareOptionsMenu(menu);

        MenuItem myItem = menu.findItem(R.id.favorites);
        myItem.setEnabled(false);

        return true;
    }
}
