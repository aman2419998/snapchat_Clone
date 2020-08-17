package com.example.snapchatclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Sent_To extends AppCompatActivity {

    ListView mylist;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> emails = new ArrayList<String>();
    ArrayList<String> Usersid = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Select User");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent__to);
        mylist = (ListView) findViewById(R.id.mylistview);
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,emails);
        mylist.setAdapter(arrayAdapter);

        FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String emailAddress = snapshot.child("email").getValue().toString();
                emails.add(emailAddress);
                Usersid.add(snapshot.getKey());
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap<String,String> mymap = new HashMap<String,String>();
                mymap.put("from", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                mymap.put("ImageName",getIntent().getStringExtra("imagename"));
                mymap.put("ImageUrl",getIntent().getStringExtra("imageurl"));
                mymap.put("message",getIntent().getStringExtra("message"));


                FirebaseDatabase.getInstance().getReference().child("users").child(Usersid.get(i)).child("snaps").push().setValue(mymap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Intent intent = new Intent(getApplicationContext(),MyMessages.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }
}