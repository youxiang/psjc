package njscky.psjc;

import android.app.Activity;
import android.app.AlertDialog;

import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/4/1.
 */
public class FeatureSelect extends Activity implements View.OnClickListener {
    private LinearLayout mlLayout;
    private Button btclose;
    private Button btok;

    public static ArrayList<Graphic> Graphiclst;
    public static ArrayList<String> TableNamelst;
    public static ArrayList<String> TableNameCNlst;
    public static ArrayList<String> DHlst;
    public static ArrayList<String> RGBlst;
    public static ArrayList<GraphicsLayer>  GDgralayerlst;
    public static ArrayList<GraphicsLayer>  GXgralayerlst;
    public static ArrayList<GraphicsLayer>  GDAnnogralayerlst;
    public static ArrayList<GraphicsLayer>  GXAnnogralayerlst;
    public static ArrayList<String>  Imglst;
    public static ArrayList<String>  Typelst;

    Graphic gra;
    String strDH;
    int index=-1;

    public static float _x;
    public static float _y;
    static PictureMarkerSymbol sms;
    static SimpleLineSymbol sls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.featureselect);
        CreatView();
    }

    void CreatView() {
        mlLayout=(LinearLayout)findViewById(R.id.selectconnect);
        btok=(Button)findViewById(R.id.selectok);
        btok.setOnClickListener(this);
        btclose=(Button)findViewById(R.id.selectclose);
        btclose.setOnClickListener(this);

        for (int i = 0; i < DHlst.size(); i++) {
            CheckBox ckbItem = new CheckBox(this);
            ckbItem.setText(TableNameCNlst.get(i).toString()+"："+DHlst.get(i).toString());
            ckbItem.setTextColor(Color.BLACK);
            ckbItem.setTag(R.id.tag_first,i);

            ckbItem.setChecked(false);
            ckbItem.setOnCheckedChangeListener(mCheckBoxChanged);
            mlLayout.addView(ckbItem);
        }

    }

    private CheckBox.OnCheckedChangeListener mCheckBoxChanged = new CheckBox.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked) {
                if(isNoOneCheck()) {
                    index = (int)buttonView.getTag(R.id.tag_first);
                    strDH=buttonView.getText().toString();

                    if(index>-1)
                    {
                        gra = (Graphic) Graphiclst.get(index);
                        if(gra.getGeometry().toString().contains("Point")) {
                            MainActivity.tmpgralayer.removeAll();
                            SimpleMarkerSymbol sms1 = new SimpleMarkerSymbol(Color.argb(255, 0, 255, 255), 18, SimpleMarkerSymbol.STYLE.CIRCLE);
                            Point pt = (Point) gra.getGeometry();
                            Graphic graphic = new Graphic(pt, sms1);
                            MainActivity.tmpgralayer.addGraphic(graphic);
                        }
                        else if(gra.getGeometry().toString().contains("Polyline"))
                        {
                            MainActivity.tmpgralayer.removeAll();
                            SimpleLineSymbol sls1 = new SimpleLineSymbol(Color.argb(255, 0, 255, 255), 8, SimpleLineSymbol.STYLE.SOLID);
                            Polyline pt = (Polyline) gra.getGeometry();
                            Graphic graphic = new Graphic(pt, sls1);
                            MainActivity.tmpgralayer.addGraphic(graphic);
                        }
                     }
                }
                else {
                    buttonView.setChecked(false);
                    new AlertDialog.Builder(FeatureSelect.this)
                            .setTitle("提示")
                            .setMessage("不能选择多个管点或管线")
                            .setPositiveButton("确定", null)
                            .show();
                }
            }
            else
            {
                if(index>-1)
                {
                    MainActivity.tmpgralayer.removeAll();
                }
            }

        }
    };

    private boolean isNoOneCheck()
    {
        int checknum = 0;
        for (int i=0;i<mlLayout.getChildCount();i++) {
            CheckBox ckbItem = (CheckBox) mlLayout.getChildAt(i);
            if (ckbItem.isChecked())
            {
                checknum++;
            }
        }
        if (checknum > 1)
            return false;
        else
            return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.selectclose:
                MainActivity.tmpgralayer.removeAll();
                this.finish();
                break;
            case R.id.selectok:
                MainActivity.tmpgralayer.removeAll();
                if(index>-1) {
                    gra = (Graphic) Graphiclst.get(index);
                    if (strDH.contains("管点")) {
                        point_class.pp = (Point) gra.getGeometry();
                        point_class._x = _x;
                        point_class._y = _y;
                        point_class.TableName = TableNamelst.get(index);
                        point_class.TableNameCN = TableNameCNlst.get(index);
                        point_class.ColorRGB = RGBlst.get(index);
                        point_class.GDgralayer = GDgralayerlst.get(index);
                        point_class.GXgralayer = GXgralayerlst.get(index);
                        point_class.GDAnnogralayer=GDAnnogralayerlst.get(index);
                        point_class._tcdh = gra.getAttributeValue("WTDH").toString();
                        point_class._fsw = gra.getAttributeValue("FSW").toString();

                        point_class._bz = gra.getAttributeValue("BZ").toString();

                        point_class.GDIMG=Imglst.get(index);
                        point_class._graphicID = gra.getId();
                        point_class.blnEdit = true;

                        Intent Pointintent = new Intent();
                        Pointintent.setClass(this, point_class.class);
                        startActivity(Pointintent);

                    } else if (strDH.contains("管线")) {
                        MainActivity.polyline=(Polyline)gra.getGeometry();
                        line_class.TableName = TableNamelst.get(index);
                        line_class.TableNameCN = TableNameCNlst.get(index);
                        line_class._x =  _x;
                        line_class._y =  _x;
                        line_class.GXAnnogralayer=GXAnnogralayerlst.get(index);
                        line_class.TYPE=Typelst.get(index);
                        line_class.ColorRGB=RGBlst.get(index);
                        line_class._qddh = gra.getAttributeValue("QDDH").toString();
                        line_class._zddh = gra.getAttributeValue("ZDDH").toString();

                        line_class._cz = gra.getAttributeValue("CZ").toString();
                        line_class._gj = gra.getAttributeValue("GJ").toString();
                        for (int i = 0; i < gra.getAttributeNames().length; i++) {
                            if (gra.getAttributeNames()[i].equals("DYZ")) {
                                line_class._dyz = gra.getAttributeValue("DYZ").toString();
                                break;
                            }
                        }
                        for (int j = 0; j < gra.getAttributeNames().length; j++) {
                            if (gra.getAttributeNames()[j].equals("YL")) {
                                line_class._yl = gra.getAttributeValue("YL").toString();
                                break;
                            }
                        }

                        line_class._bz = gra.getAttributeValue("BZ").toString();

                        line_class._graphicID = gra.getId();
                        line_class.blnEdit = true;
                        line_class._graphic = gra;
                        line_class.GXgralayer = GXgralayerlst.get(index);
                        String[] rgb = RGBlst.get(index).split(",");
                        sls = new SimpleLineSymbol(Color.argb(255, Integer.valueOf(rgb[0]), Integer.valueOf(rgb[1]), Integer.valueOf(rgb[2])), 2, SimpleLineSymbol.STYLE.SOLID);

                        Intent lineintent = new Intent();
                        lineintent.setClass(this, line_class.class);
                        startActivity(lineintent);

                        this.finish();
                    }
                }
                break;
        }
    }

}
