package njscky.psjc.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.esri.android.map.Layer;

public class LayerInfo implements Parcelable {
    public boolean visible;

    public long id;

    public String name;

    public LayerInfo() {

    }

    public LayerInfo(Layer layer) {
        visible = layer.isVisible();
        id = layer.getID();
        name = layer.getName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.visible ? (byte) 1 : (byte) 0);
        dest.writeLong(this.id);
        dest.writeString(this.name);
    }

    protected LayerInfo(Parcel in) {
        this.visible = in.readByte() != 0;
        this.id = in.readLong();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<LayerInfo> CREATOR = new Parcelable.Creator<LayerInfo>() {
        @Override
        public LayerInfo createFromParcel(Parcel source) {
            return new LayerInfo(source);
        }

        @Override
        public LayerInfo[] newArray(int size) {
            return new LayerInfo[size];
        }
    };
}
