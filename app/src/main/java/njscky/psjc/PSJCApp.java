package njscky.psjc;

import android.app.Application;
import android.util.Log;

public class PSJCApp extends Application {

    private static final String TAG = PSJCApp.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
    }
}
