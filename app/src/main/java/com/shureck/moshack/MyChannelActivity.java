package com.shureck.moshack;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class MyChannelActivity extends AppCompatActivity {

    LinearLayout universalContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_channel);

        universalContainer = findViewById(R.id.universalContainer);
        LayoutInflater eventsInflater = LayoutInflater.from(universalContainer.getContext());

        for (int i = 0; i < 5; i++) {
            View channelEvent = eventsInflater.inflate(R.layout.post_short_info, null);
            universalContainer.addView(channelEvent);
        }
    }
}