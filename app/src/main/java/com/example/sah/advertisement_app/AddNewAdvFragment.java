package com.example.sah.advertisement_app;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddNewAdvFragment extends Fragment {

    public static final String APP_PREF = "mysettings";
    public static final String APP_PREF_USER_NAME = "username";

    private SharedPreferences mSettings;
    private int PICK_IMAGE_REQUEST = 111;
    private Spinner spinnerCategory;
    private EditText et_text, et_title;
    private FirebaseAuth mAuth;
    //private DatabaseReference myRef;
    private Button btn_upl;
    private String selectedCategory;
    private Button chooseImg;
    private ImageView imgView;
    private String imgName = "";
    private Uri filePath;
    private ProgressDialog pd;
    private TextView textView;
    private String jsonAdv;
    private JSONObject jsonObj, jsonImage;
    private String jTitle, jText, jName, jImage = "";
    private LinearLayout linLay;
    private Button scale1, scale2, scale3;
    private String scaleType = "centerCrop";
    private Uri downloadUrl;


    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReferenceFromUrl("gs://advertisementapp-c96d6.appspot.com");
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReferenceFromUrl("https://advertisementapp-c96d6.firebaseio.com/");

    public AddNewAdvFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_new_adv, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinnerCategory = (Spinner) view.findViewById(R.id.spinner_add);
        et_title = (EditText) view.findViewById(R.id.et_title);
        et_text = (EditText) view.findViewById(R.id.et_text);
        btn_upl = (Button) view.findViewById(R.id.btn_upl);
        chooseImg = (Button) view.findViewById(R.id.btn_add_picture);
        imgView = (ImageView) view.findViewById(R.id.iv_img);
        textView = (TextView) view.findViewById(R.id.tv_t);
        linLay = (LinearLayout) view.findViewById(R.id.lin);

        scale1 = (Button) view.findViewById(R.id.scale1);
        scale2 = (Button) view.findViewById(R.id.scale2);
        scale3 = (Button) view.findViewById(R.id.scale3);

        scale1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                scaleType = "centerCrop";
            }
        });
        scale2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                scaleType = "fitCenter";
            }
        });
        scale3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgView.setScaleType(ImageView.ScaleType.FIT_XY);
                scaleType = "fitXY";
            }
        });

        String selected = spinnerCategory.getSelectedItem().toString();

        if (selected.equals("Медицина")) {
            selectedCategory = "Medical";
        } else {
            selectedCategory = "Comp";
        }

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    selectedCategory = "Medical";
                } else {
                    selectedCategory = "Comp";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSettings = getActivity().getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        imgName = mAuth.getCurrentUser().getUid();
        //final FirebaseUser mUser = mAuth.getInstance().getCurrentUser();

        btn_upl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkingBeforeUpload();
                uploadAdv();
            }
        });

        chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            imgView.setVisibility(View.VISIBLE);
            linLay.setVisibility(View.VISIBLE);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);

                String string = filePath.toString();
                string = string.substring(string.length() - 5, string.length());
                char[] c_arr = string.toCharArray();
                for (int i = 0; i < c_arr.length; i++) {
                    if (c_arr[i] == '/') {
                        c_arr[i] = 'o';
                    }
                }
                string = c_arr.toString();

                imgName = imgName + string;
                imgView.setImageBitmap(bitmap);

                textView.setText(imgName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void uploadAdv() {

        String title = et_title.getText().toString();
        String text = et_text.getText().toString();
        pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading....");

        Map<String, JSONObject> userMap= new HashMap<String, JSONObject>();

        userMap.put(mAuth.getCurrentUser().getUid(), jsonObj);

        //userRef .setValue(userMap);
        Advertisement adv = new Advertisement();
        //adv.setKey("1111");
//        adv.setValue(jsonObj.toString());


        if (title.equals("")) {
            Toast.makeText(getContext(), R.string.warning_title, Toast.LENGTH_SHORT).show();
        } else if (text.equals("")) {
            if (filePath != null) {
                convertContent();
                if (filePath != null) {
                    uploadImage();
                    myRef.child("Adv").child("Comp").push().setValue(jsonObj);
                }
            } else {
                Toast.makeText(getContext(), R.string.warning_text, Toast.LENGTH_SHORT).show();
            }

        } else {
            convertContent();
            myRef.child("Adv").child("Comp").push().setValue(jsonAdv);
            if (filePath != null) {
                uploadImage();
            }
        }


    }

    private void uploadImage() {

        pd.show();

        StorageReference childRef = storageRef.child("images/" + imgName + ".jpg");

        UploadTask uploadTask = childRef.putFile(filePath);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                downloadUrl = taskSnapshot.getDownloadUrl();
                Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getContext(), "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void convertContent() {



        String title = et_title.getText().toString();
        String text = et_text.getText().toString();
//        String name = mSettings.getString(APP_PREF_USER_NAME, "");
        String name ="Yo yo";
        if (filePath != null) {

        }
        jsonObj = new JSONObject();
        jsonImage = new JSONObject();
        try {
            jsonObj.put("author", name);
                    jsonObj.put("title", title);
                    jsonObj.put("text", text);
            if (filePath != null) {
                jsonImage.put("imageName", imgName + ".jpg");
                jsonImage.put("scaleType", scaleType);
                jsonObj.put("image", jsonImage);
            }else {
                jsonObj.put("image", "");
            }
        } catch (JSONException e) {
            // Handle impossible error
            e.printStackTrace();
        }
        jsonAdv = jsonObj.toString();
//        char c = '"';
//        if (filePath != null) {
//            jImage = "{"
//                    + c + "imageName" + c + ":" + c + imgName + ".jpg" + c + ","
//                    + c + "scaleType" + c + ":" + c + scaleType + c  + "}";
//        }
//        jsonAdv = "{"
//                + c + "author" + c + ":" + c + name + c + ","
//                + c + "title" + c + ":" + c + title + c + ","
//                + c + "text" + c + ":" + c + text + c + ","
//                + c + "image" + c + ":" + c + jImage + c + "}";
//
//        try {
//            jsonObj = new JSONObject(jsonAdv);
//            jTitle = jsonObj.getString("title");
//            jText = jsonObj.getString("text");
//            jName = jsonObj.getString("author");
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        textView.setText(jsonAdv.toString());


    }
}
