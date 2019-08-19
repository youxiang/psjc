package njscky.psjc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import njscky.psjc.R;
import njscky.psjc.adapter.LayerAdapter;
import njscky.psjc.base.BaseActivity;
import njscky.psjc.model.LayerInfo;

/**
 * 图层控制
 */
public class LayerActivity extends BaseActivity {

    @BindView(R.id.layer_list)
    RecyclerView rvLayerList;
    @BindView(R.id.close)
    Button btnClose;
    @BindView(R.id.select_all)
    CheckBox cbSelectAll;

    LayerAdapter adapter;
    ArrayList<LayerInfo> layers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layer);
        ButterKnife.bind(this);

        getExtras();

        rvLayerList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LayerAdapter();
        rvLayerList.setAdapter(adapter);

        cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            adapter.selectAll(isChecked);
        });

        loadLayers();
    }

    @OnClick(R.id.close)
    public void onClose() {
        Intent data = new Intent();
        data.putParcelableArrayListExtra("layersInfo", adapter.getLayersInfo());
        setResult(RESULT_OK, data);
        finish();
    }

    private void getExtras() {
        layers = getIntent().getParcelableArrayListExtra("layersInfo");
    }

    private void loadLayers() {
        adapter.setLayers(layers);
    }
}
