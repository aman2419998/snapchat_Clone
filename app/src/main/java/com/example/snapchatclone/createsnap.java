package com.example.snapchatclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import javax.xml.transform.Result;

public class createsnap extends AppCompatActivity {


    ImageView backgroundImage;
    EditText mymessage;
    Button post;
    String Imagename = UUID.randomUUID().toString() + ".jpg";

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getPhoto();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Create Snap");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createsnap);
        backgroundImage = (ImageView) findViewById(R.id.imageView);
        mymessage = (EditText) findViewById(R.id.editTextTextPersonName3);
        post = (Button) findViewById(R.id.button3);
    }

    public void chooseimage(View view){
        if((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }else{
            getPhoto();
        }
    }

    public void getPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if(requestCode == 0 && resultCode == RESULT_OK){
           Uri selectedImage = data.getData();
           backgroundImage.setImageURI(selectedImage);

       }
    }

    public void postsnap(View view){
        post.setEnabled(false);
        backgroundImage.setDrawingCacheEnabled(true);
        backgroundImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) backgroundImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] datas = baos.toByteArray();


        final UploadTask uploadTask = FirebaseStorage.getInstance().getReference().child("Images").child(Imagename).putBytes(datas);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(createsnap.this, "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Intent intent = new Intent(getApplicationContext(),Sent_To.class);
                        intent.putExtra("imagename",Imagename);
                        intent.putExtra("imageurl",uri.toString());
                        intent.putExtra("message", mymessage.getText().toString());
                        startActivity(intent);
                    }
                });


            }
        });
    }
}
