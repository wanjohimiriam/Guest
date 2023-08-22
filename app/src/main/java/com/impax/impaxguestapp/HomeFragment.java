package com.impax.impaxguestapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import es.dmoral.toasty.Toasty;

public class HomeFragment extends Fragment {
    View view;
    private RecyclerView recyclerView;
    private RecordsListAdapter myJobsAdapter;
    private List<RecordsList> listItems;
    private ProgressBar progressbar;
    private TextView emailTV;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_fragment, container, false);
        listItems = new ArrayList<>();
        progressbar = view.findViewById(R.id.progressbar);
        emailTV = view.findViewById(R.id.emailTV);
        emailTV.setText("("+SharedPrefManager.getInstance(getContext()).getKeyUserEmail()+")");
        recyclerView = view.findViewById(R.id.recyclerViewPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        loadRecyclerView();
        return view;

    }

    private void loadRecyclerView()
    {
        progressbar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.DISPLAY_GUESTS, response -> {
            //Toasty.info(getContext(),response, Toast.LENGTH_LONG,false).show();
            progressbar.setVisibility(View.GONE);

            try {
                JSONArray jsonArray = new JSONArray(response); // Parse the JSON array directly

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject o = jsonArray.getJSONObject(i);
                    String name = o.getString("name");
                    String email = o.getString("email");
                    String company = o.getString("company");
                    String phoneNumber = o.optString("phone_number", ""); // Handle the case when phone_number is null
                    String updatedAt = o.getString("updated_at");

                    RecordsList item = new RecordsList(name, email, company, phoneNumber, updatedAt);
                    listItems.add(item);
                }

                myJobsAdapter = new RecordsListAdapter(listItems, getContext());
                recyclerView.setAdapter(myJobsAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }


//            try {
//                JSONObject jsonObject = new JSONObject(response);
//                JSONArray array = jsonObject.getJSONArray("guests");
//
//
//                for (int i = 0; i < array.length(); i++) {
//                    JSONObject o = array.getJSONObject(i);
//                    String name = o.getString("name");
//                    String email = o.getString("email");
//                    String company = o.getString("company");
//                    String phoneNumber = o.getString("phone_number");
//                    String updatedAt = o.getString("updated_at");
//
//                    String[] dateTimeParts = updatedAt.split(" ");
//                    String datePart = "";
//                    String timePart = "";
//                    if (dateTimeParts.length == 2) {
//                        datePart = dateTimeParts[0];
//                        timePart = dateTimeParts[1];
//                    }
//
//                    RecordsList item = new RecordsList(name, email, company, phoneNumber, updatedAt);
//                    item.setUpdated_at(updatedAt); // Set the original updated_at value
//                    item.datePart = datePart; // Assign the date component
//                    item.timePart = timePart; // Assign the time component
//                    listItems.add(item);
//                }
//
////                for(int i=0;i<array.length();i++)
////                {
////                    JSONObject o = array.getJSONObject(i);
////                    RecordsList item = new RecordsList(o.getString("name"),o.getString("email"),o.getString("company"),o.getString("phone_number"),o.getString("updated_at"));
////                    listItems.add(item);
////                }
//                myJobsAdapter = new RecordsListAdapter(listItems,getContext());
//                recyclerView.setAdapter(myJobsAdapter);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Nullable

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params = new HashMap<>();
                //params.put("user_email",SharedPrefManager.getInstance(getContext()).getKeyUserEmail());
                return params;
            }
        };
        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
    }
}
