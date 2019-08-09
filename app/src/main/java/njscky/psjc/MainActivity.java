package njscky.psjc;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnLongPressListener;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.map.event.OnZoomListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Line;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.Symbol;
import com.esri.core.symbol.TextSymbol;
import njscky.psjc.util.LocationUtil;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import njscky.psjc.util.ProgressDialogUtils;
import njscky.psjc.util.WebServiceUtils;

import org.ksoap2.serialization.SoapObject;

public class MainActivity extends AppCompatActivity implements OnClickListener, DrawEventListener {

    public static MapView mapView;
    public static GraphicsLayer tmpgralayer;
    public static String prjDBpath;
    public static String prjDBName;

    private LinearLayout Editlyout;
    private LinearLayout Insepectyout;
    private Spinner spinner;
    private ArrayAdapter<String> adapter;
    ArrayList<GraphicClass> GraphicClasslst;
    public static GraphicClass CurGraphicClass;
    public static GraphicClass _GraphicClass;

    static PictureMarkerSymbol sms;
    static SimpleLineSymbol sls;

    public static GraphicsLayer tmp;
    public static GraphicsLayer tmpline;
    public static GraphicsLayer tmpAnno;
    public static GraphicsLayer tmplineAnno;

    static GraphicsLayer questionLayer;

    ArrayList<Graphic> selpointslt;
    ArrayList<Graphic> sellineslt;
    int[] selids;

    SQLiteDatabase db = null;
    DBManager dbManager = null;

    public static Polyline polyline;
    public static String strWorkMode;

    ImageButton BtnAddpoint;
    ImageButton BtnAddline;
    ImageButton BtnEditAttribute;
    ImageButton BtnQustion;
    TextView TextScale;
    Button btnSJBJ;

    Button btnDownloadTask;
    Button btnEventReport;
    Button btnSpeedRemind;
    String strCurFunction;

    public static boolean blndraw = false;
    public static DrawTool drawTool;

    public static ArrayList<GraphicClass> graphicClassList;
    public static ArrayList<String> ImgTypeList;
    public static ArrayList<String> ImgNameList;

    public static String strUserCode="BH01"; //当前登录用户编号
    public static String strPlanCode="2015122113001234"; //当前登录用户巡查编号
    public static String strPolygonText="(128256.719|150751.537,129253.065|150832.872,129259.843|150158.475,128449.888|150077.140,128256.719|150751.537)"; //事件范围坐标串

    private LocationManager locationManager;
    private Location location=null;
    private final  Criteria criteria = new Criteria();
    private SharedPreferences sp;

    private String strLastX;
    private String strLastY;

    private boolean blnRefresh = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
        setContentView(R.layout.activity_main);
        iniView();
        initialize();

        getOverflowMenu();
        String serviceName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) this.getSystemService(serviceName);

        //判断GPS是否正常启动
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)&&locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) {
            Toast.makeText(this, "请开启GPS导航...", Toast.LENGTH_SHORT).show();
            //返回开启GPS导航设置界面
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0);
            return;
        }

        //精度
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//ACCURACY_COARSE 模糊；ACCURACY_FINE 高精度
        criteria.setAltitudeRequired(false);//不需要海拔
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);//不需要费用
        criteria.setSpeedRequired(true); //需要速度
        criteria.setPowerRequirement(Criteria.POWER_LOW); //电量消耗低
    }
