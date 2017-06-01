package com.stabstudio.discussionapp.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager extends Application{

    private static final String SHARED_PREF_FILE = "MetaData";
    private static final String TOKEN_KEY = "fcm_token";
    private static SharedPreferenceManager sharedPreferenceManager;
    private final SharedPreferences mSharedPreferences;

    public SharedPreferenceManager() {
        this.mSharedPreferences = getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
    }

    public static SharedPreferenceManager getInstance() {
        if (sharedPreferenceManager == null) {
            synchronized (SharedPreferenceManager.class) {
                if (sharedPreferenceManager == null) {
                    sharedPreferenceManager = new SharedPreferenceManager();
                }
            }
        }
        return sharedPreferenceManager;
    }

    public void setFcmToken(String token) {
        mSharedPreferences.edit().putString(TOKEN_KEY, token).apply();
    }

    public String getFcmToken() {
        return mSharedPreferences.getString(TOKEN_KEY, "");
    }

}
