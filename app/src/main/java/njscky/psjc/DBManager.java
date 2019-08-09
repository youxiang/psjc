package njscky.psjc;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Administrator on 2015/7/18.
 */

public class DBManager {
    private final int BUFFER_SIZE = 400000;
    public static String DB_PATH;

    private SQLiteDatabase database;
    private Context context;

    DBManager(Context context) {
        this.context = context;
    }

    public SQLiteDatabase openDatabase() {
        this.database = this.openDatabase(DB_PATH);
        return this.database;
    }

    private SQLiteDatabase openDatabase(String dbfile) {
        try
        {
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
            return db;
        } catch (Exception e) {
            Log.e("Database", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
