package com.example.sah.advertisement_app;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sah.advertisement_app.auth.AuthActivity;
import com.example.sah.advertisement_app.db.DBHelper;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailsActivity extends MainActivity {

    private TextView tv_title, tv_text, tv_author_name, tv_author_email;
    private ImageView imageView;
    private JSONObject jsonObj, jImage;
    private String jTitle, jText, jName, imgName, scaleType, jUid, jEmail;
    private FirebaseStorage fStorage = FirebaseStorage.getInstance();
    private StorageReference storageRef;
    private String json;
    private DBHelper dbHelper;
    private Button btn_add_f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_text = (TextView) findViewById(R.id.tv_descr);
        tv_author_name = (TextView) findViewById(R.id.tv_author_name);
        tv_author_email = (TextView) findViewById(R.id.tv_author_email);
        imageView = (ImageView) findViewById(R.id.iv_image);
        btn_add_f = (Button) findViewById(R.id.btn_f);

        json = getIntent().getStringExtra("ITEM_JSON");

        try {
            jsonObj = new JSONObject(json);
            tv_text.setText(jsonObj.getString("text"));
            tv_title.setText(jsonObj.getString("title"));
            tv_author_name.setText(jsonObj.getString("author"));
            tv_author_email.setText(jsonObj.getString("email"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            jImage = jsonObj.getJSONObject("image");
            if (!jImage.toString().equals("")){
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

        btn_add_f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper = new DBHelper(getApplicationContext());
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put(DBHelper.KEY_ADV, json);
                database.insert(DBHelper.TABLE_ADV, null, contentValues);
                Toast.makeText(getApplicationContext(), "Операция успешна", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
