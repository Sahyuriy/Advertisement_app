package com.example.sah.advertisement_app;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sah.advertisement_app.auth.AuthActivity;
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
    private ListView listView;
    private String selectedCategory;
    private ArrayList<String> items;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText etEmail, etPass;
    private Button btnAuth, btnReg;
    private FirebaseUser user;

    private List<String> mList;
    private TextView textView;
    private FirebaseListAdapter mAdater;
    private String value;
    private RecyclerView recyclerView;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReferenceFromUrl("https://advertisementapp-c96d6.firebaseio.com/");


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
        listView = (ListView) view.findViewById(R.id.lv_item);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_item);

        selectedCategory = "Comp";

//        String selected = spinner.getSelectedItem().toString();
//
//        if (selected.equals("Медицина")){
//            selectedCategory = "Medical";
//        }else if (){
//
//        }
//        else {
//            selectedCategory = "Comp";
//        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position==1){
                    selectedCategory = "Medical";
                    displayAdv();
                }else if (position==2){
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

        AdvAdapter myAdapter = new AdvAdapter(items, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(myAdapter);
        myAdapter.setOnItemClickListener(new AdvAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(String json) {
                //Toast.makeText(getContext(), json, Toast.LENGTH_SHORT).show();
                String str = json;
                Intent intent = new Intent(getContext(), DetailsActivity.class);
                intent.putExtra("ITEM_JSON", str);
                startActivity(intent);
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragm_details, new DetailsFragment(json))
//                        .commit();
            }
        });


//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_list_item_1, items);
//        listView.setAdapter(adapter);
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
                updateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
