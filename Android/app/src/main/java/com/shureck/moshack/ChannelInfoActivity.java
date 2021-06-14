package com.shureck.moshack;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.airbnb.paris.Paris;

public class ChannelInfoActivity extends AppCompatActivity {

    LinearLayout channelEventsContainer;
    Button subsButton;
    ImageView subsCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_info);

        channelEventsContainer = findViewById(R.id.channelEventsContainer);
        subsButton = findViewById(R.id.subscribeButton);
        subsCheck = findViewById(R.id.subscribeCheck);

        LayoutInflater eventsInflater = LayoutInflater.from(channelEventsContainer.getContext());
        for (int i = 0; i < 5; i++) {
            View channelEvent = eventsInflater.inflate(R.layout.element, null);
            channelEventsContainer.addView(channelEvent);
        }
    }
}