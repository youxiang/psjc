package njscky.psjc.service;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import njscky.psjc.model.PipeLine;
import njscky.psjc.model.PipePoint;


public class DbManager {


    private static final String QUERY_PIPE_POINT = "SELECT OBJECTID,JCJBH,XZB,YZB FROM %s";

    private static final String QUERY_PIPE_LINE = "SELECT OBJECTID,QDDH,ZDDH,CZ,GJ,QDMS,ZDMS,QDXZB,QDYZB,ZDXZB,ZDYZB FROM %s";

    private SQLiteDatabase db;

    public DbManager(String dbFile) {
        openDataBase(dbFile);
    }

    void openDataBase(String dbFile) {
        try {
            db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
        } catch (Exception e) {
            Log.e("DbManager", "openDataBase: ");
        }
    }

    public Cursor getPipePoints(int type) throws Exception{
        return db.rawQuery(String.format(QUERY_PIPE_POINT, type == MapManager.TYPE_POINT_JCJ ? "YS_POINT_JCJ" : "YS_POINT_TZD"), null);
    }

    public Cursor getPipeLines(int type) throws Exception{
        return db.rawQuery(String.format(QUERY_PIPE_LINE, type == MapManager.TYPE_LINE_JCJ? "YS_LINE_JCJ" : "YS_LINE_TZD"), null);
    }

    // 获取雨水管点检查井数据
    public ArrayList<PipePoint> getPipePointList(int type) {
        ArrayList<PipePoint> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(String.format(QUERY_PIPE_POINT, type == MapManager.TYPE_POINT_JCJ ? "YS_POINT_JCJ" : "YS_POINT_TZD"), null);
            while (cursor.moveToNext()) {
                PipePoint item = new PipePoint();

                item.JCJBH = cursor.getString(cursor.getColumnIndex("JCJBH"));
                item.XZB = cursor.getDouble(cursor.getColumnIndex("XZB"));
                item.YZB = cursor.getDouble(cursor.getColumnIndex("YZB"));
                list.add(item);
            }
        } catch (Exception e) {
            Log.e("DbManager", "getPipePointList: ", e);
        } finally {
            cursor.close();
        }
        return list;
    }

    public ArrayList<PipeLine> getPipeLineList(int type) {
        ArrayList<PipeLine> list = new ArrayList<>();

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(String.format(QUERY_PIPE_LINE, type == MapManager.TYPE_POINT_JCJ ? "YS_LINE_JCJ" : "YS_LINE_TZD"), null);
            while (cursor.moveToNext()) {
                PipeLine item = new PipeLine();

                item.QDDH = cursor.getString(cursor.getColumnIndex("QDDH"));
                item.ZDDH = cursor.getString(cursor.getColumnIndex("ZDDH"));
                item.CZ = cursor.getString(cursor.getColumnIndex("CZ"));
                item.GJ = cursor.getString(cursor.getColumnIndex("GJ"));
                item.QDMS = cursor.getString(cursor.getColumnIndex("QDMS"));
                item.ZDMS = cursor.getString(cursor.getColumnIndex("ZDMS"));

                item.QDXZB = cursor.getDouble(cursor.getColumnIndex("QDXZB"));
                item.QDYZB = cursor.getDouble(cursor.getColumnIndex("QDYZB"));

                item.ZDXZB = cursor.getDouble(cursor.getColumnIndex("ZDXZB"));
                item.ZDYZB = cursor.getDouble(cursor.getColumnIndex("ZDYZB"));

                list.add(item);
            }
        } catch (Exception e) {
            Log.e("DbManager", "getPipeLineList: ", e);
        } finally {
            cursor.close();
        }
        return list;

    }

    public void closeDb() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

}
