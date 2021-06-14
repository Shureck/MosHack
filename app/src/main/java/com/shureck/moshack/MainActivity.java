package com.shureck.moshack;

import android.Manifest;
import android.content.Context;
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
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.rhexgomez.typer.roboto.TyperRoboto;
import com.shureck.moshack.Preview;

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
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    String strr;

    public ArrayList<View> surveyButtons;
    public ArrayList<Boolean> selectedGenres;
    public ArrayList<SurveyButtonContent> buttonContents;
    public ArrayList<String> genresToSend;
    String str;
    LinearLayout mainEventsContainer;
    int tappedButtons;

    int currentButtonId;
    Button currentCarouselButton;

    private final OkHttpClient client = new OkHttpClient();

    private List cards;
    private RecyclerView rv;
    private String token;
    private final int REQUEST_LOCATION_PERMISSION = 1;
    String tag = "def";
    String count = "20";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        WorkWithToken workWithToken = new WorkWithToken(MainActivity.this);
        token = workWithToken.readToken();
        System.out.println("Token " + token);

//        Intent intentTest = new Intent(MainActivity.this, NewPostActivity.class);
//        startActivity(intentTest);


        if(token == null || token.equals("")) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        else {


//        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
//                Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"));


            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            toolBarLayout.setTitle(getTitle());
            toolBarLayout.setExpandedTitleColor(getResources().getColor(R.color.black));
            toolBarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.black));
            toolBarLayout.setCollapsedTitleTypeface(TyperRoboto.ROBOTO_REGULAR());
            toolBarLayout.setExpandedTitleTypeface(TyperRoboto.ROBOTO_BOLD());
          
            new IOAsyncTask().execute("http://192.168.31.187:8083/preview?page="+new Random().nextInt(15)+"&size="+count);
        }

        currentCarouselButton = findViewById(R.id.buttonAll);
        currentButtonId = currentCarouselButton.getId();
        requestLocationPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if(EasyPermissions.hasPermissions(this, perms)) {
        }
        else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
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

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.categoriesButton:
                Intent intent = new Intent(MainActivity.this, ChannelsActivity.class);
                startActivity(intent);
                break;
            case R.id.calendarButton:
                Intent intent1 = new Intent(MainActivity.this, CalendarActivity.class);
                startActivity(intent1);
                break;
            case R.id.favButton:
                Intent intent2 = new Intent(MainActivity.this, MyEventsActivity.class);
                startActivity(intent2);
                break;
            case R.id.mapButton:
                Intent intent3 = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent3);
                break;
            default:
                changeCarouselButtonDesign(currentCarouselButton, false);
                currentCarouselButton = (Button) v;
                changeCarouselButtonDesign(currentCarouselButton, true);
                if (((Button) v).getText().equals("Всё") || ((Button) v).getText().equals("Рекомендации") || ((Button) v).getText().equals("Доступная среда")){
                    tag = "def";
                    String count = "20";
                    mainEventsContainer.removeAllViews();
                    new IOAsyncTask().execute("http://192.168.31.187:8083/preview?page="+new Random().nextInt(15)+"&size="+count);
                }
                else{
                    tag = ((Button) v).getText().toString();
                    String count = "100";
                    mainEventsContainer.removeAllViews();
                    new IOAsyncTask().execute("http://192.168.31.187:8083/preview?page=0&size="+count);
                }
                break;
        }
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

            if(tag.equals("def") || previews.get(i).sphere.get(0).equals(tag) || previews.get(i).sphere.size()>1 && previews.get(i).sphere.get(1).equals(tag)) {

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
                        Intent intent = new Intent(MainActivity.this, FullInfoActivity.class);
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
