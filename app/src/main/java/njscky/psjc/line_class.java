package njscky.psjc;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
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

import com.esri.android.map.GraphicsLayer;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.Symbol;
import com.esri.core.symbol.TextSymbol;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/7/6.
 */
public class line_class extends AppCompatActivity implements OnClickListener {

    SQLiteDatabase db = null;
    DBManager dbManager = null;
    public static String TableName;
    public static String TableNameCN;
    public static String _qddh;
    public static String _zddh;
    public static String _cz;
    public static String _gj;
    public static String _dyz;
    public static String _yl;
    public static String _bz;
    public static String _gxtype;
    public static String _gtxlmc;
    public static String _hls;
    public static String _dlts;
    public static String _xg;

    public static long _graphicID;
    public static float _x;
    public static float _y;
    public static GraphicsLayer GXgralayer;
    public static GraphicsLayer GXAnnogralayer;
    public static GraphicsLayer GDgralayer;
    public static GraphicsLayer GDAnnogralayer;
    public static String strGDImg;
    public static Graphic _graphic;
    public static String ColorRGB;
    public static String TYPE;

    TextView BT;

    EditText QDDH;
    EditText ZDDH;
    EditText GTXLMC;
    EditText HLS;
    EditText XG;
    EditText DLTS;
    Spinner CZ;
    EditText GJ;
    Spinner DYZ;
    Spinner YL;


    EditText BZ;
    LinearLayout DYZLayout;
    LinearLayout YLLayout;

