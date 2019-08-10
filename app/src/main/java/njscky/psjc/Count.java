package njscky.psjc;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/10/8.
 */
public class Count extends AppCompatActivity implements View.OnClickListener {
    SQLiteDatabase db ;
    DBManager dbManager;
    private List<countclass> list;
    int iTotalCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.count);
        ListView lview = (ListView) findViewById(R.id.count_listview);
        TextView total = (TextView) findViewById(R.id.texttotal);
        Button mbtnexit = (Button) findViewById(R.id.btnclose);
        mbtnexit.setOnClickListener(this);
        try {
            populateList();
            Count_adapter adapter = new Count_adapter(this, list);
            lview.setAdapter(adapter);
            total.setText("总计 ："+Integer.toString(iTotalCount)+"个管点");
        }
        catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage(e.getMessage())
                    .setPositiveButton("确定", null)
                    .show();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnclose:
                this.finish();
                break;
        }
    }
    private List<countclass> populateList(){

        list = new ArrayList<countclass>();
        DBManager.DB_PATH = MainActivity.prjDBpath;
        dbManager = new DBManager(this);
        db = dbManager.openDatabase();

        ArrayList gldmList=new ArrayList();
        ArrayList mxdList=new ArrayList();
        ArrayList ybdList=new ArrayList();
        ArrayList zsList=new ArrayList();

        //首先读取管点符号配置表
        Cursor cur = db.rawQuery("select GLDM,count(*) SL from POINT where FSW is NULL AND (TZD = '变材' OR TZD = '变径' OR TZD = '分支' OR TZD = '拐点' OR TZD = '三通' OR TZD = '四通' OR TZD = '弯头' OR TZD = '直线点' OR TZD = '非探测区' OR TZD = '预留口' OR TZD = '出入地点' OR TZD = '户出' OR TZD = '入户' OR TZD = '转折点') group by GLDM", null);
        if (cur != null) {
            if (cur.moveToFirst()) {
                do {
                    gldmList.add(cur.getString(cur.getColumnIndex("GLDM")));
                    ybdList.add(cur.getString(cur.getColumnIndex("SL")));
                }
                while (cur.moveToNext());
            }
        }
        cur.close();

        Cursor cur1 = db.rawQuery("select GLDM,count(*) SL from POINT group by GLDM", null);
        if (cur1 != null) {
            if (cur1.moveToFirst()) {
                do {
                    zsList.add(cur1.getString(cur1.getColumnIndex("SL")));
                }
                while (cur1.moveToNext());
            }
        }
        cur1.close();
        db.close();

        iTotalCount=0;
        for(int i=0;i<gldmList.size();i++) {
            countclass count1 = new countclass();
            count1.setGxtype(gldmList.get(i).toString());
            int ybdCount=Integer.valueOf(ybdList.get(i).toString());
            int zsCount=Integer.valueOf(zsList.get(i).toString());
            int mxdCount=zsCount-ybdCount;
            iTotalCount+=zsCount;
            count1.setMxd(Integer.toString(mxdCount));
            count1.setYbd(Integer.toString(ybdCount));
            count1.setZs(Integer.toString(zsCount));
            list.add(count1);
        }
        gldmList=null;
        mxdList=null;
        ybdList=null;
        zsList=null;
        return list;
    }

    @Override
    protected void onDestroy() {
        list=null;
        setContentView(R.layout.view_null);
        super.onDestroy();
    }
}
