package njscky.psjc;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Line;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.LineSymbol;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.TextSymbol;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2015/7/6.
 */
public class prj_class extends AppCompatActivity implements OnClickListener {

    String prjDBpath = "";
    String prjName = "";
    private String DATABASE_PATH;
    private List<String> lstFile;
    private ListView mlistview;
    // 声明数组链表，其装载的类型是ListItem(封装了一个Drawable和一个String的类)
    private ArrayList<ListItem> mList;
    SQLiteDatabase db = null;
    DBManager dbManager = null;
    ArrayList<GraphicClass> graphicClassList;
    ArrayList<String> ImgTypeList;
    ArrayList<String> ImgNameList;

    private Handler handler = new Handler();
    private ProgressDialog progressDialog;
    int result = 0;
    MainListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prj_layout);

        DATABASE_PATH = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getPackageName();
        iniEvents();
        GetFiles(DATABASE_PATH, "db", true);
    }


    private void iniEvents() {
        mlistview = (ListView) findViewById(R.id.listView1);
        Button mbtnexit = (Button) findViewById(R.id.btnexit);
        mbtnexit.setOnClickListener(this);
        Button mbtnok2 = (Button) findViewById(R.id.btnok2);
        mbtnok2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnexit:
                this.finish();
                break;

            case R.id.btnok2:
                if (prjDBpath != "") {
//                    if(!prjDBpath.equals(MainActivity.prjDBpath)) {
                    progressDialog = ProgressDialog.show(prj_class.this, "请稍候...", "正在加载数据...", true);
                    try {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                // TODO Auto-generated method stub
                                try {
                                    MainActivity.prjDBpath = prjDBpath;
                                    MainActivity.prjDBName = prjName;
                                    CreatGraphiclyr();
                                } catch (Exception e) {
                                    result = 0;
                                }
                                //更新界面
                                handler.post(new Runnable() {
                                    public void run() {
                                        if (result == 1) {
                                            progressDialog.dismiss();
                                            Intent intent = new Intent();
                                            Bundle mBundle = new Bundle();
                                            mBundle.putString("Title", prjName.substring(0, prjName.lastIndexOf(".")));//压入数据
                                            intent.putExtras(mBundle);
                                            setResult(RESULT_OK, intent);
                                            prj_class.this.finish();
                                        } else {
                                            Toast toast = Toast.makeText(prj_class.this, "打开工程文件失败！", Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                                            toast.show();
                                        }
                                    }
                                });
                                progressDialog.dismiss();
                            }
                        }).start();
                    } catch (Exception ex) {
                        progressDialog.dismiss();
                    }
                } else {
                    new AlertDialog.Builder(prj_class.this)
                            .setTitle("提示")
                            .setMessage("请选择要打开的工程文件")
                            .setPositiveButton("确定", null)
                            .show();
                }
                break;
        }
    }

    public void GetFiles(String Path, String Extension, boolean IsIterative) //搜索目录，扩展名，是否进入子文件夹
    {
        try {
            // 获取Resources对象
            Resources res = this.getResources();
            mList = new ArrayList<ListItem>();

            lstFile = new ArrayList<String>();
            File[] files = new File(Path).listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                if (f.isFile()) {
                    if (f.getPath().substring(f.getPath().length() - Extension.length()).equals(Extension)) //判断扩展名
                    {
                        ListItem item = new ListItem();
                        item.setImage(res.getDrawable(R.drawable.db));
                        item.setTitle(f.getName());
                        item.setTag(Path + "/" + f.getName());
                        mList.add(item);
                        lstFile.add(f.getPath());
                    }
                    if (!IsIterative)
                        break;
                } else if (f.isDirectory() && f.getPath().indexOf("/.") == -1) //忽略点文件（隐藏文件/文件夹）
                    GetFiles(f.getPath(), Extension, IsIterative);
            }
            // 获取MainListAdapter对象
            adapter = new MainListViewAdapter();
            // 将MainListAdapter对象传递给ListView视图
            mlistview.setAdapter(adapter);

            mlistview.setOnItemClickListener(mLeftListOnItemClick);

        } catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage(e.getMessage())
                    .setPositiveButton("确定", null)
                    .show();
        }
    }

    AdapterView.OnItemClickListener mLeftListOnItemClick = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                adapter.setSelectItem(arg2);
                adapter.notifyDataSetInvalidated();
        }
    };

    void CreatGraphiclyr() {
        try {
            Layer[] lyrlst = MainActivity.mapView.getLayers();
            for (int i = 0; i < lyrlst.length; i++) {
                Layer tmplyr = lyrlst[i];
                if (tmplyr.getClass().getName().equals(GraphicsLayer.class.getName()) && tmplyr.getName() != "tmpgralyr")
                    MainActivity.mapView.removeLayer(tmplyr);
            }

            graphicClassList = new ArrayList<GraphicClass>();
            ImgTypeList = new ArrayList<String>();
            ImgNameList = new ArrayList<String>();

            DBManager.DB_PATH = prjDBpath;
            dbManager = new DBManager(this);
            db = dbManager.openDatabase();

            String ColorRGB = "76,0,0";
            GraphicsLayer JCJGDgralry = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
            JCJGDgralry.setName("雨水管点_检查井");
            GraphicsLayer JCJGDAnnogralry = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
            JCJGDAnnogralry.setName("雨水管点_检查井注记");
            CreatGDGraphic(JCJGDgralry, JCJGDAnnogralry, "YS_POINT_JCJ", ColorRGB);
            JCJGDgralry.setMinScale(5000);
            JCJGDAnnogralry.setMinScale(5000);
            JCJGDAnnogralry.setVisible(true);
            MainActivity.mapView.addLayer(JCJGDgralry);
            MainActivity.mapView.addLayer(JCJGDAnnogralry);

            GraphicsLayer TZDGDgralry = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
            TZDGDgralry.setName("雨水管点_特征点");
            GraphicsLayer TZDGDAnnogralry = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
            TZDGDAnnogralry.setName("雨水管点_特征点注记");
            CreatGDGraphic(TZDGDgralry, TZDGDAnnogralry, "YS_POINT_TZD",  ColorRGB);
            TZDGDgralry.setMinScale(1000);
            TZDGDAnnogralry.setMinScale(5000);
            TZDGDAnnogralry.setVisible(false);
            MainActivity.mapView.addLayer(TZDGDgralry);
            MainActivity.mapView.addLayer(TZDGDAnnogralry);


            GraphicsLayer JCJGXgralry = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
            JCJGXgralry.setName("雨水管线_检查井");
            GraphicsLayer JCJGXAnnogralry = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
            JCJGXAnnogralry.setName("雨水管线_检查井注记");
            CreatGXGraphic(JCJGXgralry, JCJGXAnnogralry, "YS_LINE_JCJ",  ColorRGB);
            JCJGXAnnogralry.setMinScale(1000);
            JCJGXAnnogralry.setVisible(false);
            MainActivity.mapView.addLayer(JCJGXgralry);
            MainActivity.mapView.addLayer(JCJGXAnnogralry);

            GraphicsLayer TZDGXgralry = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
            TZDGXgralry.setName("雨水管线_特征点");
            GraphicsLayer TZDGXAnnogralry = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
            TZDGXAnnogralry.setName("雨水管线_特征点注记");
            CreatGXGraphic(TZDGXgralry, TZDGXAnnogralry, "YS_LINE_TZD",  ColorRGB);
            TZDGXAnnogralry.setMinScale(1000);
            TZDGXAnnogralry.setVisible(false);
            MainActivity.mapView.addLayer(TZDGXgralry);
            MainActivity.mapView.addLayer(TZDGXAnnogralry);

            db.close();
            result = 1;

            MainActivity.graphicClassList = graphicClassList;
            MainActivity.ImgTypeList = ImgTypeList;
            MainActivity.ImgNameList = ImgNameList;

        } catch (Exception e) {
            result = 0;
//            new AlertDialog.Builder(this)
//                    .setTitle("提示")
//                    .setMessage(e.getMessage())
//                    .setPositiveButton("确定", null)
//                    .show();
        }
    }

    void CreatGDGraphic(GraphicsLayer GDgralry, GraphicsLayer GDAnnogralry, String TableName, String ColorRGB) {
        try {
            Cursor cur = db.rawQuery("SELECT * FROM " + TableName , null);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    do {

                        String JCJBH = cur.getString(cur.getColumnIndex("JCJBH"));
                        if (JCJBH == null) JCJBH = "";

                        double XZB = cur.getDouble(cur.getColumnIndex("XZB"));
                        double YZB = cur.getDouble(cur.getColumnIndex("YZB"));
                        Point pt = new Point();
                        pt.setX(XZB);
                        pt.setY(YZB);

                        Map<String, Object> attributes = new HashMap<String, Object>();
                        attributes.put("JCJBH", JCJBH);

                        if(TableName=="YS_POINT_JCJ") {
                            String[] rgb = ColorRGB.split(",");
//                            int imgId = getResources().getIdentifier("ps_jxj", "drawable", getPackageName());
//                            Drawable image = getResources().getDrawable(imgId);
//                            PictureMarkerSymbol sms = new PictureMarkerSymbol(image);
                            SimpleMarkerSymbol sms = new SimpleMarkerSymbol(Color.argb(255, Integer.valueOf(rgb[0]), Integer.valueOf(rgb[1]), Integer.valueOf(rgb[2])),12, SimpleMarkerSymbol.STYLE.TRIANGLE);
                            Graphic graphic = new Graphic(pt, sms, attributes);
                            GDgralry.addGraphic(graphic);

                            //添加标注
                            TextSymbol tsT = new TextSymbol(12, JCJBH, Color.argb(255, Integer.valueOf(rgb[0]), Integer.valueOf(rgb[1]), Integer.valueOf(rgb[2])));
                            tsT.setFontFamily("DroidSansFallback.ttf");
                            tsT.setOffsetX(5);
                            tsT.setOffsetY(5);
                            Graphic graphicT = new Graphic(pt, tsT);
                            GDAnnogralry.addGraphic(graphicT);
                        }
                        else
                        {
                            String[] rgb = ColorRGB.split(",");
//                            int imgId = getResources().getIdentifier("ps_zxd", "drawable", getPackageName());
//                            Drawable image = getResources().getDrawable(imgId);
                            //PictureMarkerSymbol sms = new PictureMarkerSymbol(image);
                            SimpleMarkerSymbol sms = new SimpleMarkerSymbol(Color.argb(255, Integer.valueOf(rgb[0]), Integer.valueOf(rgb[1]), Integer.valueOf(rgb[2])), 8, SimpleMarkerSymbol.STYLE.CIRCLE);
                            Graphic graphic = new Graphic(pt, sms, attributes);
                            GDgralry.addGraphic(graphic);
                        }
                    }
                    while (cur.moveToNext());
                }
                cur.close();
            }
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage(e.getMessage())
                    .setPositiveButton("确定", null)
                    .show();
        }
    }

    void CreatGXGraphic(GraphicsLayer GXgralry, GraphicsLayer GXAnnogralry, String GXTableName, String ColorRGB) {
        try {
            String[] rgb = ColorRGB.split(",");
            SimpleLineSymbol sms = new SimpleLineSymbol(Color.argb(255, Integer.valueOf(rgb[0]), Integer.valueOf(rgb[1]), Integer.valueOf(rgb[2])), 2, SimpleLineSymbol.STYLE.SOLID);

            Cursor cur = db.rawQuery("SELECT * FROM " + GXTableName , null);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    do {

                        String QDDH = cur.getString(cur.getColumnIndex("QDDH"));
                        if (QDDH == null) QDDH  = "";
                        String ZDDH = cur.getString(cur.getColumnIndex("ZDDH"));
                        if (ZDDH == null) ZDDH = "";

                        String CZ = cur.getString(cur.getColumnIndex("CZ"));
                        if (CZ == null) CZ = "";
                        String GJ = cur.getString(cur.getColumnIndex("GJ"));
                        if (GJ == null) GJ = "";

                        String XG = cur.getString(cur.getColumnIndex("QDMS"));
                        if (XG == null) XG = "";

                        String ZDMS = cur.getString(cur.getColumnIndex("QDMS"));
                        if (ZDMS == null) ZDMS = "";

                        Point ptS = new Point();
                        double QDXZB = cur.getDouble(cur.getColumnIndex("QDXZB"));
                        double QDYZB = cur.getDouble(cur.getColumnIndex("QDYZB"));
                        if(QDXZB>0&&QDYZB>0) {
                            ptS.setX(QDXZB);
                            ptS.setY(QDYZB);
                        }

                        Point ptE = new Point();
                        double ZDXZB = cur.getDouble(cur.getColumnIndex("ZDXZB"));
                        double ZDYZB = cur.getDouble(cur.getColumnIndex("ZDYZB"));
                        if(ZDXZB>0&&ZDYZB>0) {
                            ptE.setX(ZDXZB);
                            ptE.setY(ZDYZB);
                        }
                        if (!ptS.isEmpty() && !ptE.isEmpty()) {
                            Line line = new Line();
                            line.setStart(ptS);
                            line.setEnd(ptE);
                            Polyline polyline = new Polyline();
                            polyline.addSegment(line, true);

                            Map<String, Object> attributes = new HashMap<String, Object>();
                            attributes.put("QDDH", QDDH);
                            attributes.put("ZDDH", ZDDH);
                            attributes.put("CZ", CZ);
                            attributes.put("GJ", GJ);
                            attributes.put("QDMS", XG);
                            attributes.put("ZDMS", ZDMS);

                            Graphic graphic = new Graphic(polyline, sms, attributes);
                            GXgralry.addGraphic(graphic);

                            //注记位置为线中心点计算 注记沿线注记，角度取线的弧度
                            // 大于90度小于等于180
                            double d_CenterX = (ptS.getX() + ptE.getX()) / 2;
                            double d_CenterY = (ptS.getY() + ptE.getY()) / 2;
                            Point pt = new Point();
                            pt.setXY(d_CenterX, d_CenterY);

                            double angle = 0;
                            double k = 0;
                            float fAngle = 0;
                            float fAngle1 = 0;
                            double X1 = ptS.getX();
                            double X2 = ptE.getX();
                            double Y1 = ptS.getY();
                            double Y2 = ptE.getY();

                            if (X2 - X1 == 0) {
                                fAngle = 90;
                                fAngle1 = fAngle;
                            } else if (X2 > X1 && Y2 > Y1) {
                                //1.X2>X1,Y2>Y1 k=Y2-Y1/X2-X1 α＝360-arctank
                                k = (Y2 - Y1) / (X2 - X1);   //斜率
                                angle = Math.atan(k);
                                fAngle = (float) (360 - Math.toDegrees(angle));
                                fAngle1 = fAngle + 180;
                            } else if (X2 > X1 && Y2 < Y1) {
                                //2.X2>X1,Y2<Y1 k=Y1-Y2/X2-X1 α＝arctank
                                k = (Y1-Y2) / (X2-X1);   //斜率
                                angle = Math.atan(k);
                                fAngle = (float) (Math.toDegrees(angle));
                                fAngle1 = 180 + fAngle;
                            } else if (X2 < X1 && Y2 < Y1) {
                                //3.X2<X1,Y2<Y1 k=Y1-Y2/X1-X2 α＝360-arctank
                                k =(Y1-Y2)/(X1-X2);   //斜率
                                angle = Math.atan(k);
                                fAngle = (float) (360 - Math.toDegrees(angle));
                                fAngle1 = fAngle;
                            } else if (X2 < X1 && Y2 > Y1) {
                                //4.X2<X1,Y2>Y1 k=Y2-Y1/X1-X2 α＝arctank
                                k = (Y2-Y1) / (X1-X2);   //斜率
                                angle = Math.atan(k);
                                fAngle = (float) (Math.toDegrees(angle));
                                fAngle1 = fAngle;
                            }
                            //添加标注
                            String strZJNR = GJ+" "+ CZ;
                            TextSymbol tsT = new TextSymbol(12, strZJNR, Color.argb(255, Integer.valueOf(rgb[0]), Integer.valueOf(rgb[1]), Integer.valueOf(rgb[2])));
                            tsT.setFontFamily("DroidSansFallback.ttf");
                            tsT.setOffsetX(5);
                            tsT.setOffsetY(5);
                            tsT.setAngle(fAngle);
                            Graphic graphicT = new Graphic(pt, tsT);
                            GXAnnogralry.addGraphic(graphicT);

                            //流向
//                            if (LX.equals("1")) {
//                                int imgId = getResources().getIdentifier("ps_jsk", "drawable", getPackageName());
//                                Drawable image = getResources().getDrawable(imgId);
//                                PictureMarkerSymbol sms1 = new PictureMarkerSymbol(image);
//                                //PictureMarkerSymbol sms1 = new PictureMarkerSymbol(getResources().getDrawable(imgId));
//                                sms1.setAngle(fAngle1);
//                                Graphic graphic0 = new Graphic(pt, sms1);
//                                GXgralry.addGraphic(graphic0);
//
//                                if (image != null) {
//                                    image.setCallback(null);
//                                    sms1 = null;
//                                    graphic = null;
//                                }
//                            } else {
//                                int imgId = getResources().getIdentifier("ps_csk", "drawable", getPackageName());
//                                Drawable image = getResources().getDrawable(imgId);
//                                PictureMarkerSymbol sms1 = new PictureMarkerSymbol(image);
//                                //PictureMarkerSymbol sms1 = new PictureMarkerSymbol(getResources().getDrawable(imgId));
//                                sms1.setAngle(fAngle1);
//                                Graphic graphic0 = new Graphic(pt, sms1);
//                                GXgralry.addGraphic(graphic0);
//
//                                if (image != null) {
//                                    image.setCallback(null);
//                                    sms1 = null;
//                                    graphic = null;
//                                }
//                            }
                        }
                    }
                    while (cur.moveToNext());
                }
                cur.close();
            }
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage(e.getMessage())
                    .setPositiveButton("确定", null)
                    .show();
        }
    }

    @Override
    protected void onDestroy() {
        setContentView(R.layout.view_null);
        super.onDestroy();
    }


    /**
     * 封装两个视图组件的类
     */
    class ListItemView {
        ImageView imageView;
        TextView textView;
    }

    /**
     * 封装了两个资源的类
     */
    class ListItem {
        private Drawable image;
        private String title;
        private String tag;

        public Drawable getImage() {
            return image;
        }

        public void setImage(Drawable image) {
            this.image = image;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

    }

    /**
     * 定义ListView适配器MainListViewAdapter
     */
    class MainListViewAdapter extends BaseAdapter {

        /**
         * 返回item的个数
         */
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mList.size();
        }

        /**
         * 返回item的内容
         */
        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return mList.get(position);
        }

        /**
         * 返回item的id
         */
        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        /**
         * 返回item的视图
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListItemView listItemView;

            // 初始化item view
            if (convertView == null) {
                // 通过LayoutInflater将xml中定义的视图实例化到一个View中
                convertView = LayoutInflater.from(prj_class.this).inflate(
                        R.layout.items, null);

                // 实例化一个封装类ListItemView，并实例化它的两个域
                listItemView = new ListItemView();
                listItemView.imageView = (ImageView) convertView
                        .findViewById(R.id.image);
                listItemView.textView = (TextView) convertView
                        .findViewById(R.id.title);

                // 将ListItemView对象传递给convertView
                convertView.setTag(listItemView);
            } else {
                // 从converView中获取ListItemView对象
                listItemView = (ListItemView) convertView.getTag();
            }

            // 获取到mList中指定索引位置的资源
            Drawable img = mList.get(position).getImage();
            String title = mList.get(position).getTitle();

            // 将资源传递给ListItemView的两个域对象
            listItemView.imageView.setImageDrawable(img);
            listItemView.textView.setText(title);

            if (position == selectItem) {
                convertView.setBackgroundColor(Color.rgb(221,221,221));
                prjDBpath=mList.get(position).getTag();
                prjName=mList.get(position).getTitle();
            }
            else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }
            // 返回convertView对象
            return convertView;
        }

        public  void setSelectItem(int selectItem) {
            this.selectItem = selectItem;
        }
        private int  selectItem=-1;
    }
}