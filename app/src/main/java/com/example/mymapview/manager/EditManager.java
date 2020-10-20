package com.example.mymapview.manager;

import android.graphics.Canvas;
import android.widget.LinearLayout;

import com.example.mymapview.EditingView;
import com.example.mymapview.tool.DrawingTool;
import com.example.mymapview.tool.Tool;


/**
 * 项  目：GIM
 * 描  述：
 * 作  者：CZY
 * 时  间：2019/11/21 14:17
 * 版  权：suntoon
 */
public enum EditManager {
    /**
     * The singleton instance.
     */
    INSTANCE;
    private Tool activeTool;
    private Tool idleTool;


    private DrawingTool toolRenderDrawing;
    // 放大镜
    private DrawingTool MagnifierTool;

    private EditingView editingView;
    private LinearLayout toolsdrawLayout;
    private LinearLayout toolseditLayout;

    public void setToolRender( DrawingTool assitrender ) {
        toolRenderDrawing = assitrender;
    }
    public DrawingTool getToolRender() {
        return toolRenderDrawing;
    }
    public DrawingTool getMagnifierTool() {
        return MagnifierTool;
    }

    public void setMagnifierTool( DrawingTool MagnifierTool ) {
        if (this.MagnifierTool != null)
            this.MagnifierTool.disable();

        this.MagnifierTool = MagnifierTool;

        if (this.MagnifierTool != null)
            this.MagnifierTool.activate();
    }


    /**
     * Set the current active {@link Tool}.
     *
     * <p>Only one tool can be active at the time.</p>
     * <p>Setting the active tool to <code>null</code> has the
     * result of disabling the current tool.
     *
     * @param newActiveTool the new active tool to set.
     */
    public void setActiveTool( Tool newActiveTool ) {
        if (this.activeTool != null) {
            // disable current active tool
            this.activeTool.disable();
            this.activeTool = null;
        }
        this.activeTool = newActiveTool;
        if (newActiveTool != null) {
            newActiveTool.activate();
        }
        invalidateEditingView();
    }

    public void setIdleTool( Tool idleTool ) {
        this.idleTool = idleTool;
    }
    public Tool getIdleTool() {
        return idleTool;
    }
    /**
     * @return the current active tool.
     */
    public Tool getActiveTool() {
        return activeTool;
    }







    /**
     * Set the editing view.
     *
     * @param editingView the editing view to set.
     * @param toolsLayout the layout for the tools gui.
     * @param editLayout  layout
     */
    public void setEditingView(EditingView editingView, LinearLayout toolsLayout, LinearLayout editLayout ) {
        this.editingView = editingView;
        this.toolsdrawLayout = toolsLayout;
        this.toolseditLayout = editLayout;
    }

    /**
     * @return the current editing view.
     */
    public EditingView getEditingView() {
        return editingView;
    }

    /**
     * Invalidate the editing view if it exists.
     */
    public void invalidateEditingView() {
        if (editingView != null) {
            editingView.invalidate();
        }
    }

    /**
     * @return the tools layout.
     */
    public LinearLayout getToolsDrawLayout() {
        return toolsdrawLayout;
    }
    /**
     *
     * @return edit tool
     */
    public LinearLayout getToolsEditLayout() {
        return toolseditLayout;
    }


    public void switchIdleTool() {
//        if (this.idleTool != null)
        setActiveTool(idleTool);
    }

    /**
     *
     * @param canvas  xx
     */
    public void onToolDraw( Canvas canvas ) {

    }
}
