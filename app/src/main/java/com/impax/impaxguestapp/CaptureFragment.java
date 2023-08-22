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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

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
    private ImageView search_img;
    ArrayList<String> items=new ArrayList<>();
    private SpinnerDialog spinnerDialog;
    private JSONArray item_visitors;
    private ArrayList<String>full_names;
    private ArrayList<String>company_name;
   // private ArrayList<String>id;
    private ArrayList<String>email;
    private ArrayList<String>pnumber;
    private ArrayList<String>designation;
    private Timer timer;

    private ListView listview;

    private ArrayAdapter<String> adapter;

    private List<String> allItems;
    private List<String> displayedItems;



//    private boolean dataChanged = false;

//    private String visitor_id ="";
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.capture_fragment, container, false);

        nameT = view.findViewById(R.id.nameT);


        emailT = view.findViewById(R.id.emailT);
        companyT = view.findViewById(R.id.companyT);
        pnumberT = view.findViewById(R.id.pnumberT);
        designationT = view.findViewById(R.id.designationT);
        buttonSubmit = view.findViewById(R.id.buttonSubmit);
        search_img = view.findViewById(R.id.search_img);
        listview= view.findViewById(R.id.listViewResults);
        //full names
        full_names= new ArrayList<>();
        //id = new ArrayList<>();
        company_name = new ArrayList<>();
        email = new ArrayList<>();
        pnumber = new ArrayList<>();
        designation = new ArrayList<>();
        buttonSubmit.setOnClickListener(v -> post());
        allItems = new ArrayList<>();
        displayedItems = new ArrayList<>(allItems);

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, displayedItems);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedItem = displayedItems.get(position);
                //showToast("Selected: " + selectedItem);
                // Perform other actions as needed when an item is selected
            }
        });




//        buttonSubmit.setEnabled(false);

        nameT.addTextChangedListener(createTextWatcher());
//        emailT.addTextChangedListener(createTextWatcher());
//        companyT.addTextChangedListener(createTextWatcher());
//        pnumberT.addTextChangedListener(createTextWatcher());
//        designationT.addTextChangedListener(createTextWatcher());


        spinnerDialog=new SpinnerDialog(getActivity(),full_names,"Search",R.style.MyMaterialTheme,"Close");// With 	Animation

        spinnerDialog.setCancellable(true); // for cancellable
        spinnerDialog.setShowKeyboard(false);// for open keyboard by default


        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                Toast.makeText(getActivity(), item + "  " + position+"", Toast.LENGTH_SHORT).show();
                //selectedItems.setText(item + " Position: " + position);
                nameT.setText(item);
                //visitor_id= id.get(position);
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
//    nameT.addTextChangedListener(new TextWatcher()
//    {
//        @Override
//        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//        //do nothing
//    }
//
//        @Override
//        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//        // user is typing: reset already started timer (if existing)
//        if (timer != null) {
//            timer.cancel();
//        }
//    }
//
//        @Override
//        public void afterTextChanged(Editable editable)
//        {
//
//
//            // user typed: start the timer
//            timer = new Timer();
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run()
//                {
//                    // do your actual work here
//                    if(!editable.toString().isEmpty())
//                    {
//                        get_asset_details(editable.toString());
//                        //serialNo_text.requestFocus();
//                    }
//
//                }
//            }, 10000); // 10s delay before the timer executes the „run“ method from TimerTask
//        }
//    });

    private TextWatcher createTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Update the dataChanged flag when text changes
                //dataChanged = true;
                buttonSubmit.setEnabled(true); // Enable the "Update" button
            }

            @Override
            public void afterTextChanged(Editable s) {

                // user typed: start the timer
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run()
                    {
                        // do your actual work here
                        if(!s.toString().isEmpty())
                        {
                            getVisitors(s.toString());
                            //serialNo_text.requestFocus();
                        }

                    }
                }, 3000); // 10s delay before the timer executes the „run“ method from TimerTask
            }

            };
        }


