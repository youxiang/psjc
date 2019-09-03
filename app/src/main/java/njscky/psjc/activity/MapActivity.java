package njscky.psjc.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnLongPressListener;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.map.event.OnZoomListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Polygon;
import com.esri.core.map.Graphic;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import njscky.psjc.DrawEventListener;
import njscky.psjc.DrawTool;
import njscky.psjc.Question;
import njscky.psjc.R;
import njscky.psjc.adapter.GraphicListAdpater;
import njscky.psjc.base.BaseActivity;
import njscky.psjc.login.LoginActivity;
import njscky.psjc.model.LayerInfo;
import njscky.psjc.service.DbManager;
import njscky.psjc.service.MapManager;
import njscky.psjc.util.AppExecutors;
import njscky.psjc.util.Utils;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;
import static njscky.psjc.service.MapManager.TYPE_POINT_JCJ;

public class MapActivity extends BaseActivity {
    private static final String TAG = "MapActivity";

    private static final String BASE_LAYER_NAME = "tmpgralyr";
    private static final int FUN_TYPE_UNKNOWN = 0;
    private static final int FUN_TYPE_QUESTION = 1;
    // requestCode of startActivityForResult
    private static final int REQ_PROJECT = 100;
    private static final int REQ_OPEN_GPS = 101;
    private static final int REQ_LAYER_MANAGE = 102;
    private static final int REQ_ADD_POINT = 103;
    private static final int REQ_PERMISSION = 1001;
    private static final int REQ_LOCATION_PERMISSIONS = 1002;
    MapManager mapManager;
    DrawTool drawTool;
    GraphicsLayer mBaseLayer;
    // question layer
    GraphicsLayer mQuestionLayer;
    // location manager
    LocationManager locationManager;
    Criteria criteria;
    // Define views
    @BindView(R.id.map)
    MapView mapView;
    // top menu
    @BindView(R.id.edit_layout)
    View editLayout;
    @BindView(R.id.spinner)
    Spinner mSpinner;
    @BindView(R.id.btnaddpoint)
    ImageButton btnAddPoint;
    @BindView(R.id.btnaddline)
    ImageButton btnAddLine;
    @BindView(R.id.btneditline)
    ImageButton btnEditLine;
    @BindView(R.id.btndelpoint)
    ImageButton btnDelPoint;
    @BindView(R.id.btndelline)
    ImageButton btnDelLine;
    @BindView(R.id.btneditattribute)
    ImageButton btnEditAttribute;
    @BindView(R.id.btncheck)
    ImageButton btnMark;
    @BindView(R.id.btnsplit)
    ImageButton btnBreakLine;
    @BindView(R.id.btn_reserved)
    ImageButton btnAddReserve;
    // right menu
    @BindView(R.id.btnyw)
    ImageButton btnQuestion;

    // bottom menu
    // scale
    @BindView(R.id.textscale)
    TextView tvScale;
    /**
     * 当前选中view
     */
    View currentSelectedView;

