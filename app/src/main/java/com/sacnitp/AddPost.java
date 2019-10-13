package com.sacnitp;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class AddPost extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 2;
    private static final int GALLERY_REQUEST = 9;
    String name_user;
    DatabaseReference db, db1;
    TextView textView_name;
    EditText edittext_post;
    ImageView imageView;
    Button Post;
    ImageView camera;
    Uri uri;
    StorageReference mStorageRef;
    String key;
    ProgressDialog prgd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        textView_name = findViewById(R.id.add_postusername);
        edittext_post = findViewById(R.id.edit_post);
        Post = findViewById(R.id.nextandupload);
        imageView = findViewById(R.id.profile_pic);
        camera = findViewById(R.id.camera);

        getSupportActionBar().hide();

        key = FirebaseAuth.getInstance().getUid().toString();
        getSupportActionBar().setTitle("Add Post");
        getSupportActionBar().setElevation(10);

        db1 = FirebaseDatabase.getInstance().getReference().child("Blogs").push();


        mStorageRef = FirebaseStorage.getInstance().getReference();
        db = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid().toString());
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name_user = dataSnapshot.child("Username").getValue().toString().trim();
                textView_name.setText(name_user);
                Glide.with(AddPost.this).load(dataSnapshot.child("imgurl").getValue().toString()).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               uploadFile();
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadimage();
            }
        });
    }

    private void uploadimage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            uri = data.getData();
            camera.setImageURI(uri);

        }
    }

    private void uploadFile() {
        if (uri != null&&!edittext_post.getText().toString().isEmpty()) {
            //displaying progress dialog while image is uploading
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            final StorageReference sRef = mStorageRef.child("post").child(uri.getLastPathSegment());

            sRef.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();


                            Toast.makeText(getApplicationContext(), "Post Uploaded ", Toast.LENGTH_LONG).show();

                            //creating the upload object to store uploaded image details
                            sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.e("Harsh", "onSuccess: uri= " + uri.toString());
                                    db1.child("name").setValue(name_user);
                                    db1.child("description").setValue(edittext_post.getText().toString().trim());
                                    db1.child("image").setValue(uri.toString());
                                    db1.child("UID").setValue(FirebaseAuth.getInstance().getUid());
                                    startActivity(new Intent(AddPost.this,Home.class));
                                    finish();
                                }
                            });
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        } else if(edittext_post.getText().toString().isEmpty()){
            Toast.makeText(this, "Please Write Something", Toast.LENGTH_SHORT).show();

        }
        else
        {
            db1.child("name").setValue(name_user);
            db1.child("description").setValue(edittext_post.getText().toString().trim());
            db1.child("UID").setValue(FirebaseAuth.getInstance().getUid());
            startActivity(new Intent(this,Home.class));
            finish();

        }
    }
    public void sendNotification(View view) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setContentTitle("My notification").setContentText("Hello World!").setSmallIcon(R.drawable.logosac);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(001, mBuilder.build());
    }
}
