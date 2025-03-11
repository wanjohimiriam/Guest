package com.impax.impaxguestapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigation;
    private TextView location, latLong, diff;
    private Double lati, longi;
    private boolean mBound = false;
    private CustomLocationHandler locationHandler;
    private static final int FILE_SELECT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();

        loadFragment(new HomeFragment());

        locationHandler = new CustomLocationHandler(this, this);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> openFileChooser());

        bottomNavigation = findViewById(R.id.navigationView);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.navigation_capture) {
                selectedFragment = new CaptureFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }

            return false;
        });

        locationHandler.setLocationListener(new CustomLocationHandler.LocationListener() {
            @Override
            public void locationOn() {
                Log.d("Location", "Location is enabled");
            }

            @Override
            public void locationCancelled() {
                Log.d("Location", "Location request cancelled");
            }

            @Override
            public void locationChanged(Location location) {
                Log.d("Location", "Location updated: " + location.getLatitude() + ", " + location.getLongitude());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            locationHandler.startLocationUpdates();
        } catch (Exception e) {
            Log.e("LocationError", "Error starting location: " + e.getMessage());
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/csv");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select a CSV File"), FILE_SELECT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK) {
            Uri fileUri = data.getData();
            if (fileUri != null) {
                uploadCSV(fileUri);
            } else {
                Toasty.error(this, "File selection failed", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 1001) { // REQUEST_CHECK_SETTINGS
            if (resultCode == Activity.RESULT_OK) {
                locationHandler.startLocationUpdates();
            } else {
                Log.d("Location", "User declined to enable location");
            }
        }
    }

//    private void uploadCSV(Uri fileUri) {
//        String uploadUrl = Constants.UPLOAD;
//
//        try {
//            // Create a VolleyMultipartRequest instead of StringRequest
//            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(
//                    Request.Method.POST, uploadUrl,
//                    response -> Toasty.success(this, "CSV Uploaded Successfully!", Toast.LENGTH_SHORT).show(),
//                    error -> {
//                        Toasty.error(this, "Upload Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//                        Log.e("Upload Error", error.toString());
//                        Log.d("Upload", "URL: " + uploadUrl);
//
//                    }) {
//                @Override
//                protected Map<String, DataPart> getByteData() {
//                    Map<String, DataPart> params = new HashMap<>();
//                    try {
//                        InputStream inputStream = getContentResolver().openInputStream(fileUri);
//                        if (inputStream == null) {
//                            Log.e("Upload Error", "File input stream is null");
//                            return params;
//                        }
//
//                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                        byte[] buffer = new byte[1024];
//                        int length;
//                        while ((length = inputStream.read(buffer)) != -1) {
//                            byteArrayOutputStream.write(buffer, 0, length);
//                        }
//                        inputStream.close();
//                        byte[] fileBytes = byteArrayOutputStream.toByteArray();
//
//                        // Get file name from the URI if possible
//                        String fileName = "guests.csv";
//                        Cursor returnCursor = getContentResolver().query(fileUri, null, null, null, null);
//                        if (returnCursor != null) {
//                            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//                            returnCursor.moveToFirst();
//                            fileName = returnCursor.getString(nameIndex);
//                            returnCursor.close();
//                            Log.d("Upload", "File size: " + fileBytes.length + " bytes");
//                            Log.d("Upload", "File name: " + fileName);
//                        }
//
//                        params.put("file", new DataPart(fileName, fileBytes));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        Log.e("Upload Error", "File reading error: " + e.getMessage());
//                    }
//                    return params;
//                }
//
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    Map<String, String> headers = new HashMap<>();
//                    // Add any required headers here
//                    return headers;
//                }
//            };
//
//            // Set timeout for larger files
//            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
//                    30000,  // 30 seconds timeout
//                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//            RequestHandler.getInstance(this).addToRequestQueue(multipartRequest);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toasty.error(this, "File Upload Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
private void uploadCSV(Uri fileUri) {
    String uploadUrl = Constants.UPLOAD;

    // First log the request details
    Log.d("Upload", "Starting upload to URL: " + uploadUrl);

    try {
        // Create a custom request with detailed error handling
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(
                Request.Method.POST, uploadUrl,
                response -> {
                    // Parse success response if needed
                    String responseData = "";
                    try {
                        responseData = new String(response.data, "UTF-8");
                        Log.d("Upload", "Success response: " + responseData);
                    } catch (UnsupportedEncodingException e) {
                        Log.e("Upload", "Error parsing response: " + e.getMessage());
                    }
                    Toasty.success(this, "CSV Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    // Get detailed error information
                    NetworkResponse networkResponse = error.networkResponse;
                    String errorMessage = "Unknown error";
                    String responseBody = "";

                    if (networkResponse != null && networkResponse.data != null) {
                        try {
                            responseBody = new String(networkResponse.data, "UTF-8");
                            Log.e("Upload Error", "Error Status Code: " + networkResponse.statusCode);
                            Log.e("Upload Error", "Error Response Body: " + responseBody);

                            // Show a more user-friendly error
                            if (responseBody.contains("Header with name") && responseBody.contains("was not found")) {
                                errorMessage = "The CSV file is missing required columns. Please ensure your file has the following columns: Id, Name, Email, PhoneNo, Company, Designation, and UpdatedAt.";
                            } else {
                                errorMessage = "Server Error: " + responseBody.substring(0, Math.min(responseBody.length(), 100));
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        Log.e("Upload Error", "No NetworkResponse: " + error.toString());
                    }

                    Toasty.error(this, "Upload Failed: " + errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("Upload Error", error.toString());
                }) {
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(fileUri);
                    if (inputStream == null) {
                        Log.e("Upload Error", "File input stream is null");
                        return params;
                    }

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, length);
                    }
                    inputStream.close();
                    byte[] fileBytes = byteArrayOutputStream.toByteArray();

                    // Get file name from the URI if possible
                    String fileName = "guests.csv";
                    Cursor returnCursor = getContentResolver().query(fileUri, null, null, null, null);
                    if (returnCursor != null) {
                        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        returnCursor.moveToFirst();
                        fileName = returnCursor.getString(nameIndex);
                        returnCursor.close();
                    }

                    Log.d("Upload", "File size: " + fileBytes.length + " bytes");
                    Log.d("Upload", "File name: " + fileName);

                    // Log first few bytes for debugging
                    String filePreview = new String(fileBytes, 0, Math.min(fileBytes.length, 100), "UTF-8");
                    Log.d("Upload", "File preview: " + filePreview);

                    params.put("file", new DataPart(fileName, fileBytes, "text/csv"));
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("Upload Error", "File reading error: " + e.getMessage());
                }
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // Add any metadata the server might need
                params.put("fileName", "guests.csv");
                params.put("fileType", "csv");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // Add any specific headers the server might expect
                // headers.put("Accept", "application/json");
                return headers;
            }
        };

        // Set timeout for larger files
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,  // 60 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestHandler.getInstance(this).addToRequestQueue(multipartRequest);

    } catch (Exception e) {
        e.printStackTrace();
        Toasty.error(this, "File Upload Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
}

    @Override
    protected void onPause() {
        super.onPause();
        locationHandler.stopLocationUpdates();
    }

    public void loadFragment(Fragment fragment) {
        if (fragment == null) return;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }
}



//    private void logout()
//    {
//        SweetAlertDialog dialog = new SweetAlertDialog(HomeActivity.this, SweetAlertDialog.NORMAL_TYPE)
//                .setTitleText("Logout?")
//                .setContentText("Want to Logout?")
//                .setConfirmText("Yes")
//                .setCancelText("Cancel");
//
//        dialog.setConfirmClickListener(sweetAlertDialog -> {
//            GoogleSignIn.getClient(getApplicationContext(), GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();
//            SharedPrefManager.getInstance(getApplicationContext()).logout();
//            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
//            startActivity(intent);
//            finish();
//        });
//        dialog.show();
//    }




//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//
//        //mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
//        Log.i("TAG", "Google Places API connected.");
//
//    }


//    @Override
//    public void onConnectionSuspended(int i) {
//        //mPlaceArrayAdapter.setGoogleApiClient(null);
//        //Log.e(TAG, "Google Places API connection suspended.");
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//        /*Log.e(TAG, "Google Places API connection failed with error code: "
//                + connectionResult.getErrorCode());*/
//
//       /* Toast.makeText(this,
//                "Google Places API connection failed with error code:" +
//                        connectionResult.getErrorCode(),
//                Toast.LENGTH_LONG).show();*/
//
//    }


//    @Override
//    public void locationOn() {
//
//    }

//    @Override
//    public void currentLocation(Location location) {
//        //Toasty.info(getApplicationContext(),String.valueOf(location.getLatitude())+","+String.valueOf(location.getLongitude()),Toast.LENGTH_LONG,false).show();
//        /*SharedPrefManager.getInstance(getApplicationContext()).saveDeviceLatitude(String.valueOf(location.getLatitude()));
//        SharedPrefManager.getInstance(getApplicationContext()).saveDeviceLng(String.valueOf(location.getLongitude()));*/
//        SharedPrefManager.getInstance(getApplicationContext()).saveDeviceLatitude("1.2");
//        SharedPrefManager.getInstance(getApplicationContext()).saveDeviceLng("3.6");
//
//    }
//
//    @Override
//    public void locationCancelled() {
//
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        try {
//            // Your existing code with EasyWayLocation
//            easyWayLocation.startLocation();
//        } catch (Exception e) {
//            Log.e("LocationError", "Error starting location: " + e.getMessage());
//            // Provide fallback behavior or show a notification to the user
//        }
//    }
