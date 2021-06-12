package com.shureck.moshack;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChannelsActivity extends AppCompatActivity {

    LinearLayout subsList;
    LinearLayout channelsList;

    String strr;

    public GridLayout gridLayout;
    public ArrayList<View> surveyButtons;
    public ArrayList<Boolean> selectedGenres;
    public ArrayList<SurveyButtonContent> buttonContents;
    public ArrayList<String> genresToSend;
    String str;
    Button button;
    int tappedButtons;

    private final OkHttpClient client = new OkHttpClient();

    private List cards;
    private RecyclerView rv;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_channel);

        setData();
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

        for (int i = 0; i < 5; i++) {
            View subView = subsInflater.inflate(R.layout.channel_item, null);
            View channelView = channelsInflater.inflate(R.layout.channel_item, null);
            ImageView check = channelView.findViewById(R.id.checkSub);
            check.setVisibility(View.INVISIBLE);
            subsList.addView(subView);
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
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }
}
