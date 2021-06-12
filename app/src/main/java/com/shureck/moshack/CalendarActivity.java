package com.shureck.moshack;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CalendarActivity extends AppCompatActivity implements View.OnClickListener{

    public GridLayout gridLayout;
    public ArrayList<View> surveyButtons;
    public ArrayList<Boolean> selectedGenres;
    public ArrayList<SurveyButtonContent> buttonContents;
    public ArrayList<String> genresToSend;
    String str;
    Button button;
    int tappedButtons;

    LinearLayout linearLayout;
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calendar);

        TextView textView = findViewById(R.id.calendarToday);

        CalendarView calendarView = findViewById(R.id.calendarView);

        Calendar date = Calendar.getInstance();

        calendarView.setDate(date.getTime().getTime());
        SimpleDateFormat sd = new SimpleDateFormat("d MMMM, EEEE");
        textView.setText(sd.format(date.getTime().getTime()));

        SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd");
        new IOAsyncTask().execute("http://192.168.31.187:8083/findByDate?page=0&size=10&date="+sdff.format(date.getTime().getTime()));

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year,
                                            int month, int dayOfMonth) {
                int mYear = year;
                int mMonth = month;
                int mDay = dayOfMonth;
                SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd");
                System.out.println(sdff.format(new GregorianCalendar(mYear, mMonth, mDay).getTime()));
                SimpleDateFormat sd = new SimpleDateFormat("d MMMM, EEEE");
                textView.setText(sd.format(new GregorianCalendar(mYear, mMonth, mDay).getTime()));

                new IOAsyncTask().execute("http://192.168.31.187:8081/findByDate?page=0&size=10&date="+sdff.format(new GregorianCalendar(mYear, mMonth, mDay).getTime()));
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    public void setData(List<DateJson> previews){

        selectedGenres = new ArrayList<>();
        buttonContents = new ArrayList<>();
        genresToSend = new ArrayList<>();

        //buttonContents = SurveyHelper.fillSurveyContent();
        tappedButtons = 0;
        linearLayout = findViewById(R.id.eventContainer);

        LayoutInflater inflater = LayoutInflater.from(linearLayout.getContext());

        selectedGenres = new ArrayList<>();
        buttonContents = new ArrayList<>();
        genresToSend = new ArrayList<>();

        //buttonContents = SurveyHelper.fillSurveyContent();
        tappedButtons = 0;

        SimpleDateFormat sddd = new SimpleDateFormat("HH:mm");

        linearLayout.removeAllViews();

        for (int i = 0; i < previews.size(); i++) {
            View calendarEventView = inflater.inflate(R.layout.element_calendar, null);

            TextView calendarEventSphere = calendarEventView.findViewById(R.id.calendarEventSphere);
            TextView calendarEventFree = calendarEventView.findViewById(R.id.calendarEventFree);
            TextView calendarEventName = calendarEventView.findViewById(R.id.calendarEventName);
            TextView calendarEventTime = calendarEventView.findViewById(R.id.calendarEventTime);

            calendarEventSphere.setText(previews.get(i).sphere.get(0));
            calendarEventFree.setText(previews.get(i).free.toString());
            calendarEventName.setText(previews.get(i).title);
            calendarEventTime.setText(sddd.format(new Date(previews.get(i).date_from_timestamp*1000)));

            linearLayout.addView(calendarEventView);
        }
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
            List<DateJson> previews = stringToArray(strr, DateJson[].class);
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
