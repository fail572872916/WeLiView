package com.example.mymapview;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

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

    // 边缘字体
    // private BorderedText mBorderedText;

    // 标题 或 名字
    private String mTitle;
    // 概率
    private float mConfidence;

    // 矩形框 corner 的角度：直角、圆角
    private int mCornerAngle;

    //直角 默认
    public static final int RIGHT_CORNER = 0;
    //圆角
    public static final int ROUND_CORNER = 1;

    //线中间添加点的集合
    List<Point> addPointList = new ArrayList<>();
    List<Point> pointList = new ArrayList<>();
    List<Point> dragPointList = new ArrayList<>();
    List<Point> oldPointList = new ArrayList<>();
    List<Integer> indexMove = new ArrayList<>();

    // Remove Rect
    private int MODE;
    private static final int MODE_OUTSIDE = 0x000000aa;/*170*/
    private static final int MODE_INSIDE = 0x000000bb;/*187*/
    private static final int MODE_POINT = 0X000000cc;/*204*/
    private static final int MODE_ILLEGAL = 0X000000dd;/*221*/
    private static final int MODE_ADD = 0X000000ee;/*221*/

    private float startX;/*start X location*/
    private float startY;/*start Y location*/
    private float endX;/*end X location*/
    private float endY;/*end Y location*/

    private float oldStartX;/*start X location*/
    private float oldStartY;/*start Y location*/
    private float oldEndX;/*end X location*/
    private float oldEndY;/*end Y location*/
    List<Point> oldShape = new ArrayList<>();

    private float currentX;/*X coordinate values while finger press*/
    private float currentY;/*Y coordinate values while finger press*/

    private float memoryX;/*the last time the coordinate values of X*/
    private float memoryY;/*the last time the coordinate values of Y*/

    private float mCoverWidth;/*width of selection box*/
    private float mCoverHeight;/*height of selection box*/

    private static final int ACCURACY = 100;/*touch accuracy*/
    private int pointPosition;/*vertex of a rectangle*/

    private static final float minWidth = 100.0f;/*the minimum width of the rectangle*/
    private static final float minHeight = 200.0f;/*the minimum height of the rectangle*/

    private static final float mRoundSize = 25.0f;/*the minimum height of the rectangle*/

    private onLocationListener mLocationListener;/*listen to the Rect */

    private static final float EDGE_WIDTH = 1.8f;

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
        mAddPaint = new Paint();
        mRectF = new RectF();
        mRectFOld = new RectF();

        //画笔设置空心
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(2);
        mPaint.setAntiAlias(true);

        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(Color.RED);
        mCirclePaint.setAntiAlias(true);

        mAddPaint.setStyle(Paint.Style.FILL);
        mAddPaint.setColor(Color.BLUE);
        mAddPaint.setAntiAlias(true);

        mOldPaint.setStyle(Paint.Style.STROKE);
        mOldPaint.setColor(Color.GRAY);
        mOldPaint.setStrokeWidth(5);
        mOldPaint.setAntiAlias(true);

        currentX = 0;
        currentY = 0;
    }

    private boolean firstDraw = true;

    private void getAddPoint() {
        addPointList.clear();
        //具体的形状
        for (int i = 0; i < pointList.size(); i++) {
            if (i < pointList.size() - 1) {
                addPointList.add(new Point(((pointList.get(i+1).getPointX()-pointList.get(i).getPointX())/2)+pointList.get(i).getPointX(),((pointList.get(i+1).getPointY()-pointList.get(i).getPointY())/2)+pointList.get(i).getPointY()));
            }
        }
        for (Point point : addPointList) {
            Log.d("sssss", "ppppp:"+point);
        }
//        float a1 = ((endX - startX) / 2) + startX;
//        float a2 = ((endY - startY) / 2) + startY;
//        addPointList.add(new Point(a1, startY));
//        addPointList.add(new Point(endX, a2));
//        addPointList.add(new Point(a1, endY));
//        addPointList.add(new Point(startX, a2));
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

//            float a1 = ((endX - startX) / 2) + startX;
//            float a2 = ((endY - startY) / 2) + startY;
            pointList.add(new Point(startX, startY));
//            pointList.add(new Point(a1, startY));
            pointList.add(new Point(endX, startY));
//            pointList.add(new Point(endX, a2));
            pointList.add(new Point(endX, endY));
//            pointList.add(new Point(a1, endY));

            pointList.add(new Point(startX, endY));
//            pointList.add(new Point(startX, a2));
            pointList.add(new Point(startX, startY));
            //起始点-首位结合

            //初始旧点
            copyOldPath();
            getAddPoint();

            //添加可拖拽定点坐标
            dragPointList.add(new Point(startX, startY));
            dragPointList.add(new Point(endX, startY));
            dragPointList.add(new Point(endX, endY));
            dragPointList.add(new Point(startX, endY));
            dragPointList.add(new Point(startX, startY));
            Log.d("sssss", "ppppp:");
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

        if (MODE == MODE_POINT) {
            Log.d("MoveAndCropRectView", "AA");
            //起始点-首位结合
            addPointList.clear();
            for (int i = 0; i < pointList.size(); i++) {
                if (i % 2 != 0) {
                    addPointList.add(pointList.get(i));
                }
            }

        }


        Log.d("sssss", MODE + "__" + addPointList.size() + "__" + pointList.size() + "dragPointList:" + dragPointList.size());
//        Log.d("sssss", );
        for (Point point : addPointList) {

            canvas.drawCircle(point.getPointX(), point.getPointY(), mRoundSize, mAddPaint);
        }
        for (Point point : pointList) {
            Log.d("MoveAndCropRectView", "point:" + point);
        }

//        //回执定点之间的节点
        for (Point point : dragPointList) {
            Log.d("ccccccc", "dragPointList:" + point);
            canvas.drawCircle(point.getPointX(), point.getPointY(), mRoundSize, mCirclePaint);
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                memoryX = event.getX();
                memoryY = event.getY();
//                checkMode(memoryX, memoryY);
                copyOldPath();
                mOldPaint.setColor(Color.BLACK);
                Log.d("MoveAndCropRectView", "oleeeeeeeeeeeeeeee):" + oldPointList.get(1));

//                Log.d("MoveAndCropRectView", "oleeeeeeeeeeeeeeee):" + oldPointList.get(1));
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

                Log.d("MoveAndCropRectView", "沃天");


                mOldPaint.setColor(Color.TRANSPARENT);

                if (isContainPoint(new Point(memoryX, memoryY))) {

                    MODE = MODE_ADD;
                    addPoint(memoryX, memoryY);

                }
                postInvalidate();
                break;
            default:
                break;
        }
        Log.d("MoveAndCropRectView", "非法模式恢复" + event.getAction());
        return true;
    }

    /*点击顶点附近时的缩放处理*/

    private void moveByPoint(float bx, float by) {
//  LogUtils.d("moveByPoint");

        float dX = bx - memoryX;
        float dY = by - memoryY;

        Log.d("MoveAndCropRectView", "dX:" + dX + "dY:" + dY);
        if (Math.abs((int) dX) > Math.abs((int) dY)) {
            dY = 0;
        } else {
            dX = 0;
        }
        //更改整体点中某一个
        for (int i = 0; i < pointList.size(); i++) {
            if (Math.abs(memoryX - pointList.get(i).getPointX()) < mRoundSize && Math.abs(memoryY - pointList.get(i).getPointY()) < mRoundSize) {
                pointList.get(i).setPointX(pointList.get(i).getPointX() + dX);
                pointList.get(i).setPointY(pointList.get(i).getPointY() + dY);
            }
        }

        //更改当前拖动的点
        for (int i = 0; i < dragPointList.size(); i++) {
            if (Math.abs(memoryX - dragPointList.get(i).getPointX()) < mRoundSize && Math.abs(memoryY - dragPointList.get(i).getPointY()) < mRoundSize) {

                dragPointList.get(i).setPointX(dragPointList.get(i).getPointX() + dX);
                dragPointList.get(i).setPointY(dragPointList.get(i).getPointY() + dY);
            }
        }
        getAddPoint();
//        getMoveIndex();
    }

    private void getMoveIndex() {
        indexMove.clear();
        for (int i = 0; i < pointList.size(); i++) {
            for (Point point1 : addPointList) {
                if (pointList.get(i).getPointX() == point1.getPointX() && pointList.get(i).getPointY() == point1.getPointY())
                    indexMove.add(i);
            }
        }
    }

    private void addPoint(float currentX, float currentY) {
        for (Point point : addPointList) {
            if (Math.abs(currentX - point.getPointX()) < mRoundSize && Math.abs(currentY - point.getPointY()) < mRoundSize) {
                int addIndex = getPointIndex(point);
                List<Point> list = getAdjoinPoint(point);
                Log.d("add", list.size() + ":_________index:" + addIndex + "_____:" + addPointList.size());
                float a = ((list.get(1).getPointX() - list.get(0).getPointX()) / 2) + list.get(0).getPointX();
                float b = ((list.get(2).getPointX() - list.get(1).getPointX()) / 2) + list.get(1).getPointX();
                float c = ((list.get(1).getPointY() - list.get(0).getPointY()) / 2) + list.get(0).getPointY();
                float d = ((list.get(2).getPointY() - list.get(1).getPointY()) / 2) + list.get(1).getPointY();


                if (list.get(0).getPointY() == list.get(1).getPointY()) {
                    addPointList.add(addIndex, new Point(a, list.get(0).getPointY()));
                    pointList.add(getCountPoint2Index(point), new Point(a, list.get(1).getPointY()));

                    dragPointList.add(addIndex, new Point(list.get(1).getPointX(), list.get(1).getPointY()));

                }


                if (list.get(1).getPointY() == list.get(2).getPointY()) {
                    addPointList.add(addIndex + 2, new Point(b, list.get(0).getPointY()));
                }

                if (list.get(0).getPointX() == list.get(1).getPointX()) {
                    addPointList.add(addIndex, new Point(list.get(0).getPointX(), c));
                }

                if (list.get(1).getPointX() == list.get(2).getPointX()) {
                    if (addIndex + 2 >= addPointList.size()) {
                        addPointList.add(new Point(list.get(0).getPointX(), d));
                    } else {
                        addPointList.add(addIndex + 2, new Point(list.get(0).getPointX(), d));
                    }
                    pointList.add(getCountPoint2Index(point) + 1, new Point(list.get(0).getPointX(), d));
                    dragPointList.add(addIndex, new Point(list.get(1).getPointX(), list.get(1).getPointY()));
                }
                removeRepetition();
                postInvalidate();


                return;
            }
        }


    }

    /**
     * 删除添加点集合与可拖动定点的交叉点
     */
    private void removeRepetition() {
        for (int i = 0; i < addPointList.size(); i++) {
            for (Point point : dragPointList) {
                if (addPointList.get(i).getPointX() == point.getPointX() && addPointList.get(i).getPointY() == point.getPointY()) {
                    addPointList.remove(i);

                }
            }
        }
    }


    private List<Point> getAdjoinPoint(Point point) {
        List<Point> list = new ArrayList<>();
        for (int i = 0; i < pointList.size(); i++) {
            if (Math.abs(point.getPointX() - pointList.get(i).getPointX()) < mRoundSize && Math.abs(point.getPointY() - pointList.get(i).getPointY()) < mRoundSize) {
                list.add(pointList.get(i - 1));
                list.add(pointList.get(i));
                list.add(pointList.get(i + 1));
            }
        }
        return list;
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

    /**
     * 得到完整路径中的下标
     *
     * @param point
     * @return
     */
    private int getCountPoint2Index(Point point) {
        for (int i = 0; i < pointList.size(); i++) {
            Log.d("MoveAndCropRectView", "Math.abs:" + Math.abs(point.getPointX() - pointList.get(i).getPointX()) + "_____________" + pointList.get(i).getPointX() + "____________" + point.getPointX());
            Log.d("MoveAndCropRectView", "Math.abs: " + (point.getPointY() - pointList.get(i).getPointY()) + "_____________" + pointList.get(i).getPointY() + "____________" + point.getPointY());
            if (Math.abs(point.getPointX() - pointList.get(i).getPointX()) < mRoundSize && Math.abs(point.getPointY() - pointList.get(i).getPointY()) < mRoundSize) {
                return i;
            }
        }
        return 0;
    }

    /*刷新矩形的坐标*/
    private void refreshLocation(float isx, float isy, float iex, float iey) {
        this.startX = isx;
        this.startY = isy;
        this.endX = iex;
        this.endY = iey;
        mCoverWidth = endX - startX;
        mCoverHeight = endY - startY;
    }

    /*检测矩形是否达到最小值*/
    private boolean checkLegalRect(float cHeight, float cWidth) {
        return (cHeight > minHeight && cWidth > minWidth);
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
        if (cx > startX && cx < endX && cy > startY && cy < endY) {
            MODE = MODE_INSIDE;//矩形内部
        } else if (nearbyPoint(cx, cy) < 4) {
            MODE = MODE_POINT;//矩形点上
        } else {
            MODE = MODE_OUTSIDE;//矩形外部
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
        for (Point point : dragPointList) {
            point.setPointX(point.getPointX() + dX);
            point.setPointY(point.getPointY() + dY);
        }


        Log.d("MoveAndCropRectView:偏移量", "dX:" + dX + "____________dY:" + dY);
        memoryX = mx;
        memoryY = my;
    }

    /*判断点(inX,inY)是否靠近矩形的4个顶点*/
    private int nearbyPoint(float floatX, float floatY) {
        if ((Math.abs(startX - floatX) <= ACCURACY && (Math.abs(floatY - startY) <= ACCURACY))) {/*left-up angle*/
            pointPosition = 0;
            return 0;
        }
        if ((Math.abs(endX - floatX) <= ACCURACY && (Math.abs(floatY - startY) <= ACCURACY))) {/*right-up angle*/
            pointPosition = 1;
            return 1;
        }
        if ((Math.abs(startX - floatX) <= ACCURACY && (Math.abs(floatY - endY) <= ACCURACY))) {/*left-down angle*/
            pointPosition = 2;
            return 2;
        }
        if ((Math.abs(endX - floatX) <= ACCURACY && (Math.abs(floatY - endY) <= ACCURACY))) {/*right-down angle*/
            pointPosition = 3;
            return 3;
        }
        pointPosition = 100;
        return 100;
    }

    // 设置矩形框
    public void setRectF(RectF rectf) {
        this.mRectF = rectf;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setConfidence(float confidence) {
        mConfidence = confidence;
    }

    public void setCornerAngle(int cornerAngle) {
        this.mCornerAngle = cornerAngle;
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