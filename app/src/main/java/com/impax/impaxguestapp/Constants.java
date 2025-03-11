package com.impax.impaxguestapp;

public class Constants {
    public static final String ROOT_URL = "http://20.86.117.62:8113/api/";
    public static final String DISPLAY_GUESTS = ROOT_URL+"Guests"; //C

    public static final String GET_GUESTS = ROOT_URL+"Guests"; //C
    public static final String POST_GUEST = ROOT_URL+"Guests"; //C

    public static final String UPLOAD = ROOT_URL+"Guests/UploadCSV";

    public static final String DOWNLOAD = ROOT_URL+"Guests/ExportToExcel"; //C

    public static final String UPDATE = ROOT_URL+"update-guest/{id}";

    public static final String SEARCH_GUEST = ROOT_URL+"get-guest-data/{id}";

}
