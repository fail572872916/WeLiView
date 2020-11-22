package com.example.mymapview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.mymapview.manager.EditManager;
import com.example.mymapview.tool.DrawingTool;
import com.example.mymapview.tool.Tool;


/**
 * 项  目：GIM
 * 描  述：
 * 作  者：CZY
 * 时  间：2019/11/21 14:22
 * 版  权：suntoon
 */
public class EditingView extends View {

    /**
     * Constructor.
     *
     * @param context  the context to use.
     * @param attrs the attributes.
     */
    public EditingView(Context context, AttributeSet attrs ) {
        super(context, attrs);
        // 禁止硬件加速，适配不同机型,解决 android.view.GLES20Canvas.clipPath(GLES20Canvas.java:412) 903机型 cl
        // java.lang.UnsupportedOperationException
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onDraw( Canvas canvas ) {
        super.onDraw(canvas);

        Tool activeTool = EditManager.INSTANCE.getActiveTool();
        if (activeTool instanceof DrawingTool) {
            ((DrawingTool) activeTool).onToolDraw(canvas);
        }

        DrawingTool toolRend = EditManager.INSTANCE.getToolRender();
        if (null != toolRend)
            toolRend.onToolDraw(canvas);

        // 绘制放大镜
        DrawingTool magnifierTool = EditManager.INSTANCE.getMagnifierTool();
        if (null != magnifierTool)
            magnifierTool.onToolDraw(canvas);
    }

    @Override
    public boolean onTouchEvent( MotionEvent event ) {
        DrawingTool magnifierTool = EditManager.INSTANCE.getMagnifierTool();
        if (null != magnifierTool)
            magnifierTool.onToolTouchEvent(event);

        Tool activeTool = EditManager.INSTANCE.getActiveTool();
        if (activeTool instanceof DrawingTool) {
            return ((DrawingTool) activeTool).onToolTouchEvent(event);
        }
        /* ToolGroup activeToolGroup = EditManager.INSTANCE.getDrawToolGroup();
         if (activeToolGroup instanceof DrawingTool) {
             return ((DrawingTool) activeToolGroup).onToolTouchEvent(event);
         }*/

        return false;
    }
}