//    private void updateData() {
//        String selectedName = nameT.getText().toString(); // Get the selected name from the spinner dialog
//
//        // Fetch other data from fields
//        final String updatedCompany = companyT.getText().toString().trim().toUpperCase();
//        final String updatedEmail = emailT.getText().toString().trim();
//        final String updatedPNumber = pnumberT.getText().toString().trim();
//        final String updatedDesignation = designationT.getText().toString().trim();
//
//        StringRequest updateRequest = new StringRequest(Request.Method.POST, Constants.UPDATE, response -> {
//            try {
//                JSONObject obj = new JSONObject(response);
//                if (!obj.getBoolean("error")) {
//                    // Handle successful update
//                    Toasty.info(getActivity(), "Update Successful", Toast.LENGTH_LONG, false).show();
//
//                    // Clear or update spinner data if needed
//
//                    // Reset UI components after update
//                    companyT.setText("");
//                    emailT.setText("");
//                    pnumberT.setText("");
//                    designationT.setText("");
//                    buttonSubmit.setVisibility(View.GONE);
//                } else {
//                    // Handle error response
//                    Toasty.error(getActivity(), obj.getString("message"), Toast.LENGTH_LONG, false).show();
//                }
//            } catch (JSONException e) {
//                throw new RuntimeException(e);
//            }
//        }, error -> {
//            // Handle error response
//            Log.d("error45", error.toString());
//            Toasty.error(getActivity(), "Update Error", Toast.LENGTH_LONG, false).show();
//        }) {
//            @Nullable
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("id", visitor_id);
//                params.put("name", selectedName);// Use the selected name for updating
//                params.put("email", updatedEmail);
//                params.put("company", updatedCompany);
//                params.put("phone_number", updatedPNumber);
//                params.put("designation", updatedDesignation);
//                return params;
//            }
//        };
//
//        RequestHandler.getInstance(getContext()).addToRequestQueue(updateRequest);
//    }




    private void loadspinner_visitors() {
        StringRequest loadSpinnerTools = new StringRequest(Request.Method.GET,Constants.GET_GUESTS , response -> {
          //  Toasty.info(getContext(),response,Toast.LENGTH_LONG,false).show();
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
                //id.add(o.getString(Bconfig.TAG_ID));
                company_name.add(o.getString(Bconfig.TAG_COMPANY_NAME));
                email.add(o.getString(Bconfig.TAG_EMAIL));
                designation.add(o.getString(Bconfig.TAG_DESIGNATION));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        /*spinnerDialogJobTypes.setAdapter(new ArrayAdapter<String>(EditProfile.this, android.R.layout.simple_spinner_dropdown_item,jobs));*/
    }

    private void getVisitors(String s)
    {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.SEARCH, response -> {
            try {
                JSONObject obj = new JSONObject(response);

                Log.d("getboolean", "post"+ obj.getString("name"));

                nameT.setText(obj.getString("name"));
                companyT.setText(obj.getString("email"));
                emailT.setText(obj.getString("phone_number"));
                pnumberT.setText(obj.getString("company"));
                designationT.setText(obj.getString("designation"));

            } catch (JSONException e) {
                //Toasty.error(getActivity(), "Error parsing server response", Toast.LENGTH_LONG, false).show();
                Log.e("CaptureFragment", "JSON parsing error: " + e.getMessage());
                e.printStackTrace();
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.e("Error", "Error during post request", error);
                        //Log.d("error45","error.toString()");

                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<>();
                params.put("search",s);
                //  params.put("captured_by",SharedPrefManager.getInstance(getContext()).getKeyUserEmail());
                return params;
            }
        };
        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
    }


    private void post()
    {
        Log.d("debugging", "post: ");
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

            Log.d("fields", "post"+name +company+email+pnumber+ designation);


            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.POST_GUEST, response -> {
                try {
                    JSONObject obj = new JSONObject(response);

                    Log.d("getboolean", "post"+ obj.getString("message"));
                    if(obj.getBoolean("success"))
                    {
                        //Toasty.info(getContext(),obj.getString("message"), Toast.LENGTH_LONG,false).show();
                        Toasty.info(getActivity(),"Checkin Success", Toast.LENGTH_LONG,false).show();
                        Log.d("ClearFields", "Clearing fields after successful POST");

                        nameT.setText("");
                        companyT.setText("");
                        emailT.setText("");
                        pnumberT.setText("");
                        designationT.setText("");
                    }
                    else
                    {
                        Toasty.error(getContext(),obj.getString("message"), Toast.LENGTH_LONG,false).show();
                    }
                } catch (JSONException e) {
                    //Toasty.error(getActivity(), "Error parsing server response", Toast.LENGTH_LONG, false).show();
                    Log.e("CaptureFragment", "JSON parsing error: " + e.getMessage());
                    e.printStackTrace();
                }
            },
                    new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Log.e("Error", "Error during post request", error);
                    //Log.d("error45","error.toString()");

                }
            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String>params = new HashMap<>();
                    params.put("name",name);
                    params.put("email",email);
                    params.put("company",company);
                    params.put("phone_number",pnumber);
                    params.put("designation",designation);
                  //  params.put("captured_by",SharedPrefManager.getInstance(getContext()).getKeyUserEmail());
                    return params;
                }
            };
            RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
        }
    }

}


//OUTCOMPLETE VIEW
