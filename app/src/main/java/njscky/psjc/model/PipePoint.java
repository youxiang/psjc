package njscky.psjc.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 管点
 */
public class PipePoint implements Parcelable {

    public static final Parcelable.Creator<PipePoint> CREATOR = new Parcelable.Creator<PipePoint>() {
        @Override
        public PipePoint createFromParcel(Parcel source) {
            return new PipePoint(source);
        }

        @Override
        public PipePoint[] newArray(int size) {
            return new PipePoint[size];
        }
    };
    public long OBJECTID;
    public long XLH;
    public String JCJBH;
    public String JGCZ;
    public String JSCC;
    // 所在道路
    public String SZDL;
    public int SFBH;
    // x坐标
    public double XZB;
    // y坐标
    public double YZB;

    public PipePoint() {
    }

    public PipePoint(Cursor cursor) throws Exception {
        JCJBH = cursor.getString(cursor.getColumnIndex("JCJBH"));
        XZB = cursor.getDouble(cursor.getColumnIndex("XZB"));
        YZB = cursor.getDouble(cursor.getColumnIndex("YZB"));
    }

    protected PipePoint(Parcel in) {
        this.OBJECTID = in.readLong();
        this.XLH = in.readLong();
        this.JCJBH = in.readString();
        this.JGCZ = in.readString();
        this.JSCC = in.readString();
        this.SZDL = in.readString();
        this.SFBH = in.readInt();
        this.XZB = in.readDouble();
        this.YZB = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.OBJECTID);
        dest.writeLong(this.XLH);
        dest.writeString(this.JCJBH);
        dest.writeString(this.JGCZ);
        dest.writeString(this.JSCC);
        dest.writeString(this.SZDL);
        dest.writeInt(this.SFBH);
        dest.writeDouble(this.XZB);
        dest.writeDouble(this.YZB);
    }
}
