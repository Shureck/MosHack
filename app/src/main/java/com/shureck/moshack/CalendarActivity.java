package com.shureck.moshack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class CalendarActivity extends AppCompatActivity implements View.OnClickListener{

    public GridLayout gridLayout;
    public ArrayList<View> surveyButtons;
    public ArrayList<Boolean> selectedGenres;
    public ArrayList<SurveyButtonContent> buttonContents;
    public ArrayList<String> genresToSend;
    String str;
    Button button;
    int tappedButtons;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calendar);

        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year,
                                            int month, int dayOfMonth) {
                int mYear = year;
                int mMonth = month;
                int mDay = dayOfMonth;
                String selectedDate = new StringBuilder().append(mMonth + 1)
                        .append("-").append(mDay).append("-").append(mYear)
                        .append(" ").toString();
                Toast.makeText(getApplicationContext(), selectedDate, Toast.LENGTH_LONG).show();
            }
        });

        selectedGenres = new ArrayList<>();
        buttonContents = new ArrayList<>();
        genresToSend = new ArrayList<>();

        buttonContents = SurveyHelper.fillSurveyContent();
        tappedButtons = 0;

        setData();
    }

    public void setData(){

        button = findViewById(R.id.button);
        gridLayout = findViewById(R.id.grid);

        LayoutInflater inflater = LayoutInflater.from(gridLayout.getContext());

        button.setOnClickListener(this);

        surveyButtons = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            View newGenreButton = inflater.inflate(R.layout.element_calendar, null);

            ImageView genreImage = newGenreButton.findViewById(R.id.eventImageView);
            TextView dateTextView = newGenreButton.findViewById(R.id.dateTextView);
            TextView sphereTextView = newGenreButton.findViewById(R.id.sphereTextView);
            TextView freeTextView = newGenreButton.findViewById(R.id.freeTextView);
            TextView eventHeader = newGenreButton.findViewById(R.id.eventHeader);

            String sss = "";

//            imageLoader.loadImage("https://www.mos.ru/upload/newsfeed/afisha/images9wlNA3(2)(15).jpg", new SimpleImageLoadingListener() {
//                @Override
//                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                    BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), loadedImage);
//                    genreImage.setBackgroundDrawable(bitmapDrawable);
//                }
//            });

            surveyButtons.add(newGenreButton.findViewById(R.id.imageButton));
            selectedGenres.add(false);

            newGenreButton.findViewById(R.id.imageButton).setOnClickListener(this);

            gridLayout.addView(newGenreButton);
        }
    }

    @Override
    public void onClick(View v) {

    }
}
