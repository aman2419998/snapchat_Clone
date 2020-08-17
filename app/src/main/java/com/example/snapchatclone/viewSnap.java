package com.example.snapchatclone;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class viewSnap extends AppCompatActivity {

    TextView mytext;
    ImageView myimage;

    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            URL url;
            HttpURLConnection httpURLConnection;
            try {
               url = new URL(strings[0]);
               httpURLConnection = (HttpURLConnection) url.openConnection();
               httpURLConnection.connect();
               InputStream in = httpURLConnection.getInputStream();
               Bitmap mymap = BitmapFactory.decodeStream(in);
               return mymap;
            } catch (Exception e) {
                Log.e("Error", e.getMessage());

                e.printStackTrace();
                return  null;
            }


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Snap View");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_snap);
        mytext = (TextView) findViewById(R.id.textView);
        myimage = (ImageView) findViewById(R.id.imageView2);
        mytext.setText(getIntent().getStringExtra("message"));


        DownloadImage task = new DownloadImage();
        try {
            Bitmap mymap = task.execute(getIntent().getStringExtra("imageurl").toString()).get();
            myimage.setImageBitmap(mymap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("snaps").child(getIntent().getStringExtra("snapkey")).removeValue();
        FirebaseStorage.getInstance().getReference().child("Images").child(getIntent().getStringExtra("imagename")).delete();
    }
}