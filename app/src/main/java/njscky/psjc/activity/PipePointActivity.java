package njscky.psjc.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.esri.core.map.Graphic;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import butterknife.BindView;
import butterknife.ButterKnife;
import njscky.psjc.R;
import njscky.psjc.adapter.PointPropertyListAdapter;
import njscky.psjc.base.BaseActivity;
import njscky.psjc.model.OptionalProperty;
import njscky.psjc.model.PipeLine;
import njscky.psjc.model.PipePoint;
import njscky.psjc.model.Property;
import njscky.psjc.service.DbManager;
import njscky.psjc.util.AppExecutors;

public class PipePointActivity extends BaseActivity {

    private static final String TAG = PipePointActivity.class.getSimpleName();
    Executor diskExecutor = AppExecutors.getInstance().diskIO();
    Executor mainExecutor = AppExecutors.getInstance().mainThread();

    @BindView(R.id.rv_property_list)
    RecyclerView rvPropertyList;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    PointPropertyListAdapter pointPropertyListAdapter;
    private Graphic graphic;
    private String JCJBH;
    private PipePoint pipePoint;
    private List<Fragment> fragments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pipe_point);
        ButterKnife.bind(this);
        graphic = (Graphic) getIntent().getSerializableExtra("graphic");
        Map<String, Object> attributes = graphic.getAttributes();
        if (attributes != null) {
            JCJBH = (String) attributes.get("JCJBH");
            Log.i(TAG, "onCreate: " + JCJBH);
        }

        // 加载连接点
        loadPipePointInfo();
    }

    private void loadPipePointInfo() {
        diskExecutor.execute(() -> {
            pipePoint = DbManager.getInstance().getPipePointByJCJBH(JCJBH);

            Log.i(TAG, "loadPipePointInfo: " + pipePoint);

            List<PipeLine> pipeLineList = DbManager.getInstance().findPipeLineByPipePoint(pipePoint);

            for (PipeLine pipeLine : pipeLineList) {
                Log.i(TAG, "loadPipePointInfo: " + pipeLine);
            }

            mainExecutor.execute(() -> {
                showInfo(pipeLineList);
            });
        });
    }

    private void showInfo(List<PipeLine> pipeLineList) {
        List<Property> pointProperties = getPointProperties(pipePoint);
        pointPropertyListAdapter = new PointPropertyListAdapter();
        rvPropertyList.setLayoutManager(new GridLayoutManager(this, 1));
        rvPropertyList.setNestedScrollingEnabled(false);
        rvPropertyList.setAdapter(pointPropertyListAdapter);
        pointPropertyListAdapter.setProperties(pointProperties);

        if (fragments == null) {
            fragments = new ArrayList<>();
        } else {
            fragments.clear();
        }

        int pipeLineCount = pipeLineList.size();
        for (int i = 0; i < pipeLineCount; i++) {
            fragments.add(ConnectPointFragment.newInstance(pipeLineList.get(i), pipePoint));
        }

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return "连接点" + (position + 1);
            }
        });

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    private List<Property> getPointProperties(PipePoint pipePoint) {
        List<Property> rst = new ArrayList<>();
        rst.add(new Property("检查井编号", pipePoint.JCJBH));
        rst.add(new OptionalProperty("井盖材质", pipePoint.JGCZ, new String[]{"铸铁", "水泥", "复合"}));
        rst.add(new OptionalProperty("井盖情况", pipePoint.JGQK, new String[]{"正常", "破损", "错盖"}));
        rst.add(new OptionalProperty("井室材质", pipePoint.JSCZ, new String[]{"砖砌", "模块", "钢筋砼"}));
        rst.add(new OptionalProperty("井室情况", pipePoint.JSQK, new String[]{"正常", "破损", "渗漏"}));
        rst.add(new Property("井室尺寸", pipePoint.JSCC, true));
        rst.add(new OptionalProperty("是否交叉井", pipePoint.SFJCJ, new String[]{"是", "否"}));
        rst.add(new OptionalProperty("雨水篦运行情况", pipePoint.YSBQK, new String[]{"良好", "破损","暗管接入"}));
        return rst;
    }

}
