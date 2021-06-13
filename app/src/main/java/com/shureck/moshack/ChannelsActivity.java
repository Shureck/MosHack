package com.shureck.moshack;

import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;
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

public class ChannelsActivity extends AppCompatActivity implements View.OnClickListener{

    LinearLayout subsList;
    LinearLayout channelsList;

    String strr;
    String username;
    Button currentCarouselButton;

    private final OkHttpClient client = new OkHttpClient();

    private String token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_channel);

        WorkWithToken workWithToken = new WorkWithToken(ChannelsActivity.this);
        token = workWithToken.readToken();

        CardView myChannelCard = findViewById(R.id.myChannelCard);
        myChannelCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChannelsActivity.this, MyChannelActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        currentCarouselButton = findViewById(R.id.catButton);

        new IOAsyncTask().execute("http://192.168.31.187:8083/user/putSphere");
    }



    void changeCarouselButtonDesign(Button button, boolean activate) {

        int buttonColor, textColor;
        if (activate) {
            buttonColor = getResources().getColor(R.color.main_blue);
            textColor = getResources().getColor(R.color.white);
        } else {
            buttonColor = getResources().getColor(R.color.light_blue);
            textColor = getResources().getColor(R.color.main_blue);
        }

        Drawable buttonDrawable = button.getBackground();
        buttonDrawable = DrawableCompat.wrap(buttonDrawable);
        DrawableCompat.setTint(buttonDrawable, buttonColor);
        button.setBackground(buttonDrawable);
        button.setTextColor(textColor);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.catButton:
                break;

            case R.id.authorsButton:
                break;
        }

        changeCarouselButtonDesign(currentCarouselButton, false);
        currentCarouselButton = (Button) v;
        changeCarouselButtonDesign(currentCarouselButton, true);
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
            SphereModel previews = gson.fromJson(strr, SphereModel.class);
            System.out.println("DDD "+previews.userName);
            setData(previews);
        }
    }

    private void setData(SphereModel previews) {
        channelsList = findViewById(R.id.channelsContainer);

        LayoutInflater channelsInflater = LayoutInflater.from(channelsList.getContext());
        Random random = new Random();
        username = previews.userName;
        for (int i = 0; i < previews.sphere.size(); i++) {
            View channelView = channelsInflater.inflate(R.layout.channel_item, null);
            ImageView check = channelView.findViewById(R.id.checkSub);
            check.setVisibility(View.INVISIBLE);

            TextView channelName = channelView.findViewById(R.id.channelName);
            TextView channelDesc = channelView.findViewById(R.id.channelDesc);

            channelName.setText(previews.sphere.get(i));
            channelDesc.setText(random.nextInt(15)+5+" мероприятий");

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
