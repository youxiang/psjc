package njscky.psjc;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/10/8.
 */
public class Check extends AppCompatActivity implements View.OnClickListener {
    SQLiteDatabase db = null;
    DBManager dbManager = null;
    public static String GXTYPE;
    public static String GLDM;
    public static String CHECKTYPE;
    public static String _gxdh;
    public static String _ljdh;
    public static String _tccz;
    public static String _tcgj;
    public static String _tcms;
    public static String _tcmslx;
    public static String _tcdyz;
    public static String _tcyl;
    public static String _tczks;
    public static String _tczyks;
    public static String _tcdlts;
    public static String _tclx;
    public static String _tcbz;

    TextView GXDH;
    TextView LJDH;
    EditText TCCZ;
    EditText JCCZ;
    EditText TCGJ;
    EditText JCGJ;
    EditText TCMS;
    EditText JCMS;
    EditText TCMSLX;
    EditText JCMSLX;
    EditText TCDYZ;
    EditText JCDYZ;
    EditText TCYL;
    EditText JCYL;
    EditText TCZKS;
    EditText JCZKS;
    EditText TCZYKS;
    EditText JCZYKS;
    EditText TCDLTS;
    EditText JCDLTS;
    EditText TCLX;
    EditText JCLX;
    EditText TCBZ;
    EditText JCBZ;

    RadioButton RBMXD;
    RadioButton RBYBD;
    RadioButton RBKWD;

