package com.shureck.moshack;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.shureck.moshack.FullData;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FullInfoActivity extends AppCompatActivity {


    private final OkHttpClient client = new OkHttpClient();
    private String token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullinfoactivity);

        WorkWithToken workWithToken = new WorkWithToken(FullInfoActivity.this);
        token = workWithToken.readToken();

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        System.out.println(id);

        new IOAsyncTask().execute("http://192.168.31.187:8083/user/putIntem?id="+id);
    }

    public void setData(FullData previews){

        View view = findViewById(android.R.id.content).getRootView();

        TextView fullInfoHeader = view.findViewById(R.id.fullInfoHeader);
        TextView placeTextView = view.findViewById(R.id.placeTextView);
        TextView timeTextView = view.findViewById(R.id.timeTextView);
        TextView phoneTextView = view.findViewById(R.id.phoneTextView);
        TextView fullInfoDesc = view.findViewById(R.id.fullInfoDesc);
        ImageView eventImageFullInfo = view.findViewById(R.id.eventImageFullInfo);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fullInfoDesc.setText(Html.fromHtml(previews.text, Html.FROM_HTML_MODE_COMPACT));
        } else {
            fullInfoDesc.setText(Html.fromHtml(previews.text));
        }

        SimpleDateFormat sddd = new SimpleDateFormat("d MMMM, HH:mm");
        SimpleDateFormat sdd = new SimpleDateFormat("HH:mm");

        fullInfoHeader.setText(previews.title);
        placeTextView.setText(previews.address.get(0).address);
        timeTextView.setText(sddd.format(new Date(previews.date_from_timestamp*1000))+"–"+sdd.format(new Date(previews.date_to_timestamp*1000)));
        phoneTextView.setText(previews.phone);

        phoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+phoneTextView.getText()));
                startActivity(intent);
            }
        });

        ImageLoader imageLoader = ImageLoader.getInstance();
        initImageLoader(getApplicationContext());

        imageLoader.displayImage(previews.jpgUrl, eventImageFullInfo);

        Button button = findViewById(R.id.mapButtonFullInfo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("geo:0,0?q="+previews.address.get(0).address));
                startActivity(intent);
            }
        });


    }

    class IOAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return sendData(params[0]);
        }

        @Override
        protected void onPostExecute(String response) {
            String strr = response;
            Gson gson = new Gson();
            FullData previews = gson.fromJson(strr, FullData.class);
            System.out.println("DDD "+previews.title);
            setData(previews);
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
