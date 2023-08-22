package com.impax.impaxguestapp;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


import androidx.appcompat.app.AppCompatActivity;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;

import in.codeshuffle.typewriterview.TypeWriterView;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        getSupportActionBar().hide();


        TedPermission.with(this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        //getLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {

                    }
                }).setDeniedMessage("If you reject this permission, this app will not work. \n Please turn on location permissions at settings")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();

        //Create Object and refer to layout view
        TypeWriterView typeWriterView=(TypeWriterView)findViewById(R.id.typeWriterView);

        //Setting each character animation delay
        typeWriterView.setDelay(1);

        //Setting music effect On/Off
        typeWriterView.setWithMusic(false);

        //Animating Text
        typeWriterView.animateText("Welcome to impax guest app.");

        // Using handler with postDelayed called runnable run method
        new Handler().postDelayed(() -> {

            Intent i = new Intent(LoadingActivity.this,HomeActivity.class);

            startActivity(i);

            // close this activity

            finish();

        }, 8*1000); // wait for 8 seconds
    }
}