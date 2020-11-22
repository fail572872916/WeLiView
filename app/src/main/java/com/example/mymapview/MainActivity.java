package com.example.mymapview;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void gotoAreaActivity(View view) {
        startActivity(new Intent(this, AreaActivity.class));
    }

    public void gotoMutilAreaActivity(View view) {
        startActivity(new Intent(this, MutilAreaActivity.class));
    }
    public void gotoPolygonActivity(View view) {
        startActivity(new Intent(this, PolygonActivity.class));
    }   public void gotoMain(View view) {
        startActivity(new Intent(this, MainActivity2.class));
    }
}
