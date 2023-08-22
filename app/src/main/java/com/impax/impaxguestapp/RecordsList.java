
package com.impax.impaxguestapp;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecordsList {
    // public String id;
    public String name;
    public String email;
    public String company;

    public String phone_number;

    public String updated_at;
    public String datePart;
    public String timePart;

    public RecordsList(String name, String email, String company, String phone_number, String updated_at) {
        this.name = name;
        this.email = email;
        this.company = company;
        this.phone_number = phone_number;
        this.updated_at = updated_at;

    }


    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCompany() {
        return company;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getUpdated_at() {
        return updated_at;
    }
    public String getDatePart() {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = inputFormat.parse(updated_at);
            return dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getTimePart() {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        try {
            Date date = inputFormat.parse(updated_at);
            return timeFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}







//package com.impax.impaxguestapp;
//
//public class RecordsList {
//    public String id;
//    public String name;
//    public String email;
//    public String company;
//    public String date_x;
//    public String time_x;
//    public String captured_by;
//
//    public RecordsList(String id,String name, String email, String company, String date_x, String time_x,String captured_by) {
//        this.id = id;
//        this.name = name;
//        this.email = email;
//        this.company = company;
//        this.date_x = date_x;
//        this.time_x = time_x;
//        this.captured_by = captured_by;
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public String getCompany() {
//        return company;
//    }
//
//    public String getDate_x() {
//        return date_x;
//    }
//
//    public String getTime_x() {
//        return time_x;
//    }
//
//    public String getCaptured_by() {
//        return captured_by;
//    }
//}
