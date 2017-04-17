package com.example.sah.advertisement_app.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.sah.advertisement_app.MainActivity;
import com.example.sah.advertisement_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegistrationActivity extends AppCompatActivity {


    public static final String APP_PREF = "mysettings";
    public static final String APP_PREF_EMAIL = "email";
    public static final String APP_PREF_PASSWORD = "password";
    public static final String APP_PREF_USER = "usertype";
    public static final String APP_PREF_USER_NAME = "username";

    private SharedPreferences mSettings;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText etEmail, etPass, etPassConf, etName;
    private Button btnReg;
    private RadioButton rb_user, rb_advertiser;
    private ImageView iv_ckeck;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mSettings = getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                } else {
                    // User is signed out
                }

            }
        };

        etEmail = (EditText) findViewById(R.id.et_my_email);
        etPass = (EditText) findViewById(R.id.et_my_pass);
        btnReg = (Button) findViewById(R.id.btn_registration);
        etPassConf = (EditText) findViewById(R.id.et_pass_conf);
        rb_user = (RadioButton) findViewById(R.id.rb_user);
        rb_advertiser = (RadioButton) findViewById(R.id.rb_advertiser);
        iv_ckeck = (ImageView) findViewById(R.id.iv_check);
        etName = (EditText) findViewById(R.id.et_my_name);





        rb_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_advertiser.clearFocus();
            }
        });
        rb_advertiser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_user.clearFocus();
            }
        });


        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etPass.getText().toString().equals(etPassConf.getText().toString())){
                    if (etEmail.getText().toString().equals("")){
                        Toast.makeText(RegistrationActivity.this, R.string.warning_email, Toast.LENGTH_SHORT).show();
                    }else if (etPass.getText().toString().equals("")){
                        Toast.makeText(RegistrationActivity.this, R.string.warning_pass, Toast.LENGTH_SHORT).show();
                    }else if (etName.getText().toString().equals("")){
                        Toast.makeText(RegistrationActivity.this, R.string.warning_name, Toast.LENGTH_SHORT).show();
                    }else {
                        registration(etEmail.getText().toString(),etPass.getText().toString());
                    }
                }else {
                    etPass.setText("");
                    etPassConf.setText("");
                    Toast.makeText(RegistrationActivity.this, R.string.warning_pass_confirm, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }



    public void registration(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegistrationActivity.this, R.string.warning_reg, Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putString(APP_PREF_EMAIL, etEmail.getText().toString());
                    editor.putString(APP_PREF_PASSWORD, etPass.getText().toString());
                    editor.putString(APP_PREF_USER_NAME, etName.getText().toString());
                    editor.apply();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);

                } else {
                    Toast.makeText(RegistrationActivity.this, R.string.warning_reg_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
