package com.sacnitp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class image extends AppCompatActivity {
private String url;
ImageView myImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        url = getIntent().getStringExtra("Image");
        myImage=(ImageView)findViewById(R.id.payal);
        Log.e("harsh",url);
        getSupportActionBar().hide();
        Glide.with(image.this).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(myImage);


    }
}
