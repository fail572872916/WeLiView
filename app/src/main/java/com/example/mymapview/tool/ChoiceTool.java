package com.example.mymapview.tool;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.mymapview.Point;
import com.example.mymapview.manager.EditManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 项  目：GIM
 * 描  述：
 * 作  者：CZY
 * 时  间：2019/11/21 14:08
 * 版  权：suntoon
 */
public class ChoiceTool extends MapTool {
    private static final int TOUCH_BOX_THRES = 2;


    private int MODE;
    private static final int MODE_OUTSIDE = 0x000000aa;/*170*/
    private static final int MODE_INSIDE = 0x000000bb;/*187*/
    private static final int MODE_POINT = 0X000000cc;/*204*/
    private static final int MODE_ILLEGAL = 0X000000dd;/*221*/
    private static final int MODE_ADD = 0X000000ee;/*221*/

    //线中间添加点的集合
    List<Point> addPointList = new ArrayList<>();
    List<Point> pointList = new ArrayList<>();
    List<Point> oldPointList = new ArrayList<>();


    private float currentX;
    private float currentY;


    //各个画笔
    private Paint mPaint = new Paint();

    private Paint mOldPaint = new Paint();

    private Paint mAddPaint = new Paint();

    private Paint mCirclePaint = new Paint();

    private Paint mInteriorCirclePaint = new Paint();

    private Paint mTextPaint = new Paint();

    private static final float DRAG_SPEED = 2.5f; //拖动时的速度
    private static final float mRoundSize = 25.0f;/*圆大小*/


    private float startX; //x轴起点
    private float startY;//y轴起点
    private float endX;//x轴终点
    private float endY;//y轴终点

    private boolean isTouch = true;

    private ProgressDialog infoProgressDialog;

    private OnSelectInfoListener onSelectInfoListener;
    private OnActionUpInfoListener onActionUpInfoListener;
    private ArrayList<Long> longs = new ArrayList<>();

    private float mCoverWidth;/*width of selection box*/
    private float mCoverHeight;/*height of selection box*/
    int constWidth = 150;
    int constHeight = constWidth * 2;

    int tmpX = 0;

    int tmpY = 0;

    private float memoryX;/*the last time the coordinate values of X*/
    private float memoryY;/*the last time the coordinate values of Y*/
    private Point movePoint;
    private int moveIndex;

    /**
     * Constructor.
     *
     * @param mapView the mapview reference.
     */
    public ChoiceTool(View mapView, ImageView ivCenterPoint, Activity activity) {
        super(mapView);
//

        Point point = getCenterPoint(ivCenterPoint);

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
        mCirclePaint.setColor(Color.parseColor("#BFEFFF"));
        mCirclePaint.setAntiAlias(true);


        mAddPaint.setStyle(Paint.Style.FILL);
        mAddPaint.setColor(Color.parseColor("#EEC900"));
        mAddPaint.setAntiAlias(true);
        mOldPaint.setStyle(Paint.Style.STROKE);
        mOldPaint.setColor(Color.GRAY);
        mOldPaint.setStrokeWidth(5);
        mOldPaint.setAntiAlias(true);

        //文字画笔
        mTextPaint = new Paint();
        mCirclePaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(Color.parseColor("#F7F7F7"));
        mTextPaint.setFakeBoldText(true);
        mTextPaint.setTextSize(30);


        startX = point.getPointX() - constWidth;
        startY = point.getPointY() - DisplayUtils.getStatusBarHeight(activity);
        endX = point.getPointX() + constWidth;
        endY = point.getPointX() + constHeight;

        mCoverWidth = constWidth;
        mCoverHeight = constHeight;

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

    //得到View 得中心点
    public Point getCenterPoint(View view) {
        int top = view.getTop();
        int left = view.getLeft();
        //右下角坐标
        int right = view.getRight();
        int bottom = view.getBottom();

        //View的 宽
        int viewWidth = right - left;
        //View的 高
        int viewHeight = bottom - top;
        //中心点的Y坐标
        int centerY = top + viewHeight / 2;
//        int centerY=bottom;
        //中心点的X坐标
        int centerX = left + viewWidth / 2;
        return new Point(centerX, centerY);
    }

    public void setTouch(boolean touch) {
        isTouch = touch;
    }

    public void setSelectInfoListener(OnSelectInfoListener onSelectInfoListener) {
        this.onSelectInfoListener = onSelectInfoListener;

    }

    public void setActionUpInfoListener(OnActionUpInfoListener onActionUpInfoListener) {
        this.onActionUpInfoListener = onActionUpInfoListener;

    }

    public void activate() {
        if (mapView != null) {
            mapView.setClickable(false);
//            EditManager.INSTANCE.setMagnifierTool(new DrawMagnifier(mapView));
        }
    }

    public void onToolDraw(Canvas canvas) {

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
            float baseX = point.getPointX() - (mRoundSize / 2) - 2;

            // 计算Baseline绘制的Y坐标 ，计算方式：画布高度的一半 - 文字总高度的一半
            float baseY = point.getPointY() + (mRoundSize / 2) - 2;
            canvas.drawText("＋", baseX, baseY, mTextPaint);
        }


//        Log.d("ChoiceTool", "偏移量juxi:________________________________");

//        //回执定点之间的节点
        for (Point point : pointList) {
//            Log.d("MoveAndCropRectView", ":偏移量juxi:" + point);
            canvas.drawCircle(point.getPointX(), point.getPointY(), mRoundSize, mCirclePaint);
            canvas.drawCircle(point.getPointX(), point.getPointY(), mRoundSize / 2, mInteriorCirclePaint);
        }

    }

