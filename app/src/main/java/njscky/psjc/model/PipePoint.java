package njscky.psjc.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 管点
 */
public class PipePoint implements Parcelable {

    public static final Creator<PipePoint> CREATOR = new Creator<PipePoint>() {
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
    // 序列号
    public long XLH;
    // 检查井编号
    public String JCJBH;
    // x坐标
    public String XZB;
    // y坐标
    public String YZB;
    // 井盖材质
    public String JGCZ;
    // 井盖情况
    public String JGQK;
    // 井室材质
    public String JSCZ;
    // 井室尺寸
    public String JSCC;
    // 井室情况
    public String JSQK;
    // 是否交叉井
    public String SFJCJ;
    // 雨水篦运行情况
    public String YSBQK;
    // 所在道路
    public String SZDL;
    public int SFBH;

    public PipePoint() {
    }

    public PipePoint(Cursor cursor) throws Exception {
        JCJBH = cursor.getString(cursor.getColumnIndex("JCJBH"));
        XZB = cursor.getString(cursor.getColumnIndex("XZB"));
        YZB = cursor.getString(cursor.getColumnIndex("YZB"));
        JGCZ = cursor.getString(cursor.getColumnIndex("JGCZ"));
        JGQK = cursor.getString(cursor.getColumnIndex("JGQK"));
        JSCZ = cursor.getString(cursor.getColumnIndex("JSCZ"));
        JSCC = cursor.getString(cursor.getColumnIndex("JSCC"));
        JSQK = cursor.getString(cursor.getColumnIndex("JSQK"));
        SFJCJ = cursor.getString(cursor.getColumnIndex("SFJCJ"));
        YSBQK = cursor.getString(cursor.getColumnIndex("YSBQK"));
    }

    protected PipePoint(Parcel in) {
        this.OBJECTID = in.readLong();
        this.XLH = in.readLong();
        this.JCJBH = in.readString();
        this.XZB = in.readString();
        this.YZB = in.readString();
        this.JGCZ = in.readString();
        this.JGQK = in.readString();
        this.JSCZ = in.readString();
        this.JSCC = in.readString();
        this.JSQK = in.readString();
        this.SFJCJ = in.readString();
        this.YSBQK = in.readString();
        this.SZDL = in.readString();
        this.SFBH = in.readInt();
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
        dest.writeString(this.XZB);
        dest.writeString(this.YZB);
        dest.writeString(this.JGCZ);
        dest.writeString(this.JGQK);
        dest.writeString(this.JSCZ);
        dest.writeString(this.JSCC);
        dest.writeString(this.JSQK);
        dest.writeString(this.SFJCJ);
        dest.writeString(this.YSBQK);
        dest.writeString(this.SZDL);
        dest.writeInt(this.SFBH);
    }
}
