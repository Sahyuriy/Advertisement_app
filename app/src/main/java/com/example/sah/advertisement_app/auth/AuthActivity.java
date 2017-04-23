package com.example.sah.advertisement_app.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sah.advertisement_app.CheckConnection;
import com.example.sah.advertisement_app.MainActivity;
import com.example.sah.advertisement_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;

public class AuthActivity extends AppCompatActivity {


    public static final String APP_PREF = "mysettings";
    public static final String APP_PREF_EMAIL = "email";
    public static final String APP_PREF_PASSWORD = "password";
    public static final String APP_PREF_BTN = "authbtn";
    public static final String APP_PREF_SIGNED = "signed";

    private SharedPreferences mSettings;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText etEmail, etPass;
    private Button btnAuth, btnReg;
    private boolean isEmailValid;
    private boolean isPasswordValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);



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

        mSettings = getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);

        etEmail = (EditText) findViewById(R.id.et_email);
        etPass = (EditText) findViewById(R.id.et_pass);

        btnAuth = (Button) findViewById(R.id.btn_auth);
        btnReg = (Button) findViewById(R.id.btn_reg);
        btnAuth.setEnabled(false);

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateEmail(s.toString());
                updateLoginButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        etPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePassword(s.toString());
                updateLoginButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckConnection cc = new CheckConnection(getApplicationContext());
                if (cc.isNetworkAvailable()){
                    auth(etEmail.getText().toString(),etPass.getText().toString());
                }else{
                    Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(AuthActivity.this, RegistrationActivity.class);
                startActivityForResult(in, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            etEmail.setText(mSettings.getString(APP_PREF_EMAIL, ""));
            etPass.setText(mSettings.getString(APP_PREF_PASSWORD, ""));
        }
    }

    public void auth(String email, String password){

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AuthActivity.this, "Aвторизация успешна", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putString(APP_PREF_EMAIL, etEmail.getText().toString());
                    editor.putString(APP_PREF_PASSWORD, etPass.getText().toString());
                    editor.putString(APP_PREF_SIGNED, "isSigned");
                    editor.apply();
                    startActivity(new Intent(AuthActivity.this,MainActivity.class));
                }else {
                    Toast.makeText(AuthActivity.this, "Ошибка авторизации", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void validatePassword(String text) {
        isPasswordValid = !text.isEmpty();
    }

    private void validateEmail(String text) {
        isEmailValid = Patterns.EMAIL_ADDRESS.matcher(text).matches();
    }
    private void updateLoginButtonState() {
        if(isEmailValid && isPasswordValid) {
            btnAuth.setEnabled(true);
        } else {
            btnAuth.setEnabled(false);
        }

    }

}
