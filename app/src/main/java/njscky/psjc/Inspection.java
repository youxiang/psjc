package njscky.psjc;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import njscky.psjc.activity.AlbumActivity;
import njscky.psjc.activity.GalleryActivity;
import njscky.psjc.util.Bimp;
import njscky.psjc.util.FileUtils;
import njscky.psjc.util.ImageItem;
import njscky.psjc.util.ProgressDialogUtils;
import njscky.psjc.util.PublicWay;
import njscky.psjc.util.Res;
import njscky.psjc.util.WebServiceUtils;

import org.ksoap2.serialization.SoapObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/20.
 */
public class Inspection extends AppCompatActivity implements View.OnClickListener {

    public static String _gddh;

    private Spinner spinner;
    private ArrayAdapter<String> adapterSJLB;

    private GridView noScrollgridview;
    private GridAdapter adapter;
    private View parentView;
    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    public static Bitmap bimap;

    private EditText txtWZSM;
    private EditText txtSJMS;
    private TextView txtTitle;


    String[] SJLBList = {"现场作业", "清淤前", "清淤后"};
    String[] SJLBBHList = {"01", "02", "03"};
    String strEventCode = "";

    int iCount = 0;
    int iCurCount = 0;
    List<byte[]> byteAttList = new ArrayList<byte[]>();

    String fileName ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inspection);

        Res.init(this);
        bimap = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.icon_addpic_unfocused);
        PublicWay.activityList.add(this);
        parentView = getLayoutInflater().inflate(R.layout.inspection, null);
        spinner = (Spinner) findViewById(R.id.spSJLB);
        txtWZSM = (EditText) findViewById(R.id.editSZWZ);
        txtSJMS = (EditText) findViewById(R.id.editSJMS);
        txtTitle=(TextView) findViewById(R.id.textViewTitle);

        txtTitle.setText("照片上传——"+_gddh);
        Button btnCancel = (Button) findViewById(R.id.btnxjclose);
        btnCancel.setOnClickListener(this);
        Button mbtnok = (Button) findViewById(R.id.btnxjok);
        mbtnok.setOnClickListener(this);
        Init();
    }

    public void Init() {

        //设置默认值
        spinner.setVisibility(View.VISIBLE);

        adapterSJLB = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, SJLBList);
        adapterSJLB.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterSJLB);
        spinner.setSelection(0, true);

        pop = new PopupWindow(Inspection.this);
        View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);
        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
        pop.setWidth(LayoutParams.MATCH_PARENT);
        pop.setHeight(LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);

        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        Button bt1 = (Button) view
                .findViewById(R.id.item_popupwindows_camera);
        Button bt2 = (Button) view
                .findViewById(R.id.item_popupwindows_Photo);
        Button bt3 = (Button) view
                .findViewById(R.id.item_popupwindows_cancel);
        parent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                photo();
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Inspection.this, AlbumActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });

        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        adapter.update();
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == Bimp.tempSelectBitmap.size()) {
                    Log.i("ddddddd", "----------");
                    ll_popup.startAnimation(AnimationUtils.loadAnimation(Inspection.this, R.anim.activity_translate_in));
                    pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                } else {
                    Intent intent = new Intent(Inspection.this,
                            GalleryActivity.class);
                    intent.putExtra("position", "1");
                    intent.putExtra("ID", arg2);
                    startActivity(intent);
                }
            }
        });

    }

    @SuppressLint("HandlerLeak")
    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int selectedPosition = -1;
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update() {
            loading();
        }

        public int getCount() {
            if (Bimp.tempSelectBitmap.size() == 9) {
                return 9;
            }
            return (Bimp.tempSelectBitmap.size() + 1);
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_published_grida,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position == Bimp.tempSelectBitmap.size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.icon_addpic_unfocused));
                if (position == 9) {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position).getBitmap());
//                imagePath = Bimp.tempSelectBitmap.get(position).getImagePath();//这是相册选中的图片路径，大神有说的
//                map.put(position, imagePath);
            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        public void loading() {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        if (Bimp.max == Bimp.tempSelectBitmap.size()) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        } else {
                            Bimp.max += 1;
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        }
                    }
                }
            }).start();
        }
    }

    public String getString(String s) {
        String path = null;
        if (s == null)
            return "";
        for (int i = s.length() - 1; i > 0; i++) {
            s.charAt(i);
        }
        return path;
    }

    protected void onRestart() {
        adapter.update();
        super.onRestart();
    }

    private static final int TAKE_PICTURE = 0x000001;

    public void photo() {
//        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(openCameraIntent, TAKE_PICTURE);

        File file = new File(Environment.getExternalStorageDirectory(), "/DCIM/Camera/");
        if (!file.exists())
            file.mkdirs();
        fileName = String.valueOf(_gddh + "_"+SJLBBHList[(int) spinner.getSelectedItemId()] +"_"+ System.currentTimeMillis() + ".JPEG");

        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileUtils.SDPATH, fileName)));
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {
//                    String fileName = String.valueOf(System.currentTimeMillis());
//                    Bitmap bm = (Bitmap) data.getExtras().get("data");
//                    FileUtils.saveBitmap(bm, fileName);
//
//                    File file = new File(FileUtils.SDPATH, fileName + ".JPEG");
//                    Bimp.tempFilePathlist.add(file);
//
//                    ImageItem takePhoto = new ImageItem();
//                    takePhoto.setBitmap(bm);
//                    Bimp.tempSelectBitmap.add(takePhoto);

                    String path = FileUtils.SDPATH + fileName;
                    Bitmap bm = FileUtils.convertToBitmap(path, 480, 800);//大小压缩
                    String savePath = FileUtils.saveBitmap(bm, fileName);//质量压缩并存储
                    ImageItem takePhoto = new ImageItem();
                    takePhoto.setBitmap(bm);
                    takePhoto.setImagePath(savePath);
                    Bimp.tempSelectBitmap.add(takePhoto);
                }
                break;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            for (int i = 0; i < PublicWay.activityList.size(); i++) {
                if (null != PublicWay.activityList.get(i)) {
                    PublicWay.activityList.get(i).finish();
                }
            }
            Bimp.tempSelectBitmap.clear();
            Bimp.tempBitmap.clear();
            Bimp.max = 0;
            MainActivity.drawTool.deactivate();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnxjclose:
                for (int i = 0; i < PublicWay.activityList.size(); i++) {
                    if (null != PublicWay.activityList.get(i)) {
                        PublicWay.activityList.get(i).finish();
                    }
                }
