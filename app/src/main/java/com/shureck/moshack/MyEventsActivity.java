package com.shureck.moshack;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class MyEventsActivity extends AppCompatActivity {

    LinearLayout plannedEventsContainer;
    LinearLayout visitedEventsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        plannedEventsContainer = findViewById(R.id.plannedContainer);
        visitedEventsContainer = findViewById(R.id.visitedContainer);

        LayoutInflater plannedEventsInflater = LayoutInflater
                .from(plannedEventsContainer.getContext());
        LayoutInflater visitedEventsInflater = LayoutInflater
                .from(visitedEventsContainer.getContext());

        for (int i = 0; i < 5; i++) {
            View newPlannedEvent = plannedEventsInflater.
                    inflate(R.layout.element, null);
            plannedEventsContainer.addView(newPlannedEvent);

            View newVisitedEvent = visitedEventsInflater
                    .inflate(R.layout.element_w_rating, null);
            visitedEventsContainer.addView(newVisitedEvent);
        }
    }
}