package com.example.mymapview;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MoveAndCropRectView extends View {

    // 绘制 损害框和损害名称
    private Paint mPaint;
    private Paint mOldPaint;
    private Paint mAddPaint;
    private RectF mRectF;

    private RectF mRectFOld;

    private Paint mCirclePaint;

    private Paint mInteriorCirclePaint;
    private Paint mTextPaint;
    // 概率
    private float mConfidence;


    //线中间添加点的集合
    List<Point> addPointList = new ArrayList<>();
    List<Point> pointList = new ArrayList<>();

    List<Point> oldPointList = new ArrayList<>();


    // Remove Rect
    private int MODE;
    private static final int MODE_OUTSIDE = 0x000000aa;/*170*/
    private static final int MODE_INSIDE = 0x000000bb;/*187*/
    private static final int MODE_POINT = 0X000000cc;/*204*/
    private static final int MODE_ILLEGAL = 0X000000dd;/*221*/
    private static final int MODE_ADD = 0X000000ee;/*221*/

    private float startX; //x轴起点
    private float startY;//y轴起点
    private float endX;//x轴终点
    private float endY;//y轴终点


    private float currentX;/*X coordinate values while finger press*/
    private float currentY;/*Y coordinate values while finger press*/

    private float memoryX;/*the last time the coordinate values of X*/
    private float memoryY;/*the last time the coordinate values of Y*/

    private float mCoverWidth;/*width of selection box*/
    private float mCoverHeight;/*height of selection box*/
    private Point movePoint;
    private int moveIndex;


    private static final float mRoundSize = 25.0f;/*the minimum height of the rectangle*/

    private onLocationListener mLocationListener;/*listen to the Rect */

    private static final float DRAG_SPEED = 1.8f; //拖动时的速度

    public MoveAndCropRectView(Context context) {
        this(context, null);
    }

    public MoveAndCropRectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MoveAndCropRectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDatas(context);
    }

    private void initDatas(Context context) {
        mPaint = new Paint();
        mOldPaint = new Paint();
        mCirclePaint = new Paint();
        mInteriorCirclePaint = new Paint();
        mAddPaint = new Paint();
        mRectF = new RectF();
        mRectFOld = new RectF();

        //画笔设置空心
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#AEEEEE"));
        mPaint.setStrokeWidth(5);
        mPaint.setAntiAlias(true);


        mInteriorCirclePaint.setStyle(Paint.Style.STROKE);
        mInteriorCirclePaint.setColor(Color.parseColor("#00bbff"));
        mInteriorCirclePaint.setStrokeWidth(8);
        mInteriorCirclePaint.setAntiAlias(true);

        mCirclePaint.setStyle(Paint.Style.FILL);
//        mCirclePaint.setColor(Color.RED);
        mCirclePaint.setColor(Color.parseColor("#BFEFFF"));
        mCirclePaint.setAntiAlias(true);

        mAddPaint.setStyle(Paint.Style.FILL);
        mAddPaint.setColor(Color.parseColor("#EEC900"));

        mAddPaint.setAntiAlias(true);

        mOldPaint.setStyle(Paint.Style.STROKE);
        mOldPaint.setColor(Color.GRAY);
        mOldPaint.setStrokeWidth(5);
        mOldPaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mCirclePaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(Color.parseColor("#F7F7F7"));
//        Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
//        mTextPaint.setTypeface(font);//文字的样式(加粗)
        mTextPaint.setFakeBoldText(true);

        mTextPaint.setTextSize(30);

        currentX = 0;
        currentY = 0;
    }

    private boolean firstDraw = true;

    private void getAddPoint() {
        addPointList.clear();
        //具体的形状
        for (int i = 0; i < pointList.size(); i++) {
            if (i < pointList.size() - 1) {
                addPointList.add(new Point(((pointList.get(i + 1).getPointX() - pointList.get(i).getPointX()) / 2) + pointList.get(i).getPointX(), ((pointList.get(i + 1).getPointY() - pointList.get(i).getPointY()) / 2) + pointList.get(i).getPointY()));
            }
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (firstDraw) {
            firstDraw = false;
            startX = mRectF.left;
            startY = mRectF.top;
            endX = mRectF.right;
            endY = mRectF.bottom;
            mCoverWidth = mRectF.width();
            mCoverHeight = mRectF.height();


            pointList.add(new Point(startX, startY));
            pointList.add(new Point(endX, startY));
            pointList.add(new Point(endX, endY));
            pointList.add(new Point(startX, endY));
            pointList.add(new Point(startX, startY));
            //起始点-首位结合

            //初始旧点
            copyOldPath();
            getAddPoint();


        }
        if (mLocationListener != null) {
            mLocationListener.locationRect(startX, startY, endX, endY);
        }

        //具体的形状
        for (int i = 0; i < pointList.size(); i++) {
            if (i < pointList.size() - 1) {
                canvas.drawLine(pointList.get(i).getPointX(), pointList.get(i).getPointY(), pointList.get(i + 1).getPointX(), pointList.get(i + 1).getPointY(), mPaint);


            }
        }

        //拖动之前的
        for (int i = 0; i < oldPointList.size(); i++) {
            if (i < oldPointList.size() - 1) {
                canvas.drawLine(oldPointList.get(i).getPointX(), oldPointList.get(i).getPointY(), oldPointList.get(i + 1).getPointX(), oldPointList.get(i + 1).getPointY(), mOldPaint);
            }
        }

        for (Point point : addPointList) {
            canvas.drawCircle(point.getPointX(), point.getPointY(), mRoundSize, mAddPaint);
            // 居中画一个文字
            float baseX =point.getPointX()-(mRoundSize/2)-2;

            // 计算Baseline绘制的Y坐标 ，计算方式：画布高度的一半 - 文字总高度的一半
            float baseY =  point.getPointY()+(mRoundSize/2)-2;
            canvas.drawText("＋", baseX, baseY, mTextPaint);
        }


//        //回执定点之间的节点
        for (Point point : pointList) {

            canvas.drawCircle(point.getPointX(), point.getPointY(), mRoundSize, mCirclePaint);
            canvas.drawCircle(point.getPointX(), point.getPointY(), mRoundSize / 2, mInteriorCirclePaint);
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                memoryX = event.getX();
                memoryY = event.getY();
                checkMode(memoryX, memoryY);
                copyOldPath();

                mOldPaint.setColor(Color.BLACK);
                //添加线段之间的中间点
                if (isContainPoint(new Point(memoryX, memoryY))) {
                    MODE = MODE_ADD;
                    addPoint(memoryX, memoryY);
                }
                getDragPoint();

                break;
            case MotionEvent.ACTION_MOVE: {
                mOldPaint.setColor(Color.GRAY);
                currentX = event.getX();
                currentY = event.getY();
                switch (MODE) {
                    case MODE_ILLEGAL:
                        recoverFromIllegal(currentX, currentY);
                        postInvalidate();
                        break;
                    case MODE_OUTSIDE:
                        //do nothing;
                        break;
                    case MODE_INSIDE://拖动.

                        moveByTouch(currentX, currentY);
//                        invalidate();
                        postInvalidate();
                        break;
                    default:
                        /*MODE_POINT*/
                        moveByPoint(currentX, currentY);
                        postInvalidate();
                        break;
                }
            }
            break;
            case MotionEvent.ACTION_UP:

                mOldPaint.setColor(Color.TRANSPARENT);


                break;
            default:
                mOldPaint.setColor(Color.TRANSPARENT);
                break;
        }

        return true;
    }

    /*点击顶点附近时的缩放处理*/

    private void moveByPoint(float bx, float by) {


        float dX = bx - memoryX;
        float dY = by - memoryY;
        //拖动速度
        if (dX > 0) {
            dX = DRAG_SPEED;
        } else {
            dX = -DRAG_SPEED;
        }
        if (dY > 0) {
            dY = DRAG_SPEED;
        } else {
            dY = -DRAG_SPEED;
        }

        //更改整体点中某一个
        movePoint.setPointX(movePoint.getPointX() + dX);
        movePoint.setPointY(movePoint.getPointY() + dY);
        pointList.set(moveIndex, movePoint);

        //处理点击收尾部分问题
        if (moveIndex == pointList.size() - 1) {
            movePoint.setPointX(movePoint.getPointX() + dX);
            movePoint.setPointY(movePoint.getPointY() + dY);
            pointList.set(0, movePoint);
        }
        getAddPoint();
    }

    /**
     * 点击某个点进行拖拽时记录按下时的点位
     */
    private void getDragPoint() {
        for (int i = 0; i < pointList.size(); i++) {
            if (Math.abs(memoryX - pointList.get(i).getPointX()) < mRoundSize && Math.abs(memoryY - pointList.get(i).getPointY()) < mRoundSize) {
                movePoint = pointList.get(i);
                moveIndex = i;
            }
        }
    }

    /**
     * 添加点
     *
     * @param currentX
     * @param currentY
     */
    private void addPoint(float currentX, float currentY) {
        for (Point point : addPointList) {
            if (Math.abs(currentX - point.getPointX()) < mRoundSize && Math.abs(currentY - point.getPointY()) < mRoundSize) {
                int addIndex = getPointIndex(point);
                pointList.add(addIndex + 1, point);
                getAddPoint();
                postInvalidate();
                return;
            }
        }
    }

    /**
     * 判断添加点是否在其中
     *
     * @param point
     * @return
     */
    private boolean isContainPoint(Point point) {
        for (int i = 0; i < addPointList.size(); i++) {
            if (Math.abs(point.getPointX() - addPointList.get(i).getPointX()) < mRoundSize && Math.abs(point.getPointY() - addPointList.get(i).getPointY()) < mRoundSize) {
                return true;
            }
        }
        return false;
    }

    /**
     * 得到点击点的index
     *
     * @param point
     * @return
     */
    private int getPointIndex(Point point) {
        for (int i = 0; i < addPointList.size(); i++) {
            if (Math.abs(point.getPointX() - addPointList.get(i).getPointX()) < mRoundSize && Math.abs(point.getPointY() - addPointList.get(i).getPointY()) < mRoundSize) {
                return i;
            }
        }
        return 0;
    }


    /*从非法状态恢复，这里处理的是达到最小值后能拉伸放大*/
    private void recoverFromIllegal(float rx, float ry) {
        if ((rx > startX && ry > startY) && (rx < endX && ry < endY)) {
            MODE = MODE_ILLEGAL;
        } else {
            MODE = MODE_POINT;
        }
    }

    /**
     * 判断点在矩形的什么位置
     *
     * @param cx
     * @param cy
     */
    private void checkMode(float cx, float cy) {
        Point point = new Point(cx, cy);

        if (cx > startX && cx < endX && cy > startY && cy < endY) {
            MODE = MODE_INSIDE;//矩形内部
            Log.d("MoveAndCropRectView", ":偏移量juxi:矩形内部");
        } else if (nearbyPoint(cx, cy)) {
            MODE = MODE_POINT;//矩形点上
            Log.d("MoveAndCropRectView", "偏移量juxi:矩形点上");
        } else {
            MODE = MODE_OUTSIDE;//矩形外部
            Log.d("MoveAndCropRectView", "偏移量juxi:矩形外部");
        }
    }

    /*矩形随手指移动*/
    private void moveByTouch(float mx, float my) {/*move center point*/
        float dX = mx - memoryX;
        float dY = my - memoryY;

        startX += dX;
        startY += dY;
        if (startX <= 0) {
            startX = 0;
        }
        if (startY <= 0) {
            startY = 0;
        }
        endX = startX + mCoverWidth;
        endY = startY + mCoverHeight;
        if (endX >= 1920) {
            endX = 1920;
            startX = endX - mCoverWidth;
        }
        if (endY >= 1080) {
            endY = 1080;
            startY = endY - mCoverHeight;
        }
        for (Point point : addPointList) {
            point.setPointX(point.getPointX() + dX);
            point.setPointY(point.getPointY() + dY);
        }
        for (Point point : pointList) {
            point.setPointX(point.getPointX() + dX);
            point.setPointY(point.getPointY() + dY);
        }
        getAddPoint();


        Log.d("MoveAndCropRectView:偏移量", "dX:" + dX + "____________dY:" + dY);
        memoryX = mx;
        memoryY = my;
    }

    /*判断点(inX,inY)是否靠近矩形的4个顶点*/
    private boolean nearbyPoint(float floatX, float floatY) {
        for (Point point : pointList) {
            if (Math.abs(floatX - point.getPointX()) < mRoundSize && Math.abs(floatY - point.getPointY()) < mRoundSize) {
                return true;
            }
        }
        return false;
    }

    // 设置矩形框
    public void setRectF(RectF rectf) {
        this.mRectF = rectf;
    }


    public void setConfidence(float confidence) {
        mConfidence = confidence;
    }


    public void setLocationListener(onLocationListener mLocationListener) {
        this.mLocationListener = mLocationListener;
    }

    public interface onLocationListener {
        void locationRect(float startX, float startY, float endX, float endY);
    }

    //复制旧点路径
    private void copyOldPath() {
        oldPointList.clear();
        for (Point point : pointList) {
            Point point1 = null;
            try {
                point1 = (Point) point.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            oldPointList.add(point1);
        }
    }


}