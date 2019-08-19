package njscky.psjc.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 管线
 */
public class PipeLine implements Parcelable {

    public String QDDH;
    public String ZDDH;

    public String CZ;
    public String GJ;

    public String QDMS;
    public String ZDMS;
    // 起点x坐标
    public double QDXZB;
    // 起点y坐标
    public double QDYZB;
    // 终点x坐标
    public double ZDXZB;
    // 终点y坐标
    public double ZDYZB;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.QDDH);
        dest.writeString(this.ZDDH);
        dest.writeString(this.CZ);
        dest.writeString(this.GJ);
        dest.writeString(this.QDMS);
        dest.writeString(this.ZDMS);
        dest.writeDouble(this.QDXZB);
        dest.writeDouble(this.QDYZB);
        dest.writeDouble(this.ZDXZB);
        dest.writeDouble(this.ZDYZB);
    }

    public PipeLine() {
    }
    
    public PipeLine(Cursor cursor) {
        QDDH = cursor.getString(cursor.getColumnIndex("QDDH"));
        ZDDH = cursor.getString(cursor.getColumnIndex("ZDDH"));
        CZ = cursor.getString(cursor.getColumnIndex("CZ"));
        GJ = cursor.getString(cursor.getColumnIndex("GJ"));
        QDMS = cursor.getString(cursor.getColumnIndex("QDMS"));
        ZDMS = cursor.getString(cursor.getColumnIndex("ZDMS"));

        QDXZB = cursor.getDouble(cursor.getColumnIndex("QDXZB"));
        QDYZB = cursor.getDouble(cursor.getColumnIndex("QDYZB"));

        ZDXZB = cursor.getDouble(cursor.getColumnIndex("ZDXZB"));
        ZDYZB = cursor.getDouble(cursor.getColumnIndex("ZDYZB"));
    }

    protected PipeLine(Parcel in) {
        this.QDDH = in.readString();
        this.ZDDH = in.readString();
        this.CZ = in.readString();
        this.GJ = in.readString();
        this.QDMS = in.readString();
        this.ZDMS = in.readString();
        this.QDXZB = in.readDouble();
        this.QDYZB = in.readDouble();
        this.ZDXZB = in.readDouble();
        this.ZDYZB = in.readDouble();
    }

    public static final Parcelable.Creator<PipeLine> CREATOR = new Parcelable.Creator<PipeLine>() {
        @Override
        public PipeLine createFromParcel(Parcel source) {
            return new PipeLine(source);
        }

        @Override
        public PipeLine[] newArray(int size) {
            return new PipeLine[size];
        }
    };
}
