package njscky.psjc.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import njscky.psjc.R;
import njscky.psjc.adapter.PhotoListAdapter;
import njscky.psjc.base.BaseActivity;
import njscky.psjc.model.PhotoInfo;
import njscky.psjc.service.WebServiceManager;
import njscky.psjc.util.AppExecutors;
import njscky.psjc.util.Utils;

import static android.view.Gravity.BOTTOM;

/**
 * 检查报告上传
 */
public class ReportActivity extends BaseActivity {

    private static final int REQ_TAKE_PHOTO = 100;
    private static final int REQ_PICK_PHOTO = 101;
    private static final String TAG = ReportActivity.class.getSimpleName();
    @BindView(R.id.root)
    View root;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.sp_type)
    Spinner spType;
    @BindView(R.id.et_remark)
    EditText etRemark;
    @BindView(R.id.rv_photo_list)
    RecyclerView rvPhotoList;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    ArrayAdapter typeAdapter;
    PhotoListAdapter photoListAdapter;

    String reportId;

    String[] typeDataList = {"现场作业", "清淤前", "清淤后"};
    String[] typeNumberList = {"01", "02", "03"};

    PopupWindow bottomPopup;

    PopupWindowBindHelper popupWindowBindHelper = new PopupWindowBindHelper();
    Executor diskExecutor = AppExecutors.getInstance().diskIO();
    Executor mainExecutor = AppExecutors.getInstance().mainThread();
    AlertDialog progressDialog;
    ProgressDialogHelper progressDialogHelper = new ProgressDialogHelper();
    private File photoFile;
    private int candidateReplacedPhotoPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);

        reportId = getIntent().getStringExtra("reportId");

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 4);

        DividerItemDecoration vertical = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        vertical.setDrawable(getResources().getDrawable(R.drawable.divider_report_vertical));

        DividerItemDecoration horizontal = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL);
        horizontal.setDrawable(getResources().getDrawable(R.drawable.divider_report_horizontal));

        rvPhotoList.addItemDecoration(vertical);
        rvPhotoList.addItemDecoration(horizontal);
        rvPhotoList.setLayoutManager(layoutManager);

        photoListAdapter = new PhotoListAdapter();
        photoListAdapter.setListener(new PhotoListAdapter.OnItemClickListener() {
            @Override
            public void onAddPhoto() {
                showBottomPopup(-1);
            }

            @Override
            public void onPhotoClick(int position) {
                showBottomPopup(position);
            }
        });
        rvPhotoList.setAdapter(photoListAdapter);

        tvTitle.setText(getString(R.string.report_title_format, reportId));

        initTypeList();

    }


    private void showBottomPopup(int position) {
        this.candidateReplacedPhotoPosition = position;
        assertBottomPopup();
        if (!bottomPopup.isShowing()) {
            bottomPopup.showAtLocation(root, BOTTOM, 0, 0);
        }
    }

    private void dismissBottomPopup() {
        if (bottomPopup != null && bottomPopup.isShowing()) {
            bottomPopup.dismiss();
        }
    }

    private void assertBottomPopup() {
        if (bottomPopup != null) return;
        bottomPopup = new PopupWindow(this);
        View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);
        ButterKnife.bind(popupWindowBindHelper, view);

        bottomPopup.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        bottomPopup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        bottomPopup.setBackgroundDrawable(new BitmapDrawable());
        bottomPopup.setFocusable(true);
        bottomPopup.setOutsideTouchable(true);
        bottomPopup.setContentView(view);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQ_TAKE_PHOTO:
                if (photoFile != null) {
                    updatePhotoList();
                }
                break;
            case REQ_PICK_PHOTO:
                if (data != null) {
                    String photoPath = Utils.getRealPathFromUri(this, data.getData());
                    if (!TextUtils.isEmpty(photoPath)) {
                        photoFile = new File(photoPath);
                        updatePhotoList();
                    }
                }

                break;
        }
    }

    private void updatePhotoList() {
        PhotoInfo photoInfo = new PhotoInfo();
        photoInfo.name = photoFile.getName();
        photoInfo.path = photoFile.getAbsolutePath();

        if (candidateReplacedPhotoPosition == -1) {
            photoListAdapter.addPhotoInfo(photoInfo);
        } else {
            photoListAdapter.replacePhotoInfo(candidateReplacedPhotoPosition, photoInfo);

        }
        candidateReplacedPhotoPosition = -1;
    }

    private void initTypeList() {
        typeAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, typeDataList);
        spType.setAdapter(typeAdapter);
        spType.setSelection(0, true);
    }

    private void goPhotoAlbum() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQ_PICK_PHOTO);
    }

    @OnClick(R.id.btn_cancel)
    public void onCancel() {
        finish();
    }

    @OnClick(R.id.btn_confirm)
    public void onConfirm() {
        if (photoListAdapter.getPhotoInfoListSize() > 0) {
            reportToServer();
        } else {
            toast(R.string.report_no_uploaded_photos);
        }
    }

    private void reportToServer() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        String eventCode = format.format(new Date());
        List<PhotoInfo> photoInfoList = photoListAdapter.getPhotoInfoList();

        String picType = typeNumberList[spType.getSelectedItemPosition()];

        String remark = etRemark.getText().toString().trim();

        if (!photoInfoList.isEmpty()) {
            showProgress();
            progressDialogHelper.progressBar.setMax(photoInfoList.size());
            reportToServer(photoInfoList, 0, eventCode, picType, remark);
        }

    }

    private void showProgress() {
        if (progressDialog == null) {
            View progressView = LayoutInflater.from(this).inflate(R.layout.layout_progress, null);
            progressDialog = new AlertDialog.Builder(this)
                    .setTitle("上传进度")
                    .setView(progressView)
                    .create();
            ButterKnife.bind(progressDialogHelper, progressView);
        }

        progressDialog.setCancelable(false);
        progressDialogHelper.reset();

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void reportToServer(
            List<PhotoInfo> photoInfoList,
            int index,
            String eventCode,
            String picType,
            String remark
    ) {
        int size = photoInfoList.size();
        if (index < size) {
            PhotoInfo photoInfo = photoInfoList.get(index);
            progressDialogHelper.append(photoInfo.name + "\n 正在操作...\n");

            WebServiceManager.getInstance(this).insertJCPic(photoInfo, reportId, eventCode, picType, remark, new WebServiceManager.InsertJCPicCallback() {
                @Override
                public void onUploadSuccess(String result) {
                    Log.i(TAG, "onUploadSuccess: " + photoInfo.name + " " + result);
                    progressDialogHelper.append(result + "\n\n");
                    progressDialogHelper.progressBar.setProgress(index + 1);
                    reportToServer(photoInfoList, index + 1, eventCode, picType, remark);
                }

                @Override
                public void onError(String result) {
                    Log.w(TAG, "onError: " + photoInfo.name + " " + result);
                    toast(photoInfo.name + result);
                    progressDialogHelper.append(result + "\n\n");
                    progressDialogHelper.progressBar.setProgress(index + 1);
                    reportToServer(photoInfoList, index + 1, eventCode, picType, remark);
                }
            });

        } else {
            Log.i(TAG, "reportToServer: All finish");
            progressDialogHelper.append(getString(R.string.report_photos_upload_success));
            progressDialog.setCancelable(true);
        }
    }

    class ProgressDialogHelper {

        @BindView(R.id.progress)
        ProgressBar progressBar;

        @BindView(R.id.tv_msg)
        TextView tvMessage;

        void append(String message) {
            tvMessage.append(message);
        }

        void reset() {
            tvMessage.setText("");
            progressBar.setProgress(0);
        }
    }

    class PopupWindowBindHelper {
        @BindView(R.id.item_popupwindows_camera)
        Button btnTakePhoto;
        @BindView(R.id.item_popupwindows_Photo)
        Button btnSelectPhoto;
        @BindView(R.id.item_popupwindows_cancel)
        Button btnCancelSelectPhoto;

        @OnClick(R.id.item_popupwindows_camera)
        public void onTakePhoto() {
            dismissBottomPopup();
            File photoDir = new File(Environment.getExternalStorageDirectory(), "/DCIM/Camera/");
            if (!photoDir.exists())
                photoDir.mkdirs();
            String fileName = reportId + "_" + typeNumberList[(int) spType.getSelectedItemId()] + "_" + System.currentTimeMillis() + ".JPEG";
            photoFile = new File(photoDir, fileName);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), "com.njscky.administrator.psjc.fileprovider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, REQ_TAKE_PHOTO);
        }

        @OnClick(R.id.item_popupwindows_Photo)
        public void onSelectPhoto() {
            dismissBottomPopup();
            goPhotoAlbum();
        }

        @OnClick(R.id.item_popupwindows_cancel)
        public void onCancelSelectPhoto() {
            dismissBottomPopup();
        }

    }
}
