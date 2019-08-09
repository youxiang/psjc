package njscky.psjc.util;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import njscky.psjc.CoordinateConversion;
import njscky.psjc.MainActivity;
import njscky.psjc.R;

/**
 * Created by Administrator on 2015/12/25.
 */
public class LocationUtil extends Application {
    public LocationClient mLocationClient;//定位SDK的核心类
    public MyLocationListener mMyLocationListener;//定义监听类
    public String mLocationResult,logMsg;

    @Override
    public void onCreate() {
        super.onCreate();
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
    }

    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());//获得当前时间
            sb.append("\nerror code : ");
            sb.append(location.getLocType());//获得erro code得知定位现状
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());//获得纬度
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());//获得经度
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation){//通过GPS定位
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());//获得速度
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\ndirection : ");
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());//获得当前地址
                sb.append(location.getDirection());//获得方位
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){//通过网络连接定位
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());//获得当前地址
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());//获得经营商？
            }
            mLocationResult=sb.toString();
//            logMsg(sb.toString());
            Log.i("BaiduLocationApiDem", sb.toString());
        }
        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

//    /**
//     * 显示请求字符串
//     * @param str
//     */
//    public void logMsg(String str) {
//        try {
//            if (mLocationResult != null)
//                mLocationResult.setText(str);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
