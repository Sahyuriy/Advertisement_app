package com.example.sah.advertisement_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sah.advertisement_app.auth.AuthActivity;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    public static final String APP_PREF = "mysettings";
    public static final String APP_PREF_SIGNED = "signed";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText etEmail, etPass;
    private Button btnAuth, btnReg;
    private SharedPreferences mSettings;
    private FirebaseUser user;
    private DatabaseReference myRef;
    private List<String> mList;
    private ListView listView;
    private TextView textView;
    private FirebaseListAdapter mAdater;


    private Button btn_new, storage;
    private EditText et_new;
    //private ArrayList<String> items = new ArrayList<>();
    private ArrayList<String> items;
    private Spinner spinner, spinnerAdd;
    private String selectedCategory;
    private Button btn_show;
    private ImageView imageView;
    private FirebaseStorage fStorage = FirebaseStorage.getInstance();
    private StorageReference storageRef = fStorage.getReferenceFromUrl("gs://advertisementapp-c96d6.appspot.com/images/niZkcNtKU8abzIO9NwUPy5P4Bxv2[C@421a3dc0.jpg");

    String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mSettings = getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);


        if (mSettings.getString(APP_PREF_SIGNED, "").equals("isSigned")) {

//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragm, new MainFragment())
//                    .commit();

        } else {
            Toast.makeText(this, "Авторизируйтесь", Toast.LENGTH_LONG).show();
            Intent in = new Intent(MainActivity.this, AuthActivity.class);
            startActivity(in);
        }


        textView = (TextView) findViewById(R.id.tv);


        btn_show = (Button) findViewById(R.id.btn_show);
        btn_new = (Button) findViewById(R.id.btn_new);
        storage = (Button) findViewById(R.id.btn_storage);
        //imageView = (ImageView) findViewById(R.id.image);
//        Glide.with(this)
//                .using(new FirebaseImageLoader())
//                .load(storageRef)
//                .into(imageView);

        myRef = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getInstance().getCurrentUser();

        //spinner = (Spinner) findViewById(R.id.spinner_watch);


        storage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StorageActivity.class);
                startActivity(intent);
            }
        });


        btn_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragm, new AdvetisementFragment()).commit();
            }
        });
        btn_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragm, new AddNewAdvFragment()).commit();
            }
        });


/////////////////////////////////////////////////////////

//        final FirebaseUser mUser = mAuth.getInstance().getCurrentUser();
//
//        btn_new.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //myRef.child("Tasks").push().setValue(et_new.getText().toString());
//                myRef.child("Adv").child(selectedCategory).push().setValue(et_new.getText().toString());
//                updateUI();
//            }
//        });
//
//
//


///////////////////////////////////////////////////





//        spinnerAdd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position==1){
//                    selectedCategory = "Medical";
//                }else {
//                    selectedCategory = "Comp";
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });





    }


//    @Override
//    public void onClick(View v) {
//        if (v.getId() == R.id.btn_new){}
//        switch (v.getId()){
//            case (R.id.btn_show):
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragm, new AdvetisementFragment()).commit();
//                break;
//            case (R.id.btn_new):
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragm, new AddNewAdvFragment()).commit();
//                break;
//
//        }
//
//    }


//    private void updateUI() {
////        AdvAdapter myAdapter = new AdvAdapter(items, getContext());
////        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
////        recyclerView.setAdapter(myAdapter);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getBaseContext(), android.R.layout.simple_list_item_1, items);
//        listView.setAdapter(adapter);
//    }
//
//    private void displayAdv() {
//        items = new ArrayList<>();
//
//        myRef.child("Adv").child("Comp").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//
//                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
//
//                    value = childDataSnapshot.getValue().toString();
//
//                    items.add(value);
//                }
//                //value = dataSnapshot.getValue(String.class);
//                updateUI();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
}
