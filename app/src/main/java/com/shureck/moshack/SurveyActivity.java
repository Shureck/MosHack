package com.shureck.moshack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class SurveyActivity extends AppCompatActivity {

    GridLayout spheresQuestionContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        spheresQuestionContainer = findViewById(R.id.spheresContainer);

        LayoutInflater spheresInflater = LayoutInflater
                .from(spheresQuestionContainer.getContext());

        for (int i = 0; i < 12; i++) {
            View queryElement = spheresInflater
                    .inflate(R.layout.spheres_query_element, null);
            spheresQuestionContainer.addView(queryElement);
        }

    }
}