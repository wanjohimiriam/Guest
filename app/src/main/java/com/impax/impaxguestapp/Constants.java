package com.impax.impaxguestapp;

public class Constants {
    public static final String ROOT_URL = "http://20.86.117.62:8101/api/";
    public static final String DISPLAY_GUESTS = ROOT_URL+"get-visitors"; //C

    public static final String GET_GUESTS = ROOT_URL+"get-guests"; //C
    public static final String POST_GUEST = ROOT_URL+"post-visitor"; //C

    public static final String VISITORS = ROOT_URL+"visitors";

    public static final String SEARCH = ROOT_URL+"get-guest/{search}/"; //C

    public static final String UPDATE = ROOT_URL+"update-guest/{id}";

    public static final String SEARCH_GUEST = ROOT_URL+"get-guest-data/{id}";

}
