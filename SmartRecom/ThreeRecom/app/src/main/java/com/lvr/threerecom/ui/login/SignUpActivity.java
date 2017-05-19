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
import com.lvr.threerecom.base.BaseActivity;
import com.lvr.threerecom.ui.login.presenter.impl.SignUpPresenterImpl;
import com.lvr.threerecom.ui.login.view.SignUpView;
import com.lvr.threerecom.utils.StatusBarSetting;

import butterknife.BindView;

/**
 * Created by lvr on 2017/4/23.
 */

public class SignUpActivity extends BaseActivity implements View.OnClickListener,SignUpView{
    private static final String TAG = "SignUpActivity";
    @BindView(R.id.input_email)
    EditText mInputEmail;
    @BindView(R.id.input_password)
    EditText mInputPassword;
    @BindView(R.id.btn_signup)
    AppCompatButton mBtnSignup;
    @BindView(R.id.link_login)
    TextView mLinkLogin;
    @BindView(R.id.sv_root)
    ScrollView mSvRoot;
    private Context mContext;
    private int radius = 25;
    private SignUpPresenterImpl mPresenter;
    private ProgressDialog mProgressDialog;

    @Override
    public int getLayoutId() {
        return R.layout.activity_signup;
    }

    @Override
    public void initPresenter() {
        mPresenter = new SignUpPresenterImpl(this);
    }

    @Override
    public void initView() {
        StatusBarSetting.setTranslucent(this);
        mContext = this;
        //高斯模糊背景
        applyBlur();
        mBtnSignup.setOnClickListener(this);
        mLinkLogin.setOnClickListener(this);
    }

    /**
     * 入口
     *
     * @param activity
     */
    public static void startAction(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, SignUpActivity.class);
        activity.startActivityForResult(intent, requestCode);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signup: {
                signup();
                break;
            }
            case R.id.link_login: {
                finish();
                break;
            }
        }
    }

    /**
     * 执行注册
     */
    private void signup() {
        if (!validate()) {
            onSignupFailed();
            return;
        }

        mBtnSignup.setEnabled(false);

        mProgressDialog = new ProgressDialog(SignUpActivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("正在创建账户...");
        mProgressDialog.show();

        String userword = mInputEmail.getText().toString();
        String password = mInputPassword.getText().toString();
        mPresenter.requestSignUp(userword,password);
    }

    /**
     * 登录成功
     */
    private void onSignupSuccess() {
        mBtnSignup.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    /**
     * 格式不正确导致注册失败
     */
    private void onSignupFailed() {
        Toast.makeText(getBaseContext(), "注册失败", Toast.LENGTH_SHORT).show();

        mBtnSignup.setEnabled(true);
    }

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

    @Override
    public void returnSignUpResult(boolean result) {
        if(result){
            mProgressDialog.dismiss();
            onSignupSuccess();
        }else{
            mProgressDialog.dismiss();
            Toast.makeText(getBaseContext(), "注册失败，用户名已存在", Toast.LENGTH_SHORT).show();

            mBtnSignup.setEnabled(true);
        }
    }
}
