package njscky.psjc.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * 管线
 */
public class PipeLine implements Parcelable {

    public String QDDH;
    public String ZDDH;

    /**
     * 材质
     */
    public String CZ;
    /**
     * 管径
     */
    public String GJ;

    public String QDMS;
    public String ZDMS;
    // 起点x坐标
    public String QDXZB;
    // 起点y坐标
    public String QDYZB;
    // 终点x坐标
    public String ZDXZB;
    // 终点y坐标
    public String ZDYZB;

    /**
     * 起点高程
     */
    public String QDGC;
    /**
     * 重点高程
     */
    public String ZDGC;

    public PipeLine() {
    }
    
    public PipeLine(Cursor cursor) {
        QDDH = cursor.getString(cursor.getColumnIndex("QDDH"));
        ZDDH = cursor.getString(cursor.getColumnIndex("ZDDH"));
        CZ = cursor.getString(cursor.getColumnIndex("CZ"));
        GJ = cursor.getString(cursor.getColumnIndex("GJ"));
        QDMS = cursor.getString(cursor.getColumnIndex("QDMS"));
        ZDMS = cursor.getString(cursor.getColumnIndex("ZDMS"));

        QDXZB = cursor.getString(cursor.getColumnIndex("QDXZB"));
        QDYZB = cursor.getString(cursor.getColumnIndex("QDYZB"));

        ZDXZB = cursor.getString(cursor.getColumnIndex("ZDXZB"));
        ZDYZB = cursor.getString(cursor.getColumnIndex("ZDYZB"));

        QDGC = cursor.getString(cursor.getColumnIndex("QDGC"));
        ZDGC = cursor.getString(cursor.getColumnIndex("ZDGC"));
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }

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
        dest.writeString(this.QDXZB);
        dest.writeString(this.QDYZB);
        dest.writeString(this.ZDXZB);
        dest.writeString(this.ZDYZB);
        dest.writeString(this.QDGC);
        dest.writeString(this.ZDGC);
    }

    protected PipeLine(Parcel in) {
        this.QDDH = in.readString();
        this.ZDDH = in.readString();
        this.CZ = in.readString();
        this.GJ = in.readString();
        this.QDMS = in.readString();
        this.ZDMS = in.readString();
        this.QDXZB = in.readString();
        this.QDYZB = in.readString();
        this.ZDXZB = in.readString();
        this.ZDYZB = in.readString();
        this.QDGC = in.readString();
        this.ZDGC = in.readString();
    }

    public static final Creator<PipeLine> CREATOR = new Creator<PipeLine>() {
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
