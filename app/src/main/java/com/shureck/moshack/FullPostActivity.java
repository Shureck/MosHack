package com.shureck.moshack;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class FullPostActivity extends AppCompatActivity {

    LinearLayout pinnedEventsContainer;
    LinearLayout commentsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_post);

        pinnedEventsContainer = findViewById(R.id.pinnedEventsContainer);
        commentsContainer = findViewById(R.id.commentsContainer);

        LayoutInflater inflaterPinned = LayoutInflater.from(pinnedEventsContainer.getContext());
        LayoutInflater inflaterComments = LayoutInflater.from(commentsContainer.getContext());

        for (int i = 0; i < 3; i++) {
            View newEventToPin = inflaterPinned.inflate(R.layout.short_event_info, null);
            View newComment = inflaterPinned.inflate(R.layout.comment_layout, null);

            pinnedEventsContainer.addView(newEventToPin);
            commentsContainer.addView(newComment);
        }
    }
}