//    @Override
//    public void onPause() {
//        unregisterListener();
//        super.onPause();
//    }
    //force to show overflow menu in actionbar for android 4.4 below
    private void getOverflowMenu() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                Bundle b = data.getExtras(); //data为B中回传的Intent
                String str = b.getString("Title");//str即为回传的值
                this.setTitle("当前打开工程：" + str);
                if (strWorkMode == "编辑") {
                   // btnSJBJ.setText("数据编辑");

                } else if (strWorkMode == "检查") {
                    if (Editlyout.getVisibility() == View.VISIBLE)
                        Editlyout.setVisibility(View.INVISIBLE);
                    //btnSJBJ.setText("数据检查");
                } else if (strWorkMode == "浏览") {
                    if (Editlyout.getVisibility() == View.VISIBLE)
                        Editlyout.setVisibility(View.INVISIBLE);
                   // btnSJBJ.setText("数据浏览");
                }
                break;
            default:
                break;
        }
    }
    //使用数组形式操作
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            CurGraphicClass = GraphicClasslst.get(arg2);
            tmp = GetGraphicLayerbyName(CurGraphicClass.getGDAliases());
            tmpAnno = GetGraphicLayerbyName(CurGraphicClass.getGDAliases() + "注记");
            tmpline = GetGraphicLayerbyName(CurGraphicClass.getGXAliases());
            tmplineAnno = GetGraphicLayerbyName(CurGraphicClass.getGXAliases() + "注记");
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    class SpinnerSelectedListener1 implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {

        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }
      @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_about) {
            new AlertDialog.Builder(this)
                    .setTitle("关于")
                    .setMessage("管道清淤检测外业调查系统 v1.0")
                    .setPositiveButton("确定", null)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static String getSDCardPath() {
        String cmd = "cat /proc/mounts";
        Runtime run = Runtime.getRuntime();// 返回与当前 Java 应用程序相关的运行时对象
        BufferedInputStream in=null;
        BufferedReader inBr=null;
        try {
            Process p = run.exec(cmd);// 启动另一个进程来执行命令
            in = new BufferedInputStream(p.getInputStream());
            inBr = new BufferedReader(new InputStreamReader(in));


            String lineStr;
            while ((lineStr = inBr.readLine()) != null) {
                // 获得命令执行后在控制台的输出信息
               // Log.i("CommonUtil:getSDCardPath", lineStr);
                if (lineStr.contains("sdcard")
                        && lineStr.contains(".android_secure")) {
                    String[] strArray = lineStr.split(" ");
                    if (strArray != null && strArray.length >= 5) {
                        String result = strArray[1].replace("/.android_secure",
                                "");
                        return result;
                    }
                }
                // 检查命令是否执行失败。
                if (p.waitFor() != 0 && p.exitValue() == 1) {
                    // p.exitValue()==0表示正常结束，1：非正常结束
                   // Log.e("CommonUtil:getSDCardPath", "命令执行失败!");
                }
            }
        } catch (Exception e) {
            //Log.e("CommonUtil:getSDCardPath", e.toString());
            //return Environment.getExternalStorageDirectory().getPath();
        }finally{
            try {
                if(in!=null){
                    in.close();
                }
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                if(inBr!=null){
                    inBr.close();
                }
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**

     * 遍历 "system/etc/vold.fstab” 文件，获取全部的Android的挂载点信息

     *

     * @return

     */

    private static ArrayList<String> getDevMountList() {

        String fileContent = "";
        File file = new File("/etc/vold.fstab");
        if (file == null || !file.isFile()) {
            return null;
        }
        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file));
            reader = new BufferedReader(is);
            String line = null;
            int i = 0;
            while ((line = reader.readLine()) != null) {
                fileContent += line + " ";
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String[] toSearch=fileContent.split(" ");
        ArrayList<String> out = new ArrayList<String>();

        for (int i = 0; i < toSearch.length; i++) {

            if (toSearch[i].contains("dev_mount")) {

                if (new File(toSearch[i + 2]).exists()) {

                    out.add(toSearch[i + 2]);

                }

            }

        }

        return out;

    }
    /**

     * 获取扩展SD卡存储目录

     *

     * 如果有外接的SD卡，并且已挂载，则返回这个外置SD卡目录

     * 否则：返回内置SD卡目录

     *

     * @return

     */

    public static String getExternalSdCardPath() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            File sdCardFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            return sdCardFile.getAbsolutePath();
        }



        String path = null;



        File sdCardFile = null;



        ArrayList<String> devMountList = getDevMountList();



        for (String devMount : devMountList) {

            File file = new File(devMount);



            if (file.isDirectory() && file.canWrite()) {

                path = file.getAbsolutePath();



                String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());

                File testWritable = new File(path, "test_" + timeStamp);



                if (testWritable.mkdirs()) {

                    testWritable.delete();

                } else {

                    path = null;

                }

            }

        }



        if (path != null) {

            sdCardFile = new File(path);

            return sdCardFile.getAbsolutePath();

        }



        return null;

    }

    //初始化地图
    private void initialize() {
        mapView = (MapView) this.findViewById(R.id.map);

        String strPath ="";
        if(getExternalSdCardPath() != null){
            strPath = getExternalSdCardPath();
        }else{
            strPath = getFilesDir().getAbsolutePath();
        }
        ArcGISRuntime.setClientId("yhMRjFXTz6F38XD9");
        String strMapPath =strPath + "/offlinemap/江东北路.tpk";
        File dir = new File(strMapPath);
        if (dir.exists()) {
            //使用离线地图
            ArcGISLocalTiledLayer locallyr = new ArcGISLocalTiledLayer(strMapPath);
            locallyr.setName("基础底图");
            mapView.addLayer(locallyr);
            Envelope env = new Envelope();
            env.setCoords(119642.008560884, 139699.14891334, 130225.363060926, 150519.536158281);
            mapView.setExtent(env);
        } else {
                    //使用在线地图
                    //网络检查
                    ConnectivityManager con = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
                    boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
                    boolean internet = con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
                    if (wifi | internet) {
                        // ArcGISTiledMapServiceLayer TitleLayerbase = new ArcGISTiledMapServiceLayer("http://10.10.31.6/arcgis/rest/services/%E5%8D%97%E4%BA%AC%E5%9C%B0%E5%BD%A2/MapServer");
                        ArcGISTiledMapServiceLayer TitleLayerbase = new ArcGISTiledMapServiceLayer("http://58.213.48.108/arcgis/rest/services/%E5%8D%97%E4%BA%AC%E5%9F%BA%E7%A1%80%E5%BA%95%E5%9B%BE2016/MapServer");
//              //ArcGISDynamicMapServiceLayer TitleLayerbase = new ArcGISDynamicMapServiceLayer("http://58.213.48.107/arcgis/rest/services/南京市普查进度/MapServer");
                        TitleLayerbase.setName("南京基础底图");
                        mapView.addLayer(TitleLayerbase);
                        Envelope env = new Envelope();
                        env.setCoords(119642.008560884, 139699.14891334, 130225.363060926, 144519.536158281);
                        mapView.setExtent(env);

                    } else {
                        Toast.makeText(this, "没有连接网络，无法加载地图", Toast.LENGTH_LONG).show();
                    }

        }

        tmpgralayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
        tmpgralayer.setName("tmpgralyr");
        mapView.addLayer(tmpgralayer);

        this.drawTool = new DrawTool(this.mapView);
        this.drawTool.addEventListener(this);

//        mapView.setMaxResolution(500);
//        mapView.setMinResolution(0.0000001);
        mapView.setMinScale(2000000);
        mapView.setMaxScale(1);
        TextScale = (TextView) findViewById(R.id.textscale);

            Intent intent1 = new Intent();
            intent1.setClass(MainActivity.this, Login.class);
            startActivity(intent1);

        mapView.setOnZoomListener(new OnZoomListener() {
            //缩放之前自动调用的方法
            public void preAction(float pivotX, float pivotY, double factor) {
            }

            //缩放之后自动调用的方法
            public void postAction(float pivotX, float pivotY, double factor) {
                double dblScale = mapView.getScale();
                DecimalFormat df = new DecimalFormat("#.00");
                TextScale.setText("当前比例尺1：" + df.format(dblScale));
            }
        });

    }

    public void handleDrawEvent(DrawEvent event) {
        Graphic DrawGraphic = event.getDrawGraphic();
        this.tmpgralayer.addGraphic(DrawGraphic);
        Polygon polygon = (Polygon) DrawGraphic.getGeometry();
        if (polygon.getPointCount() >= 3) {
            //判断当前功能是问题标记，还是事件上报
            if(strCurFunction.equals("问题标记")) {
                Question.gra = DrawGraphic;
                Intent tcintent = new Intent();
                tcintent.setClass(MainActivity.this, Question.class);
                startActivity(tcintent);
            }
            else {
                String strpolygon="(";
                for(int i=0;i<polygon.getPointCount();i++) {
                    Point pt=polygon.getPoint(i);
                    strpolygon=strpolygon+pt.getX()+"|"+pt.getY()+",";
                }
                strpolygon=strpolygon+polygon.getPoint(0).getX()+"|"+polygon.getPoint(0).getY()+")";
                MainActivity.strPolygonText=strpolygon;

                Intent intentxj = new Intent();
                intentxj.setClass(MainActivity.this, Inspection.class);
//                startActivityForResult(intentxj, 0);
                startActivity(intentxj);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog isExit = new AlertDialog.Builder(this).create();
            isExit.setTitle("系统提示");
            isExit.setMessage("确定要退出系统");
            isExit.setButton("确定", listener);
            isExit.setButton2("取消", listener);
            isExit.show();

            try {
                HashMap<String, String> properties = new HashMap<String, String>();
                properties.put("UserCode", MainActivity.strUserCode);
                WebServiceUtils.callWebService(WebServiceUtils.WEB_SERVER_URL, "UserLogout", properties, new WebServiceUtils.WebServiceCallBack() {

                    @Override
                    public void callBack(SoapObject result) {
                        ProgressDialogUtils.dismissProgressDialog();
                        if (result.toString().contains("失败")) {
                            Toast.makeText(MainActivity.this, result.toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            //MainActivity.strUserCode = result.toString().substring(result.toString().indexOf("=") + 1, result.toString().indexOf(";"));
                        }
                    }
                });
            }
            catch(Exception ex){}
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
                    System.exit(0);
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        setContentView(R.layout.view_null);
        super.onDestroy();
    }

    private void iniView() {
        Button btnGCGL = (Button) findViewById(R.id.btnGCGL);
        Button btnTCKZ = (Button) findViewById(R.id.btnTCKZ);
        btnSJBJ = (Button) findViewById(R.id.btnSJBJ);
        Button btnGDTJ = (Button) findViewById(R.id.btnGDTJ);
        Button btnXJ = (Button) findViewById(R.id.btnXJ);
        ImageButton btnFull = (ImageButton) findViewById(R.id.fullbtn);
        ImageButton btnGPS = (ImageButton) findViewById(R.id.gpsbtn);
        ImageButton btnPan = (ImageButton) findViewById(R.id.panbtn);
        btnDownloadTask=(Button) findViewById(R.id.btnDownloadTask);
        btnEventReport=(Button) findViewById(R.id.btnEventReport);
        btnSpeedRemind=(Button) findViewById(R.id.btnSpeedRemind);

        btnGCGL.setOnClickListener(this);
        btnTCKZ.setOnClickListener(this);
        btnSJBJ.setOnClickListener(this);
        btnGDTJ.setOnClickListener(this);
        btnFull.setOnClickListener(this);
        btnGPS.setOnClickListener(this);
        btnPan.setOnClickListener(this);
        btnXJ.setOnClickListener(this);
        btnDownloadTask.setOnClickListener(this);
        btnEventReport.setOnClickListener(this);
        btnSpeedRemind.setOnClickListener(this);

        Editlyout = (LinearLayout) findViewById(R.id.EditlinearLayout);
        Insepectyout = (LinearLayout) findViewById(R.id.InsepectlinearLayout);

        BtnAddpoint = (ImageButton) findViewById(R.id.btnaddpoint);
        BtnAddpoint.setOnClickListener(this);
        BtnAddline = (ImageButton) findViewById(R.id.btnaddline);
        BtnAddline.setOnClickListener(this);

        BtnEditAttribute = (ImageButton) findViewById(R.id.btneditattribute);
        BtnEditAttribute.setOnClickListener(this);

        BtnQustion = (ImageButton) findViewById(R.id.btnyw);
        BtnQustion.setOnClickListener(this);


    }

    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.btnGCGL:
                    drawTool.deactivate();
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, prj_class.class);
                    startActivityForResult(intent, 0);
                    break;
                case R.id.btnTCKZ:
                    drawTool.deactivate();
                    Intent tcintent = new Intent();
                    tcintent.setClass(MainActivity.this, LayerManager.class);
                    startActivity(tcintent);
                    break;
                case R.id.btnSJBJ:
                    drawTool.deactivate();
                    if (MainActivity.mapView.getLayers().length<= 2) {
                        new AlertDialog.Builder(this)
                                .setTitle("提示")
                                .setMessage("当前没有可编辑的对象，请先打开一个工程")
                                .setPositiveButton("确定", null)
                                .show();
                    } else {
                            BtnAddpoint.setSelected(false);
                            BtnAddline.setSelected(false);
                            BtnEditAttribute.setSelected(false);
                            EditPanelCreat();
                    }

                    break;
                case R.id.btnGDTJ:
                    drawTool.deactivate();
                    if (MainActivity.graphicClassList == null || MainActivity.graphicClassList.size() == 0) {
                        new AlertDialog.Builder(this)
                                .setTitle("提示")
                                .setMessage("请先打开一个工程!")
                                .setPositiveButton("确定", null)
                                .show();
                    } else {
                        Intent tjintent = new Intent();
                        tjintent.setClass(MainActivity.this, Count.class);
                        startActivity(tjintent);
                    }
                    break;
                case R.id.fullbtn:
                    drawTool.deactivate();
                    Envelope env = new Envelope();
                    env.setCoords(119642.008560884, 139699.14891334, 130225.363060926, 144519.536158281);
                    mapView.setExtent(env);
                    break;
                case R.id.gpsbtn:
                    drawTool.deactivate();
                    if (tmpgralayer.getNumberOfGraphics() > 0) {
                        tmpgralayer.removeAll();
//                        unregisterListener();
                        blnRefresh = false;
                    } else {
                        blnRefresh = true;
                        StartGPS();
                    }
                    break;
                case R.id.panbtn:
                    drawTool.deactivate();
                    mapView.setOnLongPressListener(null);
                    if (tmpgralayer.getNumberOfGraphics() > 0) {
                        tmpgralayer.removeAll();
                    }
                    break;
                case R.id.btnaddpoint:
                    BtnAddpoint.setSelected(true);
                    BtnAddline.setSelected(false);
                    BtnEditAttribute.setSelected(false);
                    BtnQustion.setSelected(false);
                    Toast.makeText(this, "添加管点", Toast.LENGTH_SHORT).show();
                    drawTool.deactivate();
                    AddPoint();
                    break;
                case R.id.btnaddline:
                    BtnAddpoint.setSelected(false);
                    BtnAddline.setSelected(true);
                    BtnEditAttribute.setSelected(false);
                    BtnQustion.setSelected(false);
                    Toast.makeText(this, "添加管线", Toast.LENGTH_SHORT).show();
                    drawTool.deactivate();
                    AddLine();
                    break;
                case R.id.btneditattribute:
                    BtnAddpoint.setSelected(false);
                    BtnAddline.setSelected(false);
                    BtnEditAttribute.setSelected(true);
                    BtnQustion.setSelected(false);
                    Toast.makeText(this, "属性编辑", Toast.LENGTH_SHORT).show();
                    drawTool.deactivate();
                    ModifyAtt();
                    break;
                case R.id.btnyw:
                    BtnAddpoint.setSelected(false);
                    BtnAddline.setSelected(false);
                    BtnEditAttribute.setSelected(false);
                    BtnQustion.setSelected(true);
                    strCurFunction="问题标记";

                    drawTool.activate(DrawTool.POLYGON);
                    break;
                case R.id.btnXJ:
                    try {
                        mapView.setOnSingleTapListener(null);
                        mapView.setOnLongPressListener(null);
                        mapView.setOnSingleTapListener(new OnSingleTapListener() {
                            public void onSingleTap(float x, float y) {
                                tmp = GetGraphicLayerbyName("雨水管点");
                                //获取点属性
                                selids = tmp.getGraphicIDs(x, y, 20);
                                if (selids.length != 0) {
                                    for (int i = 0; i < selids.length; i++) {
                                        Graphic gra = tmp.getGraphic(selids[i]);
                                        Point pt = (Point) gra.getGeometry();
                                        if (gra.getAttributeNames().length > 0) {
                                            String strJCJBH = gra.getAttributeValue("JCJBH").toString().trim();
                                            Intent intentxj = new Intent();
                                            intentxj.setClass(MainActivity.this, Inspection.class);
                                            Inspection._gddh=strJCJBH;
                                            Inspection.pp=pt;
                                            Inspection.GDAnnogralayer=tmpAnno;
                                            Inspection.GDgralayer=tmp;
                                            Inspection.gra=gra;
                                            startActivity(intentxj);
                                            break;
                                        }
                                    }
                                }
                            }

                        });

                    } catch (
                            Exception e
                            )

                    {
                        new AlertDialog.Builder(this)
                                .setTitle("提示")
                                .setMessage(e.getMessage())
                                .setPositiveButton("确定", null)
                                .show();
                    }
                    break;
                case R.id.btnDownloadTask:
//                    drawTool.deactivate();
//                    Intent taskintent = new Intent();
//                    taskintent.setClass(MainActivity.this, Task.class);
//                    startActivity(taskintent);
                    drawTool.deactivate();
                    if (tmpgralayer.getNumberOfGraphics() > 0) {
                        tmpgralayer.removeAll();
//                        unregisterListener();
                    } else {
                        blnRefresh=false;
                        StartGPS();
                    }
                    break;
                case R.id.btnEventReport:
                    try {
                        mapView.setOnSingleTapListener(null);
                        mapView.setOnLongPressListener(null);
                        mapView.setOnSingleTapListener(new OnSingleTapListener() {
                            public void onSingleTap(float x, float y) {
                                    tmp = GetGraphicLayerbyName("雨水管点");
                                    //获取点属性
                                    selids = tmp.getGraphicIDs(x, y, 20);
                                    if (selids.length != 0) {
                                        for (int i = 0; i < selids.length; i++) {
                                            Graphic gra = tmp.getGraphic(selids[i]);
                                            Point pt = (Point) gra.getGeometry();
                                            if (gra.getAttributeNames().length > 0) {
                                                String strJCJBH = gra.getAttributeValue("JCJBH").toString().trim();
                                                Intent intentxj = new Intent();
                                                intentxj.setClass(MainActivity.this, Inspection.class);
                                                Inspection._gddh=strJCJBH;
                                                Inspection.pp = pt;
                                                Inspection.GDAnnogralayer=tmpAnno;
                                                Inspection.GDgralayer=tmp;
                                                Inspection.gra=gra;
                                                startActivity(intentxj);
                                                break;
                                            }
                                        }
                                    }
                                }

                        });

                    } catch (
                            Exception e
                            )

                    {
                        new AlertDialog.Builder(this)
                                .setTitle("提示")
                                .setMessage(e.getMessage())
                                .setPositiveButton("确定", null)
                                .show();
                    }
                    break;
                case R.id.btnSpeedRemind:
                    drawTool.deactivate();
                    AlertDialog isExit = new AlertDialog.Builder(this).create();
                    isExit.setTitle("系统提示");
                    isExit.setMessage("确定要退出系统");
                    isExit.setButton("确定", listener);
                    isExit.setButton2("取消", listener);
                    isExit.show();

                    try {
                        HashMap<String, String> properties = new HashMap<String, String>();
                        properties.put("UserCode", MainActivity.strUserCode);
                        WebServiceUtils.callWebService(WebServiceUtils.WEB_SERVER_URL, "UserLogout", properties, new WebServiceUtils.WebServiceCallBack() {

                            @Override
                            public void callBack(SoapObject result) {
                                ProgressDialogUtils.dismissProgressDialog();
                                if (result.toString().contains("失败")) {
                                    Toast.makeText(MainActivity.this, result.toString(), Toast.LENGTH_SHORT).show();
                                } else {
                                    //MainActivity.strUserCode = result.toString().substring(result.toString().indexOf("=") + 1, result.toString().indexOf(";"));
                                }
                            }
                        });
                    }
                    catch(Exception ex){}
                    break;
            }
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage(e.getMessage())
                    .setPositiveButton("确定", null)
                    .show();
        }
    }

    void StartGPS() {

        String provider = locationManager.getBestProvider(criteria, true);

        location = locationManager.getLastKnownLocation(provider);
        updateWithNewLocation(location);
        // 第一个参数，定义当前所使用的Location Provider
        // 第二个参数，指示更新的最小时间，但并不是确定的，可能更多或更小
        // 第三个参数，两次定位之间的最小距离，单位米。
        // 第四个参数，监听器
        locationManager.requestLocationUpdates(provider, 5000,1, locationListener);

    }
    private void unregisterListener() {
        locationManager.removeUpdates(locationListener);
    }
    //监听事件
    private final LocationListener locationListener = new LocationListener() {
        //位置发生改变
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }

        //provider被用户关闭
        public void onProviderDisabled(String provider) {
            updateWithNewLocation(null);
        }

        //provider被用户开启
        public void onProviderEnabled(String provider) {
        }

        //Provider的状态在可用、暂时不可用和无服务三个状态直接切换时
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    private void updateWithNewLocation(Location location) {
        String latLongString;
        double lng = -1;
        double lat = -1;
        float speed=0;
        if (location != null) {
            lng = location.getLongitude();
            lat = location.getLatitude();
            speed=location.getSpeed();
            if( blnRefresh) {
                ShowTmpGraphic(lng, lat);
            }
            blnRefresh=false;
            String tmp = CoordinateConversion.ConvertXY(lng, lat);
            String strValues = MainActivity.strUserCode + "," + MainActivity.strPlanCode + "," + speed + "," + "2" + "," + tmp.split(";")[0] + "," + tmp.split(";")[1] + ",true";
            HashMap<String, String> properties = new HashMap<String, String>();
            properties.put("strValues", strValues);

            WebServiceUtils.callWebService(WebServiceUtils.WEB_SERVER_URL, "InsertTrackpoint", properties, new WebServiceUtils.WebServiceCallBack() {

                @Override
                public void callBack(SoapObject result) {
                    ProgressDialogUtils.dismissProgressDialog();
                    if (result.toString().contains("轨迹更新失败") || result.toString().contains("字符串格式不正确")) {
                        Toast.makeText(MainActivity.this, result.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            latLongString = "经度:" + lng + " 纬度:" + lat;
            Toast.makeText(this, latLongString, Toast.LENGTH_LONG).show();
        } else {
            latLongString = "无法获取坐标信息";
            Toast.makeText(this, latLongString, Toast.LENGTH_LONG).show();
        }
    }
    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());//获得当前时间
            sb.append("\nerror code : ");
            sb.append(location.getLocType());//获得erro code得知定位现状
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());//获得纬度
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());//获得经度
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation){//通过GPS定位
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());//获得速度
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\ndirection : ");
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());//获得当前地址
                sb.append(location.getDirection());//获得方位
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){//通过网络连接定位
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());//获得当前地址
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());//获得经营商？
            }

            Toast.makeText(MainActivity.this, sb.toString(), Toast.LENGTH_LONG).show();
