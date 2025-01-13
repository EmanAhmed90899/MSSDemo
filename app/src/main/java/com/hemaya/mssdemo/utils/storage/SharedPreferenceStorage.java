package com.hemaya.mssdemo.utils.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceStorage {
    private static final String PREF_NAME = "User"; // Shared Preference file name
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_STORAGE_NAME = "storageName";
    private static final String KEY_INITIATE_DETECTION = "initiateDetection";
    private static final String KEY_Language = "language";
    private static final String KEY_TIME_SHIFT = "TIME_SHIFT";
    private static final String KEY_USER_NAME = "userName";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    // Constructor to initialize SharedPreferences
    public SharedPreferenceStorage(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }


    // Method to clear the SharedPreferences
    public void clearData() {
        setStorageName(null);
        setUserId(null);
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, null);
    }

    public void setUserName(String userName) {
        editor.putString(KEY_USER_NAME, userName);
        editor.apply();
    }

    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    public void setUserId(String userId) {
        editor.putString(KEY_USER_ID, userId);
        editor.apply();
    }


    public String getStorageName() {
        return sharedPreferences.getString(KEY_STORAGE_NAME, null);
    }


    public void setStorageName(String storageName) {
        editor.putString(KEY_STORAGE_NAME, storageName);
        editor.apply();
    }
    public String getLanguage() {
        return sharedPreferences.getString(KEY_Language, "");
    }

    public void setLanguage(String language) {
        editor.putString(KEY_Language, language);
        editor.apply();
    }

    public boolean getInitiateDetection() {
        return sharedPreferences.getBoolean(KEY_INITIATE_DETECTION, false);
    }

    public void setInitiateDetection(boolean initiateDetection) {
        editor.putBoolean(KEY_INITIATE_DETECTION, initiateDetection);
        editor.apply();
    }

    public long getTimeShift() {
        return sharedPreferences.getLong(KEY_TIME_SHIFT, -1);
    }
    public void setTimeShift(long timeShift) {
        editor.putLong(KEY_TIME_SHIFT, timeShift);
        editor.apply();
    }
}
