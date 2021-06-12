package com.shureck.moshack;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

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

        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
//
//        new IOAsyncTask().execute("http://192.168.31.187:8083/preview");

    }

    @Override
    public void onClick(View v) {

    }

    public void setData(List<Preview> previews){

        button = findViewById(R.id.button);
        gridLayout = findViewById(R.id.grid);

        LayoutInflater inflater = LayoutInflater.from(gridLayout.getContext());

        ImageLoader imageLoader = ImageLoader.getInstance();
        initImageLoader(getApplicationContext());

        selectedGenres = new ArrayList<>();
        buttonContents = new ArrayList<>();
        genresToSend = new ArrayList<>();

        buttonContents = SurveyHelper.fillSurveyContent();
        tappedButtons = 0;

        button.setOnClickListener(this);

        surveyButtons = new ArrayList<>();
        for (int i=0; i<previews.size(); i++) {
            View newGenreButton = inflater.inflate(R.layout.element, null);

            ImageView genreImage = newGenreButton.findViewById(R.id.eventImageView);
            TextView dateTextView = newGenreButton.findViewById(R.id.dateTextView);
            TextView sphereTextView = newGenreButton.findViewById(R.id.sphereTextView);
            TextView freeTextView = newGenreButton.findViewById(R.id.freeTextView);
            TextView eventHeader = newGenreButton.findViewById(R.id.eventHeader);

            imageLoader.displayImage(previews.get(i).jpgUrl, genreImage);
            dateTextView.setText(previews.get(i).date);
            sphereTextView.setText(previews.get(i).sphere.get(0));
            //freeTextView.setText(previews.get(i);
            eventHeader.setText(previews.get(i).title);


            surveyButtons.add(newGenreButton.findViewById(R.id.imageButton));
            selectedGenres.add(false);

            newGenreButton.findViewById(R.id.imageButton).setOnClickListener(this);

            gridLayout.addView(newGenreButton);
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
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

}
