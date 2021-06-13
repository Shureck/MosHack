package com.shureck.moshack;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        EditText login = findViewById(R.id.login);
        EditText pass = findViewById(R.id.pass);
        Button reg = findViewById(R.id.button2);
        WorkWithToken workWithToken = new WorkWithToken(LoginActivity.this);
        Button log = findViewById(R.id.button);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IOAsyncTask().execute(new String[]{"http://192.168.31.187:8083/register", login.getText().toString(), pass.getText().toString()});
            }
        });
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IOAsyncTask().execute(new String[]{"http://192.168.31.187:8083/auth", login.getText().toString(), pass.getText().toString()});
            }
        });
        TextView textView = findViewById(R.id.textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workWithToken.saveToken("Free");
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        System.out.println(workWithToken.readToken());

    }

    class IOAsyncTask extends AsyncTask<String, Void, String> {

        WorkWithToken workWithToken = new WorkWithToken(LoginActivity.this);

        @Override
        protected String doInBackground(String... params) {
            return sendData(params[0],params[1],params[2]);
        }

        @Override
        protected void onPostExecute(String response) {
            String strr = response;
            System.out.println("Login "+strr);
            String previews = "";
            try {
                JSONObject jsonObj = new JSONObject(strr);
                previews = jsonObj.getString("token");
                System.out.println("DDD "+previews);
                workWithToken.saveToken(previews);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    public static <T> List<T> stringToArray(String s, Class<T[]> clazz) {
        T[] arr = new Gson().fromJson(s, clazz);
        return Arrays.asList(arr); //or return Arrays.asList(new Gson().fromJson(s, clazz)); for a one-liner
    }

    public String sendData(String str, String login, String pass){
        try {
            final MediaType JSON = MediaType.get("application/json; charset=utf-8");
            RequestBody formBody = RequestBody.create(JSON, "{\"login\": \""+login+"\",\n" +
                    "\"password\": \""+pass+"\"\n" +"}");

            Request request = new Request.Builder()
                    .url(str)
                    .post(formBody)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }
}
