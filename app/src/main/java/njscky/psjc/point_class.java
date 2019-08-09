package njscky.psjc;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;

import com.esri.android.map.GraphicsLayer;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.Symbol;
import com.esri.core.symbol.TextSymbol;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2015/7/6.
 */
public class point_class extends AppCompatActivity implements OnClickListener {

    public static String ColorRGB;
    public static GraphicsLayer GDgralayer;
    public static GraphicsLayer GDAnnogralayer;
    public static GraphicsLayer GXgralayer;
    SQLiteDatabase db = null;
    DBManager dbManager = null;
    public static String TableName;
    public static String TableNameCN;
    public static int uid;
    public static Point pp;
    public static String GDIMG;

    public static boolean blnEdit = false;
    public static String _gxtype;
    public static String _tcdh;
    public static String _fsw;
    public static String _tzd;
    public static String _gtlx;
    public static String _xg;
    public static String _bz;

    public static long _graphicID;
    public static float _x;
    public static float _y;

    TextView BT;
    EditText TCDH;
    Spinner FSW;
    Spinner TZD;
    EditText GTLX;
    EditText XG;
    EditText BZ;
    LinearLayout XGLayout;

    private ArrayAdapter<String> adapterFSW;
    private ArrayAdapter<String> adapterTZD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.point_layout);

        if (blnEdit) {
            iniEventsEdit();
        } else {
            iniEvents();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sumb) {
            SaveAttribute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog isExit = new AlertDialog.Builder(this).create();
            isExit.setTitle("系统提示");
            isExit.setMessage("尚未保存，确定要退出吗");
            isExit.setButton("确定", listener);
            isExit.setButton2("取消", listener);
            isExit.show();
        }
        return false;
    }

    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    finish();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    break;
                default:
                    break;
            }
        }
    };

    private void iniEvents() {
//        BT = (TextView) findViewById(R.id.btText);
//        BT.setText(TableNameCN + "属性");

       // TCDH = (EditText) findViewById(R.id.tcdhText);
//        FSW = (Spinner) findViewById(R.id.fswText);
//        TZD = (Spinner) findViewById(R.id.tzdText);
//        GTLX = (EditText) findViewById(R.id.gtlxText);
        XG=(EditText) findViewById(R.id.xgText);
        BZ = (EditText) findViewById(R.id.bzText);
//        XGLayout=(LinearLayout)findViewById(R.id.xgLayout);
//
//        if(TableNameCN=="供电") {
//            XGLayout.setVisibility(View.GONE);
//        }
//        else if(TableNameCN=="通讯"){
//            XGLayout.setVisibility(View.VISIBLE);
//        }
//
//
//        if(MainActivity.strWorkMode=="编辑") {
//            TCDH.setFocusable(true);
//            TZD.setFocusable(true);
//            FSW.setFocusable(true);
//            GTLX.setFocusable(true);
//            XG.setFocusable(true);
//            BZ.setFocusable(true);
//        }
//        else{
//            TCDH.setFocusable(false);
//            TZD.setFocusable(false);
//            FSW.setFocusable(false);
//            GTLX.setFocusable(false);
//            XG.setFocusable(false);
//            BZ.setFocusable(false);
//        }
//
//        //特征点    供电：电力-直线点、电力-拐点、电力-分支、电力-出入地点、电力-入户、电力-断头   通信：信息与通信-直线点、信息与通信-拐点、信息与通信-分支、信息与通信-出入地点、信息与通信-入户
//        if (TableName.startsWith("XX_")) {
//            String[] TZDList = {"", "直线点", "拐点", "分支", "出入地点", "入户"};
//            adapterTZD = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, TZDList);
//            adapterTZD.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            TZD.setAdapter(adapterTZD);
//            TZD.setSelection(0, true);
//        } else if (TableName.startsWith("DL_")) {
//            String[] TZDList = {"", "直线点", "拐点", "分支", "出入地点", "入户" , "断头" };
//            adapterTZD = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, TZDList);
//            adapterTZD.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            TZD.setAdapter(adapterTZD);
//            TZD.setSelection(0, true);
//        }  else {
//            String[] FSWList = {"", "直线点"};
//            adapterTZD = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, FSWList);
//            adapterTZD.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            TZD.setAdapter(adapterTZD);
//            TZD.setSelection(0, true);
//        }
//
//        //附属物 供电：电力-变压器、电力-电线杆、电力-铁塔、电力-钢管杆、电力-变电所  通信：信息与通信-线杆
//       if (TableName.startsWith("XX_")) {
//            String[] FSWList = {"", "线杆"};
//            adapterFSW = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, FSWList);
//           adapterFSW.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            FSW.setAdapter(adapterFSW);
//            FSW.setSelection(0, true);
//        } else if (TableName.startsWith("DL_")) {
//            String[] FSWList = {"", "变压器", "电线杆", "铁塔", "钢管杆", "变电所", "分线箱" };
//           adapterFSW = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, FSWList);
//           adapterFSW.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            FSW.setAdapter(adapterFSW);
//            FSW.setSelection(0, true);
//        } else {
//            String[] FSWList = {"", "线杆"};
//           adapterFSW = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, FSWList);
//           adapterFSW.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            FSW.setAdapter(adapterFSW);
//            FSW.setSelection(0, true);
//        }

    }

    private void iniEventsEdit() {

       // BT = (TextView) findViewById(R.id.btText);
       // BT.setText(TableNameCN + "属性");

       // TCDH = (EditText) findViewById(R.id.tcdhText);
//        FSW = (Spinner) findViewById(R.id.fswText);
//        TZD = (Spinner) findViewById(R.id.tzdText);
//        GTLX = (EditText) findViewById(R.id.gtlxText);
        XG=(EditText) findViewById(R.id.xgText);
        BZ = (EditText) findViewById(R.id.bzText);
//        XGLayout=(LinearLayout)findViewById(R.id.xgLayout);


//        TCDH.setText(_tcdh);
//        GTLX.setText(_gtlx);
//        XG.setText(_xg);
//        BZ.setText(_bz);

//        //特征点
//        if (TableName.startsWith("XX_")) {
//            String[] TZDList = {"", "直线点", "拐点", "分支", "出入地点", "入户", "断头"};
//            adapterTZD = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, TZDList);
//            adapterTZD.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            TZD.setAdapter(adapterTZD);
//
//            for (int i = 0; i < TZDList.length; i++) {
//                if (TZDList[i].toString().equals(_tzd)) {
//                    TZD.setSelection(i, true);
//                    break;
//                }
//            }
//        } else if (TableName.startsWith("DL_")) {
//            String[] TZDList = {"",  "直线点", "拐点", "分支", "出入地点", "入户" , "断头"};
//            adapterTZD = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, TZDList);
//            adapterTZD.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            TZD.setAdapter(adapterTZD);
//
//            for (int i = 0; i < TZDList.length; i++) {
//                if (TZDList[i].toString().equals(_tzd)) {
//                    TZD.setSelection(i, true);
//                    break;
//                }
//            }
//        }
//
//        //附属物
//        if (TableName.startsWith("XX_")) {
//            String[] FSWList = {"", "线杆"};
//            adapterFSW = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, FSWList);
//            adapterFSW.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            FSW.setAdapter(adapterFSW);
//
//            for (int i = 0; i < FSWList.length; i++) {
//                if (FSWList[i].toString().equals(_fsw)) {
//                    FSW.setSelection(i, true);
//                    break;
//                }
//            }
//        } else if (TableName.startsWith("DL_")) {
//            String[] FSWList = {"", "变压器", "电线杆", "铁塔", "钢管杆", "变电所", "分线箱"};
//            adapterFSW = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, FSWList);
//            adapterFSW.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            FSW.setAdapter(adapterFSW);
//
//            for (int i = 0; i < FSWList.length; i++) {
//                if (FSWList[i].toString().equals(_fsw)) {
//                    FSW.setSelection(i, true);
//                    break;
//                }
//            }
//        }

    }

    @Override
    public void onClick(View v) {

    }

    void SaveAttribute() {
        try {
            DBManager.DB_PATH = MainActivity.prjDBpath;
            dbManager = new DBManager(this);
            db = dbManager.openDatabase();

            final ContentValues values = new ContentValues();
            Map<String, Object> attributes = new HashMap<String, Object>();

            values.put("WTDH", TCDH.getText().toString().trim());
            attributes.put("WTDH", TCDH.getText().toString().trim());

            values.put("XZB", pp.getY());
            values.put("YZB", pp.getX());

            values.put("TZD", TZD.getSelectedItem().toString().trim());
            attributes.put("TZD", TZD.getSelectedItem().toString().trim());

            values.put("FSW", FSW.getSelectedItem().toString().trim());
            attributes.put("FSW", FSW.getSelectedItem().toString().trim());

            values.put("GTLX", GTLX.getText().toString().trim());
            attributes.put("GTLX", GTLX.getText().toString().trim());

            values.put("DMGC", XG.getText().toString().trim());
            attributes.put("DMGC", XG.getText().toString().trim());

            values.put("BZ", BZ.getText().toString().trim());
            attributes.put("BZ", BZ.getText().toString().trim());

            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = sDateFormat.format(new java.util.Date());
            values.put("TCRQ", date);

            values.put("GXDM", TableName);
            attributes.put("GXDM", TableName);

            if (!blnEdit) {
                //判断探测点号是否已存在
                String sql = "SELECT * FROM POINT WHERE WTDH ='" + TCDH.getText().toString().trim() + "' and GXDM = '"+TableName+"'";
                Cursor cur = db.rawQuery(sql, null);
                if (cur != null) {
                    if (cur.moveToFirst()) {
                        db.close();

                        Toast toast = Toast.makeText(this, "点号" + TCDH.getText().toString().trim() + "已存在", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                }

                db.insert("POINT", "WTDH", values);
                db.close();

                String[] rgb = ColorRGB.split(",");
                SimpleMarkerSymbol sms1 = new SimpleMarkerSymbol(Color.argb(255,Integer.valueOf(rgb[0]), Integer.valueOf(rgb[1]), Integer.valueOf(rgb[2])), 8, SimpleMarkerSymbol.STYLE.CIRCLE);
                Graphic graphic = new Graphic(pp, sms1, attributes);
                GDgralayer.addGraphic(graphic);

                Toast toast = Toast.makeText(this, "提交成功！", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                toast.show();

                TextSymbol tsT = new TextSymbol(16, TCDH.getText().toString().trim(), Color.argb(255, Integer.valueOf(rgb[0]), Integer.valueOf(rgb[1]), Integer.valueOf(rgb[2])));
                tsT.setOffsetX(5);
                tsT.setOffsetY(5);
                Graphic graphicT = new Graphic(pp, tsT);
                GDAnnogralayer.addGraphic(graphicT);

            } else {
                //修改条件
                String whereClause = "WTDH=? and GXDM=?";
                //修改添加参数
                String[] whereArgs = {String.valueOf(_tcdh),TableName};
                //修改
                db.update("POINT", values, whereClause, whereArgs);

                //若点号修改 对应的线表也修改
                if (!_tcdh.equals(TCDH.getText().toString().trim())) {
                    String sql = "update LINE set QDDH ='" + TCDH.getText().toString().trim() + "' where QDDH = '" + _tcdh + "' and GXDM = '"+TableName+"'";
                    db.execSQL(sql);

                    String sql1 = "update LINE set ZDDH ='" + TCDH.getText().toString().trim() + "' where ZDDH = '" + _tcdh + "' and GXDM = '"+TableName+"'";
                    db.execSQL(sql1);

                    //更新管点标注
                    Point pt1 = MainActivity.mapView.toScreenPoint(pp);
                    int[] selids = GDAnnogralayer.getGraphicIDs((float) pt1.getX(), (float) pt1.getY(), 20);
                    String[] rgb = ColorRGB.split(",");
                    TextSymbol tsT = new TextSymbol(16, TCDH.getText().toString().trim(), Color.argb(255, Integer.valueOf(rgb[0]), Integer.valueOf(rgb[1]), Integer.valueOf(rgb[2])));
                    tsT.setOffsetX(5);
                    tsT.setOffsetY(5);
                    if (selids != null) {
                        for (int i = 0; i < selids.length; i++) {
                            Graphic gra = GDAnnogralayer.getGraphic(selids[i]);
                            Symbol symbol = gra.getSymbol();
                            TextSymbol tsT1 = (TextSymbol) symbol;
                            if (tsT1.getText().toString().equals(_tcdh)) {
                                GDAnnogralayer.updateGraphic(selids[i], tsT);
                                break;
                            }
                        }
                    }

                    //更新管线Attribute
                    selids = GXgralayer.getGraphicIDs((float) pt1.getX(), (float) pt1.getY(), 20);
                    for (int i = 0; i < selids.length; i++) {
                        Graphic graline = GXgralayer.getGraphic(selids[i]);
                        if (graline.getGeometry().toString().contains("Polyline")) {
                            //修改属性
                            Map<String, Object> attributes1 = graline.getAttributes();
                            Map<String, Object> newattributes = new HashMap<String, Object>();
                            Iterator<String> iterator = attributes1.keySet().iterator();
                            while (iterator.hasNext()) {
                                String key = iterator.next();
                                String value = (String) attributes1.get(key);
                                if (key.equals("QDDH")) {
                                    if (value.equals(_tcdh)) {
                                        newattributes.put("QDDH", TCDH.getText().toString().trim());
                                    } else {
                                        newattributes.put(key, value);
                                    }
                                } else if (key.equals("ZDDH")) {
                                    if (value.equals(_tcdh)) {
                                        newattributes.put("ZDDH", TCDH.getText().toString().trim());
                                    } else {
                                        newattributes.put(key, value);
                                    }
                                } else {
                                    newattributes.put(key, value);
                                }
                            }
                            GXgralayer.updateGraphic((int) graline.getId(), newattributes);
                        }
                    }
                }
                db.close();

                String[] rgb = ColorRGB.split(",");
                    SimpleMarkerSymbol sms1 = new SimpleMarkerSymbol(Color.argb(255,  Integer.valueOf(rgb[0]), Integer.valueOf(rgb[1]), Integer.valueOf(rgb[2])), 8, SimpleMarkerSymbol.STYLE.CIRCLE);
                    Graphic graphic = new Graphic(pp, sms1, attributes);
                    GDgralayer.removeGraphic((int) _graphicID);
                    GDgralayer.addGraphic(graphic);



                Toast toast = Toast.makeText(this, "提交成功！", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                toast.show();

            }
            _tcdh = TCDH.getText().toString().trim();
            _fsw = FSW.getSelectedItem().toString().trim();
            _gtlx = GTLX.getText().toString().trim();
            _xg = XG.getText().toString().trim();
            _gxtype = TableName;
            this.finish();
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage(e.getMessage())
                    .setPositiveButton("确定", null)
                    .show();
        }

    }


}