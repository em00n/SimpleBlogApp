package com.emon.simpleblogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AddData_Activity extends AppCompatActivity {
    EditText titleET, discripET;
    Button addBTN, showBTN;
    ProgressDialog pd;
    String useruid;
    String title;
    String discrip;

    private static final int PICK_IMAGE_REQUEST = 21;
    ImageView imageView;
    Uri filepath;

    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    private FirebaseUser mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adddata);

        titleET = findViewById(R.id.titleEDT);
        discripET = findViewById(R.id.discripEDT);
        addBTN = findViewById(R.id.addBTN);
        showBTN = findViewById(R.id.showBTN);

        imageView = findViewById(R.id.picET);
        FirebaseApp.initializeApp(this);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("EMON");

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        //mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        useruid = mAuth.getCurrentUser().getUid();

        addBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addData();


            }
        });

        showBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  startActivity(new Intent(AddData_Activity.this, MainActivity.class));
                finish();
            }
        });

    }


    //add data to database
    private void addData() {

        title = titleET.getText().toString();
        discrip = discripET.getText().toString();
        if (filepath != null) {
            pd=new ProgressDialog(AddData_Activity.this);
            pd.setTitle("Please wait");
            pd.setMessage("Loading....");
            pd.show();
            StorageReference reference = storageReference.child("images/" + UUID.randomUUID().toString());
            reference.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful()) ;
                    Uri downloadUrl = urlTask.getResult();

                    final String sdownload_url = String.valueOf(downloadUrl);


//                    dataReference.push()  //use this method to creat unik id
//                            .setValue(sdownload_url);
                    if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(discrip)) {
                        Toast.makeText(AddData_Activity.this, "add", Toast.LENGTH_SHORT).show();
                        Post post = new Post(title, discrip, useruid, sdownload_url);
                        databaseReference.push()  //use this method to creat unik id
                                .setValue(post);

                       // startActivity(new Intent(AddData_Activity.this, MainActivity.class));
                        clear();
                        pd.dismiss();
                        finish();


                    } else {
                        Toast.makeText(AddData_Activity.this, "enter values", Toast.LENGTH_SHORT).show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddData_Activity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });


        }


        // adapter.notifyDataSetChanged();
    }

    public void clear() {
        titleET.setText("");
        discripET.setText("");
        imageView.setImageURI(null);
    }

    public void choosePhoto(View view) {
        chooseImg();
    }

    private void chooseImg() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            try {
                filepath = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
