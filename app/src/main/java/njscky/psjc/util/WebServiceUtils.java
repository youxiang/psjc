package njscky.psjc.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.os.Handler;
import android.os.Message;

public class WebServiceUtils {
	//public static final String WEB_SERVER_URL = "http://10.10.31.6/Webservices/GXXJWebService/Service1.asmx";
	//public static final String WEB_SERVER_URL = "http://112.25.235.28/GXXJWebService/Service1.asmx";
	public static final String WEB_SERVER_URL = "http://58.213.48.109/GXXJWebService1/Service1.asmx";
	private static final ExecutorService executorService = Executors
			.newFixedThreadPool(3);
	private static final String NAMESPACE = "http://tempuri.org/";

	public static void callWebService(String url, final String methodName,
			HashMap<String, String> properties,
			final WebServiceCallBack webServiceCallBack) {

		final HttpTransportSE httpTransportSE = new HttpTransportSE(url);
		SoapObject soapObject = new SoapObject(NAMESPACE, methodName);

		if (properties != null) {
			for (Iterator<Map.Entry<String, String>> it = properties.entrySet()
					.iterator(); it.hasNext();) {
				Map.Entry<String, String> entry = it.next();
				soapObject.addProperty(entry.getKey(), entry.getValue());
			}
		}

		final SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER12);

		soapEnvelope.setOutputSoapObject(soapObject);
		soapEnvelope.dotNet = true;
		httpTransportSE.debug = true;

		final Handler mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				webServiceCallBack.callBack((SoapObject) msg.obj);
			}

		};


		executorService.submit(new Runnable() {

			@Override
			public void run() {
				SoapObject resultSoapObject = null;
				try {
					httpTransportSE.call(NAMESPACE + methodName, soapEnvelope);
					if (soapEnvelope.getResponse() != null) {

						resultSoapObject = (SoapObject) soapEnvelope.bodyIn;
					}

				} catch (IOException e) {
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				} finally {
					mHandler.sendMessage(mHandler.obtainMessage(0,
							resultSoapObject));
				}
			}
		});
	}

	public interface WebServiceCallBack {
		public void callBack(SoapObject result);
	}

}