    /**
     * 当前的功能类型
     */
    int funType = FUN_TYPE_UNKNOWN;
    Executor diskExecutor = AppExecutors.getInstance().diskIO();
    Executor mainExecutor = AppExecutors.getInstance().mainThread();
    private DecimalFormat scaleFormat = new DecimalFormat("#.00");
    private DrawEventListener drawEventListener = event -> {
        Graphic graphic = event.getDrawGraphic();
        mBaseLayer.addGraphic(graphic);

        Polygon polygon = (Polygon) graphic.getGeometry();
        if (polygon.getPointCount() >= 3) {
            if (funType == FUN_TYPE_QUESTION) {
                startActivity(new Intent(this, Question.class).putExtra("graphic", graphic));
            } else {
                String polygonStr = Utils.parsePolygonPath(polygon);
                startActivity(new Intent(this, ReportActivity.class));
            }
        }
    };
    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
            locationManager.removeUpdates(locationListener);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    private DbManager dbManager;
    private AlertDialog choosePointsDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CAMERA
                },
                REQ_PERMISSION
        );
    }

    private void init() {
        ButterKnife.bind(this);
        initMapView();

        startLoginActivity();

        getOverflowMenu();

        mSpinner.setVisibility(View.VISIBLE);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        initLocationManager();
    }

    private void initMapView() {
        dbManager = DbManager.getInstance();
        mapManager = new MapManager(this);
        mapManager.loadMap(mapView);
        mBaseLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
        mBaseLayer.setName(BASE_LAYER_NAME);
        mapView.addLayer(mBaseLayer);

        drawTool = new DrawTool(mapView);
        drawTool.addEventListener(drawEventListener);

        mapView.setMinScale(2000000);
        mapView.setMaxScale(1);

        mapView.setOnZoomListener(new OnZoomListener() {

            @Override
            public void preAction(float pivotX, float pivotY, double factor) {
            }

            @Override
            public void postAction(float pivotX, float pivotY, double factor) {
                updateScale();
            }
        });

        mapView.setOnSingleTapListener((OnSingleTapListener) (x, y) -> {

            GraphicsLayer layer = (GraphicsLayer) mapManager.getLayerByName("雨水管点_检查井", mapView);

            if (layer != null) {
                int[] graphicIDs = layer.getGraphicIDs(x, y, 10);

                if (graphicIDs == null || graphicIDs.length == 0) {
                    Toast.makeText(this, "此处无检查井", Toast.LENGTH_SHORT).show();
                    return;
                }

//                if (graphicIDs.length == 1) {
//                    Intent intent = new Intent(this, PipePointActivity.class);
//                    intent.putExtra("graphic", layer.getGraphic(graphicIDs[0]));
//                    startActivity(intent);
//                }

                Log.i(TAG, "initMapView: " + Arrays.toString(graphicIDs));

                List<Graphic> graphics = new ArrayList<>(graphicIDs.length);

                for (int graphicId : graphicIDs) {
                    Graphic graphic = layer.getGraphic(graphicId);
                    Log.i(TAG, "initMapView: " + graphic.getGeometry());
                    graphics.add(graphic);
                }

                choosePoints(graphics);
            }


        });

        mapView.setOnStatusChangedListener((OnStatusChangedListener) (o, status) -> {
            Log.i(TAG, "initMapView: " + o + ", " + status);
            if (status == OnStatusChangedListener.STATUS.INITIALIZED) {
                updateScale();
            }
        });

        mapView.setOnLongPressListener((OnLongPressListener) (x, y) -> {
            if (currentSelectedView == btnAddPoint) {
                Intent intent = new Intent(this, AddPointActivity.class);
                intent.putExtra("point", mapView.toMapPoint(x, y));
                startActivityForResult(intent, REQ_ADD_POINT);
            }
            return true;
        });

    }

    private void choosePoints(List<Graphic> graphics) {
        choosePointsDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.choose_points)
                .setAdapter(
                        new GraphicListAdpater(graphics),
                        (dialog, which) -> {
                            Log.i(TAG, "choosePoints: " + which);
                            Intent intent = new Intent(this, PipePointActivity.class);
                            intent.putExtra("graphic", graphics.get(which));
                            startActivity(intent);
                        }
                )
                .create();

        choosePointsDialog.show();

    }

    private void report() {
        Layer layer = mapManager.getLayerByName("雨水管点_检查井", mapView);
        if (layer instanceof GraphicsLayer) {
            int[] graphicsIds = ((GraphicsLayer) layer).getGraphicIDs();

            if (graphicsIds != null && graphicsIds.length > 0) {
                for (int graphicsId : graphicsIds) {
                    Graphic graphic = ((GraphicsLayer) layer).getGraphic(graphicsId);
                    String[] attributeNames = graphic.getAttributeNames();

                    if (attributeNames != null && attributeNames.length > 0) {
                        Intent intent = new Intent(this, ReportActivity.class);
                        intent.putExtra("reportId", graphic.getAttributeValue("JCJBH").toString().trim());
                        startActivity(intent);
                        break;
                    }
                }
            }
        }
    }

    private void updateScale() {
        tvScale.setText(getString(R.string.scale_text, scaleFormat.format(mapView.getScale())));
    }


    private void initLocationManager() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(GPS_PROVIDER)
                && !locationManager.isProviderEnabled(NETWORK_PROVIDER)) {
            toast(R.string.open_gps);
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQ_OPEN_GPS);
        } else {
            criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);//ACCURACY_COARSE 模糊；ACCURACY_FINE 高精度
            criteria.setAltitudeRequired(false);//不需要海拔
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(false);//不需要费用
            criteria.setSpeedRequired(true); //需要速度
            criteria.setPowerRequirement(Criteria.POWER_LOW); //电量消耗低
        }
    }

    private void startLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        drawTool.removeEventListener(drawEventListener);
    }

    @OnClick(R.id.btnGCGL)
    public void onProject() {
        drawTool.deactivate();
        startActivityForResult(new Intent(this, ProjectManagementActivity.class), REQ_PROJECT);
    }

    @OnClick(R.id.btnSJBJ)
    public void onEdit() {
        drawTool.deactivate();

        Layer[] layers = mapView.getLayers();
        if (layers == null || layers.length <= 2) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_title)
                    .setMessage(R.string.no_edit_project)
                    .setPositiveButton(R.string.confirm, null)
                    .show();
        } else {
            setCurrentSelected(null);

            if (editLayout.getVisibility() == View.VISIBLE) {
                editLayout.setVisibility(View.GONE);
            } else {
                editLayout.setVisibility(View.VISIBLE);


            }
        }

    }

    @OnClick(R.id.btnTCKZ)
    public void onLayer() {
        // 图层控制
        drawTool.deactivate();
        ArrayList<LayerInfo> layerInfoList = new ArrayList<>();
        for (Layer layer : mapView.getLayers()) {
            if (TextUtils.equals(layer.getName(), BASE_LAYER_NAME)) {
                continue;
            }
            if (TextUtils.isEmpty(layer.getName())) {
                continue;
            }

            layerInfoList.add(new LayerInfo(layer));


        }
        Intent intent = new Intent(this, LayerActivity.class);
        intent.putParcelableArrayListExtra("layersInfo", layerInfoList);
        startActivityForResult(intent, REQ_LAYER_MANAGE);
    }

    @OnClick(R.id.btnyw)
    public void onQuestion() {
        funType = FUN_TYPE_QUESTION;
        setCurrentSelected(btnQuestion);
        drawTool.activate(DrawTool.POLYGON);
    }

    @OnClick(R.id.btnFull)
    public void onFull() {
        drawTool.deactivate();
        Envelope env = new Envelope();
        env.setCoords(119642.008560884, 139699.14891334, 130225.363060926, 144519.536158281);
        mapView.setExtent(env);
    }

    @OnClick(R.id.btnPan)
    public void onPan() {
        setCurrentSelected(null);
        drawTool.deactivate();
        mBaseLayer.removeAll();
    }

    @OnClick(R.id.btnGps)
    public void onGps() {
        drawTool.deactivate();
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                REQ_LOCATION_PERMISSIONS
        );
    }


    @OnClick(R.id.btnaddpoint)
    public void onAddPoint() {
        setCurrentSelected(btnAddPoint);
        toast(R.string.add_point);
        drawTool.deactivate();
    }

    @OnClick(R.id.btndelpoint)
    public void onDelPoint() {
        setCurrentSelected(btnDelPoint);

    }

    @OnClick(R.id.btndelline)
    public void onDelLine() {
        setCurrentSelected(btnDelLine);

    }

    @OnClick(R.id.btnaddline)
    public void onAddLine() {
        setCurrentSelected(btnAddLine);

    }

    @OnClick(R.id.btneditattribute)
    public void onEditAttribute() {
        setCurrentSelected(btnEditAttribute);

    }

    @OnClick(R.id.btncheck)
    public void onMark() {
        // 采集标记
        setCurrentSelected(btnMark);
        toast(R.string.collect_mark);
        drawTool.deactivate();
    }

    @OnClick(R.id.btnsplit)
    public void onBreakLine() {
        setCurrentSelected(btnBreakLine);
    }

    @OnClick(R.id.btn_reserved)
    public void onAddReserved() {
        setCurrentSelected(btnAddReserve);
    }

    @OnClick(R.id.btneditline)
    public void onEditLine() {
        setCurrentSelected(btnEditLine);
    }

    private void setCurrentSelected(View view) {
        View prev = currentSelectedView;

        if (prev != null && prev.isSelected()) {
            prev.setSelected(false);
        }

        if (view != null && !view.isSelected()) {
            view.setSelected(true);
        }

        currentSelectedView = view;

    }

    private void getOverflowMenu() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQ_PROJECT:
                handleProject(data);
                break;
            case REQ_OPEN_GPS:
                // TODO
                break;
            case REQ_LAYER_MANAGE:
                handleLayerManage(data);
                break;
            case REQ_ADD_POINT:
                // TODO
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_PERMISSION) {
            handlePermissionResult(permissions, grantResults, this::init);
        } else if (requestCode == REQ_LOCATION_PERMISSIONS) {
            handlePermissionResult(permissions, grantResults, this::startLocation);
        }
    }

    private void handlePermissionResult(String[] permissions, int[] grantResults, Runnable runnable) {
        boolean isGranted = true;
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
        }

        if (!isGranted) {
            toast(R.string.no_permission);
        } else {
            runnable.run();
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocation() {
        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);
        updateWithNewLocation(location);
        locationManager.requestLocationUpdates(provider, 5000, 1, locationListener);
    }

    private void updateWithNewLocation(Location location) {
        mapManager.addCurrentLocation(mapView, mBaseLayer, location);
    }

    private void handleLayerManage(Intent data) {
        ArrayList<LayerInfo> layerInfos = data.getParcelableArrayListExtra("layersInfo");
        if (layerInfos == null) {
            return;
        }

        for (LayerInfo layerInfo : layerInfos) {
            Layer layer = mapView.getLayerByID(layerInfo.id);
            layer.setVisible(layerInfo.visible);
        }
    }

    private void handleProject(Intent data) {
        String dbFilePath = data.getStringExtra("dbFilePath");

        if (TextUtils.isEmpty(dbFilePath)) {
            onHandleProjectError();
            return;
        }

        diskExecutor.execute(() -> {

            if (!dbManager.isOpen()) {
                dbManager.openDataBase(dbFilePath);
            }

            mapManager.addPipePointLayers(mapView, dbManager, TYPE_POINT_JCJ);
            mapManager.addPipePointLayers(mapView, dbManager, MapManager.TYPE_POINT_TZD);

            mapManager.addPipeLineLayers(mapView, dbManager, MapManager.TYPE_LINE_JCJ);
            mapManager.addPipeLineLayers(mapView, dbManager, MapManager.TYPE_LINE_TZD);

            Log.i(TAG, "handleProject: handle project done!");

        });


    }

    private void onHandleProjectError() {
        toast(R.string.open_project_file_fail);
    }
}
