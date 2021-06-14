package com.shureck.moshack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyChannelActivity extends AppCompatActivity implements View.OnClickListener{

    LinearLayout universalContainer;
    Button currentCarouselButton;
    private String token;
    String strr;

    String username;
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_channel);

        FloatingActionButton newPostButton = findViewById(R.id.newPostButton);
        newPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentTest = new Intent(MyChannelActivity.this, NewPostActivity.class);
                startActivity(intentTest);
            }
        });

        WorkWithToken workWithToken = new WorkWithToken(MyChannelActivity.this);
        token = workWithToken.readToken();

        universalContainer = findViewById(R.id.universalContainer);
        LayoutInflater eventsInflater = LayoutInflater.from(universalContainer.getContext());

//        for (int i = 0; i < 5; i++) {
//            View channelEvent = eventsInflater.inflate(R.layout.channel_item, null);
//            universalContainer.addView(channelEvent);
//        }

        currentCarouselButton = findViewById(R.id.myPostsButton);
        new IOAsyncTask().execute("http://192.168.31.187:8083/user/getUser","1");
    }

    @Override
    public void onClick(View v) {
        changeCarouselButtonDesign(currentCarouselButton, false);
        currentCarouselButton = (Button) v;
        changeCarouselButtonDesign(currentCarouselButton, true);
        if (v.getId() == R.id.catButtonFullChannel){
            universalContainer = findViewById(R.id.universalContainer);
            LayoutInflater eventsInflater = LayoutInflater.from(universalContainer.getContext());
            universalContainer.removeAllViews();
            for (int i = 0; i < 5; i++) {
                View channelEvent = eventsInflater.inflate(R.layout.channel_item, null);
                universalContainer.addView(channelEvent);
            }
        }
        if (v.getId() == R.id.subsButtonFullChannel){
            universalContainer = findViewById(R.id.universalContainer);
            LayoutInflater eventsInflater = LayoutInflater.from(universalContainer.getContext());
            universalContainer.removeAllViews();
            for (int i = 0; i < 5; i++) {
                View channelEvent = eventsInflater.inflate(R.layout.authors_channel_short_info, null);
                universalContainer.addView(channelEvent);
            }
        }
        if (v.getId() == R.id.myPostsButton){
            universalContainer = findViewById(R.id.universalContainer);
            LayoutInflater eventsInflater = LayoutInflater.from(universalContainer.getContext());
            universalContainer.removeAllViews();
//            for (int i = 0; i < 5; i++) {
//                View channelEvent = eventsInflater.inflate(R.layout.post_short_info, null);
//                channelEvent.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(MyChannelActivity.this,FullPostActivity.class);
//                        startActivity(intent);
//                    }
//                });
//                universalContainer.addView(channelEvent);
//            }
            new IOAsyncTask().execute("http://192.168.31.187:8083/user/getUser","1");
        }
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

    class IOAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return sendData(params[0]);
        }

        @Override
        protected void onPostExecute(String response) {
            strr = response;
            Gson gson = new Gson();
            BigJson previews = gson.fromJson(strr, BigJson.class);
            setData(previews);
        }
    }

    private void setData2(BigJson previews) {
        universalContainer = findViewById(R.id.universalContainer);
        LayoutInflater eventsInflater = LayoutInflater.from(universalContainer.getContext());
        universalContainer.removeAllViews();

        ImageLoader imageLoader = ImageLoader.getInstance();
        initImageLoader(getApplicationContext());

        for (int i = 0; i < previews.posts.size(); i++) {
            View channelEvent = eventsInflater.inflate(R.layout.post_short_info, null);

            TextView shortPostChannelName = channelEvent.findViewById(R.id.shortPostChannelName);
            TextView shortPostHeader = channelEvent.findViewById(R.id.shortPostHeader);
            TextView shortPostText = channelEvent.findViewById(R.id.shortPostText);
            ImageView shortPostImage = channelEvent.findViewById(R.id.shortPostImage);

            imageLoader.displayImage(previews.posts.get(i).linksUrl, shortPostImage);

            shortPostChannelName.setText(previews.posts.get(i).name);
            shortPostHeader.setText(previews.posts.get(i).title);
            shortPostText.setText(previews.posts.get(i).text);

            channelEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyChannelActivity.this,FullPostActivity.class);
                    startActivity(intent);
                }
            });
            universalContainer.addView(channelEvent);
        }
    }

    private void setData(BigJson previews) {
        universalContainer = findViewById(R.id.universalContainer);
        LayoutInflater eventsInflater = LayoutInflater.from(universalContainer.getContext());
        universalContainer.removeAllViews();

        ImageLoader imageLoader = ImageLoader.getInstance();
        initImageLoader(getApplicationContext());

        for (int i = 0; i < previews.posts.size(); i++) {
            View channelEvent = eventsInflater.inflate(R.layout.post_short_info, null);

            TextView shortPostChannelName = channelEvent.findViewById(R.id.shortPostChannelName);
            TextView shortPostHeader = channelEvent.findViewById(R.id.shortPostHeader);
            TextView shortPostText = channelEvent.findViewById(R.id.shortPostText);
            ImageView shortPostImage = channelEvent.findViewById(R.id.shortPostImage);

            imageLoader.displayImage(previews.posts.get(i).linksUrl, shortPostImage);

            shortPostChannelName.setText(previews.posts.get(i).name);
            shortPostHeader.setText(previews.posts.get(i).title);
            shortPostText.setText(previews.posts.get(i).text);

            channelEvent.setId(previews.posts.get(i).postId);
            channelEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyChannelActivity.this,FullPostActivity.class);
                    intent.putExtra("id",v.getId());
                    startActivity(intent);
                }
            });
            universalContainer.addView(channelEvent);
        }
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
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