//                this.finish();
                Bimp.tempSelectBitmap.clear();
                Bimp.tempBitmap.clear();
                Bimp.max = 0;
//                MainActivity.drawTool.deactivate();
                break;
            case R.id.btnxjok:
                //事件上报
                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                strEventCode = sDateFormat.format(new java.util.Date());
                HashMap<String, String> properties = new HashMap<String, String>();


                iCount = Bimp.tempSelectBitmap.size();
                if (iCount > 0) {
                    iCurCount=0;
                    //上传照片
                     UploadImage(iCurCount);
                 } else {
                          Toast toast = Toast.makeText(Inspection.this, "请先拍照或者从相册选择要上传的照片！", Toast.LENGTH_SHORT);
                           toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                           toast.show();

                 }
                break;
        }

}


    private void UploadImage(int index) {
        //上传照片
        ImageItem takePhoto = Bimp.tempSelectBitmap.get(index);
        Bitmap bmp = takePhoto.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteArray = stream.toByteArray();
        String uploadBuffer = new String(Base64.encode(byteArray, Base64.DEFAULT));  //进行Base64编码

       // string strJCJBH,string strUSERCODE,string strPICTYPE,string strPICCODE,string strBZ,string uploadBuffer
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("strJCJBH", _gddh);
        properties.put("strUSERCODE", MainActivity.strUserCode);
        properties.put("strPICTYPE", SJLBBHList[(int) spinner.getSelectedItemId()] );
        properties.put("strPICCODE", strEventCode);
        properties.put("strBZ", txtSJMS.getText().toString().trim() );
        properties.put("uploadBuffer", uploadBuffer);

        WebServiceUtils.callWebService(WebServiceUtils.WEB_SERVER_URL, "InsertJCPic", properties, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(SoapObject result) {
                try {
                    ProgressDialogUtils.dismissProgressDialog();
                    if (result != null) {
                        iCurCount++;
                        if(iCurCount<Bimp.tempSelectBitmap.size()) {
                            UploadImage(iCurCount);
                        }
                        else if(iCurCount==Bimp.tempSelectBitmap.size()){
                            Toast.makeText(Inspection.this, "照片上传成功！", Toast.LENGTH_SHORT).show();
                            for (int i = 0; i < PublicWay.activityList.size(); i++) {
                                if (null != PublicWay.activityList.get(i)) {
                                    PublicWay.activityList.get(i).finish();
                                }
                            }

                            Bimp.tempSelectBitmap.clear();
                            Bimp.tempBitmap.clear();
                            Bimp.max = 0;
                        }
                    }
                } catch (Exception ex) {

                }
            }

        });

    }

    public static File getFileFromBytes(byte[] b, String outputFile) {
        BufferedOutputStream stream = null;
        File file = null;
        try {
            file = new File(outputFile);
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }

    private InputStream Bitmap2Bytes(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 得到输出流
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        // 转输入流
        InputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        return isBm;
    }

    @Override
    protected void onDestroy() {
        setContentView(R.layout.view_null);
        super.onDestroy();
    }

    /**
     * 把一个文件转化为字节
     *
     * @param file
     * @return byte[]
     * @throws Exception
     */
    public static byte[] getByte(File file) {
        byte[] bytes = null;
        if (file != null) {
            try {
                InputStream is = new FileInputStream(file);
                int length = (int) file.length();
                if (length > Integer.MAX_VALUE)   //当文件的长度超过了int的最大值
                {
                    System.out.println("this file is max ");
                    return null;
                }
                bytes = new byte[length];
                int offset = 0;
                int numRead = 0;
                while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                    offset += numRead;
                }
                //如果得到的字节长度和file实际的长度不一致就可能出错了
                if (offset < bytes.length) {
                    System.out.println("file length is error");
                    return null;
                }
                is.close();
            } catch (Exception ex) {

            }
        }
        return bytes;
    }
}
