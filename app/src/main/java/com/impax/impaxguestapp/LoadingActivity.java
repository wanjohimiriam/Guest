package com.impax.impaxguestapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        getSupportActionBar().hide();

        // Reference the custom TypewriterTextView
        TypewriterTextView typeWriterView = findViewById(R.id.typeWriterView);
        typeWriterView.setCharacterDelay(100); // Set delay between characters
        typeWriterView.animateText("Welcome to Impax Guest App.");

        // Delay before navigating to HomeActivity
        new Handler().postDelayed(() -> {
            Intent i = new Intent(LoadingActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        }, 5000); // 5 seconds delay
    }
}


//package com.impax.impaxguestapp;
//
//import android.Manifest;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.gun0912.tedpermission.PermissionListener;
//import com.gun0912.tedpermission.rx2.TedPermission;
//
//import java.util.List;
//
//
//public class LoadingActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_loading);
//
//        getSupportActionBar().hide();
//
//
//        TedPermission.create()
//                .setPermissionListener(new PermissionListener() {
//                    @Override
//                    public void onPermissionGranted() {
//                        //getLocationUpdates();
//                    }
//
//                    @Override
//                    public void onPermissionDenied(List<String> deniedPermissions) {
//
//                    }
//                }).setDeniedMessage("If you reject this permission, this app will not work. \n Please turn on location permissions at settings")
//                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
//                .setPermissions();
//
//        TypeWriterView typeWriterView = findViewById(R.id.typeWriterView);
//        typeWriterView.setDelay(1);
//        typeWriterView.setWithMusic(false);
//        typeWriterView.animateText("Welcome to Impax Guest App.");
//
//        // Using Handler with postDelayed
//        new Handler().postDelayed(() -> {
//            Intent i = new Intent(LoadingActivity.this, HomeActivity.class);
//            startActivity(i);
//            finish();
//        }, 8000); // 8 seconds
//    }
//}

        //Create Object and refer to layout view
//        TypeWriterView typeWriterView=(TypeWriterView)findViewById(R.id.typeWriterView);
//
//        //Setting each character animation delay
//        typeWriterView.setDelay(1);
//
//        //Setting music effect On/Off
//        typeWriterView.setWithMusic(false);
//
//        //Animating Text
//        typeWriterView.animateText("Welcome to impax guest app.");
//
//        // Using handler with postDelayed called runnable run method
//        new Handler().postDelayed(() -> {
//
//            Intent i = new Intent(LoadingActivity.this,HomeActivity.class);
//
//            startActivity(i);
//
//            // close this activity
//
//            finish();
//
//        }, 8*1000); // wait for 8 seconds
//    }
//}