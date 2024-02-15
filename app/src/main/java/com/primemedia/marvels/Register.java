package com.primemedia.marvels;

import static com.primemedia.marvels.utility.Utils.getMd5;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.primemedia.marvels.utility.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Register extends AppCompatActivity {
    TextInputEditText EmailText, password_text;
    MaterialButton login;
    Context context;

    //https://www.youtube.com/watch?v=a-ywdyrfdIY
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        EmailText = findViewById(R.id.EmailText);
        password_text = findViewById(R.id.password_text);
        login = findViewById(R.id.login);
        String email = Objects.requireNonNull(EmailText.getText()).toString().trim();
        String pass = Objects.requireNonNull(password_text.getText()).toString();
        String originalInput = "login:" + email + ":" + getMd5(pass);
        String encoded = Utils.toBase64(originalInput);
        login.setOnClickListener(v ->
        {
            Login(encoded);
        });

    }

    private void Login(String encoded) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST, Constants.url + "authentication", response -> {
            JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
            String status = jsonObject.get("Status").toString();
            status = status.substring(1, status.length() - 1);

            if (response.equals("invalid")) {

            } else if (status.equals("Successful")) {
                saveData(response);
                Intent intent = new Intent(Register.this, Splash.class);
                startActivity(intent);
                finish();
            }


        }, error -> {


        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", Constants.apiKey);
                return params;
            }

            @SuppressLint("HardwareIds")
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("encoded", encoded);
                params.put("device", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                return params;
            }
        };
        queue.add(sr);
    }

    private void saveData(String response) {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserData", response);
        editor.apply();
    }
}