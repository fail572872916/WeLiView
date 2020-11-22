package com.example.mymapview;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import com.views.graphics.CustomGraphicsView;

public class PolygonActivity extends AppCompatActivity {

    CustomGraphicsView customGraphicsView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polygon);
        customGraphicsView = findViewById(R.id.customGraphicsView);
    }




}