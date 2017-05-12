package com.example.sah.advertisement_app;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sah.advertisement_app.auth.AuthActivity;
import com.example.sah.advertisement_app.db.DBHelper;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailsActivity extends MainActivity {


    private TextView tv_title, tv_text, tv_author_name;
    private ImageView imageView, btn_del;
    private JSONObject jsonObj, jImage;
    private String jTitle, imgName, scaleType, jUid, jEmail;
    private FirebaseStorage fStorage = FirebaseStorage.getInstance();
    private StorageReference storageRef;
    private String json, uid, category, key;
    private DBHelper dbHelper;
    private Button btn_add_f;
    private ImageButton ib_send;
    private FirebaseAuth mAuth;
    private LinearLayout ll_author;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReferenceFromUrl("https://advertisementapp-c96d6.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.getBackground().setAlpha(125);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_text = (TextView) findViewById(R.id.tv_descr);
        tv_author_name = (TextView) findViewById(R.id.tv_author_name);
        imageView = (ImageView) findViewById(R.id.iv_image);
        btn_add_f = (Button) findViewById(R.id.btn_f);
        ib_send = (ImageButton) findViewById(R.id.ib_send);
        ll_author = (LinearLayout) findViewById(R.id.isAuthor);
        btn_del = (ImageButton) findViewById(R.id.btn_del);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        json = getIntent().getStringExtra("ITEM_JSON");
        category = getIntent().getStringExtra("CATEGORY");
        key = getIntent().getStringExtra("KEY");

        try {
            jsonObj = new JSONObject(json);
            tv_text.setText(jsonObj.getString("text"));
            tv_title.setText(jsonObj.getString("title"));
            tv_author_name.setText(jsonObj.getString("author"));
            //tv_author_email.setText(jsonObj.getString("email"));
            jEmail = jsonObj.getString("email");
            jTitle = jsonObj.getString("title");
            jUid = jsonObj.getString("uid");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            jImage = jsonObj.getJSONObject("image");
            if (!jImage.toString().equals("")) {
                imageView.setVisibility(View.VISIBLE);
                imgName = jImage.getString("imageName");
                scaleType = jImage.getString("scaleType");


                storageRef = fStorage.getReferenceFromUrl("gs://advertisementapp-c96d6.appspot.com/images/" + imgName);
                Glide.with(getApplicationContext())
                        .using(new FirebaseImageLoader())
                        .load(storageRef)
                        .into(imageView);
                if (scaleType.equals("centerCrop")) {
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (scaleType.equals("fitCenter")) {
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                } else {
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (uid.equals(jUid)) {
            ll_author.setVisibility(View.VISIBLE);
        }

        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAdv();
            }
        });


        btn_add_f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper = new DBHelper(getApplicationContext());
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put(DBHelper.KEY_ADV, json);
                database.insert(DBHelper.TABLE_ADV, null, contentValues);
                Toast.makeText(getApplicationContext(), R.string.add_to_fav_ok, Toast.LENGTH_SHORT).show();
            }
        });


        ib_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });


    }



    private void deleteAdv() {

        final AlertDialog.Builder deleteDialog = new AlertDialog.Builder(this);

        deleteDialog.setMessage(R.string.dialogText_del);
        deleteDialog.setPositiveButton(R.string.dialogBtn_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myRef.child("Adv").child(category).child(key).removeValue();
                dialog.dismiss();
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                Toast.makeText(getApplicationContext(), R.string.add_to_fav_ok, Toast.LENGTH_SHORT).show();
            }
        }).setCancelable(false)
                .setNegativeButton(R.string.dialogBtn_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        deleteDialog.create();
        deleteDialog.show();

    }


    private void sendEmail() {


        final AlertDialog.Builder emailDialog = new AlertDialog.Builder(this);

        emailDialog.setTitle(R.string.dialogTitle_send);

        View linearlayout = getLayoutInflater().inflate(R.layout.dialoglayout, null);
        emailDialog.setView(linearlayout);

        final EditText et = (EditText) linearlayout.findViewById(R.id.et_send_email);


        emailDialog.setPositiveButton(R.string.dialogBtn_send,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sending(et.getText().toString());
                        dialog.dismiss();
                    }
                })

                .setNegativeButton(R.string.dialogBtn_no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        emailDialog.create();
        emailDialog.show();
    }

    private void sending(String text) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Adv" + jTitle);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setData(Uri.parse("mailto:" + jEmail));
        startActivity(intent);
    }


}
