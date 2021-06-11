package com.shureck.moshack;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    String str;

    private final OkHttpClient client = new OkHttpClient();

    class IOAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return sendData(params[0]);
        }

        @Override
        protected void onPostExecute(String response) {
            str = response;
            System.out.println("DDD "+response);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

//        Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
//        startActivity(intent);



        //new IOAsyncTask().execute("");
    }

    public String sendData(String str){
        try {
//            RequestBody formBody = new FormBody.Builder()
//                    .add("LatLong", str)
//                    .build();

            Request request = new Request.Builder()
                    .url("http://192.168.31.187:8082/getUser")
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

}
