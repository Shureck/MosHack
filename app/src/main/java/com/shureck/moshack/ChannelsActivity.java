package com.shureck.moshack;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.shureck.moshack.Preview;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChannelsActivity extends AppCompatActivity {

    LinearLayout subsList;
    LinearLayout channelsList;

    String strr;

    private final OkHttpClient client = new OkHttpClient();

    private String token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_channel);

        WorkWithToken workWithToken = new WorkWithToken(ChannelsActivity.this);
        token = workWithToken.readToken();

        setData();
        //new IOAsyncTask().onPostExecute("http://192.168.31.187:8083/putSphere");
    }

    class IOAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return sendData(params[0]);
        }

        @Override
        protected void onPostExecute(String response) {
            strr = response;
            Gson gson = new Gson();
            List<Preview> previews = stringToArray(strr, Preview[].class);
            System.out.println("DDD "+previews.get(0).date);
            //setData(previews);
        }
    }

    private void setData() {
        subsList = findViewById(R.id.subsContainer);
        channelsList = findViewById(R.id.channelsContainer);

        LayoutInflater subsInflater = LayoutInflater.from(subsList.getContext());
        LayoutInflater channelsInflater = LayoutInflater.from(channelsList.getContext());

        String[] s = {"Спектакли","Экскурсии","Выставки"};
        int[] st = {11, 7, 10};
        String[] ss = {"Концерты","Для детей","Лекции","Мастер-классы","Кино"};
        int[] sst = {18, 5, 3, 17, 6};

        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            View subView = subsInflater.inflate(R.layout.channel_item, null);

            TextView channelName = subView.findViewById(R.id.channelName);
            TextView channelDesc = subView.findViewById(R.id.channelDesc);
            ImageView check = subView.findViewById(R.id.checkSub);

            channelName.setText(s[i]);
            channelDesc.setText( st[i]+" мероприятий");

            subView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView textView = v.findViewById(R.id.channelName);
                    Intent intent = new Intent(ChannelsActivity.this, EventsActivity.class);
                    intent.putExtra("tag", String.valueOf(textView.getText()));
                    intent.putExtra("flag", String.valueOf(check.getVisibility()));
                    intent.putExtra("msg", String.valueOf(channelDesc.getText()));
                    startActivity(intent);
                }
            });
            subsList.addView(subView);

        }
        for (int i = 0; i < 5; i++) {
            View channelView = channelsInflater.inflate(R.layout.channel_item, null);
            ImageView check = channelView.findViewById(R.id.checkSub);
            check.setVisibility(View.INVISIBLE);

            TextView channelName = channelView.findViewById(R.id.channelName);
            TextView channelDesc = channelView.findViewById(R.id.channelDesc);

            channelName.setText(ss[i]);
            channelDesc.setText(sst[i]+" мероприятий");

            channelView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView textView = v.findViewById(R.id.channelName);
                    Intent intent = new Intent(ChannelsActivity.this, EventsActivity.class);
                    intent.putExtra("tag", String.valueOf(textView.getText()));
                    intent.putExtra("flag", String.valueOf(check.getVisibility()));
                    intent.putExtra("msg", String.valueOf(channelDesc.getText()));
                    startActivity(intent);
                }
            });
            channelsList.addView(channelView);
        }
    }

    public static <T> List<T> stringToArray(String s, Class<T[]> clazz) {
        T[] arr = new Gson().fromJson(s, clazz);
        return Arrays.asList(arr); //or return Arrays.asList(new Gson().fromJson(s, clazz)); for a one-liner
    }

    public String sendData(String str){
        try {
//            RequestBody formBody = new FormBody.Builder()
//                    .add("LatLong", str)
//                    .build();

            Request request = new Request.Builder()
                    .url(str)
                    .header("Authorization","Bearer "+token)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }
}
