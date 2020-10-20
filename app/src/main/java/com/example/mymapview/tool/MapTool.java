package com.example.mymapview.tool;

import android.view.View;


/**
 * 项  目：GIM
 * 描  述：
 * 作  者：CZY
 * 时  间：2019/10/21 18:44
 * 版  权：suntoon
 */
public abstract class MapTool  implements DrawingTool{
    protected View mapView;

    /**
     * Constructor.
     *
     * @param mapView the mapview to work on
     */
    public MapTool(View mapView ) {
        this.mapView = mapView;
    }
    public boolean isActive() {return !mapView.isClickable();}
}
