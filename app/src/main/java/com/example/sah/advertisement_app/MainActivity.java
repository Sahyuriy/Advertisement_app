package com.example.sah.advertisement_app;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    public static final String APP_PREF_USER = "usertype";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private SharedPreferences mSettings;
    private FirebaseUser user;
    private DatabaseReference myRef;
    private ImageButton btn_new;
    private Button btn_show;
    private Toolbar toolbar;
    private ProgressDialog pd;
    private ImageView iv_noSignal;
    String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.getBackground().setAlpha(125);
        //toolbar.setTitle("Bla");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pd = new ProgressDialog(getApplicationContext());

        myRef = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getInstance().getCurrentUser();
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

        btn_show = (Button) findViewById(R.id.btn_show);
        btn_new = (ImageButton) findViewById(R.id.btn_new);
        if (!mSettings.getString(APP_PREF_USER, "").equals("advertiser")) {
            btn_new.setVisibility(View.GONE);
        }
        iv_noSignal = (ImageView) findViewById(R.id.iv_signal);


        CheckConnection cc = new CheckConnection(this);
        if (cc.isNetworkAvailable()) {
            if (!mSettings.getString(APP_PREF_SIGNED, "").equals("isSigned")) {
                Toast.makeText(this, R.string.toast_auth, Toast.LENGTH_LONG).show();
                startActivity(new Intent(MainActivity.this, AuthActivity.class));
            }

        } else {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
            iv_noSignal.setVisibility(View.VISIBLE);

        }


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

            case android.R.id.home:
                if (getParentActivityIntent() == null) {
                    onBackPressed();
                } else {
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;

            case R.id.logout:
                logout();
                return true;

            case R.id.favorites:
                startActivity(new Intent(MainActivity.this, FavoritesActivity.class));
                return true;

            case R.id.close:
                exit();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        final AlertDialog.Builder logoutDialog = new AlertDialog.Builder(this);
        logoutDialog.setMessage(R.string.dialogLogout);
        logoutDialog.setPositiveButton(R.string.dialogBtn_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mAuth.signOut();
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString(APP_PREF_SIGNED, "notSigned");
                editor.apply();
                Intent i = new Intent(MainActivity.this, AuthActivity.class);
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
        logoutDialog.create();
        logoutDialog.show();
    }


    private void exit() {
        final AlertDialog.Builder exitDialog = new AlertDialog.Builder(this);
        exitDialog.setMessage(R.string.dialogExit);
        exitDialog.setPositiveButton(R.string.dialogBtn_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        }).setCancelable(false)
                .setNegativeButton(R.string.dialogBtn_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        exitDialog.create();
        exitDialog.show();
    }


}
