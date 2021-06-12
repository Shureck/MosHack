package com.shureck.moshack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ChannelsActivity extends AppCompatActivity {

    LinearLayout subsList;
    LinearLayout channelsList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_channel);
        subsList = findViewById(R.id.subsContainer);
        channelsList = findViewById(R.id.channelsContainer);

        LayoutInflater subsInflater = LayoutInflater.from(subsList.getContext());
        LayoutInflater channelsInflater = LayoutInflater.from(channelsList.getContext());

        for (int i = 0; i < 5; i++) {
            View subView = subsInflater.inflate(R.layout.channel_item, null);
            View channelView = channelsInflater.inflate(R.layout.channel_item, null);
            ImageView check = channelView.findViewById(R.id.checkSub);
            check.setVisibility(View.INVISIBLE);
            subsList.addView(subView);
            channelsList.addView(channelView);
        }
    }
}
