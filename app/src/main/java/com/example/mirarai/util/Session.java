package com.example.mirarai.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class Session {

    public static Session yourPreference;
    public SharedPreferences sharedPreferences;
    private SharedPreferences.Editor prefsEditor;

    public static Session getInstance(Context context) {
        if (yourPreference == null) {
            yourPreference = new Session(context);
        }
        return yourPreference;
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }

    public Session(Context context) {

        sharedPreferences = context.getSharedPreferences(Constant.PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void setValue(String key, String value) {
        prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(key, value);
        prefsEditor.apply();
    }

    public String getValue(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(key, "");
        }
        return "";
    }

    public void setBoolean(String key, boolean value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(key, defaultValue);
        }
        return false;
    }



    public Object getObject(String key, Class<?> classOfT) {
        String json = getString(key);
        return new Gson().fromJson(json, classOfT);
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void putObject(String key, Object obj) {
        checkForNullKey(key);
        Gson gson = new Gson();
        putString(key, gson.toJson(obj));
    }
    public void putString(String key, String value) {
        checkForNullKey(key);
        checkForNullValue(value);
        sharedPreferences.edit().putString(key, value).apply();
    }

    public boolean checkForNullKey(String key) {
        return key == null;
    }

    public void checkForNullValue(String value) {
        if (value == null) {
            throw new NullPointerException();
        }
    }
}
