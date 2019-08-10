package njscky.psjc.service;

import android.text.TextUtils;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executor;

import njscky.psjc.util.AppExecutors;

public class WebServiceManager {

    private static final String TAG = WebServiceManager.class.getSimpleName();

    private static final String SERVER_URL = "http://58.213.48.109/GXXJWebService1/Service1.asmx";
    private static final String NAMESPACE = "http://tempuri.org/";

    private static final String METHOD_LOGIN = "UserLogin";
    private static final String RESPONSE_LOGIN = "UserLoginResponse";
    private static final String PROPERTY_LOGIN = "UserLoginResult";
    private static volatile WebServiceManager instance;
    Executor bgThreadExecutor;
    Executor mainThreadExecutor;
    private HttpTransportSE httpTransportSE;

    private WebServiceManager() {
        this(AppExecutors.getInstance().networkIO(), AppExecutors.getInstance().mainThread());
    }

    private WebServiceManager(Executor backgroundThreadExecutor, Executor mainThreadExecutor) {
        this.bgThreadExecutor = backgroundThreadExecutor;
        this.mainThreadExecutor = mainThreadExecutor;

        httpTransportSE = new HttpTransportSE(SERVER_URL);
        httpTransportSE.debug = true;
    }

    public static WebServiceManager getInstance() {
        if (instance == null) {
            synchronized (WebServiceManager.class) {
                if (instance == null) {
                    instance = new WebServiceManager();
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
                    String result = response.getPropertyCount()> 0 ? response.getPropertyAsString(0) : null;

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

    public interface Callback {
        void onResponse(SoapObject response);
    }

    public interface LoginCallback {
        void onUserCode(String userCode);

        void onError(String msg);
    }
}
