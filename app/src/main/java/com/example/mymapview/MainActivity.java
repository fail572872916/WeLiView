package com.example.mymapview;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    MoveAndCropRectView cropRectView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cropRectView = (MoveAndCropRectView)findViewById(R.id.main_img);
        RectF rectF = new RectF(330, 120, 660, 420);
        cropRectView.setRectF(rectF);

        cropRectView.setLocationListener(new MoveAndCropRectView.onLocationListener() {
            @Override
            public void locationRect(float startX, float startY, float endX, float endY) {
                Log.e("MainActivity","[ startX:(" + startX + ")--startY:(" + startY + ")--endX:(" + endX + ")--endY:(" + endY + ") ]");
            }
        });
    }
}