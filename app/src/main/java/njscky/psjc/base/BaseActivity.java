package njscky.psjc.base;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import njscky.psjc.R;

public class BaseActivity extends AppCompatActivity {

    protected AlertDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getClass().getSimpleName(), "onCreate: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(getClass().getSimpleName(), "onDestroy: ");
    }

    protected void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void toast(@StringRes int msgRes) {
        Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
    }

    public void showLoading() {
        if (loadingDialog == null) {
            loadingDialog = new AlertDialog.Builder(this)
                    .setView(R.layout.layout_loading)
                    .setCancelable(false)
                    .create();
        }

        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    public void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}
