package com.shureck.moshack;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpRequest {

    private final OkHttpClient client = new OkHttpClient();
    public String str;

    class IOAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            return sendData(params[0]);
        }

        @Override
        protected void onPostExecute(String response) {
            str = response;
            Log.d("DDD",response);
        }
    }

    public String sendData(String str){
        try {
            RequestBody formBody = new FormBody.Builder()
                    .add("LatLong", str)
                    .build();

            Request request = new Request.Builder()
                    .url("http://25.101.65.98:8080/api"+str)
                    .post(formBody)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }
}
