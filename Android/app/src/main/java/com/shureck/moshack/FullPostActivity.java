package com.shureck.moshack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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

    ConstraintLayout layoutll;
    LinearLayout pinnedEventsContainer;
    LinearLayout commentsContainer;
    private String token;
    String strr;
    int id;
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

        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        System.out.println("FFFFFFF "+id);

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


        new IOAsyncTask().execute("http://45.157.140.16:23200/user/getPost?postId="+id);
    }

    public void setData(HardPostModel previews){

        layoutll = findViewById(R.id.fullPostContainer);

        LayoutInflater inflater = LayoutInflater.from(layoutll.getContext());

        ImageLoader imageLoader = ImageLoader.getInstance();
        initImageLoader(getApplicationContext());


        ImageView profilePostPic = findViewById(R.id.profilePostPic);
        ImageView fullPostEventImage = findViewById(R.id.fullPostEventImage);
        TextView postTime = findViewById(R.id.postTime);
        TextView profilePostChannelName = findViewById(R.id.profilePostChannelName);
        TextView fullPostText = findViewById(R.id.fullPostText);
        TextView fullPostHeader = findViewById(R.id.fullPostHeader);

        imageLoader.displayImage(previews.jpgUrl, fullPostEventImage);
        profilePostChannelName.setText(previews.name);
        fullPostText.setText(previews.text);
        fullPostHeader.setText(previews.title);

        for (int i=0; i < 1; i++) {

            View newEventToPin = inflaterPinned.inflate(R.layout.short_event_info, null);;

            pinnedEventsContainer.addView(newEventToPin);
        }

        for (int i = 0; i < 3; i++) {
            View newComment = inflaterComments.inflate(R.layout.comment_layout, null);
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
            HardPostModel previews = gson.fromJson(strr, HardPostModel.class);
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