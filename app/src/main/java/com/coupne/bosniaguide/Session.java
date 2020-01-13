package com.coupne.bosniaguide;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class Session {

    private SharedPreferences prefs;

    public Session(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setusename(String usename) {
        prefs.edit().putString("usename", usename).commit();
    }

    public boolean isLoggedIn() {
        String usename = prefs.getString("usename","");
        return !TextUtils.isEmpty(usename.trim());
    }

    public String getName(){
        return prefs.getString("usename","").split("@")[0];
    }
}