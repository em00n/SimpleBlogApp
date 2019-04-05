package com.emon.simpleblogapp;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class UpDeActivity extends AppCompatActivity {
    EditText discripET, titleET;
    Button updateBtn, deletBtn;
    ImageView imageView;
    ProgressDialog pd;

    private static final int PICK_IMAGE_REQUEST = 2;
    Uri filepath;

    FirebaseStorage storage;
    StorageReference storageReference;


    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private DatabaseReference mDatabaseUsers;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;

    String muid;
    String preUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_de);
        mAuth = FirebaseAuth.getInstance();

        discripET = findViewById(R.id.discripET);
        titleET = findViewById(R.id.titleET);
        imageView = findViewById(R.id.picupde);
        updateBtn = findViewById(R.id.updateBTN);
        deletBtn = findViewById(R.id.deletBTN);
        final String selectedkey = getIntent().getStringExtra("selectedkey");
        String discrip = getIntent().getStringExtra("disc");
        String title = getIntent().getStringExtra("title");
        String image = getIntent().getStringExtra("url");
        preUid = getIntent().getStringExtra("uid");


        discripET.setText(discrip);
        titleET.setText(title);
        Picasso.get().load(image).into(imageView);



        muid = mAuth.getCurrentUser().getUid();

        //firebase
        FirebaseApp.initializeApp(this);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("EMON");
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if (mAuth.getCurrentUser().getUid().equals(preUid)) {

            updateBtn.setVisibility(View.VISIBLE);
            deletBtn.setVisibility(View.VISIBLE);
            imageView.setClickable(true);
        } else {
            updateBtn.setVisibility(View.INVISIBLE);
            deletBtn.setVisibility(View.INVISIBLE);
            imageView.setClickable(false);
        }

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (filepath != null) {
                    pd=new ProgressDialog(UpDeActivity.this);
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



                            databaseReference
                                    .child(selectedkey)
                                    .setValue(new Post(titleET.getText().toString(), discripET.getText().toString(), muid, sdownload_url))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            clear();

                                            finish();
                                            pd.dismiss();
                                            Toast.makeText(UpDeActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UpDeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpDeActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });


                }


            }
        });

        deletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference
                        .child(selectedkey)
                        .removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                clear();
                                Intent intent = new Intent(UpDeActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(UpDeActivity.this, "delete", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpDeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


    }

    public void clear() {
        discripET.setText("");
        titleET.setText("");
        imageView.setImageURI(null);
    }

    public void choose(View view) {
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
