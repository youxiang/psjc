package njscky.psjc.service;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Line;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.TextSymbol;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import njscky.psjc.CoordinateConversion;
import njscky.psjc.R;
import njscky.psjc.model.PipeLine;
import njscky.psjc.model.PipePoint;
import njscky.psjc.util.Utils;

import static android.os.Environment.MEDIA_MOUNTED;

public class MapManager {

    public static final int TYPE_POINT_JCJ = 0;
    public static final int TYPE_POINT_TZD = 1;
    public static final int TYPE_LINE_JCJ = 2;
    public static final int TYPE_LINE_TZD = 3;
    private static final String TAG = MapManager.class.getSimpleName();
    private static final String LOCAL_MAP_PATH = "/offlinemap/jdzl.tpk";
    private static final String CLIENT_ID = "yhMRjFXTz6F38XD9";
    private final Context context;


    public MapManager(Context context) {
        this.context = context.getApplicationContext();
        ArcGISRuntime.setClientId(CLIENT_ID);
    }

    public void loadMap(MapView mapView) {
        loadOnlineMap(mapView);
//        loadLocalMap(mapView);
    }

    boolean loadLocalMap(MapView mapView) {
        Log.i(TAG, "loadLocalMap: ");
        String localMapDir = getLocalMapDir();
        String localMapPath = localMapDir + LOCAL_MAP_PATH;

        File file = new File(localMapPath);
        if (file.exists()) {
            ArcGISLocalTiledLayer localTiledLayer = new ArcGISLocalTiledLayer(localMapPath);
            localTiledLayer.setName("基础地图");
            mapView.addLayer(localTiledLayer);

            Envelope env = new Envelope();
            env.setCoords(119642.008560884, 139699.14891334, 130225.363060926, 150519.536158281);
            mapView.setExtent(env);
            return true;

        } else {
            return false;
        }
    }

    void loadOnlineMap(MapView mapView) {
        Log.i(TAG, "loadOnlineMap: ");
        if (Utils.hasNetwork(context)) {
            ArcGISTiledMapServiceLayer TitleLayerbase = new ArcGISTiledMapServiceLayer("http://58.213.48.108/arcgis/rest/services/%E5%8D%97%E4%BA%AC%E5%9F%BA%E7%A1%80%E5%BA%95%E5%9B%BE2016/MapServer");
            TitleLayerbase.setName("南京基础底图");

            mapView.addLayer(TitleLayerbase);

            Envelope env = new Envelope();
            env.setCoords(119642.008560884, 139699.14891334, 130225.363060926, 144519.536158281);
            mapView.setExtent(env);


        } else {
            Toast.makeText(context, "没有连接网络，无法加载地图", Toast.LENGTH_SHORT).show();
        }
    }


    private String getLocalMapDir() {
        String path = null;
        if (Environment.getExternalStorageState().equals(MEDIA_MOUNTED)) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        }

        if (TextUtils.isEmpty(path)) {
            path = context.getFilesDir().getAbsolutePath();
        }

