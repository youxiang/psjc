package njscky.psjc.local;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPrefHelper {

    private static final String TAG = AppPrefHelper.class.getSimpleName();
    private static final String K_USER_CODE = "USERCODE";
    private static final String K_USERNAME = "USER_NAME";
    private static final String K_PASSWORD = "PASSWORD";
    private static final String K_REMEMBER = "remember";
    private static final String K_AUTO_LOGIN = "autologin";

    private static volatile AppPrefHelper instance;

    private final Context context;
    private SharedPreferences sp;

    private AppPrefHelper(Context context) {
        this.context = context.getApplicationContext();
        sp = this.context.getSharedPreferences(this.context.getPackageName(), Context.MODE_PRIVATE);
    }

    public static AppPrefHelper get(Context context) {
        if (instance == null) {
            synchronized (AppPrefHelper.class) {
                if (instance == null) {
                    instance = new AppPrefHelper(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public String getUserName() {
        return sp.getString(K_USERNAME, "");
    }

    public void saveUserName(String userName) {
        sp.edit().putString(K_USERNAME, userName).apply();
    }

    public String getPassword() {
        return sp.getString(K_PASSWORD, "");
    }

    public void savePassword(String password) {
        sp.edit().putString(K_PASSWORD, password).apply();
    }

    public boolean isRemember() {
        return sp.getBoolean(K_REMEMBER, false);
    }

    public void setRemember(boolean remember) {
        sp.edit().putBoolean(K_REMEMBER, remember).apply();
    }


    public boolean isAutoLogin() {
        return sp.getBoolean(K_AUTO_LOGIN, false);
    }

    public void setAutoLogin(boolean autoLogin) {
        sp.edit().putBoolean(K_AUTO_LOGIN, autoLogin).apply();
    }


    public void saveUserCode(String userCode) {
        sp.edit().putString(K_USER_CODE, userCode).apply();
    }

    public String getUserCode() {
        return sp.getString(K_USER_CODE, "");
    }
}
