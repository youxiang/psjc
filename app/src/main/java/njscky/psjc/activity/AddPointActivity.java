package njscky.psjc.activity;

import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import njscky.psjc.R;
import njscky.psjc.base.BaseActivity;

public class AddPointActivity extends BaseActivity {

    @BindView(R.id.tcdhText)
    EditText etPointNum;

    @BindView(R.id.tzdText)
    EditText etPoint;

    //附属物
    @BindView(R.id.fswText)
    EditText etAppendant;

    @BindView(R.id.xgText)
    EditText etHeight;

    @BindView(R.id.gtlxText)
    EditText etTowerType;

    @BindView(R.id.bzText)
    EditText etRemark;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_point);
        ButterKnife.bind(this);
    }
}
