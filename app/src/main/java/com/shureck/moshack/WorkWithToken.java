package com.shureck.moshack;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

public class WorkWithToken extends AppCompatActivity {

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_TOKEN = "Token"; // возраст кота
    SharedPreferences mSettings;
    public WorkWithToken(Context context) {
        mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void saveToken(String str) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_TOKEN, str);
        editor.apply();
    }

    public String readToken(){
        if(mSettings.contains(APP_PREFERENCES_TOKEN)) {
            return mSettings.getString(APP_PREFERENCES_TOKEN, "");
        }
        return null;
    }

}
