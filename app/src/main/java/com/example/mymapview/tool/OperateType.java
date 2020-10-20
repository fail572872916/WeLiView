package com.example.mymapview.tool;

/**
 * 项  目：GIM
 * 描  述：
 * 作  者：CZY
 * 时  间：2019/10/21 18:36
 * 版  权：suntoon
 */
public enum OperateType {
    /**
     *
     */
    nullOperate("nullOperate", "nullOperate", -1, false),
    /**
     * Mbtiles based database.
     */
    deleteFeature("deleteFeature", "deleteFeature", 0, true),
    /**
     * A spatialite/sqlite database.
     */
    selectAttribute("selectAttribute", "selectAttribute", 1, true),
    /**
     * A spatialite/sqlite database.
     */
    addVertexBygpsStream("addVertexBygpsStream", "addVertexBygpsStream", 2, true),
    /**
     * A geopackage database.
     */
    addVertexByTap("addVertexByTap", "", 3, true),
    /**
     * A mapsforge map file.
     */
    addVertexByDrawing("addVertexByDrawing", "addVertexByDrawing", 4, false),
    /**
     * A mapsurl definition file.
     */
    selectFeature("selectFeature", "selectFeature", 5, false),
    /**
     * A Rasterlite2 Image in a spatialite 4.2.0 database.
     * - avoids .db being read 2x
     * - real spatialite .atlas files can also be read
     */
    undoVertex("undoVertex", "undoVertex", 6, true),

    measureLength("measureArea", "measureArea", 7, true),

    measureArea("measureArea", "measureArea", 8, true),

    tapAttribute("tapAttribute", "tapAttribute", 9, true);

    private String name;
    // extention must be unique
    // - otherwise will be read in twice in SpatialDatabasesManager.init
    private String helpcontext;
    private int code;
    private boolean simbol;

    /**
     * @param name a name for the db type.
     * @param extension the extension used by the db type.
     * @param code a code for the db type.
     */
    private OperateType(String name, String help, int code, boolean simbol ) {
        this.name = name;
        this.helpcontext = help;
        this.code = code;
        this.simbol = simbol;
    }


    /**
     * @return the db type's name.
     */
    public String getTypeName() {
        return name;
    }

    /**
     * @return the db type's extension.
     */
    public String getHelp() {
        return helpcontext;
    }

    /**
     * @return the db's type code.
     */
    public int getCode() {
        return code;
    }

    /**
     * @return <code>true</code> if the type is spatialite based.
     */

}
