package com.example.sah.advertisement_app.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.sah.advertisement_app.CheckConnection;
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
    private ImageView iv_check, iv_check_pass, iv_pass_conf, iv_name;
    private boolean isEmailValid;
    private boolean isPasswordValid;
    private boolean isNameValid;
    private boolean isPassword;
    private boolean userType = false;
    private RadioGroup radioGroup;


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

        iv_check = (ImageView) findViewById(R.id.iv_check);
        iv_check_pass = (ImageView) findViewById(R.id.iv_check_pass);
        iv_pass_conf = (ImageView) findViewById(R.id.iv_pass_conf);
        iv_name = (ImageView) findViewById(R.id.iv_check_name);

        etEmail = (EditText) findViewById(R.id.et_my_email);
        etPass = (EditText) findViewById(R.id.et_my_pass);
        etPassConf = (EditText) findViewById(R.id.et_pass_conf);

        btnReg = (Button) findViewById(R.id.btn_registration);
        radioGroup = (RadioGroup) findViewById(R.id.rg);

        etName = (EditText) findViewById(R.id.et_my_name);
        btnReg.setEnabled(false);


        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateName(s.toString());
                updateRegisterButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateEmail(s.toString());
                updateRegisterButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePassword(s.toString());
                updateRegisterButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etPassConf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                confirmPassword(s.toString());
                updateRegisterButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                SharedPreferences.Editor editor = mSettings.edit();
                switch (checkedId) {
                    case R.id.rb_user:
                        userType = true;
                        editor.putString(APP_PREF_USER, "user");
                        editor.apply();
                        updateRegisterButtonState();
                        break;
                    case R.id.rb_advertiser:
                        userType = true;
                        editor.putString(APP_PREF_USER, "advertiser");
                        editor.apply();
                        updateRegisterButtonState();
                        break;

                    default:
                        break;
                }
            }
        });

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etPass.getText().toString().equals(etPassConf.getText().toString())) {
                    CheckConnection cc = new CheckConnection(getApplicationContext());
                    if (cc.isNetworkAvailable()) {
                        registration(etEmail.getText().toString(), etPass.getText().toString());
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
                    }
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


    private void validateName(String text) {
        isNameValid = !text.isEmpty();
    }

    private void validatePassword(String text) {
        isPasswordValid = text.length() > 5;
    }

    private void validateEmail(String text) {
        isEmailValid = Patterns.EMAIL_ADDRESS.matcher(text).matches();
    }

    private void confirmPassword(String string) {
        isPassword = etPass.getText().toString().equals(string);
    }

    private void updateRegisterButtonState() {
        if (isEmailValid && isPasswordValid && isPassword && userType && isNameValid) {
            btnReg.setEnabled(true);
        } else {
            btnReg.setEnabled(false);
        }
        if (isEmailValid) {
            iv_check.setVisibility(View.VISIBLE);
            iv_check.setImageResource(R.mipmap.ic_ok);
        } else {
            iv_check.setVisibility(View.VISIBLE);
            iv_check.setImageResource(R.mipmap.ic_wrong);
        }
        if (isPasswordValid) {
            iv_check_pass.setVisibility(View.VISIBLE);
            iv_check_pass.setImageResource(R.mipmap.ic_ok);
        } else {
            iv_check_pass.setVisibility(View.VISIBLE);
            iv_check_pass.setImageResource(R.mipmap.ic_wrong);
        }
        if (isPassword) {
            iv_pass_conf.setVisibility(View.VISIBLE);
            iv_pass_conf.setImageResource(R.mipmap.ic_ok);
        } else {
            iv_pass_conf.setVisibility(View.VISIBLE);
            iv_pass_conf.setImageResource(R.mipmap.ic_wrong);
        }
        if (isNameValid) {
            iv_name.setVisibility(View.VISIBLE);
            iv_name.setImageResource(R.mipmap.ic_ok);
        } else {
            iv_name.setVisibility(View.VISIBLE);
            iv_name.setImageResource(R.mipmap.ic_wrong);
        }
    }
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        mAuth.fetchProvidersForEmail(etEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
//            @Override
//            public void onComplete(@NonNull Task<ProviderQueryResult> task) {
//                if (!task.getResult().getProviders().isEmpty()) {
//                    Toast.makeText(getApplicationContext(), "Email is taken", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

//        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
//            Toast.makeText(SignUpActivity.this, "User with this email already exist.", Toast.LENGTH_SHORT).show();
//        }
//        if(etEmail.getText().toString().equals(user.getEmail().trim())) {
//            Toast.makeText(this, "Email is taken", Toast.LENGTH_SHORT).show();
//        }
}
