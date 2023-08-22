package com.impax.impaxguestapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easywaylocation.EasyWayLocation;
import com.example.easywaylocation.Listener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class HomeActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, Listener {
    BottomNavigationView bottomNavigation;
    EasyWayLocation easyWayLocation;
    private TextView location, latLong, diff;
    private Double lati, longi;
    private boolean mBound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();

        loadFragment(new HomeFragment());

        easyWayLocation = new EasyWayLocation(this, false,false,this);

        bottomNavigation = findViewById(R.id.navigationView);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    loadFragment(new HomeFragment());
                    return true;
                case R.id.navigation_capture:
                    loadFragment(new CaptureFragment());
                    return true;

            }
            return false;
        });

        //getPermissionsAndLocation();
    }

    private void logout()
    {
        SweetAlertDialog dialog = new SweetAlertDialog(HomeActivity.this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Logout?")
                .setContentText("Want to Logout?")
                .setConfirmText("Yes")
                .setCancelText("Cancel");

        dialog.setConfirmClickListener(sweetAlertDialog -> {
            GoogleSignIn.getClient(getApplicationContext(), GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();
            SharedPrefManager.getInstance(getApplicationContext()).logout();
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        });
        dialog.show();
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        //mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i("TAG", "Google Places API connected.");

    }

    @Override
    public void onConnectionSuspended(int i) {
        //mPlaceArrayAdapter.setGoogleApiClient(null);
        //Log.e(TAG, "Google Places API connection suspended.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        /*Log.e(TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());*/

       /* Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();*/

    }


    @Override
    public void locationOn() {

    }

    @Override
    public void currentLocation(Location location) {
        //Toasty.info(getApplicationContext(),String.valueOf(location.getLatitude())+","+String.valueOf(location.getLongitude()),Toast.LENGTH_LONG,false).show();
        /*SharedPrefManager.getInstance(getApplicationContext()).saveDeviceLatitude(String.valueOf(location.getLatitude()));
        SharedPrefManager.getInstance(getApplicationContext()).saveDeviceLng(String.valueOf(location.getLongitude()));*/
        SharedPrefManager.getInstance(getApplicationContext()).saveDeviceLatitude("1.2");
        SharedPrefManager.getInstance(getApplicationContext()).saveDeviceLng("3.6");

    }

    @Override
    public void locationCancelled() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        easyWayLocation.startLocation();
    }
}