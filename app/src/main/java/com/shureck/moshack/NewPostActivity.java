package com.shureck.moshack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewPostActivity extends AppCompatActivity {

    Button buttonPinEvent;
    Button buttonPublish;
    EditText postHeader;
    EditText postImage;
    EditText postText;
    String strr;
    private String token;
    private final OkHttpClient client = new OkHttpClient();
    List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        WorkWithToken workWithToken = new WorkWithToken(NewPostActivity.this);
        token = workWithToken.readToken();

        buttonPinEvent = findViewById(R.id.buttonPinBackEvent);
        buttonPublish = findViewById(R.id.buttonPublish);
        postHeader = findViewById(R.id.postHeader);
        postImage = findViewById(R.id.postImage);
        postText = findViewById(R.id.postText);

        buttonPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IOAsyncTask().execute("http://192.168.31.187:8083/user/setPost");
            }
        });

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
            } else {
                postHeader.setText(extras.getString("postHeader"));
                postImage.setText(extras.getString("postImage"));
                postText.setText(extras.getString("postText"));
                list = extras.getStringArrayList("list");
            }
        }
        System.out.println("QQQQQQ "+ list);

        buttonPinEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewPostActivity.this, NewPostAddEventActivity.class);
                intent.putExtra("postHeader",postHeader.getText().toString());
                intent.putExtra("postImage",postImage.getText().toString());
                intent.putExtra("postText",postText.getText().toString());
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
            strr = response;
        }
    }

    public static <T> List<T> stringToArray(String s, Class<T[]> clazz) {
        T[] arr = new Gson().fromJson(s, clazz);
        return Arrays.asList(arr); //or return Arrays.asList(new Gson().fromJson(s, clazz)); for a one-liner
    }

    public String sendData(String str){
        try {


//            RequestBody formBody = new FormBody.Builder()
//                    .add("title", postHeader.getText().toString())
//                    .add("text", postText.getText().toString())
//                    .add("jpgUrl", new Gson().toJson(postImage.getText().toString().split("\\ ")))
//                    .add("itemId", new Gson().toJson(list))
//                    .build();
            PostModel postModel = new PostModel();
            postModel.text = postText.getText().toString();
            postModel.title = postHeader.getText().toString();
            postModel.itemId = list.stream().map(Integer::parseInt).collect(Collectors.toList());
            postModel.jpgUrl = Arrays.asList(postImage.getText().toString().split("\\ "));

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, new Gson().toJson(postModel));

            Request request = new Request.Builder()
                    .url(str)
                    .header("Authorization","Bearer "+token)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

}