package com.shureck.moshack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.shureck.moshack.Preview;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyEventsActivity extends AppCompatActivity {

    LinearLayout plannedEventsContainer;
    LinearLayout visitedEventsContainer;

    String strr;

    public ArrayList<View> surveyButtons;
    public ArrayList<Boolean> selectedGenres;
    public ArrayList<SurveyButtonContent> buttonContents;
    public ArrayList<String> genresToSend;
    int tappedButtons;

    private final OkHttpClient client = new OkHttpClient();

    private RecyclerView rv;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        WorkWithToken workWithToken = new WorkWithToken(MyEventsActivity.this);
        token = workWithToken.readToken();

        plannedEventsContainer = findViewById(R.id.plannedContainer);
        visitedEventsContainer = findViewById(R.id.visitedContainer);

        LayoutInflater plannedEventsInflater = LayoutInflater
                .from(plannedEventsContainer.getContext());
        LayoutInflater visitedEventsInflater = LayoutInflater
                .from(visitedEventsContainer.getContext());

        new IOAsyncTask().execute("http://192.168.31.187:8083/preview?page=7&size=20");
    }


    public void setData(List<Preview> previews){

        plannedEventsContainer = findViewById(R.id.plannedContainer);
        visitedEventsContainer = findViewById(R.id.visitedContainer);

        LayoutInflater inflaterUp = LayoutInflater.from(plannedEventsContainer.getContext());
        LayoutInflater inflaterDown = LayoutInflater.from(visitedEventsContainer.getContext());

        ImageLoader imageLoader = ImageLoader.getInstance();
        initImageLoader(getApplicationContext());

        selectedGenres = new ArrayList<>();
        buttonContents = new ArrayList<>();
        genresToSend = new ArrayList<>();

        tappedButtons = 0;

        surveyButtons = new ArrayList<>();
        for (int i=0; i<previews.size()/2; i++) {
            View newGenreButton = inflaterUp.inflate(R.layout.element_horisontal, null);

            ImageView genreImage = newGenreButton.findViewById(R.id.eventImageView);
            TextView dateTextView = newGenreButton.findViewById(R.id.dateTextView);
            TextView sphereTextView = newGenreButton.findViewById(R.id.sphereTextView);
            TextView freeTextView = newGenreButton.findViewById(R.id.freeTextView);
            TextView eventHeader = newGenreButton.findViewById(R.id.eventHeader);

            imageLoader.displayImage(previews.get(i).jpgUrl, genreImage);
            sphereTextView.setText(previews.get(i).sphere.get(0));
            freeTextView.setText(previews.get(i).free.toString());

            SimpleDateFormat sddd = new SimpleDateFormat("d MMMM");
            dateTextView.setText(sddd.format(new Date(Long.valueOf(previews.get(i).date_from_timestamp) * 1000)));

            if (previews.get(i).free){
                freeTextView.setText("Бесплатно");
            }
            else {
                freeTextView.setText("");
            }
            eventHeader.setText(previews.get(i).title);

            newGenreButton.setId(previews.get(i).idItem);
            newGenreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyEventsActivity.this, FullInfoActivity.class);
                    intent.putExtra("id", String.valueOf(v.getId()));
                    startActivity(intent);
                }
            });
            plannedEventsContainer.addView(newGenreButton);
        }

        for (int i=previews.size()/2; i<previews.size(); i++) {
            View newGenreButton = inflaterDown.inflate(R.layout.element_w_rating_horizontal, null);

            ImageView genreImage = newGenreButton.findViewById(R.id.eventImageView);
            TextView dateTextView = newGenreButton.findViewById(R.id.dateTextView);
            TextView sphereTextView = newGenreButton.findViewById(R.id.sphereTextView);
            TextView freeTextView = newGenreButton.findViewById(R.id.freeTextView);
            TextView eventHeader = newGenreButton.findViewById(R.id.eventHeader);

            imageLoader.displayImage(previews.get(i).jpgUrl, genreImage);
            sphereTextView.setText(previews.get(i).sphere.get(0));
            freeTextView.setText(previews.get(i).free.toString());

            SimpleDateFormat sddd = new SimpleDateFormat("d MMMM");
            dateTextView.setText(sddd.format(new Date(Long.valueOf(previews.get(i).date_from_timestamp) * 1000)));

            if (previews.get(i).free){
                freeTextView.setText("Бесплатно");
            }
            else {
                freeTextView.setText("");
            }
            eventHeader.setText(previews.get(i).title);

            newGenreButton.setId(previews.get(i).idItem);
            newGenreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyEventsActivity.this, FullInfoActivity.class);
                    intent.putExtra("id", String.valueOf(v.getId()));
                    startActivity(intent);
                }
            });
            visitedEventsContainer.addView(newGenreButton);
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