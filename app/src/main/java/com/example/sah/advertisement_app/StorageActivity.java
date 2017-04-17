package com.example.sah.advertisement_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sah.advertisement_app.auth.AuthActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class StorageActivity extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 111;

    private Button chooseImg, uploadImg;
    private ImageView imgView;
    private Uri filePath;
    private ProgressDialog pd;
    private TextView textView;
    private String imgName = "";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //creating reference to firebase storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://advertisementapp-c96d6.appspot.com");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        chooseImg = (Button)findViewById(R.id.btn_add_picture);
        uploadImg = (Button)findViewById(R.id.btn_upl);
        imgView = (ImageView)findViewById(R.id.iv_piture);
        textView = (TextView) findViewById(R.id.tv_text);

        pd = new ProgressDialog(this);
        pd.setMessage("Uploading....");


//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    //imgName = user.getDisplayName();
//
//                } else {
//                    // User is signed out
//
//                }
//
//            }
//        };
        mAuth = FirebaseAuth.getInstance();
        imgName = mAuth.getCurrentUser().getUid();
        textView.setText(imgName);

        chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            }
        });

        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(filePath != null) {
                    pd.show();

                    //StorageReference childRef = storageRef.child("images/image.jpg");
                    StorageReference childRef = storageRef.child("images/" + imgName + ".jpg");

                    //uploading the image
                    UploadTask uploadTask = childRef.putFile(filePath);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pd.dismiss();
                            Toast.makeText(StorageActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(StorageActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    Toast.makeText(StorageActivity.this, "Select an image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();


            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                //Setting image to ImageView
                String string = filePath.toString();
                string = string.substring(string.length()-5, string.length());
                char[] c_arr = string.toCharArray();
                for (int i = 0; i <  c_arr.length; i++) {
                    if (c_arr[i] == '/'){
                        c_arr[i] = 'o';
                    }
                }
                string = c_arr.toString();

                imgName = imgName + string;
                imgView.setImageBitmap(bitmap);


                //textView.setText(imgName);
                textView.setText(imgName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