    private ArrayAdapter<String> adapterCZ;
    private List<checkclass> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check);
        iniEvents();
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
        try {
            int iChkCount = getChkCount();
            this.setTitle(GXTYPE + "管点检查,当前检查个数：" + String.valueOf(iChkCount + 1));
            GXDH = (TextView) findViewById(R.id.gxdhText);
            LJDH = (TextView) findViewById(R.id.ljdhText);
            TCCZ = (EditText) findViewById(R.id.tcczText);
            JCCZ = (EditText) findViewById(R.id.jcczText);
            TCGJ = (EditText) findViewById(R.id.tcgjText);
            JCGJ = (EditText) findViewById(R.id.jcgjText);
            TCMS = (EditText) findViewById(R.id.tcmsText);
            JCMS = (EditText) findViewById(R.id.jcmsText);
            TCMSLX = (EditText) findViewById(R.id.tcmslxText);
            JCMSLX = (EditText) findViewById(R.id.jcmslxText);
            TCDYZ = (EditText) findViewById(R.id.tcdyzText);
            JCDYZ = (EditText) findViewById(R.id.jcdyzText);
            TCYL = (EditText) findViewById(R.id.tcylText);
            JCYL = (EditText) findViewById(R.id.jcylText);
            TCZKS = (EditText) findViewById(R.id.tczyksText);
            JCZKS = (EditText) findViewById(R.id.jczksText);
            TCZYKS = (EditText) findViewById(R.id.tczyksText);
            JCZYKS = (EditText) findViewById(R.id.jczyksText);
            TCDLTS = (EditText) findViewById(R.id.tcdltsText);
            JCDLTS = (EditText) findViewById(R.id.jcdltsText);
            TCLX = (EditText) findViewById(R.id.tclxText);
            JCLX = (EditText) findViewById(R.id.jclxText);
            TCBZ = (EditText) findViewById(R.id.tcbzText);
            JCBZ = (EditText) findViewById(R.id.jcbzext);

            GXDH.setText("管线点号：" + _gxdh);
            LJDH.setText("连接点号：" + _ljdh);
            TCCZ.setText(_tccz);
            TCGJ.setText(_tcgj);
            TCMS.setText(_tcms);
            TCMSLX.setText(_tcmslx);
            TCDYZ.setText(_tcdyz);
            TCYL.setText(_tcyl);
            TCZKS.setText(_tczks);
            TCZYKS.setText(_tczyks);
            TCDLTS.setText(_tcdlts);
            TCLX.setText(_tclx);
            TCBZ.setText(_tcbz);

            ListView lview = (ListView) findViewById(R.id.check_listview);
            populateList();
            Check_adapter adapter = new Check_adapter(this, list);
            lview.setAdapter(adapter);

            RBMXD=(RadioButton)findViewById(R.id.radioMxd);
            RBYBD=(RadioButton)findViewById(R.id.radioYbd);
            RBKWD=(RadioButton)findViewById(R.id.radioKwd);

            if(CHECKTYPE=="明显点")
            {
                RBMXD.setChecked(true);
                RBYBD.setChecked(false);
                RBKWD.setChecked(false);
            }
            else if(CHECKTYPE=="隐蔽点")
            {
                RBMXD.setChecked(false);
                RBYBD.setChecked(true);
                RBKWD.setChecked(false);
            }
            else if(CHECKTYPE=="开挖点")
            {
                RBMXD.setChecked(false);
                RBYBD.setChecked(false);
                RBKWD.setChecked(true);
            }
        }
        catch(Exception e){}
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
            values.put("GXDH", _gxdh);
            values.put("LJDH", _ljdh);
            values.put("CHECKTYPE", CHECKTYPE);
            values.put("GXTYPE", GXTYPE);

            double dbltcsd = 0.0;
            if (!TCMS.getText().toString().trim().equals("")) {
                dbltcsd = Double.valueOf(TCMS.getText().toString().trim());
                dbltcsd = dbltcsd * 100;
                values.put("TCSD", dbltcsd);
            }
            double dbljcsd = 0.0;
            if (!JCMS.getText().toString().trim().equals("")) {
                dbljcsd = Double.valueOf(JCMS.getText().toString().trim());
                dbljcsd = dbljcsd * 100;
                values.put("JCSD", dbljcsd);
            }

            double dblcz = dbljcsd - dbltcsd;
            values.put("SDCZ", Math.abs(dblcz));
            if (Math.abs(dblcz) > 5) {
                values.put("SFCX", "是");
            } else {
                values.put("SFCX", "否");
            }
            if (JCGJ.getText().toString().trim().equals("")) {
                values.put("JCGJ", TCGJ.getText().toString().trim());
            } else {
                values.put("JCGJ", JCGJ.getText().toString().trim());
            }
            if (JCCZ.getText().toString().trim().equals("")) {
                values.put("JCCZ", TCCZ.getText().toString().trim());
            } else {
                values.put("JCCZ", JCCZ.getText().toString().trim());
            }
            values.put("BZ", JCBZ.getText().toString().trim());

            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = sDateFormat.format(new java.util.Date());
            values.put("JCRQ", date);
            db.insert("GXCHK", "GXDH", values);

            //更新线表或点表
            if(CHECKTYPE.equals("明显点")){
                String sql = "update LINE set SFJC = '是' WHERE (QDDH ='" + _gxdh + "' and ZDDH = '" + _ljdh + "') or (QDDH ='" + _ljdh + "' and ZDDH = '" + _gxdh + "') and GLDM = '" + GLDM + "'";
                db.execSQL(sql);
            }
            else{
                String sql = "update POINT set SFJC = '是' WHERE TCDH ='" + _gxdh + "' and GLDM = '" + GLDM + "'";
                db.execSQL(sql);
            }
            db.close();

            Toast toast = Toast.makeText(this, "提交成功！", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
            toast.show();

            Intent intent = new Intent();
            Bundle mBundle = new Bundle();
            mBundle.putString("CheckState", "1");//压入数据
            intent.putExtras(mBundle);
            setResult(RESULT_OK, intent);
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

    //获取已检查个数
    private int getChkCount() {
        int iCount = 0;
        try {
            DBManager.DB_PATH = MainActivity.prjDBpath;
            dbManager = new DBManager(this);
            db = dbManager.openDatabase();
            Cursor cur = db.rawQuery("select count(*) totalcount from GXCHK", null);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    iCount = cur.getInt(cur.getColumnIndex("totalcount"));
                }
            }
            cur.close();
            db.close();
        } catch (Exception e) {
            if (db != null) {
                db.close();
            }
        }
        return iCount;
    }

    private List<checkclass> populateList(){

        list = new ArrayList<checkclass>();
        DBManager.DB_PATH = MainActivity.prjDBpath;
        dbManager = new DBManager(this);
        db = dbManager.openDatabase();

        ArrayList checktypeList=new ArrayList();
        ArrayList gxtypeList=new ArrayList();
        ArrayList gxdhList=new ArrayList();
        ArrayList ljdhList=new ArrayList();
        ArrayList czList=new ArrayList();
        ArrayList sfcxList=new ArrayList();

        Cursor cur = db.rawQuery("select * from GXCHK", null);
        if (cur != null) {
            if (cur.moveToFirst()) {
                do {
                    checktypeList.add(cur.getString(cur.getColumnIndex("CHECKTYPE")));
                    gxtypeList.add(cur.getString(cur.getColumnIndex("GXTYPE")));
                    gxdhList.add(cur.getString(cur.getColumnIndex("GXDH")));
                    ljdhList.add(cur.getString(cur.getColumnIndex("LJDH")));
                    czList.add(cur.getString(cur.getColumnIndex("SDCZ")));
                    sfcxList.add(cur.getString(cur.getColumnIndex("SFCX")));
                }
                while (cur.moveToNext());
            }
        }
        cur.close();
        db.close();

        for(int i=0;i<checktypeList.size();i++) {
            checkclass check1 = new checkclass();
            check1.setGxtype(gxtypeList.get(i).toString());
            check1.setChecktype(checktypeList.get(i).toString());
            check1.setGxdh(gxdhList.get(i).toString());
            check1.setLjdh(ljdhList.get(i).toString());
            check1.setCz(czList.get(i).toString());
            check1.setSfcx(sfcxList.get(i).toString());
            list.add(check1);
        }
        checktypeList=null;
        gxtypeList=null;
        gxdhList=null;
        ljdhList=null;
        czList=null;
        sfcxList=null;
        return list;
    }

}
