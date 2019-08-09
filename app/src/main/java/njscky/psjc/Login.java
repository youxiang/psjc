package njscky.psjc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import njscky.psjc.util.WebServiceUtils;
import njscky.psjc.util.ProgressDialogUtils;
import org.ksoap2.serialization.SoapObject;

import java.util.HashMap;


/**
 * Created by Administrator on 2015/4/1.
 */
public class Login extends Activity implements View.OnClickListener {
    private Button btclose;
    private Button btok;
    private EditText txtUserName;
    private EditText txtPsw;
    private SharedPreferences sp;
    private CheckBox remember;
    private CheckBox autologin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        CreatView();
    }

    void CreatView() {
        btok = (Button) findViewById(R.id.loginok);
        btok.setOnClickListener(this);
        btclose = (Button) findViewById(R.id.loginclose);
        btclose.setOnClickListener(this);
        txtUserName=(EditText)findViewById(R.id.editUser);
        txtPsw=(EditText)findViewById(R.id.editPSW);
        remember = (CheckBox) findViewById(R.id.remember);
        autologin = (CheckBox) findViewById(R.id.autologin);
        sp = getSharedPreferences("userInfo", 0);
        String name=sp.getString("USER_NAME", "");
        String pass =sp.getString("PASSWORD", "");
        String usercode = sp.getString("USERCODE", "");
        boolean choseRemember =sp.getBoolean("remember", false);
        boolean choseAutoLogin =sp.getBoolean("autologin", false);

        if(choseRemember){
            txtUserName.setText(name);
            txtPsw.setText(pass);
            remember.setChecked(true);
        }
        if(choseAutoLogin){
            autologin.setChecked(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginclose:
                this.finish();

                break;
            case R.id.loginok:

                String strUserName=txtUserName.getText().toString().trim();
                String strPsw=txtPsw.getText().toString().trim();

                if(strUserName.equals("")) {
                    Toast.makeText(this, "请输入账号！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(strPsw.equals("")) {
                    Toast.makeText(this, "请输入密码！", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    HashMap<String, String> properties = new HashMap<String, String>();
                    properties.put("UserName", strUserName);
                    properties.put("UserPSW", strPsw);
                    WebServiceUtils.callWebService(WebServiceUtils.WEB_SERVER_URL, "UserLogin", properties, new WebServiceUtils.WebServiceCallBack() {

                        @Override
                        public void callBack(SoapObject result) {
                            ProgressDialogUtils.dismissProgressDialog();
                            if (result.toString().contains("登录失败") || result.toString().contains("账号密码不正确")) {
                                Toast.makeText(Login.this, result.toString(), Toast.LENGTH_SHORT).show();
                            } else {
                                MainActivity.strUserCode = result.toString().substring(result.toString().indexOf("=") + 1, result.toString().indexOf(";"));
                                SharedPreferences.Editor editor =sp.edit();
                                editor.putString("USER_NAME", txtUserName.getText().toString().trim());
                                editor.putString("PASSWORD", txtPsw.getText().toString().trim());
                                editor.putString("USERCODE", MainActivity.strUserCode);
                                if(remember.isChecked()){
                                    editor.putBoolean("remember", true);
                                }else{
                                    editor.putBoolean("remember", false);
                                }
                                if(autologin.isChecked()){
                                    editor.putBoolean("autologin", true);
                                }else{
                                    editor.putBoolean("autologin", false);
                                }
                                editor.commit();

                            }
                        }
                    });
                }
                catch(Exception ex){}
                this.finish();
                break;
        }
    }
}