    public boolean onToolTouchEvent(MotionEvent event) {
        if (mapView == null || mapView.isClickable() || !isTouch) {
            return false;
        }
        // handle drawing
        currentX = (int) event.getX();
        currentY = (int) event.getY();

        Log.d("ChoiceTool", "currentX:" + currentX);
        Log.d("ChoiceTool", "currentY:" + currentY);

        int action = event.getAction();
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
                        EditManager.INSTANCE.invalidateEditingView();
                        break;
                    case MODE_OUTSIDE:
                        //do nothing;
                        break;
                    case MODE_INSIDE://拖动.
                        moveByTouch(currentX, currentY);
                        EditManager.INSTANCE.invalidateEditingView();
                        break;
                    default:
                        /*MODE_POINT*/
                        moveByPoint(currentX, currentY);
                        EditManager.INSTANCE.invalidateEditingView();
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

    public void disable() {
        if (mapView != null) {
            mapView.setClickable(true);
            mapView = null;
        }

    }

    private void infoDialog(final double n, final double w, final double s, final double e) {
        final Context context = EditManager.INSTANCE.getEditingView().getContext();
        Toast.makeText(context, "n:" + n + "w:" + w + "s:" + s + "e" + e, Toast.LENGTH_LONG).show();
    }

    public interface OnSelectInfoListener {
        void OnSelectInfo(float left, float right, float bottom, float top);
    }

    public interface OnActionUpInfoListener {
        void OnActionUp(ArrayList<Long> longs, final double n, final double w, final double s, final double e);
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

        if (isDragPoint(point)) {
            MODE = MODE_POINT;
            return;

        }

        if (PtInPolygon(point, pointList)) {
            Log.d("MoveAndCropRectView", ":偏移量juxi:矩形内部");
            MODE = MODE_INSIDE;
        } else {
            MODE = MODE_OUTSIDE;
            Log.d("MoveAndCropRectView", "偏移量juxi:矩形外部");
        }

//        Log.d("MoveAndCropRectView", ":偏移量juxi:" + cx + "cy:" + cy + "startX:" + startX + "endX:" + endX + "endY:" + endY);
//        if (cx > startX && cx < endX && cy > startY && cy < endY) {
//            MODE = MODE_INSIDE;//矩形内部
//            Log.d("MoveAndCropRectView", ":偏移量juxi:矩形内部");
//        } else if (nearbyPoint(cx, cy)) {
//            MODE = MODE_POINT;//矩形点上
//            Log.d("MoveAndCropRectView", "偏移量juxi:矩形点上");
//        } else {
//            MODE = MODE_OUTSIDE;//矩形外部
//            Log.d("MoveAndCropRectView", "偏移量juxi:矩形外部");
//        }
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

    /*矩形随手指移动*/
    private void moveByTouch(float mx, float my) {/*move center point*/
        float dX = mx - memoryX;
        float dY = my - memoryY;

//        startX += dX;
//        startY += dY;
//        endX +=dX;
//        endY += dY;
//        if (startX <= 0) {
//            startX = 0;
//        }
//        if (startY <= 0) {
//            startY = 0;
//        }
//        endX = startX + mCoverWidth;
//        endY = startY + mCoverHeight;

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
        startX += dX;
        startY += dY;
        endX += dX;
        endY += dY;
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
                EditManager.INSTANCE.invalidateEditingView();
                return;
            }
        }
    }

    /**
     * 功能：判断点是否在多边形内 方法：求解通过该点的水平线与多边形各边的交点 结论：单边交点为奇数，成立!
     *
     * @param point   指定的某个点
     * @param APoints 多边形的各个顶点坐标（首末点可以不一致）
     * @return
     */
    public boolean PtInPolygon(Point point, List<Point> APoints) {
        int nCross = 0;
        for (int i = 0; i < APoints.size(); i++) {
            Point p1 = APoints.get(i);
            Point p2 = APoints.get((i + 1) % APoints.size());
            // 求解 y=p.y 与 p1p2 的交点
            if (p1.getPointY() == p2.getPointY()) // p1p2 与 y=p0.y平行
                continue;
            if (point.getPointY() < Math.min(p1.getPointY(), p2.getPointY())) // 交点在p1p2延长线上
                continue;
            if (point.getPointY() >= Math.max(p1.getPointY(), p2.getPointY())) // 交点在p1p2延长线上
                continue;
            // 求交点的 X 坐标
            // --------------------------------------------------------------
            double x = (double) (point.getPointY() - p1.getPointY())
                    * (double) (p2.getPointX() - p1.getPointX())
                    / (double) (p2.getPointY() - p1.getPointY()) + p1.getPointX();
            if (x > point.getPointX())
                nCross++; // 只统计单边交点
        }
        // 单边交点为偶数，点在多边形之外 ---
        return (nCross % 2 == 1);
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
     * 判断点是否是可拖动点
     *
     * @param point
     * @returna
     */
    private boolean isDragPoint(Point point) {
        for (int i = 0; i < pointList.size(); i++) {
            if (Math.abs(point.getPointX() - pointList.get(i).getPointX()) < mRoundSize && Math.abs(point.getPointY() - pointList.get(i).getPointY()) < mRoundSize) {
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

    private void getAddPoint() {
        addPointList.clear();
        //具体的形状
        for (int i = 0; i < pointList.size(); i++) {
            if (i < pointList.size() - 1) {
                addPointList.add(new Point(((pointList.get(i + 1).getPointX() - pointList.get(i).getPointX()) / 2) + pointList.get(i).getPointX(), ((pointList.get(i + 1).getPointY() - pointList.get(i).getPointY()) / 2) + pointList.get(i).getPointY()));
            }
        }

    }
}
