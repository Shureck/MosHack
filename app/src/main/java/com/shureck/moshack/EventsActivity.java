package com.shureck.moshack;

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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

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

public class EventsActivity extends AppCompatActivity {

    String strr;

    public ArrayList<View> surveyButtons;
    public ArrayList<Boolean> selectedGenres;
    public ArrayList<SurveyButtonContent> buttonContents;
    public ArrayList<String> genresToSend;
    LinearLayout mainEventsContainer;
    int tappedButtons;

    private final OkHttpClient client = new OkHttpClient();

    private String token;
    String tag;
    String ff;
    String msg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_events);

        WorkWithToken workWithToken = new WorkWithToken(EventsActivity.this);
        token = workWithToken.readToken();

        Intent intent = getIntent();
        tag = intent.getStringExtra("tag");
        ff = intent.getStringExtra("flag");
        msg = intent.getStringExtra("msg");
        System.out.println(tag);
        Button button = findViewById(R.id.subscribeButton);

        if(!ff.equals("4")){
            button.setText("Отписаться");
            activateButton(button, false);
        }
        else{
            ImageView imageView = findViewById(R.id.subscribeCheck);
            imageView.setVisibility(View.INVISIBLE);
            activateButton(button, true);
        }

        TextView mess = findViewById(R.id.channelDescFull);
        mess.setText(msg);

        TextView top = findViewById(R.id.channelNameInfo);
        top.setText(tag);

        new IOAsyncTask().execute("http://192.168.31.187:8083/user/preview?page=0&size=80");
    }

    void activateButton(Button button, boolean activate) {

        int buttonColor, textColor;
        if (activate) {
            buttonColor = getResources().getColor(R.color.main_blue);
            textColor = getResources().getColor(R.color.white);
        } else {
            buttonColor = getResources().getColor(R.color.light_gray);
            textColor = getResources().getColor(R.color.dark_gray);
        }

        Drawable buttonDrawable = button.getBackground();
        buttonDrawable = DrawableCompat.wrap(buttonDrawable);
        DrawableCompat.setTint(buttonDrawable, buttonColor);
        button.setBackground(buttonDrawable);
        button.setTextColor(textColor);
    }

    public void setData(List<Preview> previews){

        mainEventsContainer = findViewById(R.id.mainEventContainer);

        LayoutInflater inflater = LayoutInflater.from(mainEventsContainer.getContext());

        ImageLoader imageLoader = ImageLoader.getInstance();
        initImageLoader(getApplicationContext());

        selectedGenres = new ArrayList<>();
        buttonContents = new ArrayList<>();
        genresToSend = new ArrayList<>();

        tappedButtons = 0;

        surveyButtons = new ArrayList<>();
        for (int i=0; i<previews.size(); i++) {
            View newGenreButton = inflater.inflate(R.layout.element, null);

            ImageView genreImage = newGenreButton.findViewById(R.id.eventImageView);
            TextView dateTextView = newGenreButton.findViewById(R.id.dateTextView);
            TextView sphereTextView = newGenreButton.findViewById(R.id.sphereTextView);
            TextView freeTextView = newGenreButton.findViewById(R.id.freeTextView);
            TextView eventHeader = newGenreButton.findViewById(R.id.eventHeader);

            if(previews.get(i).sphere.get(0).equals(tag) || previews.get(i).sphere.size()>1 && previews.get(i).sphere.get(1).equals(tag)) {

                imageLoader.displayImage(previews.get(i).jpgUrl, genreImage);
                sphereTextView.setText(previews.get(i).sphere.get(0));
                freeTextView.setText(previews.get(i).free.toString());

                SimpleDateFormat sddd = new SimpleDateFormat("d MMMM");
                dateTextView.setText(sddd.format(new Date(Long.valueOf(previews.get(i).date_from_timestamp) * 1000)));

                if (previews.get(i).free) {
                    freeTextView.setText("Бесплатно");
                } else {
                    freeTextView.setText("");
                }
                eventHeader.setText(previews.get(i).title);

                newGenreButton.setId(previews.get(i).idItem);
                newGenreButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(EventsActivity.this, FullInfoActivity.class);
                        intent.putExtra("id", String.valueOf(v.getId()));
                        startActivity(intent);
                    }
                });
                mainEventsContainer.addView(newGenreButton);
            }
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