//            logMsg(sb.toString());
            Log.i("BaiduLocationApiDem", sb.toString());
        }
        public void onReceivePoi(BDLocation poiLocation) {
        }
    }


    void ShowTmpGraphic(double lng, double lat) {
        String tmp = CoordinateConversion.ConvertXY(lng, lat);
        tmpgralayer.removeAll();
        Drawable image = getResources().getDrawable(R.drawable.location);
        PictureMarkerSymbol PicMarkSyb = new PictureMarkerSymbol(image);
        Point pt = new Point(Double.valueOf(tmp.split(";")[0]), Double.valueOf(tmp.split(";")[1]));
        Graphic graphic = new Graphic(pt, PicMarkSyb);
        tmpgralayer.addGraphic(graphic);
        PicMarkSyb = null;
        if (image != null) {
            image.setCallback(null);
            PicMarkSyb = null;
            graphic = null;
        }
        mapView.zoomTo(pt, 1);
    }

    void EditPanelCreat() {
        try {

            if (Editlyout.getVisibility() == View.VISIBLE)
                Editlyout.setVisibility(View.INVISIBLE);
            else {
                Editlyout.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage(e.getMessage())
                    .setPositiveButton("确定", null)
                    .show();
        }
    }

    private GraphicsLayer GetGraphicLayerbyName(String lyrname) {
        try {
            GraphicsLayer Gralyr = null;
            Layer[] lyrlst = MainActivity.mapView.getLayers();
            for (int i = 0; i < lyrlst.length; i++) {
                Layer tmplyr = lyrlst[i];
                if (tmplyr.getName().equals(lyrname)) {
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

    void AddPoint() {
        try {
            mapView.setOnSingleTapListener(null);
            mapView.setOnLongPressListener(null);
//            tmp = GetGraphicLayerbyName(CurGraphicClass.getGDAliases());
//            tmpAnno = GetGraphicLayerbyName(CurGraphicClass.getGDAliases() + "注记");
            mapView.setOnLongPressListener(new OnLongPressListener() {
                public boolean onLongPress(float x, float y) {
                    Point pt = mapView.toMapPoint(x, y);
                    point_class.pp = pt;
                    point_class.TableName = CurGraphicClass.getGLDM();
                    point_class.TableNameCN = CurGraphicClass.getGDAliases();
                    point_class.ColorRGB = CurGraphicClass.getColorRGB();
                    point_class.GDgralayer = tmp;
                    point_class.GDAnnogralayer = tmpAnno;
                    point_class.GDIMG = CurGraphicClass.getGDIMG();
                    point_class.blnEdit = false;
                    Intent pointintent = new Intent();
                    pointintent.setClass(MainActivity.this, point_class.class);
                    startActivity(pointintent);
                    return true;
                }
            });
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage(e.getMessage())
                    .setPositiveButton("确定", null)
                    .show();
        }
    }

    void AddLine() {
        try {
            mapView.setOnSingleTapListener(null);
            mapView.setOnLongPressListener(null);
            blndraw = false;
            tmp = GetGraphicLayerbyName(CurGraphicClass.getGDAliases());
            tmpline = GetGraphicLayerbyName(CurGraphicClass.getGXAliases());
            tmplineAnno = GetGraphicLayerbyName(CurGraphicClass.getGXAliases() + "注记");
            selpointslt = new ArrayList<Graphic>();
            mapView.setOnSingleTapListener(new OnSingleTapListener() {
                public void onSingleTap(float x, float y) {
                    if (selpointslt.size() == 0) {
                           if (selpointslt.size() != 2) {
                               int[] selids = tmp.getGraphicIDs(x, y, 20);
                               if (selids.length != 0) {
                                   Graphic graPoint = tmp.getGraphic(selids[0]);
                                   selpointslt.add(graPoint);
                                   Toast toast = Toast.makeText(MainActivity.this, "已选择第" + String.valueOf(selpointslt.size()) + "个点", Toast.LENGTH_SHORT);
                                   toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, -100);
                                   toast.show();
                               }
                           }
                    } else if (selpointslt.size() == 1) {
                        int[] selids = tmp.getGraphicIDs(x, y, 20);
                        if (selids.length != 0) {
                            Graphic graPoint = tmp.getGraphic(selids[0]);
                            selpointslt.add(graPoint);
                            Toast toast = Toast.makeText(MainActivity.this, "已选择第" + String.valueOf(selpointslt.size()) + "个点", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, -100);
                            toast.show();

                            if (selpointslt.get(0).getAttributeValue("WTDH").toString().equals(selpointslt.get(1).getAttributeValue("WTDH").toString())) {
                                selpointslt = new ArrayList<Graphic>();
                                toast = Toast.makeText(MainActivity.this, "选择了同一点，请重新选择", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, -100);
                                toast.show();
                                //Toast.makeText(MainActivity.this, "选择了同一点，请重新选择", Toast.LENGTH_SHORT).show();
                            } else {
                                String[] rgb = CurGraphicClass.getColorRGB().split(",");
                                blndraw = false;
                                sls = new SimpleLineSymbol(Color.argb(255, Integer.valueOf(rgb[0]), Integer.valueOf(rgb[1]), Integer.valueOf(rgb[2])), 2, SimpleLineSymbol.STYLE.SOLID);

                                Line line = new Line();
                                line.setStart((Point) selpointslt.get(0).getGeometry());
                                line.setEnd((Point) selpointslt.get(1).getGeometry());
                                Polyline poly = new Polyline();
                                poly.addSegment(line, true);
                                Graphic graphic = new Graphic(poly, sls);
                                tmpgralayer.addGraphic(graphic);
                                polyline = poly;

                                line_class._qddh = selpointslt.get(0).getAttributeValue("WTDH").toString();
                                line_class._zddh = selpointslt.get(1).getAttributeValue("WTDH").toString();
                                line_class.TableName = CurGraphicClass.getGLDM();
                                line_class.TableNameCN = CurGraphicClass.getGXAliases();
                                line_class._graphicID = graphic.getId();
                                line_class.blnEdit = false;
                                line_class.GXgralayer = tmpline;
                                line_class.GXAnnogralayer = tmplineAnno;
                                line_class.ColorRGB = CurGraphicClass.getColorRGB();
                                line_class.TYPE = CurGraphicClass.getTYPE();

                                Intent lineintent = new Intent();
                                lineintent.setClass(MainActivity.this, line_class.class);
                                startActivityForResult(lineintent, 0);

                                selpointslt = new ArrayList<Graphic>();
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage(e.getMessage())
                    .setPositiveButton("确定", null)
                    .show();
        }

    }
    public static void CreatGDGraphic(Point pt, Map<String, Object> attributes) {
        Graphic graphic = new Graphic(pt, sms, attributes);
        tmp.addGraphic(graphic);
    }

    public static void CreatGXGraphic(Polyline line, Map<String, Object> attributes) {
        Graphic graphic = new Graphic(line, sls, attributes);
        tmpline.addGraphic(graphic);
        if (tmpgralayer.getNumberOfGraphics() > 0) {
            tmpgralayer.removeAll();
        }
    }

    public static void UpdateGXGraphic(int _graphicID, Map<String, Object> attributes) {
        tmpline.updateGraphic(_graphicID, attributes);
        if (tmpgralayer.getNumberOfGraphics() > 0) {
            tmpgralayer.removeAll();
        }
    }

    void DelFromDB(String sql) {
        try {
            DBManager.DB_PATH = MainActivity.prjDBpath;
            dbManager = new DBManager(this);
            db = dbManager.openDatabase();
            db.execSQL(sql);
            db.close();
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage(e.getMessage())
                    .setPositiveButton("确定", null)
                    .show();
        }
    }

    void ModifyAtt() {
        try {
            mapView.setOnSingleTapListener(null);
            mapView.setOnLongPressListener(null);
            mapView.setOnSingleTapListener(new OnSingleTapListener() {
                public void onSingleTap(float x, float y) {
//                    for (int m = 0; m < graphicClassList.size(); m++) {
//                        _GraphicClass =CurGraphicClass;
////                        tmp = GetGraphicLayerbyName(_GraphicClass.getGDAliases());
//                        tmpline = GetGraphicLayerbyName(_GraphicClass.getGXAliases());
//                        tmpAnno = GetGraphicLayerbyName(_GraphicClass.getGDAliases() + "注记");
//                        tmplineAnno = GetGraphicLayerbyName(_GraphicClass.getGXAliases() + "注记");

                        //判断线段长度是否超过8米，若小于8米弹出选择框
                        selids = tmpline.getGraphicIDs(x, y, 20);
                        if (selids.length != 0) {
                            for (int n = 0; n < selids.length; n++) {
                                Graphic gra1 = tmpline.getGraphic(selids[n]);
                                if (gra1.getAttributeNames().length > 0) {
                                    Polyline pl = (Polyline) gra1.getGeometry();
                                    double dbllen = pl.calculateLength2D();
                                        //获取点属性
                                        selids = tmp.getGraphicIDs(x, y, 20);
                                        if (selids.length != 0) {
                                            for (int i = 0; i < selids.length; i++) {
                                                Graphic gra = tmp.getGraphic(selids[i]);
                                                if (gra.getAttributeNames().length > 0) {
                                                    point_class.pp = (Point) gra.getGeometry();
                                                    point_class._x = x;
                                                    point_class._y = y;
//                                                    point_class.TableName = _GraphicClass.getGLDM();
//                                                    point_class.TableNameCN = _GraphicClass.getGDAliases();
//                                                    point_class.ColorRGB = _GraphicClass.getColorRGB();
//                                                    point_class.GDgralayer = tmp;
//                                                    point_class.GXgralayer = tmpline;
//                                                    point_class.GDAnnogralayer = tmpAnno;
//                                                    point_class._tcdh = gra.getAttributeValue("WTDH").toString();
//                                                    point_class._tzd = gra.getAttributeValue("TZD").toString();
//                                                    point_class._fsw = gra.getAttributeValue("FSW").toString();
//                                                    point_class._bz = gra.getAttributeValue("BZ").toString();
//                                                    point_class._gtlx=gra.getAttributeValue("GTLX").toString();
//                                                    point_class._xg= gra.getAttributeValue("DMGC").toString();
//
//                                                    point_class.GDIMG = _GraphicClass.getGDIMG();
//                                                    point_class._graphicID = gra.getId();
//                                                    point_class.blnEdit = true;

                                                    Intent Pointintent = new Intent();
                                                    Pointintent.setClass(MainActivity.this, point_class.class);
                                                    startActivity(Pointintent);
                                                    break;
                                                }
                                            }
                                            break;
                                        } else {
                                            selids = tmpline.getGraphicIDs(x, y, 20);
                                            if (selids.length != 0) {
                                                for (int t = 0; t < selids.length; t++) {
                                                    Graphic gra = tmpline.getGraphic(selids[t]);
                                                    if (gra.getAttributeNames().length > 0) {
                                                        polyline = (Polyline) gra.getGeometry();
                                                        line_class.TableName = _GraphicClass.getGLDM();
                                                        line_class.TableNameCN = _GraphicClass.getGXAliases();
                                                        line_class._x = x;
                                                        line_class._y = y;
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
                                                        line_class._gtxlmc = gra.getAttributeValue("XLMC").toString();
                                                        line_class._hls = gra.getAttributeValue("HLS").toString();
                                                        line_class._dlts = gra.getAttributeValue("DLTS").toString();
                                                        line_class._xg = gra.getAttributeValue("QDMS").toString();

                                                        line_class._graphicID = gra.getId();
                                                        line_class.blnEdit = true;
                                                        line_class._graphic = gra;
                                                        line_class.GXgralayer = tmpline;
                                                        line_class.GXAnnogralayer = tmplineAnno;
                                                        line_class.ColorRGB = _GraphicClass.getColorRGB();
                                                        line_class.TYPE = _GraphicClass.getTYPE();
                                                        String[] rgb = _GraphicClass.getColorRGB().split(",");
                                                        sls = new SimpleLineSymbol(Color.argb(255, Integer.valueOf(rgb[0]), Integer.valueOf(rgb[1]), Integer.valueOf(rgb[2])), 2, SimpleLineSymbol.STYLE.SOLID);

                                                        Intent lineintent = new Intent();
                                                        lineintent.setClass(MainActivity.this, line_class.class);
                                                        startActivity(lineintent);
                                                        break;
                                                    }
                                                }
                                                break;
                                            }
                                        }


                                    break;
                                }

                            }

//                            break;
                        } else {
                            //获取点属性
                            selids = tmp.getGraphicIDs(x, y, 20);
                            if (selids.length != 0) {
                                for (int i = 0; i < selids.length; i++) {
                                    Graphic gra = tmp.getGraphic(selids[i]);
                                    if (gra.getAttributeNames().length > 0) {
                                        point_class.pp = (Point) gra.getGeometry();
                                        point_class._x = x;
                                        point_class._y = y;
                                        point_class.TableName = _GraphicClass.getGLDM();
                                        point_class.TableNameCN = _GraphicClass.getGDAliases();
                                        point_class.ColorRGB = _GraphicClass.getColorRGB();
                                        point_class.GDgralayer = tmp;
                                        point_class.GXgralayer = tmpline;
                                        point_class.GDAnnogralayer = tmpAnno;
                                        point_class._tcdh = gra.getAttributeValue("WTDH").toString();
                                        point_class._tzd = gra.getAttributeValue("TZD").toString();
                                        point_class._fsw = gra.getAttributeValue("FSW").toString();
                                        point_class._bz = gra.getAttributeValue("BZ").toString();
                                        point_class._gtlx=gra.getAttributeValue("GTLX").toString();
                                        point_class._xg= gra.getAttributeValue("DMGC").toString();
                                        point_class.GDIMG = _GraphicClass.getGDIMG();

                                        point_class._graphicID = gra.getId();
                                        point_class.blnEdit = true;

                                        Intent Pointintent = new Intent();
                                        Pointintent.setClass(MainActivity.this, point_class.class);
                                        startActivity(Pointintent);
                                        break;
                                    }
                                }
//                                break;
                            }
                        }

//                    }
                }
            });

        } catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage(e.getMessage())
                    .setPositiveButton("确定", null)
                    .show();
        }
    }

    void CheckAtt() {
        try {
            mapView.setOnSingleTapListener(null);
            mapView.setOnLongPressListener(null);
            mapView.setOnSingleTapListener(new OnSingleTapListener() {
                public void onSingleTap(float x, float y) {
                    for (int m = 0; m < graphicClassList.size(); m++) {
                        _GraphicClass = graphicClassList.get(m);
                        tmp = GetGraphicLayerbyName(_GraphicClass.getGDAliases());
                        tmpline = GetGraphicLayerbyName(_GraphicClass.getGXAliases());

                        //获取点属性
                        selids = tmp.getGraphicIDs(x, y, 20);
                        if (selids.length != 0) {
                            for (int i = 0; i < selids.length; i++) {
                                Graphic gra = tmp.getGraphic(selids[i]);
                                Point pt = (Point) gra.getGeometry();
                                if (gra.getAttributeNames().length > 0) {
                                    String strTZD = gra.getAttributeValue("TZD").toString().trim();
                                    String strFSW = gra.getAttributeValue("FSW").toString().trim();
                                    if ((strFSW.equals("")) && (strTZD.equals("变材") || strTZD.equals("变径") || strTZD.equals("分支") || strTZD.equals("拐点") || strTZD.equals("三通") || strTZD.equals("四通") || strTZD.equals("弯头") || strTZD.equals("直线点") || strTZD.equals("非探测区") || strTZD.equals("预留口") || strTZD.equals("出入地点") || strTZD.equals("户出") || strTZD.equals("入户") || strTZD.equals("转折点"))) {
                                        Check.CHECKTYPE = "隐蔽点";
                                        Check.GXTYPE = _GraphicClass.getGXTYPE();
                                        Check.GLDM = _GraphicClass.getGLDM();
                                        String strTCDH = gra.getAttributeValue("WTDH").toString();
                                        String strGLDM = gra.getAttributeValue("GXDM").toString();
                                        Check._gxdh = strTCDH;

                                        //获取对应的管线
                                        DBManager.DB_PATH = MainActivity.prjDBpath;
                                        dbManager = new DBManager(MainActivity.this);
                                        db = dbManager.openDatabase();

                                        Cursor cur = db.rawQuery("SELECT * FROM LINE WHERE (QDDH ='" + strTCDH + "' or ZDDH = '" + strTCDH + "') and GLDM = '" + strGLDM + "'", null);
                                        if (cur != null) {
                                            if (cur.moveToFirst()) {
                                                do {
                                                    //读取管线信息
                                                    String strQDDH = cur.getString(cur.getColumnIndex("QDDH"));
                                                    String strZDDH = cur.getString(cur.getColumnIndex("ZDDH"));
                                                    if (!strTCDH.equals(strQDDH)) {
                                                        Check._ljdh = strQDDH;
                                                        Check._tcms = cur.getString(cur.getColumnIndex("QDMS"));
                                                    } else {
                                                        Check._ljdh = strZDDH;
                                                        Check._tcms = cur.getString(cur.getColumnIndex("ZDMS"));
                                                    }
                                                    Check._tccz = cur.getString(cur.getColumnIndex("CZ"));
                                                    Check._tcgj = cur.getString(cur.getColumnIndex("GJ"));

                                                    Check._tcmslx = cur.getString(cur.getColumnIndex("MSLX"));
                                                    Check._tcdyz = cur.getString(cur.getColumnIndex("DYZ"));
                                                    Check._tcyl = cur.getString(cur.getColumnIndex("YL"));
                                                    Check._tczks = cur.getString(cur.getColumnIndex("ZKS"));
                                                    Check._tczyks = cur.getString(cur.getColumnIndex("ZYKS"));
                                                    Check._tcdlts = cur.getString(cur.getColumnIndex("DLTS"));
                                                    Check._tclx = cur.getString(cur.getColumnIndex("LX"));
                                                    Check._tcbz = cur.getString(cur.getColumnIndex("BZ"));
                                                    break;
                                                }
                                                while (cur.moveToNext());
                                            }
                                        }
                                        cur.close();
                                        db.close();

                                        Intent Checkintent = new Intent();
                                        Checkintent.setClass(MainActivity.this, Check.class);
                                        startActivity(Checkintent);

                                    } else {
                                        String strGXTYPE = _GraphicClass.getGXTYPE();
                                        String strTCDH = gra.getAttributeValue("WTDH").toString();
                                        String strGLDM = gra.getAttributeValue("GXDM").toString();

                                        ArrayList<mxdcheckclass> checklist = new ArrayList<mxdcheckclass>();
                                        ArrayList<String> dhlist = new ArrayList<String>();
                                        ArrayList<Graphic> gralist = new ArrayList<Graphic>();
                                        ArrayList<GraphicsLayer> graphicLayerlist = new ArrayList<GraphicsLayer>();

                                        Point pt1 = MainActivity.mapView.toScreenPoint(pt);
                                        int[] selidsline = tmpline.getGraphicIDs((float) pt1.getX(), (float) pt1.getY(), 20);
                                        if (selidsline.length != 0) {
                                            for (int j = 0; j < selidsline.length; j++) {
                                                Graphic graline = tmpline.getGraphic(selidsline[j]);
                                                if (graline.getAttributeNames().length > 0) {
                                                    mxdcheckclass _mxdcheckclass = new mxdcheckclass();
                                                    _mxdcheckclass.setChecktype("明显点");
                                                    _mxdcheckclass.setGxtype(strGXTYPE);
                                                    _mxdcheckclass.setGldm(strGLDM);
                                                    _mxdcheckclass.setGxdh(strTCDH);
                                                    String strQDDH = graline.getAttributeValue("QDDH").toString();
                                                    String strZDDH = graline.getAttributeValue("ZDDH").toString();
                                                    if (!strTCDH.equals(strQDDH)) {
                                                        _mxdcheckclass.setLjdh(strQDDH);
                                                        _mxdcheckclass.setMs(graline.getAttributeValue("QDMS").toString());
                                                    } else {
                                                        _mxdcheckclass.setLjdh(strZDDH);
                                                        _mxdcheckclass.setMs(graline.getAttributeValue("ZDMS").toString());
                                                    }
                                                    _mxdcheckclass.setCz(graline.getAttributeValue("CZ").toString());
                                                    _mxdcheckclass.setGj(graline.getAttributeValue("GJ").toString());

                                                    _mxdcheckclass.setMslx(graline.getAttributeValue("MSLX").toString());
                                                    for (int k = 0; k < graline.getAttributeNames().length; k++) {
                                                        if (graline.getAttributeNames()[k].equals("DYZ")) {
                                                            _mxdcheckclass.setDyz(graline.getAttributeValue("DYZ").toString());
                                                            break;
                                                        }
                                                    }
                                                    for (int k = 0; k < graline.getAttributeNames().length; k++) {
                                                        if (graline.getAttributeNames()[k].equals("YL")) {
                                                            _mxdcheckclass.setYl(graline.getAttributeValue("YL").toString());
                                                            break;
                                                        }
                                                    }
                                                    for (int k = 0; k < graline.getAttributeNames().length; k++) {
                                                        if (graline.getAttributeNames()[k].equals("LX")) {
                                                            _mxdcheckclass.setLx(graline.getAttributeValue("LX").toString());
                                                            break;
                                                        }
                                                    }
                                                    for (int k = 0; k < graline.getAttributeNames().length; k++) {
                                                        if (graline.getAttributeNames()[k].equals("ZKS")) {
                                                            _mxdcheckclass.setZks(graline.getAttributeValue("ZKS").toString());
                                                            break;
                                                        }
                                                    }
                                                    for (int k = 0; k < graline.getAttributeNames().length; k++) {
                                                        if (graline.getAttributeNames()[k].equals("ZYKS")) {
                                                            _mxdcheckclass.setZyks(graline.getAttributeValue("ZYKS").toString());
                                                            break;
                                                        }
                                                    }
                                                    for (int k = 0; k < graline.getAttributeNames().length; k++) {
                                                        if (graline.getAttributeNames()[k].equals("DLTS")) {
                                                            _mxdcheckclass.setDlts(graline.getAttributeValue("DLTS").toString());
                                                            break;
                                                        }
                                                    }
                                                    _mxdcheckclass.setBz(graline.getAttributeValue("BZ").toString());
                                                    checklist.add(_mxdcheckclass);
                                                    String strDH = strQDDH + "-" + strZDDH;
                                                    dhlist.add(strDH);
                                                    gralist.add(graline);
                                                    graphicLayerlist.add(tmpline);
                                                }
                                            }
                                        }

                                        MxdCheck.Checklst = checklist;
                                        MxdCheck.DHlst = dhlist;
                                        MxdCheck.Graphiclst = gralist;
                                        MxdCheck.GXgralayerlst = graphicLayerlist;
                                        Intent Checkintent = new Intent();
                                        Checkintent.setClass(MainActivity.this, MxdCheck.class);
                                        startActivity(Checkintent);
                                    }
                                    break;
                                }
                            }

                            break;
                        }
                    }
                }
            });

        } catch (
                Exception e
                )

        {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage(e.getMessage())
                    .setPositiveButton("确定", null)
                    .show();
        }

    }


    void ModifyQuestion() {
        try {
            mapView.setOnSingleTapListener(null);
            mapView.setOnLongPressListener(null);
            mapView.setOnSingleTapListener(new OnSingleTapListener() {
                public void onSingleTap(float x, float y) {
                    questionLayer = GetGraphicLayerbyName("问题标记");
                    //获取点属性
                    selids = questionLayer.getGraphicIDs(x, y, 20);
                    if (selids.length != 0) {
                        for (int i = 0; i < selids.length; i++) {
                            Graphic gra = questionLayer.getGraphic(selids[i]);
                            if (gra.getAttributeNames().length > 0) {
                                point_class._x = x;
                                point_class._y = y;
                                point_class.TableName = _GraphicClass.getGLDM();
                                point_class.TableNameCN = _GraphicClass.getGDAliases();
                                point_class.ColorRGB = _GraphicClass.getColorRGB();
                                point_class.GDgralayer = tmp;
                                point_class.GXgralayer = tmpline;
                                point_class.GDAnnogralayer = tmpAnno;
                                point_class._tcdh = gra.getAttributeValue("WTDH").toString();
                                point_class._tzd = gra.getAttributeValue("TZD").toString();
                                point_class._fsw = gra.getAttributeValue("FSW").toString();
                                point_class._gtlx = gra.getAttributeValue("GTLX").toString();
                                point_class._xg= gra.getAttributeValue("DMGC").toString();
                                point_class._bz = gra.getAttributeValue("BZ").toString();

                                point_class.GDIMG = _GraphicClass.getGDIMG();
                                point_class._graphicID = gra.getId();
                                point_class.blnEdit = true;

                                Intent Pointintent = new Intent();
                                Pointintent.setClass(MainActivity.this, point_class.class);
                                startActivity(Pointintent);
                                break;
                            }
                        }

                    }

                }
            });

        } catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage(e.getMessage())
                    .setPositiveButton("确定", null)
                    .show();
        }
    }

}
