package com.shureck.moshack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewPostAddEventActivity extends AppCompatActivity {

    LinearLayout pinEventContainer;
    private final OkHttpClient client = new OkHttpClient();
    String strr;
    private String token;

    String postHeader;
    String postImage;
    String postText;

    List<String> list = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post_add_event);

        WorkWithToken workWithToken = new WorkWithToken(NewPostAddEventActivity.this);
        token = workWithToken.readToken();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
            } else {
                postHeader = extras.getString("postHeader");
                postImage = extras.getString("postImage");
                postText = extras.getString("postText");
            }
        }

        System.out.println("QQQQQQqq "+postHeader);

        Button button = findViewById(R.id.buttonPinBackEvent);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewPostAddEventActivity.this, NewPostActivity.class);
                intent.putStringArrayListExtra("list", (ArrayList<String>) list);
                intent.putExtra("postHeader",postHeader);
                intent.putExtra("postImage",postImage);
                intent.putExtra("postText",postText);
                startActivity(intent);
            }
        });

        new IOAsyncTask().execute("http://192.168.31.187:8083/preview?page="+new Random().nextInt(15)+"&size=20");
    }

    public void setData(List<Preview> previews){

        pinEventContainer = findViewById(R.id.pinEventContainer);

        LayoutInflater inflater = LayoutInflater.from(pinEventContainer.getContext());


        for (int i=0; i<previews.size(); i++) {
            View newGenreButton = inflater.inflate(R.layout.short_event_info, null);

            TextView shortInfoTime = newGenreButton.findViewById(R.id.shortInfoTime);
            TextView shortInfoSphere = newGenreButton.findViewById(R.id.shortInfoSphere);
            TextView shortInfoFree = newGenreButton.findViewById(R.id.shortInfoFree);
            TextView shortInfoHeader = newGenreButton.findViewById(R.id.shortInfoHeader);

            shortInfoSphere.setText(previews.get(i).sphere.get(0));
            shortInfoFree.setText(previews.get(i).free.toString());

            SimpleDateFormat sddd = new SimpleDateFormat("d MMMM");
            shortInfoTime.setText(sddd.format(new Date(Long.valueOf(previews.get(i).date_from_timestamp) * 1000)));

            if (previews.get(i).free) {
                shortInfoFree.setText("Бесплатно");
            } else {
                shortInfoFree.setText("");
            }

            shortInfoHeader.setText(previews.get(i).title);
            newGenreButton.setId(previews.get(i).idItem);
            newGenreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.add(String.valueOf(v.getId()));
                    System.out.println(list);
                }
            });
            pinEventContainer.addView(newGenreButton);
        }
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
                    .header("Authorization","Bearer "+ token)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }
}