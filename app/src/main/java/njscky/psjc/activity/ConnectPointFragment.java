package njscky.psjc.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import njscky.psjc.R;
import njscky.psjc.adapter.PointPropertyListAdapter;
import njscky.psjc.model.PipeLine;
import njscky.psjc.model.PipePoint;
import njscky.psjc.model.Property;
import njscky.psjc.util.AppExecutors;

public class ConnectPointFragment extends Fragment {
    private static final String TAG = ConnectPointFragment.class.getSimpleName();
    RecyclerView recyclerView;
    PointPropertyListAdapter adapter;
    PipeLine pipeLine;
    PipePoint pipePoint;
    Executor diskExecutor = AppExecutors.getInstance().diskIO();
    Executor mainExecutor = AppExecutors.getInstance().mainThread();

    public static Fragment newInstance(PipeLine pipeLine, PipePoint pipePoint) {
        ConnectPointFragment fragment = new ConnectPointFragment();
        Bundle args = new Bundle();
        args.putParcelable("pipeLine", pipeLine);
        args.putParcelable("pipePoint", pipePoint);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connect_point, container, false);
        recyclerView = view.findViewById(R.id.connect_point_property_list_view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PointPropertyListAdapter();
        recyclerView.setAdapter(adapter);

        Bundle args = getArguments();
        if (args != null) {
            pipeLine = args.getParcelable("pipeLine");
            pipePoint = args.getParcelable("pipePoint");
        }

        diskExecutor.execute(() -> {
            List<Property> properties = getProperties();
            mainExecutor.execute(() -> {
                adapter.setProperties(properties);
            });
        });
    }

    /**
     * @return
     */
    boolean isStartPoint() {
        if (pipePoint != null && pipeLine != null) {
            if (TextUtils.equals(pipeLine.QDDH, pipePoint.JCJBH)) {
                return true;
            }
        }
        return false;
    }

    boolean isEndPoint() {
        if (pipePoint != null && pipeLine != null) {
            if (TextUtils.equals(pipeLine.ZDDH, pipePoint.JCJBH)) {
                return true;
            }
        }
        return false;
    }

    private List<Property> getProperties() {
        List<Property> properties = new ArrayList<>();

        boolean isStartPoint = isStartPoint();
        boolean isEndPoint = isEndPoint();

        if (pipeLine != null) {
            if (isStartPoint) {
                properties.add(new Property("连接点号", pipeLine.ZDDH));
                properties.add(new Property("接入方向", "", true));
                properties.add(new Property("本点标高", pipeLine.ZDGC, true));
                properties.add(new Property("接点标高", pipeLine.QDGC, true));
                properties.add(new Property("管径", pipeLine.GJ, true));
                properties.add(new Property("管材", pipeLine.CZ, true));
                properties.add(new Property("是否混接", "", true));
                properties.add(new Property("管道缺陷", "", true));
            } else if (isEndPoint) {
                properties.add(new Property("连接点号", pipeLine.QDDH));
                properties.add(new Property("接入方向", "", true));
                properties.add(new Property("本点标高", pipeLine.QDGC, true));
                properties.add(new Property("接点标高", pipeLine.ZDGC, true));
                properties.add(new Property("管径", pipeLine.GJ, true));
                properties.add(new Property("管材", pipeLine.CZ, true));
                properties.add(new Property("是否混接", "", true));
                properties.add(new Property("管道缺陷", "", true));
            }

        }

        Log.i(TAG, "getProperties: " + properties.size());
        return properties;
    }
}
