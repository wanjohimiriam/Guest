package com.impax.impaxguestapp;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class CaptureFragment extends Fragment {
    View view;
    private EditText nameT,emailT,companyT,pnumberT,designationT;

    private Button buttonSubmit;
    private Button addSubmit;
    private Button confirmSubmit;
    private ImageView search_img;
    ArrayList<String> items=new ArrayList<>();
    private SpinnerDialog spinnerDialog;
    private JSONArray item_visitors;
    private ArrayList<String>full_names;
    private ArrayList<String>company_name;
    private ArrayList<String>email;
    private ArrayList<String>pnumber;
    private ArrayList<String>designation;
    private Timer timer;

    private ListView listview;

    private ArrayAdapter<String> adapter;

    private List<String> allItems;
    private List<String> displayedItems;
    private TextView textViewDownloadCSV;
    private static final int PERMISSION_REQUEST_CODE = 100;




    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.capture_fragment, container, false);

        nameT = view.findViewById(R.id.nameT);


        emailT = view.findViewById(R.id.emailT);
        companyT = view.findViewById(R.id.companyT);
        pnumberT = view.findViewById(R.id.pnumberT);
        designationT = view.findViewById(R.id.designationT);
        addSubmit= view.findViewById(R.id.buttonNew);
        confirmSubmit = view.findViewById(R.id.buttonConfirm);
        search_img = view.findViewById(R.id.search_img);
        listview= view.findViewById(R.id.listViewResults);
        //full names
        full_names= new ArrayList<>();
        //id = new ArrayList<>();
        company_name = new ArrayList<>();
        email = new ArrayList<>();
        pnumber = new ArrayList<>();
        designation = new ArrayList<>();

        addSubmit.setOnClickListener(v -> post());
        confirmSubmit.setOnClickListener(v -> post());
        allItems = new ArrayList<>();
        displayedItems = new ArrayList<>(allItems);

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, displayedItems);
        listview.setAdapter(adapter);
        spinnerDialog=new SpinnerDialog(getActivity(),full_names,"Search",R.style.MyMaterialTheme,"Close");// With 	Animation

        spinnerDialog.setCancellable(true); // for cancellable
        spinnerDialog.setShowKeyboard(false);// for open keyboard by default
        textViewDownloadCSV = view.findViewById(R.id.tv_download_csv);
        textViewDownloadCSV.setOnClickListener(v -> {
            File downloadedFile = new File(requireContext().getExternalFilesDir(null), "yourfile.csv");
            downloadCSV();
        });




        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position)
            {
                nameT.setText(item);
                companyT.setText(company_name.get(position));
                emailT.setText(email.get(position));
                pnumberT.setText(pnumber.get(position));
                designationT.setText(designation.get(position));
            }
        });
        view.findViewById(R.id.search_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerDialog.showSpinerDialog();
            }
        });
        loadspinner_visitors();
        return view;


    }

    private void downloadCSV() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10+, use MediaStore or app-specific storage
            startDownload();
        } else {
            // For older Android versions, check permission
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                return;
            }
            startDownload();
        }
    }


    private void startDownload() {
        new Thread(() -> {
            try {
                String fileURL = Constants.DOWNLOAD;
                Log.d("Download", "URL: " + fileURL);
                URL url = new URL(fileURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.setConnectTimeout(15000); // Set timeouts
                connection.setReadTimeout(15000);
                connection.connect();


                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                    Log.d("Download", "Download successful!");

                    // Different file handling for Android 10+
                    File downloadFile;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // Use app-specific directory for Android 10+
                        File appDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                        downloadFile = new File(appDir, "GuestsTemplate.csv");
                    } else {
                        // Use public directory for older versions
                        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                        if (!downloadDir.exists()) {
                            downloadDir.mkdirs(); // Create directory if it doesn't exist
                        }
                        downloadFile = new File(downloadDir, "GuestsTemplate.csv");
                    }

                    FileOutputStream outputStream = new FileOutputStream(downloadFile);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    outputStream.flush();
                    outputStream.close();
                    inputStream.close();

                    requireActivity().runOnUiThread(() ->
                            Snackbar.make(requireView(), "Excel Downloaded Successfully", Snackbar.LENGTH_LONG)
                                    .setAction("OPEN", v -> {

                                        Uri fileUri = FileProvider.getUriForFile(
                                                requireContext(),
                                                requireContext().getPackageName() + ".provider",
                                                downloadFile
                                        );

                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setDataAndType(fileUri, "text/csv"); // Change MIME type based on file type
                                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        startActivity(intent);
                                    }).show());

//
                } else {
                    final int responseCode = connection.getResponseCode(); // This line throws IOException
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), "Download Failed: Server Error Code " +
                                    responseCode, Toast.LENGTH_LONG).show()
                    );
                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getActivity(), "Download Failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                startDownload();
            } else {
                Toast.makeText(getActivity(), "Storage Permission Denied", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void loadspinner_visitors() {
        StringRequest loadSpinnerTools = new StringRequest(Request.Method.GET,Constants.DISPLAY_GUESTS , response -> {
            JSONObject json = null;

            try {
                item_visitors = new JSONArray(response);

                getRankSubLocations(item_visitors);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> System.out.println("Network error.")){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<>();
                return params;
            }
        };
        RequestHandler.getInstance(getContext()).addToRequestQueue(loadSpinnerTools);
    }

    private void getRankSubLocations(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject o = array.getJSONObject(i);
                full_names.add(o.getString(Bconfig.TAG_FULL_NAMES));
                pnumber.add(o.getString(Bconfig.TAG_PNUMBER));
                company_name.add(o.getString(Bconfig.TAG_COMPANY_NAME));
                email.add(o.getString(Bconfig.TAG_EMAIL));
                designation.add(o.getString(Bconfig.TAG_DESIGNATION));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void post()
    {
       // Log.d("debugging", "post: ");
        if(nameT.getText().toString().equals(""))
        {
            nameT.setError("This field is required");
            nameT.requestFocus();
        }
        else if(companyT.getText().toString().equals(""))
        {
            companyT.setError("This field is required");
            companyT.requestFocus();
        }
        else if(emailT.getText().toString().equals(""))
        {
            emailT.setError("This field is required");
            emailT.requestFocus();
        }
        else if(pnumberT.getText().toString().equals(""))
        {
            pnumberT.setError("This field is required");
            pnumberT.requestFocus();
        }
        else if(designationT.getText().toString().equals(""))
        {
            designationT.setError("This field is required");
            designationT.requestFocus();
        }
        else
        {
            final String name = nameT.getText().toString().trim().toUpperCase();
            final String company = companyT.getText().toString().trim().toUpperCase();
            final String email = emailT.getText().toString().trim();
            final String pnumber = pnumberT.getText().toString().trim();
            final String designation = designationT.getText().toString().trim();

            sendToDB(name,company,email,pnumber,designation);

        }
    }

    private void sendToDB(String name, String company, String email, String pnumber, String designation) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("name", name);
            postData.put("email", email);
            postData.put("company", company);
            postData.put("phoneNo", pnumber);
            postData.put("designation", designation);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                Constants.POST_GUEST,
                postData,
                response -> {
                    Log.d("API Response", response.toString()); // Debugging: Print full response

                    if (response.has("message")) {
                        String message = response.optString("message", "No message found");
                        Log.d("API Response", "Message: " + message);

                        requireActivity().runOnUiThread(() -> {
                            if ("success".equalsIgnoreCase(message)) {
                                Toasty.success(requireActivity(), "Check-in Successful!", Toast.LENGTH_LONG, true).show();
                            } else {
                                Toasty.error(requireActivity(), "Check-in Failed. Try again!", Toast.LENGTH_LONG, true).show();
                            }
                        });
                    } else {
                        Log.e("JSON Error", "Key 'message' not found in response.");
                        requireActivity().runOnUiThread(() ->
                                Toasty.warning(requireActivity(), "Unexpected response format", Toast.LENGTH_LONG, true).show()
                        );
                    }
                },
                error -> {
                    Log.e("API Error", "Request Failed: " + error.toString());
                    Toasty.info(getActivity(), "Network Error: Unable to process request", Toast.LENGTH_LONG, false).show();
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonObjectRequest);
    }


}

