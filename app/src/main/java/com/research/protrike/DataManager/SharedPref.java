package com.research.protrike.DataManager;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPref {

    private static final String PREFS_NAME = "COM_RESEARCH_PROTRIKE_SP";
    public static final String TRICYCLE_FARE_LAST_UPDATE = "TRICYCLE_FARE_LAST_UPDATE";
    public static final String CONTACTS_LAST_UPDATE = "CONTACTS_LAST_UPDATE";


    public static void write(Context context, String code, String object ) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(code, object);
        editor.apply();
    }

    public static void write(Context context, String code, Boolean object ) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(code, object);
        editor.apply();
    }

    public static void write(Context context, String code, Integer object ) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt(code, object);
        editor.apply();
    }

    public static String readString(Context context, String code, String default_value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(code, default_value);
    }

    public static Boolean readBool(Context context, String code, Boolean default_value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(code, default_value);
    }

    public static Integer readInt(Context context, String code, Integer default_value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getInt(code, default_value);
    }
}