    private ArrayAdapter<String> adapterCZ;
    private ArrayAdapter<String> adapterDYZ;
    private ArrayAdapter<String> adapterYL;
    public static boolean blnEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.line_layout);

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
                    if (MainActivity.tmpgralayer.getNumberOfGraphics() > 0) {
                        MainActivity.tmpgralayer.removeAll();
                    }
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

        BT = (TextView) findViewById(R.id.btText);
        BT.setText(TableNameCN + "属性");

        DYZLayout = (LinearLayout) findViewById(R.id.dyzLayout);
        YLLayout = (LinearLayout) findViewById(R.id.ylLayout);

        QDDH = (EditText) findViewById(R.id.QDDHText);
        ZDDH = (EditText) findViewById(R.id.ZDDHText);

        QDDH.setText(_qddh);
        ZDDH.setText(_zddh);

        CZ = (Spinner) findViewById(R.id.czText);
        GJ = (EditText) findViewById(R.id.gjText);
        DYZ = (Spinner) findViewById(R.id.dyzText);
        YL = (Spinner) findViewById(R.id.ylText);
        BZ = (EditText) findViewById(R.id.bzText);
        GTXLMC = (EditText) findViewById(R.id.gtxlmcText);
        HLS = (EditText) findViewById(R.id.hlsText);
        DLTS = (EditText) findViewById(R.id.dltsText);
        XG = (EditText) findViewById(R.id.xgText);

        if (TableName.equals(_gxtype)) {
            GTXLMC.setText(_gtxlmc);
        }
        if (TableName.equals(_gxtype)) {
            HLS.setText(_hls);
        }
        if (TableName.equals(_gxtype)) {
            DLTS.setText(_dlts);
        }
        if (TableName.equals(_gxtype)) {
            GJ.setText(_gj);
        }
        if (TableName.equals(_gxtype)) {
            XG.setText(_xg);
        }

        //材质
        if (TableName.startsWith("DL_") || TableName.startsWith("ZH_")) {
            String[] CZList = {"铜", "铝", "空管"};
            adapterCZ = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, CZList);
            adapterCZ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            CZ.setAdapter(adapterCZ);
            if (TableName.equals(_gxtype)) {
                for (int j = 0; j < CZList.length; j++) {
                    if (CZList[j].toString().equals(_cz)) {
                        CZ.setSelection(j, true);
                        break;
                    }
                }
            } else {
                CZ.setSelection(0, true);
            }
        } else if (TableName.startsWith("XX_")) {
            String[] CZList = {"铜", "光纤", "铜/光", "空管", "光缆"};
            adapterCZ = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, CZList);
            adapterCZ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            CZ.setAdapter(adapterCZ);
            if (TableName.equals(_gxtype)) {
                for (int j = 0; j < CZList.length; j++) {
                    if (CZList[j].toString().equals(_cz)) {
                        CZ.setSelection(j, true);
                        break;
                    }
                }
            } else {
                CZ.setSelection(0, true);
            }
        } else if (TableName.startsWith("PS_")) {
            String[] CZList = {"砼", "PVC", "砖", "钢", "铸铁", "玻璃钢", "PE", "陶瓷", "塑料"};
            adapterCZ = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, CZList);
            adapterCZ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            CZ.setAdapter(adapterCZ);
            if (TableName.equals(_gxtype)) {
                for (int j = 0; j < CZList.length; j++) {
                    if (CZList[j].toString().equals(_cz)) {
                        CZ.setSelection(j, true);
                        break;
                    }
                }
            } else {
                CZ.setSelection(0, true);
            }
        } else if (TableName.startsWith("JS_")) {
            String[] CZList = {"砼", "PVC", "玻璃钢", "钢", "铸铁", "镀锌管", "PE", "塑料"};
            adapterCZ = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, CZList);
            adapterCZ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            CZ.setAdapter(adapterCZ);
            if (TableName.equals(_gxtype)) {
                for (int j = 0; j < CZList.length; j++) {
                    if (CZList[j].toString().equals(_cz)) {
                        CZ.setSelection(j, true);
                        break;
                    }
                }
            } else {
                CZ.setSelection(0, true);
            }
        } else if (TableName.startsWith("RL_")) {
            String[] CZList = {"钢", "铸铁", "砖"};
            adapterCZ = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, CZList);
            adapterCZ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            CZ.setAdapter(adapterCZ);
            if (TableName.equals(_gxtype)) {
                for (int j = 0; j < CZList.length; j++) {
                    if (CZList[j].toString().equals(_cz)) {
                        CZ.setSelection(j, true);
                        break;
                    }
                }
            } else {
                CZ.setSelection(0, true);
            }
        } else if (TableName.startsWith("RQ_")) {
            String[] CZList = {"钢", "铸铁", "PE", "塑料"};
            adapterCZ = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, CZList);
            adapterCZ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            CZ.setAdapter(adapterCZ);
            if (TableName.equals(_gxtype)) {
                for (int j = 0; j < CZList.length; j++) {
                    if (CZList[j].toString().equals(_cz)) {
                        CZ.setSelection(j, true);
                        break;
                    }
                }
            } else {
                CZ.setSelection(0, true);
            }
        } else if (TableName.startsWith("GY_")) {
            String[] CZList = {"钢", "铸铁", "PE", "PVC", "砼"};
            adapterCZ = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, CZList);
            adapterCZ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            CZ.setAdapter(adapterCZ);
            if (TableName.equals(_gxtype)) {
                for (int j = 0; j < CZList.length; j++) {
                    if (CZList[j].toString().equals(_cz)) {
                        CZ.setSelection(j, true);
                        break;
                    }
                }
            } else {
                CZ.setSelection(0, true);
            }
        } else if (TableName.startsWith("BM_")) {
            String[] CZList = {"砼", "PVC", "铜", "光纤", "铸铁", "钢"};
            adapterCZ = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, CZList);
            adapterCZ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            CZ.setAdapter(adapterCZ);
            if (TableName.equals(_gxtype)) {
                for (int j = 0; j < CZList.length; j++) {
                    if (CZList[j].toString().equals(_cz)) {
                        CZ.setSelection(j, true);
                        break;
                    }
                }
            } else {
                CZ.setSelection(0, true);
            }
        } else {
            String[] CZList = {"砼", "PVC", "铜", "光纤", "铸铁", "钢"};
            adapterCZ = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, CZList);
            adapterCZ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            CZ.setAdapter(adapterCZ);
            if (TableName.equals(_gxtype)) {
                for (int j = 0; j < CZList.length; j++) {
                    if (CZList[j].toString().equals(_cz)) {
                        CZ.setSelection(j, true);
                        break;
                    }
                }
            } else {
                CZ.setSelection(0, true);
            }
        }


        //电压值
        if (TableName.startsWith("DL_")) {
            DYZ.setEnabled(true);
            DYZLayout.setVisibility(View.VISIBLE);
            String[] DYZList = {" ", "0","0.4", "10", "20", "35", "110", "220", "500"};
            adapterDYZ = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, DYZList);
            adapterDYZ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            DYZ.setAdapter(adapterDYZ);
            if (TableName.equals(_gxtype)) {
                for (int j = 0; j < DYZList.length; j++) {
                    if (DYZList[j].toString().equals(_dyz)) {
                        DYZ.setSelection(j, true);
                        break;
                    }
                }
            } else {
                DYZ.setSelection(0, true);
            }
        } else {
            DYZ.setEnabled(false);
            DYZLayout.setVisibility(View.GONE);
        }

        //压力
        if (TableName.startsWith("GY_")) {
            YL.setEnabled(true);
            YLLayout.setVisibility(View.VISIBLE);
            String[] YLList = {"无压", "低压", "中压", "高压"};
            adapterYL = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, YLList);
            adapterYL.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            YL.setAdapter(adapterYL);
            if (TableName.equals(_gxtype)) {
                for (int j = 0; j < YLList.length; j++) {
                    if (YLList[j].toString().equals(_yl)) {
                        YL.setSelection(j, true);
                        break;
                    }
                }
            } else {
                YL.setSelection(0, true);
            }
        } else if (TableName.startsWith("RQ_")) {
            YL.setEnabled(true);
            YLLayout.setVisibility(View.VISIBLE);
            String[] YLList = {"低压", "中压", "高压"};
            adapterYL = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, YLList);
            adapterYL.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            YL.setAdapter(adapterYL);
            if (TableName.equals(_gxtype)) {
                for (int j = 0; j < YLList.length; j++) {
                    if (YLList[j].toString().equals(_yl)) {
                        YL.setSelection(j, true);
                        break;
                    }
                }
            } else {
                YL.setSelection(0, true);
            }
        } else if (TableName.startsWith("PS_")) {
            YL.setEnabled(true);
            YLLayout.setVisibility(View.VISIBLE);
            String[] YLList = {" ", "有压"};
            adapterYL = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, YLList);
            adapterYL.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            YL.setAdapter(adapterYL);
            if (TableName.equals(_gxtype)) {
                for (int j = 0; j < YLList.length; j++) {
                    if (YLList[j].toString().equals(_yl)) {
                        YL.setSelection(j, true);
                        break;
                    }
                }
            } else {
                YL.setSelection(0, true);
            }
        } else {
            YL.setEnabled(false);
            YLLayout.setVisibility(View.GONE);
        }

    }

    private void iniEventsEdit() {
        BT = (TextView) findViewById(R.id.btText);
        BT.setText(TableNameCN + "属性");

        DYZLayout = (LinearLayout) findViewById(R.id.dyzLayout);
        YLLayout = (LinearLayout) findViewById(R.id.ylLayout);

        QDDH = (EditText) findViewById(R.id.QDDHText);
        ZDDH = (EditText) findViewById(R.id.ZDDHText);

        QDDH.setText(_qddh);
        ZDDH.setText(_zddh);
        CZ = (Spinner) findViewById(R.id.czText);
        GJ = (EditText) findViewById(R.id.gjText);
        DYZ = (Spinner) findViewById(R.id.dyzText);
        YL = (Spinner) findViewById(R.id.ylText);
        BZ = (EditText) findViewById(R.id.bzText);
        GTXLMC = (EditText) findViewById(R.id.gtxlmcText);
        HLS = (EditText) findViewById(R.id.hlsText);
        DLTS = (EditText) findViewById(R.id.dltsText);
        XG = (EditText) findViewById(R.id.xgText);

        if(MainActivity.strWorkMode=="编辑") {
            QDDH.setFocusable(true);
            ZDDH.setFocusable(true);
            CZ.setFocusable(true);
            GJ.setFocusable(true);
            DYZ.setFocusable(true);
            YL.setFocusable(true);
            BZ.setFocusable(true);
            GTXLMC.setFocusable(true);
            HLS.setFocusable(true);
            DLTS.setFocusable(true);
            XG.setFocusable(true);
        }
        else{
            QDDH.setFocusable(false);
            ZDDH.setFocusable(false);
            CZ.setFocusable(false);
            GJ.setFocusable(false);
            DYZ.setFocusable(false);
            YL.setFocusable(false);
            BZ.setFocusable(false);
            GTXLMC.setFocusable(false);
            HLS.setFocusable(false);
            DLTS.setFocusable(false);
            XG.setFocusable(false);
        }

        //材质
        if (TableName.startsWith("DL_") || TableName.startsWith("ZH_")) {
            String[] CZList = {"铜", "铝", "空管"};
            adapterCZ = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, CZList);
            adapterCZ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            CZ.setAdapter(adapterCZ);

            for (int j = 0; j < CZList.length; j++) {
                if (CZList[j].toString().equals(_cz)) {
                    CZ.setSelection(j, true);
                    break;
                }
            }

        } else if (TableName.startsWith("XX_")) {
            String[] CZList = {"铜", "光纤", "铜/光", "空管", "光缆"};
            adapterCZ = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, CZList);
            adapterCZ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            CZ.setAdapter(adapterCZ);

            for (int j = 0; j < CZList.length; j++) {
                if (CZList[j].toString().equals(_cz)) {
                    CZ.setSelection(j, true);
                    break;
                }
            }

        } else if (TableName.startsWith("PS_")) {
            String[] CZList = {"砼", "PVC", "砖", "钢", "铸铁", "玻璃钢", "PE", "陶瓷", "塑料"};
            adapterCZ = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, CZList);
            adapterCZ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            CZ.setAdapter(adapterCZ);

            for (int j = 0; j < CZList.length; j++) {
                if (CZList[j].toString().equals(_cz)) {
                    CZ.setSelection(j, true);
                    break;
                }
            }

        } else if (TableName.startsWith("JS_")) {
            String[] CZList = {"砼", "PVC", "玻璃钢", "钢", "铸铁", "镀锌管", "PE", "塑料"};
            adapterCZ = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, CZList);
            adapterCZ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            CZ.setAdapter(adapterCZ);

            for (int j = 0; j < CZList.length; j++) {
                if (CZList[j].toString().equals(_cz)) {
                    CZ.setSelection(j, true);
                    break;
                }
            }

        } else if (TableName.startsWith("RL_")) {
            String[] CZList = {"钢", "铸铁", "砖"};
            adapterCZ = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, CZList);
            adapterCZ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            CZ.setAdapter(adapterCZ);

            for (int j = 0; j < CZList.length; j++) {
                if (CZList[j].toString().equals(_cz)) {
                    CZ.setSelection(j, true);
                    break;
                }
            }

        } else if (TableName.startsWith("RQ_")) {
            String[] CZList = {"钢", "铸铁", "PE", "塑料"};
            adapterCZ = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, CZList);
            adapterCZ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            CZ.setAdapter(adapterCZ);

            for (int j = 0; j < CZList.length; j++) {
                if (CZList[j].toString().equals(_cz)) {
                    CZ.setSelection(j, true);
                    break;
                }
            }

        } else if (TableName.startsWith("GY_")) {
            String[] CZList = {"钢", "铸铁", "PE", "PVC", "砼"};
            adapterCZ = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, CZList);
            adapterCZ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            CZ.setAdapter(adapterCZ);

            for (int j = 0; j < CZList.length; j++) {
                if (CZList[j].toString().equals(_cz)) {
                    CZ.setSelection(j, true);
                    break;
                }
            }

        } else if (TableName.startsWith("BM_")) {
            String[] CZList = {"砼", "PVC", "铜", "光纤", "铸铁", "钢"};
            adapterCZ = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, CZList);
            adapterCZ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            CZ.setAdapter(adapterCZ);

            for (int j = 0; j < CZList.length; j++) {
                if (CZList[j].toString().equals(_cz)) {
                    CZ.setSelection(j, true);
                    break;
                }
            }

        } else {
            String[] CZList = {"砼", "PVC", "铜", "光纤", "铸铁", "钢"};
            adapterCZ = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, CZList);
            adapterCZ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            CZ.setAdapter(adapterCZ);

            for (int j = 0; j < CZList.length; j++) {
                if (CZList[j].toString().equals(_cz)) {
                    CZ.setSelection(j, true);
                    break;
                }
            }

        }


        //电压值
        if (TableName.startsWith("DL_")) {
            DYZ.setEnabled(true);
            DYZLayout.setVisibility(View.VISIBLE);
            String[] DYZList = {" ", "0","0.4", "10", "20", "35", "110", "220", "500"};
            adapterDYZ = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, DYZList);
            adapterDYZ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            DYZ.setAdapter(adapterDYZ);

            for (int j = 0; j < DYZList.length; j++) {
                if (DYZList[j].toString().equals(_dyz)) {
                    DYZ.setSelection(j, true);
                    break;
                }
            }

        } else {
            DYZ.setEnabled(false);
            DYZLayout.setVisibility(View.GONE);
        }

        //压力
        if (TableName.startsWith("GY_")) {
            YL.setEnabled(true);
            YLLayout.setVisibility(View.VISIBLE);
            String[] YLList = {" ", "无压", "低压", "中压", "高压"};
            adapterYL = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, YLList);
            adapterYL.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            YL.setAdapter(adapterYL);

            for (int j = 0; j < YLList.length; j++) {
                if (YLList[j].toString().equals(_yl)) {
                    YL.setSelection(j, true);
                    break;
                }
            }

        } else if (TableName.startsWith("RQ_")) {
            YL.setEnabled(true);
            YLLayout.setVisibility(View.VISIBLE);
            String[] YLList = {" ", "低压", "中压", "高压"};
            adapterYL = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, YLList);
            adapterYL.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            YL.setAdapter(adapterYL);

            for (int j = 0; j < YLList.length; j++) {
                if (YLList[j].toString().equals(_yl)) {
                    YL.setSelection(j, true);
                    break;
                }
            }

        } else if (TableName.startsWith("PS_")) {
            YL.setEnabled(true);
            YLLayout.setVisibility(View.VISIBLE);
            String[] YLList = {" ", "有压"};
            adapterYL = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, YLList);
            adapterYL.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            YL.setAdapter(adapterYL);

            for (int j = 0; j < YLList.length; j++) {
                if (YLList[j].toString().equals(_yl)) {
                    YL.setSelection(j, true);
                    break;
                }
            }

        } else {
            YL.setEnabled(false);
            YLLayout.setVisibility(View.GONE);
        }


        GJ.setText(_gj);
        BZ.setText(_bz);
        GTXLMC.setText(_gtxlmc);
        HLS.setText(_hls);
        DLTS.setText(_dlts);
        XG.setText(_xg);
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

            values.put("QDDH", QDDH.getText().toString().trim());
            attributes.put("QDDH", QDDH.getText().toString().trim());

            values.put("ZDDH", ZDDH.getText().toString().trim());
            attributes.put("ZDDH", ZDDH.getText().toString().trim());

            values.put("CZ", CZ.getSelectedItem().toString().trim());
            attributes.put("CZ", CZ.getSelectedItem().toString().trim());

            values.put("GJ", GJ.getText().toString().trim());
            attributes.put("GJ", GJ.getText().toString().trim());

            if (TableName.startsWith("DL_")) {
                values.put("DYZ", DYZ.getSelectedItem().toString().trim());
                attributes.put("DYZ", DYZ.getSelectedItem().toString().trim());
            }
            if (TableName.startsWith("GY_") || TableName.startsWith("RQ_") || TableName.startsWith("PS_")) {
                values.put("YL", YL.getSelectedItem().toString().trim());
                attributes.put("YL", YL.getSelectedItem().toString().trim());
            }

            values.put("BZ", BZ.getText().toString().trim());
            attributes.put("BZ", BZ.getText().toString().trim());

            values.put("XLMC", GTXLMC.getText().toString().trim());
            attributes.put("XLMC", GTXLMC.getText().toString().trim());

            values.put("HLS", HLS.getText().toString().trim());
            attributes.put("HLS", HLS.getText().toString().trim());

            values.put("DLTS", DLTS.getText().toString().trim());
            attributes.put(" ", DLTS.getText().toString().trim());

            values.put("QDMS", XG.getText().toString().trim());
            attributes.put("QDMS", XG.getText().toString().trim());

            if(!blnEdit) {
//                values.put("GXBH", "("+_qddh+")-"+"("+_zddh+")");
//                attributes.put("GXBH", "("+_qddh+")-"+"("+_zddh+")");

                Double dblSx = MainActivity.polyline.getPoint(0).getX();
                Double dblSy = MainActivity.polyline.getPoint(0).getY();
                Double dblEx = MainActivity.polyline.getPoint(1).getX();
                Double dblEy = MainActivity.polyline.getPoint(1).getY();

                values.put("QDXZB", dblSx);
                attributes.put("QDXZB", dblSx);

                values.put("QDYZB", dblSy);
                attributes.put("QDYZB", dblSy);

                values.put("ZDXZB", dblEx);
                attributes.put("ZDXZB", dblEx);

                values.put("ZDYZB", dblEy);
                attributes.put("ZDYZB", dblEy);
            }

            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = sDateFormat.format(new java.util.Date());
            values.put("TCRQ", date);

            values.put("GXDM", TableName);
            attributes.put("GXDM", TableName);

            if (!blnEdit) {
                if(MainActivity.blndraw==true){
                    final ContentValues values1 = new ContentValues();
                    Map<String, Object> attributes1 = new HashMap<String, Object>();
                    values1.put("WTDH", _zddh);
                    attributes1.put("WTDH", _zddh);
                    values1.put("XZB", _zddh.substring(0,_zddh.indexOf("-")));
                    values1.put("YZB",  _zddh.substring(_zddh.indexOf("-")+1));
                    values1.put("FSW", "");
                    attributes1.put("FSW", "");
                    values1.put("BZ", "");
                    attributes1.put("BZ", "");
                    values1.put("TCRQ", date);
                    values1.put("GXDM", TableName);
                    attributes1.put("GXDM", TableName);
                    values1.put("GTLX",  "");
                    attributes1.put("GTLX",  "");

                    db.insert("POINT", "WTDH", values1);
                    db.insert("LINE", "QDDH", values);
                    MainActivity.blndraw = false;

                    //创建新增管点
                    Point pt = new Point();
                    double dblX = Double.valueOf(_zddh.substring(0, _zddh.indexOf("-")));
                    double dblY = Double.valueOf(_zddh.substring(_zddh.indexOf("-")+1));
                    pt.setXY(dblY, dblX);
                    String strImgName = MainActivity.ImgNameList.get(MainActivity.ImgTypeList.indexOf(strGDImg + "-" + "直线点"));
                    int imgId = getResources().getIdentifier(strImgName, "drawable", getPackageName());
                    Drawable image = getResources().getDrawable(imgId);
                    PictureMarkerSymbol sms = new PictureMarkerSymbol(image);
                    Graphic graphic0 = new Graphic(pt, sms, attributes1);
                    GDgralayer.addGraphic(graphic0);
                    if (image != null) {
                        image.setCallback(null);
                        sms = null;
                        graphic0=null;
                    }

                    //创建管点注记
                    String[] rgb = ColorRGB.split(",");
                    TextSymbol tsT = new TextSymbol(16, _zddh, Color.argb(255, Integer.valueOf(rgb[0]), Integer.valueOf(rgb[1]), Integer.valueOf(rgb[2])));
                    tsT.setOffsetX(5);
                    tsT.setOffsetY(5);
                    Graphic graphicT = new Graphic(pt, tsT);
                    GDAnnogralayer.addGraphic(graphicT);
                }
                else
                {
                    db.insert("LINE", "QDDH", values);
                }
                db.close();

                MainActivity.CreatGXGraphic(MainActivity.polyline, attributes);


                //添加标注
                //注记位置为线中心点计算 注记沿线注记，角度取线的弧度大于90度小于等于180
                Point ptS = MainActivity.polyline.getPoint(0);
                Point ptE = MainActivity.polyline.getPoint(1);
                double d_CenterX = (ptE.getX() + ptS.getX()) / 2;
                double d_CenterY = (ptE.getY() + ptS.getY()) / 2;
                Point pt = new Point();
                pt.setXY(d_CenterX, d_CenterY);

                // 如果直线的斜率为k，倾斜角为α，则①当k不存在时，α＝pi/2 ；②当k≥0时，α＝arctank；当k<0时，α＝pi＋arctank.
                double angle = 0;
                if ((Math.abs(ptE.getX() - ptS.getX()) == 0)) {
                    angle = Math.PI / 2;
                } else {
                    double k = (ptE.getY() - ptS.getY()) / (ptE.getX() - ptS.getX());   //斜率
                    if (k >= 0) {
                        angle = Math.atan(k);
                    } else {
                        angle = 2 * Math.PI + Math.atan(k);
                    }
                }
                String strZJNR = "("+_qddh+")-"+"("+_zddh+")";
//                if (TableName.contains("DL")) {
//                    //电力标注电压值
//                    strZJNR = TYPE + " " + GJ.getText().toString().trim() + " " + DYZ.getSelectedItem().toString().trim() + " " + CZ.getSelectedItem().toString().trim();
//                } else {
//                    strZJNR = TYPE + " " + GJ.getText().toString().trim() + " " + CZ.getSelectedItem().toString().trim();
//                }

                String[] rgb = ColorRGB.split(",");
                TextSymbol tsT = new TextSymbol(16, strZJNR, Color.argb(255, Integer.valueOf(rgb[0]), Integer.valueOf(rgb[1]), Integer.valueOf(rgb[2])));
                tsT.setFontFamily("DroidSansFallback.ttf");
                tsT.setOffsetX(5);
                tsT.setOffsetY(5);
                tsT.setAngle((float) (360 - Math.toDegrees(angle)));
                Graphic graphicT = new Graphic(pt, tsT);
                GXAnnogralayer.addGraphic(graphicT);

                Toast toast = Toast.makeText(this, "提交成功！", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                String whereClause = "QDDH=? and ZDDH=? and GXDM=?";
                String[] whereArgs = {String.valueOf(_qddh), String.valueOf(_zddh),TableName};
                db.update("LINE", values, whereClause, whereArgs);
                db.close();

                MainActivity.UpdateGXGraphic((int) _graphicID, attributes);
                //更新标注
                //注记位置为线中心点计算 注记沿线注记，角度取线的弧度大于90度小于等于180
                Point ptS = MainActivity.polyline.getPoint(0);
                Point ptE = MainActivity.polyline.getPoint(1);
                double d_CenterX = (ptE.getX() + ptS.getX()) / 2;
                double d_CenterY = (ptE.getY() + ptS.getY()) / 2;
                Point pt = new Point();
                pt.setXY(d_CenterX, d_CenterY);
                Point pt1 = MainActivity.mapView.toScreenPoint(pt);
                int[] selids = GXAnnogralayer.getGraphicIDs((float) pt1.getX(), (float) pt1.getY(), 20);
                // 如果直线的斜率为k，倾斜角为α，则①当k不存在时，α＝pi/2 ；②当k≥0时，α＝arctank；当k<0时，α＝pi＋arctank.
                double angle = 0;
                if ((Math.abs(ptE.getX() - ptS.getX()) == 0)) {
                    angle = Math.PI / 2;
                } else {
                    double k = (ptE.getY() - ptS.getY()) / (ptE.getX() - ptS.getX());   //斜率
                    if (k >= 0) {
                        angle = Math.atan(k);
                    } else {
                        angle = 2 * Math.PI + Math.atan(k);
                    }
                }
                String strZJNR = "("+_qddh+")-"+"("+_zddh+")";
//                if (TableName.contains("DL")) {
//                    //电力标注电压值
//                    strZJNR = TYPE + " " + GJ.getText().toString().trim() + " " + DYZ.getSelectedItem().toString().trim() + " " + CZ.getSelectedItem().toString().trim();
//                } else {
//                    strZJNR = TYPE + " " + GJ.getText().toString().trim() + " " + CZ.getSelectedItem().toString().trim();
//                }

                String[] rgb = ColorRGB.split(",");
                TextSymbol tsT = new TextSymbol(16, strZJNR, Color.argb(255, Integer.valueOf(rgb[0]), Integer.valueOf(rgb[1]), Integer.valueOf(rgb[2])));
                tsT.setFontFamily("DroidSansFallback.ttf");
                tsT.setOffsetX(5);
                tsT.setOffsetY(5);
                tsT.setAngle((float) (360 - Math.toDegrees(angle)));
                for (int i = 0; i < selids.length; i++) {
                    Graphic gra = GXAnnogralayer.getGraphic(selids[i]);
                    Symbol symbol = gra.getSymbol();
                    GXAnnogralayer.updateGraphic(selids[i], tsT);
                    break;
                }

                Toast toast = Toast.makeText(this, "提交成功！", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                toast.show();
            }

            _gj = GJ.getText().toString().trim();
            _cz = CZ.getSelectedItem().toString().trim();
            if (TableName.startsWith("DL")) {
                _dyz = DYZ.getSelectedItem().toString().trim();
            }
            if (TableName.startsWith("GY_") || TableName.startsWith("RQ_") || TableName.startsWith("PS_")) {
                _yl = YL.getSelectedItem().toString().trim();
            }

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


    //判断是否为数字
    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}