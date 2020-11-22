package com.example.mymapview.tool;

import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * 项  目：GIM
 * 描  述：
 * 作  者：CZY
 * 时  间：2019/10/21 18:44
 * 版  权：suntoon
 */
public interface DrawingTool extends Tool  {
    /**
     * Called when the tool should draw.
     *
     * @param canvas the {@link Canvas} to draw on.
     */
    public void onToolDraw(Canvas canvas);

    /**
     * Called on a touch event.
     *
     * @param event the current triggered event.
     * @return <code>true</code> if the event has been handled.
     */
    public boolean onToolTouchEvent(MotionEvent event);
}
