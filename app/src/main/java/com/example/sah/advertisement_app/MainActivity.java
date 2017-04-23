package com.example.sah.advertisement_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.example.sah.advertisement_app.db.FavoritesActivity;
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

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
//                    Intent intent = new Intent(AuthActivity.this,MainActivity.class);
//                    startActivity(intent);
                    // User is signed in
//                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
//                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };

        if (mSettings.getString(APP_PREF_SIGNED, "").equals("isSigned")) {

//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragm, new MainFragment())
//                    .commit();

        } else {
            Toast.makeText(this, "Авторизируйтесь", Toast.LENGTH_LONG).show();
            startActivity(new Intent(MainActivity.this, AuthActivity.class));
        }


        textView = (TextView) findViewById(R.id.tv);

        btn_show = (Button) findViewById(R.id.btn_show);
        btn_new = (Button) findViewById(R.id.btn_new);

        myRef = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getInstance().getCurrentUser();

        //spinner = (Spinner) findViewById(R.id.spinner_watch);


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


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.logout:
                mAuth.signOut();
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString(APP_PREF_SIGNED, "notSigned");
                editor.apply();
                Intent i = new Intent(MainActivity.this, AuthActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            case R.id.favorites:
                startActivity(new Intent(MainActivity.this, FavoritesActivity.class));
            case R.id.close:
                finishAffinity();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
