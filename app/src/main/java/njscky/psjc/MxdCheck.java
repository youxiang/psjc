package njscky.psjc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.esri.android.map.GraphicsLayer;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2015/4/1.
 */
public class MxdCheck extends Activity implements View.OnClickListener {
    private LinearLayout mlLayout;
    private Button btclose;
    private Button btok;

    public static ArrayList<String> DHlst;
    public static ArrayList<mxdcheckclass> Checklst;
    public static ArrayList<Graphic> Graphiclst;
    public static ArrayList<GraphicsLayer>  GXgralayerlst;

    Graphic gra;
    GraphicsLayer gxLayer;
    mxdcheckclass _mxdcheckclass;
    String strDH;
    int index = -1;
    static SimpleLineSymbol sls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mxdcheck);
        CreatView();
    }

    void CreatView() {
        mlLayout = (LinearLayout) findViewById(R.id.mxdcheckconnect);
        btok = (Button) findViewById(R.id.mxdcheckok);
        btok.setOnClickListener(this);
        btclose = (Button) findViewById(R.id.mxdcheckclose);
        btclose.setOnClickListener(this);

        for (int i = 0; i < DHlst.size(); i++) {
            CheckBox ckbItem = new CheckBox(this);
            ckbItem.setText(DHlst.get(i).toString());
            ckbItem.setTextColor(Color.BLACK);
            ckbItem.setTag(R.id.tag_first, i);

            ckbItem.setChecked(false);
            ckbItem.setOnCheckedChangeListener(mCheckBoxChanged);
            mlLayout.addView(ckbItem);

        }

    }

    private CheckBox.OnCheckedChangeListener mCheckBoxChanged = new CheckBox.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                if (isNoOneCheck()) {
                    index = (int) buttonView.getTag(R.id.tag_first);
                    strDH = buttonView.getText().toString();
                    if (index > -1) {
                        gra = (Graphic) Graphiclst.get(index);
                        gxLayer=(GraphicsLayer)GXgralayerlst.get(index);
                        MainActivity.tmpgralayer.removeAll();
                        SimpleLineSymbol sls1 = new SimpleLineSymbol(Color.argb(255, 0, 255, 255), 8, SimpleLineSymbol.STYLE.SOLID);
                        Polyline pt = (Polyline) gra.getGeometry();
                        Graphic graphic = new Graphic(pt, sls1);
                        MainActivity.tmpgralayer.addGraphic(graphic);

                    }
                } else {
                    buttonView.setChecked(false);
                    new AlertDialog.Builder(MxdCheck.this)
                            .setTitle("提示")
                            .setMessage("不能选择多个管点或管线")
                            .setPositiveButton("确定", null)
                            .show();
                }
            } else {
                if (index > -1) {
                    MainActivity.tmpgralayer.removeAll();
                }
            }

        }
    };

    private boolean isNoOneCheck() {
        int checknum = 0;
        for (int i = 0; i < mlLayout.getChildCount(); i++) {
            CheckBox ckbItem = (CheckBox) mlLayout.getChildAt(i);
            if (ckbItem.isChecked()) {
                checknum++;
            }
        }
        if (checknum > 1)
            return false;
        else
            return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mxdcheckclose:
                MainActivity.tmpgralayer.removeAll();
                this.finish();
                break;
            case R.id.mxdcheckok:
                MainActivity.tmpgralayer.removeAll();
                if (index > -1) {
                    gra = (Graphic) Graphiclst.get(index);
                    _mxdcheckclass = Checklst.get(index);
                    strDH = DHlst.get(index);
                    //读取管线信息
                    Check.CHECKTYPE = "明显点";
                    Check.GXTYPE = _mxdcheckclass.getGxtype();
                    Check.GLDM=_mxdcheckclass.getGldm();
                    Check._gxdh= _mxdcheckclass.getGxdh();
                    Check._ljdh = _mxdcheckclass.getLjdh();
                    Check._tcms = _mxdcheckclass.getMs();

                    Check._tccz = _mxdcheckclass.getCz();
                    Check._tcgj = _mxdcheckclass.getGj();

                    Check._tcmslx = _mxdcheckclass.getMslx();
                    Check._tcdyz =_mxdcheckclass.getDyz();
                    Check._tcyl = _mxdcheckclass.getYl();
                    Check._tczks = _mxdcheckclass.getZks();
                    Check._tczyks = _mxdcheckclass.getZyks();
                    Check._tcdlts = _mxdcheckclass.getDlts();
                    Check._tclx = _mxdcheckclass.getLx();
                    Check._tcbz = _mxdcheckclass.getBz();

                    Intent Checkintent = new Intent();
                    Checkintent.setClass(this, Check.class);
                    startActivityForResult(Checkintent, 0);
//                    this.finish();

                }
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                Bundle b = data.getExtras(); //data为B中回传的Intent
                String str = b.getString("CheckState");//str即为回传的值
                //改变选中记录的颜色
                CheckBox ckbItem = (CheckBox) mlLayout.getChildAt(index);
                ckbItem.setTextColor(Color.RED);
                //改变graphic颜色
                SimpleLineSymbol sls = new SimpleLineSymbol(Color.argb(255, 156, 156, 156), 8, SimpleLineSymbol.STYLE.SOLID);
                //修改属性
                Map<String, Object> attributes = gra.getAttributes();
                Map<String, Object> newattributes = new HashMap<String, Object>();

                Iterator<String> iterator = attributes.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = (String) attributes.get(key);
                    if (key.equals("SFJC")) {
                        newattributes.put("SFJC", "是");
                    } else {
                        newattributes.put(key, value);
                    }
                }
                Graphic graphic1 = new Graphic(gra.getGeometry(), sls, newattributes);
                gxLayer.removeGraphic((int) gra.getId());
                gxLayer.addGraphic(graphic1);
                break;
            default:
                break;
        }
    }

}
