package njscky.psjc.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.ksoap2.serialization.SoapObject;

import njscky.psjc.R;
import njscky.psjc.base.BaseActivity;
import njscky.psjc.local.AppPrefHelper;
import njscky.psjc.service.WebServiceManager;


/**
 * Created by Administrator on 2015/4/1.
 */
public class LoginActivity extends BaseActivity {
    private Button btnClose;
    private Button btnOk;
    private EditText edUserName;
    private EditText etPassword;
    private CheckBox cbRemember;
    private CheckBox cbAutoLogin;

    private AppPrefHelper prefHelper;
    private WebServiceManager webServiceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        prefHelper = AppPrefHelper.get(this);
        webServiceManager = WebServiceManager.getInstance();
        initView();
    }

    private void initView() {
        btnOk = findViewById(R.id.loginok);
        btnClose = findViewById(R.id.loginclose);
        edUserName = findViewById(R.id.editUser);
        etPassword = findViewById(R.id.editPSW);
        cbRemember = findViewById(R.id.remember);
        cbAutoLogin = findViewById(R.id.autologin);

        String userName = prefHelper.getUserName();
        String password = prefHelper.getPassword();
        boolean isRemember = prefHelper.isRemember();
        boolean isAutoLogin = prefHelper.isAutoLogin();

        cbRemember.setChecked(isRemember);
        cbAutoLogin.setChecked(isAutoLogin);
        edUserName.setText(isRemember ? userName : "");
        etPassword.setText(isRemember ? password : "");

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogin();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void startLogin() {
        final String userName = edUserName.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, R.string.username_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.password_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        webServiceManager.userLogin(userName, password, new WebServiceManager.LoginCallback() {
            @Override
            public void onUserCode(String userCode) {
                prefHelper.saveUserCode(userCode);
                prefHelper.saveUserName(userName);
                prefHelper.savePassword(password);
                prefHelper.setRemember(cbRemember.isChecked());
                prefHelper.setAutoLogin(cbAutoLogin.isChecked());

                Toast.makeText(LoginActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getUserNumber(SoapObject result) {
        return result.toString().substring(result.toString().indexOf("=") + 1, result.toString().indexOf(";"));
    }

    private boolean isLoginError(SoapObject result) {
        return result.toString().contains("登录失败") || result.toString().contains("账号密码不正确");
    }

}