package com.primemedia.studioflix.utility;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public class ConfigManager
{
    public static void saveConfig(Context context, String config) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("MyApp", config);
        editor.apply();
    }

    public static JSONObject loadConfig(Context context) throws JSONException {
        SharedPreferences sharedPreferences = context.getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        String config = sharedPreferences.getString("MyApp", null);
        return new JSONObject(config);
    }
}
