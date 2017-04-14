package com.example.jokklan.finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class LocationResultActivity extends AppCompatActivity {
    public static final String PLACE_EXTRA = "com.example.jokklan.finalproject.extra.place";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_result);
        Log.d("LocationResultActivity", "I'm running");

        Intent intent = getIntent();
        String place = intent.getStringExtra(PLACE_EXTRA);
        Log.d("LocationResultActivity", "place: " + place);

        TextView textView = (TextView) findViewById(R.id.locationResultMessage);
        textView.setText(place);
    }
}
