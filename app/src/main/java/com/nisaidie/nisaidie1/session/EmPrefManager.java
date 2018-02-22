package com.nisaidie.nisaidie1.session;

import android.content.Context;
import android.content.SharedPreferences;

public class EmPrefManager {

    SharedPreferences emPref;
    SharedPreferences.Editor editor;
    Context _context;


    //shared pref mcode
    int PRIVATE_MODE = 0;

    //sharedpref file name
    private static final String PREF_NAME = "Nisaidie-Em-Contacts";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public EmPrefManager(Context context) {
        this._context = context;
        emPref = _context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor = emPref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return emPref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }
}
