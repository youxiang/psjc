package njscky.psjc;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;

import com.esri.android.map.Layer;

import java.util.ArrayList;


/**
 * Created by Administrator on 2015/4/1.
 */
public class LayerManager extends Activity implements OnClickListener {
    private LinearLayout mlLayout;
    private Button btclose;
    private Button btgdzj;
    private Button btgxzj;
    private CheckBox chkall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layer);
        mlLayout = (LinearLayout) findViewById(R.id.layerconnect);
        mlLayout.setBackgroundColor(Color.TRANSPARENT);
        btclose = (Button) findViewById(R.id.layerclose);
        btclose.setOnClickListener(this);

        btgdzj = (Button) findViewById(R.id.btngdzj);
        btgdzj.setOnClickListener(this);

        btgxzj = (Button) findViewById(R.id.btngxzj);
        btgxzj.setOnClickListener(this);

        chkall = (CheckBox) findViewById(R.id.chkall);
        chkall.setOnClickListener(this);
        CreatView();
    }

    void CreatView() {
        try {
            mlLayout.removeAllViews();
            boolean blnGDZJ = false;
            boolean blnGXZJ = false;
            Layer[] lyrlst = MainActivity.mapView.getLayers();
            ArrayList LayerNameList = new ArrayList();
            //图层按管线种类来，不细分管点管线
            for (int i = 0; i < lyrlst.length; i++) {
                Layer tmplyr = lyrlst[i];
                if (tmplyr.getName()==null||"tmpgralyr".equals(tmplyr.getName())||"drawgralyr".equals(tmplyr.getName()))
                    continue;

                    CheckBox ckbItem = new CheckBox(this);
                    ckbItem.setText(tmplyr.getName());
                    ckbItem.setTextColor(Color.BLACK);
                    ckbItem.setTag(R.id.tag_first, tmplyr.getName());

                    if (tmplyr.isVisible())
                        ckbItem.setChecked(true);
                    else
                        ckbItem.setChecked(false);

                    ckbItem.setOnCheckedChangeListener(mCheckBoxChanged);
                    mlLayout.addView(ckbItem);

            }

            chkall.setChecked(false);

        } catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage(e.getMessage())
                    .setPositiveButton("确定", null)
                    .show();
        }
    }

    private CheckBox.OnCheckedChangeListener mCheckBoxChanged = new CheckBox.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            String strName = (String) buttonView.getTag(R.id.tag_first);
            String strLayerName = buttonView.getText().toString();
            Layer layer;
            //加载
            if (isChecked) {
                    layer = GetLayerbyName(strLayerName);
                    layer.setVisible(true);

            }
            //移除
            else {

                    layer = GetLayerbyName(strLayerName);
                    layer.setVisible(false);

            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layerclose:
                this.finish();
                break;
            case R.id.chkall:
                if (chkall.isChecked() == true) {
                    //全选
                    Layer layer;
                    for (int i = 0; i < mlLayout.getChildCount(); i++) {
                        CheckBox ckbItem = (CheckBox) mlLayout.getChildAt(i);
                        String strName = (String) ckbItem.getTag(R.id.tag_first);
                        String strLayerName = ckbItem.getText().toString();


                            layer = GetLayerbyName(strLayerName);
                            ckbItem.setChecked(true);
                            layer.setVisible(true);

                    }
                } else {
                    //全不选
                    Layer layer;
                    for (int i = 0; i < mlLayout.getChildCount(); i++) {
                        CheckBox ckbItem = (CheckBox) mlLayout.getChildAt(i);
                        String strName = (String) ckbItem.getTag(R.id.tag_first);
                        String strLayerName = ckbItem.getText().toString();


                            layer = GetLayerbyName(strLayerName);
                            ckbItem.setChecked(false);
                            layer.setVisible(false);

                    }
                }
                break;
            case R.id.btngdzj:
                Layer[] lyrlst = MainActivity.mapView.getLayers();
                Layer layer;
                for (int i = 0; i < lyrlst.length; i++) {
                    Layer tmplyr = lyrlst[i];
                    if (tmplyr.getName().endsWith("管点")) {
                        String strLayerName = tmplyr.getName();
                        layer = GetLayerbyName(strLayerName + "注记");
                        if (tmplyr.isVisible())
                            if(layer.isVisible())
                                layer.setVisible(false);
                            else
                                layer.setVisible(true);
                        else
                            layer.setVisible(false);
                    }
                }
                break;
            case R.id.btngxzj:
                Layer[] lyrlst1 = MainActivity.mapView.getLayers();
                Layer layer1;
                for (int i = 0; i < lyrlst1.length; i++) {
                    Layer tmplyr = lyrlst1[i];
                    if (tmplyr.getName().endsWith("管线")) {
                        String strLayerName = tmplyr.getName();
                        layer1 = GetLayerbyName(strLayerName + "注记");
                        if (tmplyr.isVisible())
                            if(layer1.isVisible())
                               layer1.setVisible(false);
                            else
                                layer1.setVisible(true);
                        else
                            layer1.setVisible(false);
                    }
                }
                break;
        }
    }

    private Layer GetLayerbyName(String lyrname) {
        try {
            Layer lyr = null;
            Layer[] lyrlst = MainActivity.mapView.getLayers();
            for (int i = 0; i < lyrlst.length; i++) {
                Layer tmplyr = lyrlst[i];
                if (tmplyr.getName().equals(lyrname)) {
                    lyr = tmplyr;
                    break;
                }
            }
            return lyr;
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage(e.getMessage())
                    .setPositiveButton("确定", null)
                    .show();
            return null;
        }
    }
}
