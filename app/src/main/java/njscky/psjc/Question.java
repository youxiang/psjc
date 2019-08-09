package njscky.psjc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.Symbol;
import com.esri.core.symbol.TextSymbol;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/4/1.
 */
public class Question extends Activity implements View.OnClickListener {
    private Button btclose;
    private Button btok;
    private Button btdel;
    SQLiteDatabase db = null;
    DBManager dbManager = null;
    public static Graphic gra;
    public static String _bz;
    EditText BZ;
    public static boolean blnEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question);
        if (blnEdit) {
            iniEventsEdit();
        } else {
            iniEvents();
        }
    }

    void iniEvents() {
        btok = (Button) findViewById(R.id.qok);
        btok.setOnClickListener(this);
        btclose = (Button) findViewById(R.id.qclose);
        btclose.setOnClickListener(this);
        BZ = (EditText) findViewById(R.id.qbzText);
        btdel= (Button) findViewById(R.id.qdel);
        btdel.setOnClickListener(this);
    }

    void iniEventsEdit() {
        btok = (Button) findViewById(R.id.qok);
        btok.setOnClickListener(this);
        btclose = (Button) findViewById(R.id.qclose);
        btclose.setOnClickListener(this);
        BZ = (EditText) findViewById(R.id.qbzText);
        BZ.setText(_bz);
        btdel= (Button) findViewById(R.id.qdel);
        btdel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.qclose:
                MainActivity.tmpgralayer.removeAll();
                this.finish();
                break;
            case R.id.qok:
                try {
                    DBManager.DB_PATH = MainActivity.prjDBpath;
                    dbManager = new DBManager(this);
                    db = dbManager.openDatabase();

                    Polygon polygon = (Polygon) gra.getGeometry();
                    String strZBs = "";
                    double dblCenterX = 0.0;
                    double dblCenterY = 0.0;
                    for (int i = 0; i < polygon.getPointCount(); i++) {
                        Point pt = polygon.getPoint(i);
                        double dblX = pt.getX();
                        double dblY = pt.getY();
                        dblCenterX += dblX;
                        dblCenterY += dblY;
                        DecimalFormat df = new DecimalFormat("#.###");
                        String strZB = df.format(dblX) + "-" + df.format(dblY);
                        if (!strZBs.equals("")) {
                            strZBs += ";" + strZB;
                        } else {
                            strZBs += strZB;
                        }
                    }

                    final ContentValues values = new ContentValues();
                    values.put("ZB", strZBs);
                    values.put("BZ", BZ.getText().toString().trim());

                    db.insert("WTBJ", "ZB", values);
                    db.close();

                    //中心点坐标
                    dblCenterX = dblCenterX / polygon.getPointCount();
                    dblCenterY = dblCenterY / polygon.getPointCount();
                    Point pt = new Point();
                    pt.setXY(dblCenterX, dblCenterY);
                    //标注
                    TextSymbol tsT = new TextSymbol(16, BZ.getText().toString().trim(), Color.argb(255, 0, 0, 0));
                    tsT.setFontFamily("DroidSansFallback.ttf");
                    tsT.setOffsetX(5);
                    tsT.setOffsetY(5);
                    Graphic graT = new Graphic(pt, tsT);

                    MainActivity.tmpgralayer.removeAll();

                    GraphicsLayer drawLayer=GetGraphicLayerbyName("问题标记");
                    drawLayer.addGraphic(gra);
                    drawLayer.addGraphic(graT);
                    Toast toast=Toast.makeText(Question.this, "问题标记成功！", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER| Gravity.CENTER,0, 0);
                    toast.show();
                    this.finish();

                } catch (Exception e) {
                    new AlertDialog.Builder(this)
                            .setTitle("提示")
                            .setMessage(e.getMessage())
                            .setPositiveButton("确定", null)
                            .show();
                }

                break;
            case R.id.qdel:
                try {
                    DBManager.DB_PATH = MainActivity.prjDBpath;
                    dbManager = new DBManager(this);
                    db = dbManager.openDatabase();

                    Polygon polygon = (Polygon) gra.getGeometry();
                    String strZBs = "";
                    double dblCenterX = 0.0;
                    double dblCenterY = 0.0;
                    for (int i = 0; i < polygon.getPointCount(); i++) {
                        Point pt = polygon.getPoint(i);
                        double dblX = pt.getX();
                        double dblY = pt.getY();
                        dblCenterX += dblX;
                        dblCenterY += dblY;
                        DecimalFormat df = new DecimalFormat("#.###");
                        String strZB = df.format(dblX) + "-" + df.format(dblY);
                        if (!strZBs.equals("")) {
                            strZBs += ";" + strZB;
                        } else {
                            strZBs += strZB;
                        }
                    }

                    final ContentValues values = new ContentValues();
                    values.put("ZB", strZBs);
                    values.put("BZ", BZ.getText().toString().trim());

                    db.insert("WTBJ", "ZB", values);
                    db.close();

                    //中心点坐标
                    dblCenterX = dblCenterX / polygon.getPointCount();
                    dblCenterY = dblCenterY / polygon.getPointCount();
                    Point pt = new Point();
                    pt.setXY(dblCenterX, dblCenterY);
                    //标注
                    TextSymbol tsT = new TextSymbol(16, BZ.getText().toString().trim(), Color.argb(255, 0, 0, 0));
                    tsT.setFontFamily("DroidSansFallback.ttf");
                    tsT.setOffsetX(5);
                    tsT.setOffsetY(5);
                    Graphic graT = new Graphic(pt, tsT);

                    MainActivity.tmpgralayer.removeAll();

                    GraphicsLayer drawLayer=GetGraphicLayerbyName("问题标记");
                    drawLayer.addGraphic(gra);
                    drawLayer.addGraphic(graT);
                    Toast toast=Toast.makeText(Question.this, "问题标记成功！", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER| Gravity.CENTER,0, 0);
                    toast.show();
                    this.finish();

                } catch (Exception e) {
                    new AlertDialog.Builder(this)
                            .setTitle("提示")
                            .setMessage(e.getMessage())
                            .setPositiveButton("确定", null)
                            .show();
                }
                this.finish();
                break;
        }
    }

    private GraphicsLayer GetGraphicLayerbyName(String lyrname) {
        try {
            GraphicsLayer Gralyr = null;
            Layer[] lyrlst = MainActivity.mapView.getLayers();
            for (int i = 0; i < lyrlst.length; i++) {
                Layer tmplyr = lyrlst[i];
                if (tmplyr.getClass().getName().equals(GraphicsLayer.class.getName()) && tmplyr.getName().equals(lyrname)) {
                    Gralyr = (GraphicsLayer) tmplyr;
                    break;
                }
            }

            return Gralyr;
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage(e.getMessage())
                    .setPositiveButton("确定", null)
                    .show();
            return null;
        }
    }
}
