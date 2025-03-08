package com.impax.impaxguestapp;
import android.os.Bundle;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    private void loadspinner_visitors() {
        StringRequest loadSpinnerTools = new StringRequest(Request.Method.GET,Constants.GET_GUESTS , response -> {
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

    private void sendToDB(String name, String company, String email, String pnumber, String designation)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("name", name);
            postData.put("email", email);
            postData.put("company", company);
            postData.put("phone_number", pnumber);
            postData.put("designation", designation);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.POST_GUEST, postData,
                response -> {
                    try {
                        String message = response.getString("message");
                        if (message.equals("Success")) {
                            // The submission was successful
                            Toasty.info(getActivity(),"Checkin Successful", Toast.LENGTH_LONG,false).show();

                        } else {
                            Toasty.info(getActivity(),"Checkin Failed\n Check again", Toast.LENGTH_LONG,false).show();

                        }
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                        // Handle JSON parsing error
                        Toasty.info(getActivity(),"Error Occurred1", Toast.LENGTH_LONG,false).show();
                    }
                },
                error -> {
                    // Handle error response here
                    Toasty.info(getActivity(),"Error Occurred2", Toast.LENGTH_LONG,false).show();

                });


        // Add the request to the Volley request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity()); // Replace with your Context
        requestQueue.add(jsonObjectRequest);

    }

}

