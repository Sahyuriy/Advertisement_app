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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sah.advertisement_app.auth.AuthActivity;
import com.firebase.ui.database.FirebaseListAdapter;
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


    private Button btn_new;
    private EditText et_new;
    //private ArrayList<String> items = new ArrayList<>();
    private ArrayList<String> items;
    private Spinner spinner, spinnerAdd;
    private String selectedCategory;


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

        listView = (ListView) findViewById(R.id.lv_item);

        btn_new = (Button) findViewById(R.id.btn_new);
        et_new = (EditText) findViewById(R.id.et_new);


        myRef = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getInstance().getCurrentUser();

        spinner = (Spinner) findViewById(R.id.spinner_watch);
        spinnerAdd = (Spinner) findViewById(R.id.spinner_add);

        String selected = spinner.getSelectedItem().toString();

        if (selected.equals("Медицина")){
            selectedCategory = "Medical";
        }
        else {
            selectedCategory = "Comp";
        }





        final FirebaseUser mUser = mAuth.getInstance().getCurrentUser();

        btn_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //myRef.child("Tasks").push().setValue(et_new.getText().toString());
                myRef.child("Adv").child(selectedCategory).push().setValue(et_new.getText().toString());
                updateUI();
            }
        });
        //displayAdv();





        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tab1");
        tabSpec.setContent(R.id.lin_one);
        tabSpec.setIndicator("Смотреть рекламу");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tab2");
        tabSpec.setContent(R.id.lin_two);
        tabSpec.setIndicator("Разместить");
        tabHost.addTab(tabSpec);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position==1){
                    selectedCategory = "Medical";
                    displayAdv();
                }else {
                    selectedCategory = "Comp";
                    displayAdv();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerAdd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position==1){
                    selectedCategory = "Medical";
                }else {
                    selectedCategory = "Comp";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





    }

    private void updateUI() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
    }

    private void displayAdv() {
        items = new ArrayList<>();

        myRef.child("Adv").child(selectedCategory).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {

                    value = childDataSnapshot.getValue().toString();
                    items.add(value);
                }
                //value = dataSnapshot.getValue(String.class);
                //textView.setText(value.toString());
                updateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
