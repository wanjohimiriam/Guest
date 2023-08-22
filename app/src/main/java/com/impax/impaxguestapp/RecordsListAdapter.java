
package com.impax.impaxguestapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class RecordsListAdapter extends RecyclerView.Adapter<RecordsListAdapter.ViewHolder>{

    //private SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a", Locale.US);


    String searchString="";
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private List<RecordsList> listItems;
    private Context context;


    public RecordsListAdapter(List<RecordsList> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.records_list,parent,false);

        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder,  final int position) {

        final RecordsList listItem = listItems.get(position);
        Random mRandom = new Random();
        int color = Color.argb(255, mRandom.nextInt(256), mRandom.nextInt(256), mRandom.nextInt(256));
        ((GradientDrawable) holder.imageView.getBackground()).setColor(color);
        holder.imageView.setText(listItem.getName().substring(0,1).toUpperCase());
        holder.name_of_guestTV.setText(listItem.getName());
        holder.companyTV.setText(listItem.getCompany());
        holder.emailTV.setText(listItem.getEmail());
        holder.phoneTV.setText(listItem.getPhone_number());
        holder.timeTV.setText("Time: "+listItem.getTimePart()+"             "+"Date: "+listItem.getDatePart());



        //holder.timeTV.setText(listItem.getTime_x()+"-"+"Reg by"+" "+listItem.getCaptured_by());
        //holder.sublocationTV.setText(listItem.getLocation_name());
       /* holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/

    }


    @Override
    public int getItemCount() {
        return  listItems.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {


        public TextView name_of_guestTV,imageView,companyTV,phoneTV,emailTV,timeTV;

        private LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            name_of_guestTV = itemView.findViewById(R.id.name_of_guestTV);
            companyTV = itemView.findViewById(R.id.companyTV);
            phoneTV= itemView.findViewById(R.id.phoneTV);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            emailTV = itemView.findViewById(R.id.emailTV);
            timeTV = itemView.findViewById(R.id.timeTV);

        }

        /*ImageView getDeleteTripIV() {
           // return cancel_tripIV;
        }*/

    }
    public static void setFilter(List<RecordsList> listItems) {
        listItems = new ArrayList<>();
        listItems.addAll(listItems);

    }
    public void update(List<RecordsList> listItems) {
        listItems.clear();
        listItems.addAll(listItems);
        notifyDataSetChanged();
    }

    public void setItems(ArrayList<RecordsList> listItems) {
        this.listItems = listItems;
    }

    public void notify(ArrayList<RecordsList> list) {
        if (listItems != null) {
            listItems.clear();
            listItems.addAll(list);

        } else {
            listItems = list;
        }
        notifyDataSetChanged();
    }

}
