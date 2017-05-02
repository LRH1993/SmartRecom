package com.lvr.threerecom.ui.movie;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.lvr.threerecom.R;
import com.lvr.threerecom.base.BaseActivity;

import butterknife.BindView;

/**
 * Created by lvr on 2017/4/23.
 */

public class MoviePlayActivity extends BaseActivity {
    @BindView(R.id.wb_movie)
    WebView mWbMovie;
    private String mUrl;
    private ProgressDialog mProgressDlg;

    @Override
    public int getLayoutId() {
        return R.layout.activity_movie_play;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mUrl = (String) extras.get("url");
        }
        mProgressDlg = new ProgressDialog(mContext);
        setWebView();

    }

    private void setWebView() {
        mWbMovie.getSettings().setJavaScriptEnabled(true);
        mWbMovie.loadUrl(mUrl);
        mWbMovie.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (!mProgressDlg.isShowing()) {
                    mProgressDlg.show();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mProgressDlg.isShowing()) {
                    mProgressDlg.dismiss();
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                // 加载某些网站的时候会报:ERR_CONNECTION_REFUSED,因此需要在这里取消进度条的显示
                Toast.makeText(MoviePlayActivity.this, "error", Toast.LENGTH_SHORT).show();
                if (mProgressDlg.isShowing()) {
                    mProgressDlg.dismiss();
                }
            }
        });
        mWbMovie.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress <= 90) {
                    mProgressDlg.setProgress(newProgress);
                } else {
                    mProgressDlg.dismiss();
                }
            }
        });
    }


}
