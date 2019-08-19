package njscky.psjc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import njscky.psjc.R;
import njscky.psjc.adapter.DBFileListAdapter;
import njscky.psjc.base.BaseActivity;
import njscky.psjc.model.PipeLine;
import njscky.psjc.model.PipePoint;
import njscky.psjc.service.DbManager;
import njscky.psjc.util.AppExecutors;

public class ProjectManagementActivity extends BaseActivity {

    private static final String TAG = ProjectManagementActivity.class.getSimpleName();
    @BindView(R.id.rv_project_list)
    RecyclerView rvProjectList;
    @BindView(R.id.btn_exit)
    Button btnExit;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    DBFileListAdapter dbFileListAdapter;
    Executor ioExecutor;
    Executor mainExecutor;
    private String dbFilePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_management);
        ButterKnife.bind(this);
        getExtras();

        ioExecutor = AppExecutors.getInstance().diskIO();
        mainExecutor = AppExecutors.getInstance().mainThread();

        dbFileListAdapter = new DBFileListAdapter();
        rvProjectList.setLayoutManager(new LinearLayoutManager(this));
        rvProjectList.setAdapter(dbFileListAdapter);

        loadDBFiles();
    }

    private void loadDBFiles() {
        ioExecutor.execute(() -> {
            List<File> dbFile = new ArrayList<>();
            getDBFiles(dbFilePath, dbFile);

            mainExecutor.execute(() -> {
                dbFileListAdapter.setData(dbFile);
            });

        });
    }

    private void getExtras() {
        dbFilePath = getIntent().getStringExtra("dbFilePath");
        if (TextUtils.isEmpty(dbFilePath)) {
            dbFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getPackageName();
        }
        Log.i(TAG, "getExtras: " + dbFilePath);
    }

    private void getDBFiles(String dirPath, List<File> outFiles) {
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                getDBFiles(file.getAbsolutePath(), outFiles);
            } else if (file.getPath().endsWith(".db")) {
                outFiles.add(file);
            }
        }

    }

    @OnClick(R.id.btn_exit)
    public void onExit() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @OnClick(R.id.btn_confirm)
    public void onConfirm() {
        File dbFile = dbFileListAdapter.getSelectedItem();
        if (dbFile == null) {
            onError();
        } else {
            onResult(dbFile.getAbsolutePath());
        }
    }


    private void onResult(String dbFilePath
    ) {
        Log.i(TAG, "onResult: " + dbFilePath);
        Intent data = new Intent();
        data.putExtra("dbFilePath", dbFilePath);
        setResult(RESULT_OK, data);
        finish();
    }

    private void onError() {
        Log.i(TAG, "onError: ");
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setMessage(R.string.open_project_file_fail)
                .show();
    }
}
