package com.shureck.moshack;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class AuthorChannelActivity extends AppCompatActivity {

    LinearLayout authorContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_channel);

        authorContainer = findViewById(R.id.channelFeedContainer);
        LayoutInflater eventsInflater = LayoutInflater.from(authorContainer.getContext());

        for (int i = 0; i < 5; i++) {
            View channelEvent = eventsInflater.inflate(R.layout.post_short_info, null);
            authorContainer.addView(channelEvent);
        }
    }
}