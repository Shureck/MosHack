package com.shureck.moshack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FullPostActivity extends AppCompatActivity {

    LinearLayout pinnedEventsContainer;
    LinearLayout commentsContainer;
    private String token;
    String strr;
    LayoutInflater inflaterPinned;
    LayoutInflater inflaterComments;
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_post);

        WorkWithToken workWithToken = new WorkWithToken(FullPostActivity.this);
        token = workWithToken.readToken();
        System.out.println("Token " + token);

        pinnedEventsContainer = findViewById(R.id.pinnedEventsContainer);
        commentsContainer = findViewById(R.id.commentsContainer);

        inflaterPinned = LayoutInflater.from(pinnedEventsContainer.getContext());
        inflaterComments = LayoutInflater.from(commentsContainer.getContext());

        EditText editTextTextPersonName = findViewById(R.id.editTextTextPersonName);
        ImageView sendCommentButton = findViewById(R.id.sendCommentButton);
        sendCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextTextPersonName.getText().toString();
            }
        });
//
//        for (int i = 0; i < 3; i++) {
//            View newEventToPin = inflaterPinned.inflate(R.layout.short_event_info, null);
//            View newComment = inflaterPinned.inflate(R.layout.comment_layout, null);
//
//            pinnedEventsContainer.addView(newEventToPin);
//            commentsContainer.addView(newComment);
//        }
        new IOAsyncTask().execute("http://192.168.31.187:8083/user/getUser");
    }

    public void setData(BigJson previews){

        pinnedEventsContainer = findViewById(R.id.pinnedEventsContainer);

        LayoutInflater inflater = LayoutInflater.from(pinnedEventsContainer.getContext());

        ImageLoader imageLoader = ImageLoader.getInstance();
        initImageLoader(getApplicationContext());

        View newGenreButton = inflater.inflate(R.layout.element, null);

        ImageView profilePostPic = newGenreButton.findViewById(R.id.profilePostPic);
        ImageView fullPostEventImage = newGenreButton.findViewById(R.id.fullPostEventImage);
        TextView postTime = newGenreButton.findViewById(R.id.postTime);
        TextView profilePostChannelName = newGenreButton.findViewById(R.id.profilePostChannelName);
        TextView fullPostText = newGenreButton.findViewById(R.id.fullPostText);
        TextView fullPostHeader = newGenreButton.findViewById(R.id.fullPostHeader);

        for (int i=0; i < previews.posts.get(0).idItemForPosts.size(); i++) {

            View newEventToPin = inflaterPinned.inflate(R.layout.short_event_info, null);

            TextView shortInfoTime = newEventToPin.findViewById(R.id.shortInfoTime);
            TextView shortInfoSphere = newEventToPin.findViewById(R.id.shortInfoSphere);
            TextView shortInfoFree = newEventToPin.findViewById(R.id.shortInfoFree);
            TextView shortInfoHeader = newEventToPin.findViewById(R.id.shortInfoHeader);

            shortInfoHeader.setText("Kek");

//            if (previews.get(i).free) {
//                freeTextView.setText("Бесплатно");
//            } else {
//                freeTextView.setText("");
//            }

//            newGenreButton.setId(previews.get(i).idItem);
//            newGenreButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(FullPostActivity.this, FullInfoActivity.class);
//                    intent.putExtra("id", String.valueOf(v.getId()));
//                    startActivity(intent);
//                }
//            });
            pinnedEventsContainer.addView(newEventToPin);
        }

        for (int i=0; i < previews.posts.get(0).idItemForPosts.size(); i++) {

            View newComment = inflaterPinned.inflate(R.layout.comment_layout, null);

            TextView commentTime = newComment.findViewById(R.id.commentTime);
            ImageView profileCommentPic = newComment.findViewById(R.id.profileCommentPic);
            TextView commentText = newComment.findViewById(R.id.commentText);
            TextView profileCommentChannelName = newComment.findViewById(R.id.profileCommentChannelName);

            profileCommentChannelName.setText("Kek");
//
//            SimpleDateFormat sddd = new SimpleDateFormat("d MMMM");
//            commentTime.setText(sddd.format(new Date(Long.valueOf(previews.get(i).date_from_timestamp) * 1000)));


            commentsContainer.addView(newComment);
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
            System.out.println("DDD "+previews.name);
            setData(previews);
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