package com.example.snapchatclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MyMessages extends AppCompatActivity {

    FirebaseAuth mAuth;
    ListView mylist;
    ArrayList<String> emails = new ArrayList<String>();
    ArrayList<DataSnapshot> snapshots = new ArrayList<DataSnapshot>();
    ArrayAdapter<String> arrayAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.snap){
            Intent intent = new Intent(this,createsnap.class);
            startActivity(intent);
        }else if(item.getItemId() == R.id.logout){
            mAuth.signOut();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Received Snaps.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_messages);
        mAuth = FirebaseAuth.getInstance();
        mylist = (ListView) findViewById(R.id.mylistview);
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,emails);
        mylist.setAdapter(arrayAdapter);

        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("snaps").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String fromUser = snapshot.child("from").getValue().toString();
                emails.add(fromUser);
                snapshots.add(snapshot);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                int index = 0;
                for(DataSnapshot snaps: snapshots){

                    if(snaps.getKey().equals(snapshot.getKey())){
                        snapshots.remove(index);
                        emails.remove(index);

                    }else{
                        index++;
                    }
                }
                arrayAdapter.notifyDataSetChanged();
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
                Intent intent = new Intent(getApplicationContext(),viewSnap.class);
                intent.putExtra("message",snapshots.get(i).child("message").getValue().toString());
                intent.putExtra("imageurl",snapshots.get(i).child("ImageUrl").getValue().toString());
                intent.putExtra("imagename",snapshots.get(i).child("ImageName").getValue().toString());
                intent.putExtra("snapkey",snapshots.get(i).getKey());
                startActivity(intent);
            }
        });
    }


}