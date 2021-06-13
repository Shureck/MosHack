package com.shureck.moshack;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class NewPostAddEventActivity extends AppCompatActivity {

    LinearLayout pinEventContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post_add_event);

        pinEventContainer = findViewById(R.id.pinEventContainer);
        LayoutInflater inflater = LayoutInflater.from(pinEventContainer.getContext());
        for (int i = 0; i < 5; i++) {
            View newEventToPin = inflater.inflate(R.layout.short_event_info, null);
            pinEventContainer.addView(newEventToPin);
        }
    }
}