package com.example.mymapview;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * 自定义的View
 */

public class MyImageView extends ImageView {

    private int lastX;
    private int lastY;

    public MyImageView(Context context) {
        super(context);

    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }



    public boolean onTouchEvent(MotionEvent event) {

        //获取到手指处的横坐标和纵坐标
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN://手指按下时
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE://手指移动时
                //计算移动的距离
                int offX = x - lastX;
                int offY = y - lastY;
                //调用layout方法来重新放置它的位置
                Log.e("TAG","距离左边="+getLeft()+offX+",距离顶部="+getTop()+offY+",距离右边="+ getRight()+offX+",距离底部="+ getBottom()+offY);

                movingXY(offX,offY);

                break;

            case MotionEvent.ACTION_UP:


                break;

        }
        return true;
    }

    public void movingXY(int offX,int offY){
        //移动View的关键代码
        layout(getLeft()+offX, getTop()+offY,    getRight()+offX    , getBottom()+offY);
    }


}