package njscky.psjc.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executor;

import njscky.psjc.MainActivity;
import njscky.psjc.activity.ImageFile;
import njscky.psjc.local.AppPrefHelper;
import njscky.psjc.model.PhotoInfo;
import njscky.psjc.util.AppExecutors;
import njscky.psjc.util.GlideApp;
import njscky.psjc.util.Utils;

public class WebServiceManager {

    private static final String TAG = WebServiceManager.class.getSimpleName();

    private static final String SERVER_URL = "http://58.213.48.109/GXXJWebService1/Service1.asmx";
    private static final String NAMESPACE = "http://tempuri.org/";

    private static final String METHOD_LOGIN = "UserLogin";
    private static final String RESPONSE_LOGIN = "UserLoginResponse";
    private static final String PROPERTY_LOGIN = "UserLoginResult";

    // InsertJCPicResponse{InsertJCPicResult=操作成功！; }

    private static final String METHOD_INSERT_JC_PIC = "InsertJCPic";
    private static final String RESPONSE_INSERT_JC_PIC = "InsertJCPicResponse";
    private static final String PROPERTY_INSERT_JC_PIC = "InsertJCPicResult";

    private static volatile WebServiceManager instance;
    Executor diskExecutor;
    Executor bgThreadExecutor;
    Executor mainThreadExecutor;
    private HttpTransportSE httpTransportSE;

    AppPrefHelper appPrefHelper;

    private WebServiceManager(Context context) {
        this(AppPrefHelper.get(context),AppExecutors.getInstance().diskIO(), AppExecutors.getInstance().networkIO(), AppExecutors.getInstance().mainThread());
    }

    private WebServiceManager(AppPrefHelper appPrefHelper, Executor diskExecutor, Executor backgroundThreadExecutor, Executor mainThreadExecutor) {
        this.appPrefHelper = appPrefHelper;
        this.diskExecutor = diskExecutor;
        this.bgThreadExecutor = backgroundThreadExecutor;
        this.mainThreadExecutor = mainThreadExecutor;

        httpTransportSE = new HttpTransportSE(SERVER_URL);
        httpTransportSE.debug = true;
    }

    public static WebServiceManager getInstance(Context context) {
        if (instance == null) {
            synchronized (WebServiceManager.class) {
                if (instance == null) {
                    instance = new WebServiceManager(context);
                }
            }
        }
        return instance;
    }

    public void userLogin(String username, String password, final LoginCallback callback) {
        Map<String, String> properties = new HashMap<>();
        properties.put("UserName", username);
        properties.put("UserPSW", password);

        callWebService(METHOD_LOGIN, properties, new Callback() {
            @Override
            public void onResponse(SoapObject response) {
                if (response != null && TextUtils.equals(response.getName(), RESPONSE_LOGIN)) {
                    String result = response.getPropertyCount() > 0 ? response.getPropertyAsString(0) : null;

                    if (result.contains("登录失败") || result.contains("账号密码不正确")) {
                        callback.onError(result);
                    } else {
                        callback.onUserCode(result);
                    }
                } else {
                    callback.onError("登录异常");
                }
            }
        });
    }


    void callWebService(final String method, final Map<String, String> properties, final Callback callback) {

        Log.i(TAG, "callWebService:>>> " + method);
        Log.i(TAG, "callWebService:>>> " + Utils.getPrintStr(properties));

        bgThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                SoapSerializationEnvelope soapEnvelope = buildSoapEnvelope(method, properties);
                SoapObject result = null;
                try {
                    httpTransportSE.call(NAMESPACE + method, soapEnvelope);

                    if (soapEnvelope.getResponse() != null) {
                        result = (SoapObject) soapEnvelope.bodyIn;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } finally {
                    Log.i(TAG, "run:<<< " + method + ", result: " + result);
                    onResult(callback, result);
                }
            }
        });
    }

    private SoapSerializationEnvelope buildSoapEnvelope(String method, Map<String, String> properties) {
        SoapObject soapObject = new SoapObject(NAMESPACE, method);

        if (properties != null) {
            Iterator<Map.Entry<String, String>> it = properties.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> property = it.next();
                soapObject.addProperty(property.getKey(), property.getValue());
            }
        }
        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        soapEnvelope.setOutputSoapObject(soapObject);
        soapEnvelope.dotNet = true;


        return soapEnvelope;
    }

    void onResult(final Callback callback, final SoapObject result) {
        mainThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onResponse(result);
                }
            }
        });
    }

    public void insertJCPic(
            PhotoInfo photoInfo,
            String reportId,
            String eventCode,
            String picType,
            String remark,
            InsertJCPicCallback callback
    ) {
        diskExecutor.execute(() -> {
            Bitmap bitmap = Utils.getBitmap(photoInfo.path, 480, 800);
            byte[] data = Utils.compressBitmap(bitmap);
            String base64Data = new String(Base64.encode(data, Base64.DEFAULT));

            HashMap<String, String> properties = new HashMap<String, String>();
            properties.put("strJCJBH", reportId);
            properties.put("strUSERCODE", appPrefHelper.getUserCode());
            properties.put("strPICTYPE", picType);
            properties.put("strPICCODE", eventCode);
            properties.put("strBZ", remark);
            properties.put("uploadBuffer", base64Data);

            callWebService("InsertJCPic", properties, response -> {
                // InsertJCPicResponse{InsertJCPicResult=操作成功！; }
                if (response != null && TextUtils.equals(response.getName(), RESPONSE_INSERT_JC_PIC)) {
                    String result = response.getPropertyCount() > 0 ? response.getPropertyAsString(0) : null;

                    if (!result.contains("操作成功！")) {
                        callback.onError(result);
                    } else {
                        callback.onUploadSuccess(result);
                    }
                } else {
                    callback.onError("操作异常");
                }

            });
        });


    }

    public interface Callback {
        void onResponse(SoapObject response);
    }

    public interface LoginCallback {
        void onUserCode(String userCode);

        void onError(String msg);
    }

    public interface InsertJCPicCallback {
        void onUploadSuccess(String result);

        void onError(String result);
    }

}
