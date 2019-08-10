package njscky.psjc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import njscky.psjc.util.ProgressDialogUtils;
import njscky.psjc.util.WebServiceUtils;

import org.ksoap2.serialization.SoapObject;

/**
 * Created by Administrator on 2015/10/8.
 */
public class Task extends AppCompatActivity implements View.OnClickListener {
    /**
     * Called when the activity is first created.
     */
    SQLiteDatabase db = null;
    DBManager dbManager = null;
    private List<Map<String, Object>> mData;
    private int flag;
    public static String title[] = new String[]{"20160001", "20160002", "20160003", "20160004", "20160005", "20160006", "20160007"};
    public static String info[] = new String[]{"中山南路管线巡查", "中山北路管线巡查", "长江路管线巡查", "华侨路管线巡查", "中山路管线巡查", "中山东路管线巡查", "洪武路管线巡查"};
    public static String status[] = new String[]{"已完成", "进行中", "未开始", "未开始", "未开始", "未开始", "未开始"};
    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    ViewHolder holder = null;
    private LocationManager locationManager;
    private Location location=null;
    private final  Criteria criteria = new Criteria();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task);
        Button btnClose = (Button) findViewById(R.id.btnTaskclose);
        btnClose.setOnClickListener(this);

        mData = getData();
        ListView listView = (ListView) findViewById(R.id.task_listview);
        MyAdapter adapter = new MyAdapter(this);
        listView.setAdapter(adapter);

        String serviceName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) this.getSystemService(serviceName);

        //判断GPS是否正常启动
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)&&locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) {
            Toast.makeText(this, "请开启GPS导航...", Toast.LENGTH_SHORT).show();
            //返回开启GPS导航设置界面
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0);
            return;
        }

        //精度
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//ACCURACY_COARSE 模糊；ACCURACY_FINE 高精度
        criteria.setAltitudeRequired(false);//不需要海拔
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);//不需要费用
        criteria.setSpeedRequired(true); //需要速度
        criteria.setPowerRequirement(Criteria.POWER_LOW); //电量消耗低
    }


    @Override
    public void onPause() {
        unregisterListener();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTaskclose:
                this.finish();
                break;
        }
    }

    //获取动态数组数据  可以由其他地方传来(json等)
    private List<Map<String, Object>> getData() {

//        for(int i=0;i<title.length;i++){
//            Map<String, Object> map = new HashMap<String, Object>();
//            map.put("PlanCode", title[i].toString());
//            map.put("PlanName", info[i].toString());
//            map.put("PlanSpacing", "1");
////                        map.put("status", "未开始");
//            list.add(map);
//        }



        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("UserCode", MainActivity.strUserCode);
        list.clear();
        WebServiceUtils.callWebService(WebServiceUtils.WEB_SERVER_URL, "GetTaskInfo", properties, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(SoapObject result) {
                ProgressDialogUtils.dismissProgressDialog();
                if (result == null) {
                    Toast.makeText(Task.this, "获取任务失败！", Toast.LENGTH_SHORT).show();
                } else {
                    SoapObject soap = (SoapObject) result.getProperty(0);
                    for (int i = 0; i < soap.getPropertyCount(); i++) {
                        SoapObject soap1 = (SoapObject) soap.getProperty(i);
                        String strPlanCode = soap1.getProperty("PlanCode").toString();
                        String strPlanName = soap1.getProperty("PlanName").toString();
                        String strPlanSpacing = soap1.getProperty("PlanSpacing").toString();
                        if(strPlanSpacing.equals("0.5")){
                            strPlanSpacing="一天2次";
                        }
                        else{
                            strPlanSpacing=strPlanSpacing+"天一次";
                        }

                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("PlanCode", strPlanCode);
                        map.put("PlanName", strPlanName);
                        map.put("PlanSpacing", strPlanSpacing);
//                        map.put("status", "未开始");
                        list.add(map);
                    }

                }
            }
        });
        return list;
    }

    public class MyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        //****************************************final方法
        //注意原本getView方法中的int position变量是非final的，现在改为final
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {

                holder = new ViewHolder();

                //可以理解为从vlist获取view  之后把view返回给ListView

                convertView = mInflater.inflate(R.layout.task_listitem, null);
                holder.PlanCode = (TextView) convertView.findViewById(R.id.taskID);
                holder.PlanName = (TextView) convertView.findViewById(R.id.taskName);
                holder.PlanSpacing = (TextView) convertView.findViewById(R.id.taskSpace);
                holder.viewBtn = (Button) convertView.findViewById(R.id.btnTaskStatus);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.PlanCode.setText((String) mData.get(position).get("PlanCode"));
            holder.PlanName.setText((String) mData.get(position).get("PlanName"));
            holder.PlanSpacing.setText((String) mData.get(position).get("PlanSpacing"));
            holder.viewBtn.setTag(position);
           // holder.viewBtn.setText((String) mData.get(position).get("status"));
            //给Button添加单击事件  添加Button之后ListView将失去焦点  需要的直接把Button的焦点去掉
            holder.viewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button btn = (Button) v.findViewById(R.id.btnTaskStatus);
                    if(btn.getText().toString().equals("未开始")){
                        btn.setText("进行中");
                        MainActivity.strPlanCode=mData.get(position).get("PlanCode").toString();
                        //开始记录轨迹
                        registerListener();
                    }
                    else if(btn.getText().toString().equals("进行中"))
                    {
                        btn.setText("已完成");
                        //停止记录轨迹
                        unregisterListener();
                    }
                }
            });

            //holder.viewBtn.setOnClickListener(MyListener(position));
            return convertView;
        }
    }

    //提取出来方便点
    public final class ViewHolder {
        public TextView PlanCode;
        public TextView PlanName;
        public TextView PlanSpacing;
        public Button viewBtn;
    }

