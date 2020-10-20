package com.example.mymapview.tool;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.mymapview.Point;
import com.example.mymapview.R;
import com.example.mymapview.manager.EditManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FreeChoiceTool extends MapTool {
    private static final int TOUCH_BOX_THRES = 2;

    private final Paint paintStroke = new Paint();
    private final Paint paintFill = new Paint();
    private final Paint oldPaintStroke = new Paint();
    private final Paint oldPaintFill = new Paint();
    private final Paint pointPaint = new Paint();
    private final Rect rect = new Rect();
    private final Bitmap bitmapDelete;
    private final Bitmap bitmapPoint;
    private ArrayList<Point> points = new ArrayList<>();
    private ArrayList<Point> oldPointFs = new ArrayList<>();
    private float currentX;
    private float currentY;
    private float lastX = -1;
    private float lastY = -1;

    private final PointF tmpP = new PointF();
    private final PointF startP = new PointF();
    private Point movePoint = null;
    private float left;
    private float right;
    private float bottom;
    private float top;
    private boolean isTouch = true;
    private ProgressDialog infoProgressDialog;
    private int iconWidth = 50;
    private int iconHeight = 50;
    private OnSelectInfoListener onSelectInfoListener;
    private OnPointListener onPointListener;
    private ArrayList<Long> longs = new ArrayList<>();
    private final Region region;
    private boolean isCanvasSelector = false;
    private Context context;
    private final Bitmap bitmapAdd;
    private boolean isMove = false;

    /**
     * Constructor.
     *
     * @param mapView the mapview reference.
     */
    public FreeChoiceTool( View mapView, Context context, int x, int y) {
        super(mapView);

        this.context = context;
        region = new Region();
        points.add(new Point(x - 100, y - 100));
        points.add(new Point(x + 100, y - 100));
        points.add(new Point(x + 100, y + 100));
        points.add(new Point(x - 100, y + 100));
        oldPointFs.add(new Point(x - 100, y - 100));
        oldPointFs.add(new Point(x + 100, y - 100));
        oldPointFs.add(new Point(x + 100, y + 100));
        oldPointFs.add(new Point(x - 100, y + 100));
        paintFill.setAntiAlias(true);
        paintFill.setColor(context.getResources().getColor(R.color.colorPrimary));
        paintFill.setAlpha(70);
        paintFill.setStyle(Paint.Style.FILL);
        paintFill.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        paintStroke.setAntiAlias(true);
        paintStroke.setStrokeWidth(6f);
        paintStroke.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        paintStroke.setColor(context.getResources().getColor(R.color.colorPrimary));
        paintStroke.setStyle(Paint.Style.STROKE);//设置空心

        oldPaintFill.setAntiAlias(true);
        oldPaintFill.setColor(context.getResources().getColor(R.color.bg_gray));
        oldPaintFill.setAlpha(70);
        oldPaintFill.setStyle(Paint.Style.FILL);
        oldPaintFill.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        oldPaintStroke.setAntiAlias(true);
        oldPaintStroke.setStrokeWidth(6f);
        oldPaintStroke.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        oldPaintStroke.setColor(context.getResources().getColor(R.color.bg_gray));
        oldPaintStroke.setStyle(Paint.Style.STROKE);
        oldPaintStroke.setStyle(Paint.Style.STROKE);//设置空心
        bitmapAdd = setBitmap(R.mipmap.ic_add_1);
        bitmapDelete = setBitmap(R.mipmap.ic_delete);
        bitmapPoint = setBitmap(R.mipmap.ic_point);
        EditManager.INSTANCE.invalidateEditingView();


    }

    public void setOnPointListener(OnPointListener onPointListener) {
        this.onPointListener = onPointListener;

    }

    public void activate() {
//        if (mapView != null) {
//            mapView.setClickable(false);
//        }
    }

    public void onToolDraw(Canvas canvas) {

        Path path = new Path();
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            if (i == 0) {
                path.moveTo(point.getPointX(), point.getPointY());
            } else {
                path.lineTo(point.getPointX(), point.getPointY());
            }
            int centerPosition = i - 1;
            if (i == 0 && points.size() > 2) {
                centerPosition = points.size() - 1;
            }
            if (centerPosition > -1) {
                Point pointFOld = points.get(centerPosition);
                canvas.drawBitmap(bitmapAdd, (pointFOld.getPointX() + point.getPointX()) / 2 - iconWidth / 2, (pointFOld.getPointY() + point.getPointY()) / 2 - iconHeight / 2, pointPaint);
            }
            canvas.drawBitmap(bitmapPoint, point.getPointX() - iconWidth / 2, point.getPointY() - iconHeight / 2, pointPaint);
        }
        path.close();//封闭
        canvas.drawPath(path, paintStroke);
        canvas.drawPath(path, paintFill);
        RectF r = new RectF();
        path.computeBounds(r, true);
        region.setPath(path, new Region((int) r.left, (int) r.top, (int) r.right, (int) r.bottom));
        if (isMove) {
            Path oldPath = new Path();
            for (int i = 0; i < oldPointFs.size(); i++) {
                Point point = oldPointFs.get(i);
                if (i == 0) {
                    oldPath.moveTo(point.getPointX(), point.getPointY());
                } else {
                    oldPath.lineTo(point.getPointX(), point.getPointY());
                }
            }
            oldPath.close();
            canvas.drawPath(oldPath, oldPaintStroke);
            canvas.drawPath(oldPath, oldPaintFill);

        }

    }

    /**
     * 资源图片转bitmap
     *
     * @ param
     * @ return
     * @ 创建人 CZY
     * @ 创建时间 2020/9/24 18:54
     * @ 修改人
     * @ 修改时间
     */
    private Bitmap setBitmap(int id) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = iconWidth;
        int newHeight = iconHeight;
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    public boolean onToolTouchEvent(MotionEvent event) {
//        if (mapView == null || mapView.isClickable() || !isTouch) {
//            return false;
//        }


        // handle drawing
        currentX = event.getX();
        currentY = event.getY();

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                boolean isTouchPoint = false;
                for (int i = 0; i < points.size(); i++) {
                    try {
                        oldPointFs.add((Point) points.get(i).clone());
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("FreeChoiceTool", "ACTION_MOVE:" + "oldPointFsSzie:" + oldPointFs.size() + "__pointFsSzie:" + points.size());
                for (int i = 0; i < points.size(); i++) {
                    Point point = points.get(i);
                    Point pointFOld = points.get(i);
                    if (i > 0) {
                        pointFOld = points.get(i - 1);
                    } else if (points.size() > 2) {
                        pointFOld = points.get(points.size() - 1);
                    }
                    Point pointCenter = new Point((pointFOld.getPointX() + point.getPointX()) / 2, (pointFOld.getPointY() + point.getPointY()) / 2);
                    if (getPointRegion(pointCenter, iconWidth, iconHeight).contains((int) currentX, (int) currentY)) {
                        if (i == 0) {
                            points.add(pointCenter);
                        } else {
                            points.add(i, pointCenter);
                        }
                        movePoint = pointCenter;
                        isTouchPoint = true;
                        break;
                    } else if (getPointRegion(point, iconWidth, iconHeight).contains((int) currentX, (int) currentY)) {
                        movePoint = points.get(i);
                        isTouchPoint = true;
                    }
                }
                Log.i("FreeChoiceTool", "ACTION_MOVE:" + "oldPointFsSzie:" + oldPointFs.size() + "__pointFsSzie:" + points.size());
                if (!isTouchPoint && region.contains((int) currentX, (int) currentY)) {
                    isCanvasSelector = true;
                }

                startP.x = currentX;
                startP.y = currentY;

                lastX = currentX;
                lastY = currentY;

                right = left = startP.x;
                top = bottom = startP.y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = currentX - lastX;
                float dy = currentY - lastY;
                if (isCanvasSelector) {
                    isMove = true;
                    for (Point point : points) {
                        setPointMove(point, dx, dy);
                    }
                    Log.i("FreeChoiceTool", "ACTION_MOVE:" + "currentX:" + currentX + "___startP.x:" + startP.x + "___dx:" + dx + "____currentY:" + currentY + "___startP.y:" + startP.y + "___dy:" + dy);
                } else if (movePoint != null) {
                    isMove = true;

                    setPointMove(movePoint, dx, dy);
                }

                lastX = currentX;
                lastY = currentY;


                tmpP.x = currentX;
                tmpP.y = currentY;
                EditManager.INSTANCE.invalidateEditingView();

                break;
            case MotionEvent.ACTION_UP: {
                isCanvasSelector = false;
                movePoint = null;
                isMove = false;
                oldPointFs.clear();
                EditManager.INSTANCE.invalidateEditingView();
                onPointListener.OnPoint(points);

            }

            break;
        }

        return true;
    }

    private void setPointMove(Point point, float moveX, float movey) {
        point.setPointX(point.getPointX() + moveX);
        point.setPointY(point.getPointY() + movey);
    }

    private Region getPointRegion(Point startPoint, int width, int height) {
        Path path = new Path();
        path.moveTo(startPoint.getPointX()- width / 2, startPoint.getPointY() - height / 2);
        path.lineTo(startPoint.getPointX()+ width / 2, startPoint.getPointY() - height / 2);
        path.lineTo(startPoint.getPointX()+ width / 2, startPoint.getPointY() + height / 2);
        path.lineTo(startPoint.getPointX()- width / 2, startPoint.getPointY() + height / 2);
        path.close();
        RectF r = new RectF();
        path.computeBounds(r, true);
        Region startRegion = new Region();
        startRegion.setPath(path, new Region((int) r.left, (int) r.top, (int) r.right, (int) r.bottom));

        return startRegion;

    }

    public void disable() {
//        if (mapView != null) {
//            mapView.setClickable(true);
//            mapView = null;
//        }
//        EditManager.INSTANCE.setMagnifierTool(null);
    }

    private void infoDialog(final double n, final double w, final double s, final double e) {
        final Context context = EditManager.INSTANCE.getEditingView().getContext();
        Toast.makeText(context, "n:" + n + "w:" + w + "s:" + s + "e" + e, Toast.LENGTH_LONG).show();
    }

    public interface OnSelectInfoListener {
        void OnSelectInfo(float left, float right, float bottom, float top);
    }

    public interface OnPointListener {
        void OnPoint(ArrayList<Point> points);
    }


    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }
}
