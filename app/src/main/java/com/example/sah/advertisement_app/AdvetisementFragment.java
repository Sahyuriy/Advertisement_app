package com.example.sah.advertisement_app;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class AdvetisementFragment extends Fragment {


    private Spinner spinner;
    private String selectedCategory;
    private ArrayList<String> items, keys, adv, advKeys;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private String value, key;
    private RecyclerView recyclerView;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReferenceFromUrl("https://advertisementapp-c96d6.firebaseio.com/");
    private ProgressDialog pd;


    public AdvetisementFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_advetisement, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spinner = (Spinner) view.findViewById(R.id.spinner_watch);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_item);


        selectedCategory = "Comp";

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position==1) {
                    selectedCategory = "Medical";
                    displayAdv();
                }else if (position==2){
                    selectedCategory = "Furniture";
                    displayAdv();
                }else if (position==3){
                    selectedCategory = "Other";
                    displayAdv();
                } else {
                    selectedCategory = "Comp";
                    displayAdv();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private void updateUI() {
        pd.dismiss();
        AdvAdapter myAdapter = new AdvAdapter(adv, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(myAdapter);
        myAdapter.setOnItemClickListener(new AdvAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(String json, int position) {
                String str = json;
                Intent intent = new Intent(getContext(), DetailsActivity.class);
                intent.putExtra("ITEM_JSON", str);
                intent.putExtra("CATEGORY", selectedCategory);
                intent.putExtra("KEY", advKeys.get(position));
                startActivity(intent);
            }
        });

    }

    private void displayAdv() {
        items = new ArrayList<>();
        keys= new ArrayList<>();
        pd = new ProgressDialog(getContext());
        pd.setMessage(getContext().getResources().getString(R.string.pd_wait));
        pd.show();
        myRef.child("Adv").child(selectedCategory).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    value = childDataSnapshot.getValue().toString();
                    items.add(value);

                    key = childDataSnapshot.getKey();
                    keys.add(key);
                }

                adv = new ArrayList<>();
                advKeys = new ArrayList<>();
                int size = items.size();
                for (int i = 0; i < size; ++i) {
                    adv.add(items.get(size-i-1));
                    advKeys.add(keys.get(size-i-1));
                }

                updateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
