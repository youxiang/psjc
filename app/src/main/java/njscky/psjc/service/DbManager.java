package njscky.psjc.service;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import njscky.psjc.model.PipeLine;
import njscky.psjc.model.PipePoint;


public class DbManager {


    private static final String QUERY_PIPE_POINT = "SELECT * FROM %s";

    private static final String QUERY_PIPE_LINE = "SELECT * FROM %s";

    private volatile static DbManager instance;

    private SQLiteDatabase db;

    private String dbFile;


    private DbManager() {

    }

    public static DbManager getInstance() {
        if (instance == null) {
            synchronized (DbManager.class) {
                if (instance == null) {
                    instance = new DbManager();
                }
            }
        }
        return instance;
    }

    public void openDataBase(String dbFile) {
        try {
            db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
            this.dbFile = dbFile;
        } catch (Exception e) {
            Log.e("DbManager", "openDataBase: ");
        }
    }

    public Cursor getPipePoints(int type) throws Exception {
        return db.rawQuery(String.format(QUERY_PIPE_POINT, type == MapManager.TYPE_POINT_JCJ ? "YS_POINT_JCJ" : "YS_POINT_TZD"), null);
    }

    public Cursor getPipeLines(int type) throws Exception {
        return db.rawQuery(String.format(QUERY_PIPE_LINE, type == MapManager.TYPE_LINE_JCJ ? "YS_LINE_JCJ" : "YS_LINE_TZD"), null);
    }

    public void closeDb() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    public boolean isOpen() {
        return !TextUtils.isEmpty(dbFile) && db != null && db.isOpen();
    }

    public List<PipeLine> findPipeLineByPipePoint(PipePoint pipePoint) {
        List<PipeLine> rst = new ArrayList<>();
        String sql = String.format("select * from %s where QDDH = '%s' or ZDDH = '%s'", "YS_LINE_JCJ", pipePoint.JCJBH, pipePoint.JCJBH);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                rst.add(new PipeLine(cursor));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return rst;
    }

    public PipePoint getPipePointByJCJBH(String jcjbh) {
        String sql = String.format("SELECT * FROM YS_POINT_JCJ where JCJBH = '%s'", jcjbh);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst()) {
                return new PipePoint(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }
}
