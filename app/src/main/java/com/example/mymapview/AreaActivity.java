package com.example.mymapview;

import android.os.Bundle;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.views.graphics.GraphicsView;


public class AreaActivity extends AppCompatActivity {
    GraphicsView graphicsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area);

        graphicsView = findViewById(R.id.graphicsView);
    }


    public void complateAction(View view) {
        graphicsView.getPointBeans();
        graphicsView.setDottedLine(false);
    }

    public void delAction(View view) {
        graphicsView.delPoint();
    }

    public void cleanAction(View view) {
        graphicsView.clearGraphics();
    }
}