        Log.i(TAG, "getLocalMapDir: " + path);
        return path;
    }

    private TextSymbol createTextSymbolInLineCenter(String txt, int colorSymbol, int widthLineSymbol, Point startPoint, Point endPoint) {

        double angle = 0;
        double k = 0;
        float fAngle = 0;
        double x1 = startPoint.getX();
        double x2 = endPoint.getX();
        double y1 = startPoint.getY();
        double y2 = endPoint.getY();

        if (x2 - x1 == 0) {
            fAngle = 90;
        } else if (x2 > x1 && y2 > y1) {
            //1.X2>X1,Y2>Y1 k=Y2-Y1/X2-X1 α＝360-arctank
            k = (y2 - y1) / (x2 - x1);   //斜率
            angle = Math.atan(k);
            fAngle = (float) (360 - Math.toDegrees(angle));
        } else if (x2 > x1 && y2 < y1) {
            //2.X2>X1,Y2<Y1 k=Y1-Y2/X2-X1 α＝arctank
            k = (y1 - y2) / (x2 - x1);   //斜率
            angle = Math.atan(k);
            fAngle = (float) (Math.toDegrees(angle));
        } else if (x2 < x1 && y2 < y1) {
            //3.X2<X1,Y2<Y1 k=Y1-Y2/X1-X2 α＝360-arctank
            k = (y1 - y2) / (x1 - x2);   //斜率
            angle = Math.atan(k);
            fAngle = (float) (360 - Math.toDegrees(angle));
        } else if (x2 < x1 && y2 > y1) {
            //4.X2<X1,Y2>Y1 k=Y2-Y1/X1-X2 α＝arctank
            k = (y2 - y1) / (x1 - x2);   //斜率
            angle = Math.atan(k);
            fAngle = (float) (Math.toDegrees(angle));
        }

        TextSymbol textSymbol = new TextSymbol(widthLineSymbol, txt, colorSymbol);
        textSymbol.setFontFamily("DroidSansFallback.ttf");
        textSymbol.setOffsetX(5);
        textSymbol.setOffsetY(5);
        textSymbol.setAngle(fAngle);

        return textSymbol;
    }

    public void addPipePointLayers(MapView mapView, DbManager dbManager, int type) {

        Log.i(TAG, "addPipePointLayers: ");

        GraphicsLayer graphicsLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
        graphicsLayer.setName(getPipePointLayerName(type));

        GraphicsLayer annotationLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
        annotationLayer.setName(getPipePointAnnotationLayerName(type));

        int colorSymbol = Color.rgb(76, 0, 0);
        int sizeSymbol = 12;

        Cursor cursor = null;
        try {
            cursor = dbManager.getPipePoints(type);

            while (cursor.moveToNext()) {
                PipePoint pipePoint = new PipePoint(cursor);

                Point point = new Point(pipePoint.XZB, pipePoint.YZB);
                SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(colorSymbol, sizeSymbol, SimpleMarkerSymbol.STYLE.TRIANGLE);

                Map<String, Object> attributes = new HashMap<String, Object>();
                attributes.put("JCJBH", pipePoint.JCJBH);

                Graphic graphic = new Graphic(point, markerSymbol, attributes);
                graphicsLayer.addGraphic(graphic);

                if (type == TYPE_POINT_JCJ) {
                    TextSymbol textSymbol = new TextSymbol(sizeSymbol, pipePoint.JCJBH, colorSymbol);
                    textSymbol.setFontFamily("DroidSansFallback.ttf");
                    textSymbol.setOffsetX(5);
                    textSymbol.setOffsetY(5);
                    Graphic graphicT = new Graphic(point, textSymbol);
                    annotationLayer.addGraphic(graphicT);
                }
            }

            graphicsLayer.setMinScale(type == TYPE_POINT_JCJ ? 5000 : 1000);
            annotationLayer.setMinScale(5000);
            annotationLayer.setVisible(type == TYPE_POINT_JCJ);

            mapView.addLayer(graphicsLayer);
            mapView.addLayer(annotationLayer);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }

    private String getPipePointLayerName(int type) {
        return type == TYPE_POINT_JCJ ? "雨水管点_检查井" : "雨水管点_特征点";
    }

    private String getPipePointAnnotationLayerName(int type) {
        return type == TYPE_POINT_JCJ ? "雨水管点_检查井注记" : "雨水管点_特征点注记";
    }

    public void addPipeLineLayers(MapView mapView, DbManager dbManager, int type) {
        Log.i(TAG, "addPipeLineLayers: ");
        GraphicsLayer graphicsLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
        graphicsLayer.setName(getPipeLineLayerName(type));

        GraphicsLayer annotationLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
        annotationLayer.setName(getPipeLineAnnotationLayerName(type));

        int colorSymbol = Color.rgb(76, 0, 0);
        int widthLineSymbol = 2;
        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(colorSymbol, widthLineSymbol, SimpleLineSymbol.STYLE.SOLID);

        Cursor cursor = null;
        try {
            cursor = dbManager.getPipeLines(type);

            while (cursor.moveToNext()) {
                PipeLine pipeLine = new PipeLine(cursor);

                Point startPoint = new Point();
                if (pipeLine.QDXZB > 0 && pipeLine.QDYZB > 0) {
                    startPoint.setX(pipeLine.QDXZB);
                    startPoint.setY(pipeLine.QDYZB);
                }
                Point endPoint = new Point();
                if (pipeLine.ZDXZB > 0 && pipeLine.ZDYZB > 0) {
                    endPoint.setX(pipeLine.ZDXZB);
                    endPoint.setY(pipeLine.ZDYZB);
                }
                if (startPoint.isValid() && endPoint.isValid()) {
                    Line line = new Line();
                    line.setStart(startPoint);
                    line.setEnd(endPoint);

                    Polyline polyline = new Polyline();
                    polyline.addSegment(line, true);

                    Map<String, Object> attributes = new HashMap<String, Object>();
                    attributes.put("QDDH", pipeLine.QDDH);
                    attributes.put("ZDDH", pipeLine.ZDDH);
                    attributes.put("CZ", pipeLine.CZ);
                    attributes.put("GJ", pipeLine.GJ);
                    attributes.put("QDMS", pipeLine.QDMS);
                    attributes.put("ZDMS", pipeLine.ZDMS);

                    Graphic polylineGraphic = new Graphic(polyline, lineSymbol, attributes);
                    graphicsLayer.addGraphic(polylineGraphic);

                    TextSymbol textSymbol = createTextSymbolInLineCenter(pipeLine.GJ + " " + pipeLine.CZ, colorSymbol, widthLineSymbol, startPoint, endPoint);
                    Graphic textSymbolGraphic = new Graphic(new Point((startPoint.getX() + endPoint.getX()) / 2, (startPoint.getY() + endPoint.getY()) / 2), textSymbol);
                    annotationLayer.addGraphic(textSymbolGraphic);
                }

            }

            annotationLayer.setMinScale(1000);
            annotationLayer.setVisible(false);

            mapView.addLayer(graphicsLayer);
            mapView.addLayer(annotationLayer);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    private String getPipeLineLayerName(int type) {
        return type == TYPE_LINE_JCJ ? "雨水管线_检查井" : "雨水管线_特征点";
    }

    private String getPipeLineAnnotationLayerName(int type) {
        return type == TYPE_LINE_JCJ ? "雨水管线_检查井注记" : "雨水管线_特征点注记";
    }

    public Layer getLayerByName(String name, MapView mapView) {
        for (Layer layer : mapView.getLayers()) {
            if (TextUtils.equals(name, layer.getName())) {
                return layer;
            }
        }
        return null;
    }

    public void addCurrentLocation(MapView mapView, GraphicsLayer layer, Location location) {
        double[] converted = CoordinateConversion.convert(location.getLongitude(), location.getLatitude());

        Drawable img = context.getResources().getDrawable(R.drawable.location);
        PictureMarkerSymbol pictureMarkerSymbol = new PictureMarkerSymbol(img);
        Point point = new Point(converted[0], converted[1]);
        Graphic graphic = new Graphic(point, pictureMarkerSymbol);

        layer.removeAll();
        layer.addGraphic(graphic);

        mapView.zoomTo(point, 1);
    }
}
