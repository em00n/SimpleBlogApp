package com.emon.simpleblogapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    Button addBTN, updateBTN;
    RecyclerView recyclerView;

//firebase

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseRecyclerOptions<Post> option;
    FirebaseRecyclerAdapter<Post, MyRecyclerViewHolder> adapter;


    private DatabaseReference mDatabaseUsers;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;

    String muid;
    String preUid;

    Post selectedpost;
    String selectedkey;

    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        //recyclerview
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


//        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
//        mCurrentUser = mAuth.getCurrentUser();
//        muid=mCurrentUser.getUid();
        muid = mAuth.getCurrentUser().getUid();
        //firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("EMON");
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        showData();
    }


    @Override
    protected void onStop() {
        if (adapter != null)
            adapter.stopListening();
        super.onStop();
    }

    @Override
    protected void onStart() {
        showData();
        super.onStart();
    }

    //show data
    private void showData() {
        option = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(databaseReference, Post.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Post, MyRecyclerViewHolder>(option) {
            @Override
            protected void onBindViewHolder(@NonNull MyRecyclerViewHolder holder, int position, @NonNull final Post model) {
                holder.titleTV.setText(model.getTitle());
                holder.discripTV.setText(model.getDiscrip());
                Picasso.get().load(model.getUrl()).into(holder.imageView);
                holder.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public void onLongClick(View view, int position) {
                        preUid = model.getUid();
                        selectedkey = getSnapshots().getSnapshot(position).getKey();
                        // updateDelet(position);
                        String disc = model.getDiscrip();
                        String title = model.getTitle();
                        String url = model.getUrl();
                        Intent intent = new Intent(MainActivity.this, UpDeActivity.class);
                        intent.putExtra("selectedkey", selectedkey);
                        intent.putExtra("disc", disc);
                        intent.putExtra("title", title);
                        intent.putExtra("uid", preUid);
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }

//                            @Override
//                            public void onClick(View view, int position) {
//
//                            }
                });
            }

            @NonNull
            @Override
            public MyRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_post, parent, false);
                return new MyRecyclerViewHolder(itemView);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    public void addbutton(View view) {
        Intent intent = new Intent(MainActivity.this, AddData_Activity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu1:
                FirebaseAuth.getInstance().signOut();
                finish();
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
           finish();
        } else {
            Toast.makeText(this, "Press back again to finish", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();
    }


}