//    private Handler handler = new Handler();
//    private Runnable runnable=new Runnable() {
//        @Override
//        public void run() {
//            // TODO Auto-generated method stub
//            //要做的事情
//
//            handler.postDelayed(this, 2000);
//        }
//    };


    private void registerListener() {
        unregisterListener();
        String provider = locationManager.getBestProvider(criteria, true);
        location = locationManager.getLastKnownLocation(provider);
        updateWithNewLocation(location);
        // 第一个参数，定义当前所使用的Location Provider
        // 第二个参数，指示更新的最小时间，但并不是确定的，可能更多或更小
        // 第三个参数，两次定位之间的最小距离，单位米。
        // 第四个参数，监听器
        locationManager.requestLocationUpdates(provider, 5000,1, locationListener);
    }

    private void unregisterListener() {
        locationManager.removeUpdates(locationListener);
    }
    //监听事件
    private final LocationListener locationListener = new LocationListener() {
        //位置发生改变
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }

        //provider被用户关闭
        public void onProviderDisabled(String provider) {
            updateWithNewLocation(null);
        }

        //provider被用户开启
        public void onProviderEnabled(String provider) {
        }

        //Provider的状态在可用、暂时不可用和无服务三个状态直接切换时
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    private void updateWithNewLocation(Location location) {

        double lng = -1;
        double lat = -1;
        float speed=0;
        if (location != null) {
            lng = location.getLongitude();
            lat = location.getLatitude();
            speed=location.getSpeed();

            if(speed>25){
                Intent speedintent = new Intent();
                speedintent.setClass(Task.this, speed.class);
                startActivity(speedintent);
            }

            String tmp = CoordinateConversion.ConvertXY(lng, lat);
            String strValues=MainActivity.strUserCode+","+MainActivity.strPlanCode+","+speed+","+"2"+","+tmp.split(";")[0]+","+tmp.split(";")[1]+",true";
            HashMap<String, String> properties = new HashMap<String, String>();
            properties.put("strValues", strValues);

            WebServiceUtils.callWebService(WebServiceUtils.WEB_SERVER_URL, "InsertTrackpoint", properties, new WebServiceUtils.WebServiceCallBack() {

                @Override
                public void callBack(SoapObject result) {
                    ProgressDialogUtils.dismissProgressDialog();
                    if(result.toString().contains("轨迹更新失败")||result.toString().contains("字符串格式不正确")){
                        Toast.makeText(Task.this, result.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    private void InsertTrackPoint(String strSpeed,String strX,String strY){
        try {
            String strValues= MainActivity.strUserCode+","+MainActivity.strPlanCode+","+strSpeed+",2,"+strX+","+strY+",true";
            HashMap<String, String> properties = new HashMap<String, String>();
            properties.put("strValues", strValues);
            WebServiceUtils.callWebService(WebServiceUtils.WEB_SERVER_URL, "InsertTrackpoint", properties, new WebServiceUtils.WebServiceCallBack() {

                @Override
                public void callBack(SoapObject result) {
                    ProgressDialogUtils.dismissProgressDialog();
                    if (result.toString().contains("失败")) {
                        Toast.makeText(Task.this, result.toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        //MainActivity.strUserCode = result.toString().substring(result.toString().indexOf("=") + 1, result.toString().indexOf(";"));
                    }
                }
            });
        }
        catch(Exception ex){}
    }

}
