package com.lvr.threerecom.ui.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatButton;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lvr.threerecom.R;
import com.lvr.threerecom.app.AppApplication;
import com.lvr.threerecom.app.AppConstantValue;
import com.lvr.threerecom.base.BaseActivity;
import com.lvr.threerecom.bean.LoginBean;
import com.lvr.threerecom.ui.login.presenter.impl.LogInPresenterImpl;
import com.lvr.threerecom.ui.login.view.LogInView;
import com.lvr.threerecom.utils.SPUtils;
import com.lvr.threerecom.utils.StatusBarSetting;

import butterknife.BindView;
import de.greenrobot.event.EventBus;

/**
 * Created by lvr on 2017/4/23.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener,LogInView {
    private static final String TAG = "LoginActivity";

    @BindView(R.id.input_email)
    EditText mInputEmail;
    @BindView(R.id.input_password)
    EditText mInputPassword;
    @BindView(R.id.btn_login)
    AppCompatButton mBtnLogin;
    @BindView(R.id.sv_root)
    ScrollView mSvRoot;
    @BindView(R.id.tv_signup)
    TextView mTvSignup;
    private Context mContext;
    private int radius = 25;
    private LogInPresenterImpl mPresenter;
    private ProgressDialog mProgressDialog;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initPresenter() {
        mPresenter = new LogInPresenterImpl(this);
    }

    @Override
    public void initView() {
        StatusBarSetting.setTranslucent(this);
        mContext = this;
        //高斯模糊背景
        applyBlur();
        mTvSignup.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);

    }

    private void applyBlur() {
        Drawable db = getResources().getDrawable(R.drawable.login_bg);
        BitmapDrawable drawable = (BitmapDrawable) db;
        Bitmap bgBitmap = drawable.getBitmap();
        //处理得到模糊效果的图
        RenderScript renderScript = RenderScript.create(mContext);
        Log.i(TAG, "scale size:" + bgBitmap.getWidth() + "*" + bgBitmap.getHeight());
        // Allocate memory for Renderscript to work with
        final Allocation input = Allocation.createFromBitmap(renderScript, bgBitmap);
        final Allocation output = Allocation.createTyped(renderScript, input.getType());
        // Load up an instance of the specific script that we want to use.
        ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        scriptIntrinsicBlur.setInput(input);
        // Set the blur radius
        scriptIntrinsicBlur.setRadius(radius);
        // Start the ScriptIntrinisicBlur
        scriptIntrinsicBlur.forEach(output);
        // Copy the output to the blurred bitmap
        output.copyTo(bgBitmap);
        renderScript.destroy();
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bgBitmap);
        mSvRoot.setBackground(bitmapDrawable);

    }

    /**
     * 入口
     *
     * @param activity
     */
    public static void startAction(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login: {
                login();
                break;
            }
            case R.id.tv_signup: {
                SignUpActivity.startAction(LoginActivity.this,AppConstantValue.REQUEST_SIGNUP);
                break;
            }
        }
    }

    /**
     * 登录逻辑
     */
    private void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        mBtnLogin.setEnabled(false);

        mProgressDialog = new ProgressDialog(LoginActivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("正在验证...");
        mProgressDialog.show();

        String userword = mInputEmail.getText().toString();
        String password = mInputPassword.getText().toString();

        mPresenter.requestLogIn(userword,password);

    }

    /**
     * 邮箱，密码是否格式正确
     * @return
     */
    public boolean validate() {
        boolean valid = true;

        String email = mInputEmail.getText().toString();
        String password = mInputPassword.getText().toString();

        if (email.isEmpty() || !Patterns.PHONE.matcher(email).matches()) {
            mInputEmail.setError("请输入有效的手机号码");
            valid = false;
        } else {
            mInputEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            mInputPassword.setError("密码长度在4-10位之间");
            valid = false;
        } else {
            mInputPassword.setError(null);
        }

        return valid;
    }

    /**
     * 格式错误导致登录失败逻辑
     */
    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "登录失败", Toast.LENGTH_SHORT).show();

        mBtnLogin.setEnabled(true);
    }

    /**
     * 登录成功
     */
    public void onLoginSuccess() {
        mBtnLogin.setEnabled(true);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppConstantValue.REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                //注册成功更新UI
                LoginBean loginBean = new LoginBean();
                loginBean.setUserid(SPUtils.getSharedStringData(AppApplication.getAppContext(),"userid"));
                EventBus.getDefault().post(loginBean);
                this.finish();
            }
        }
    }

    @Override
    public void returnLogInResult(boolean result) {
        if(result){
            mProgressDialog.dismiss();
            onLoginSuccess();
        }else{
            mProgressDialog.dismiss();
            Toast.makeText(getBaseContext(), "登录失败,用户名不存在或密码错误", Toast.LENGTH_SHORT).show();
            mBtnLogin.setEnabled(true);
        }
    }
}
