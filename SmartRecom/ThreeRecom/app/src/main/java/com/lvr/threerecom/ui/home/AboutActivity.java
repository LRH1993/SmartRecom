package com.lvr.threerecom.ui.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lvr.threerecom.R;
import com.lvr.threerecom.base.BaseActivity;

import butterknife.BindView;

/**
 * Created by lvr on 2017/5/17.
 */

public class AboutActivity extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rl_rating)
    RelativeLayout mRlRating;
    @BindView(R.id.rl_version)
    RelativeLayout mRlVersion;
    @BindView(R.id.rl_introduction)
    RelativeLayout mRlIntroduction;
    @BindView(R.id.rl_permission)
    RelativeLayout mRlPermission;
    @BindView(R.id.rl_author)
    RelativeLayout mRlAuthor;
    private ActionBar mBar;

    @Override
    public int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        mToolbar.setTitle("关于SmartRecom");
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRlAuthor.setOnClickListener(this);
        mRlIntroduction.setOnClickListener(this);
        mRlPermission.setOnClickListener(this);
        mRlRating.setOnClickListener(this);
        mRlVersion.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_author:{
                //联系作者
                Intent intent = new Intent(AboutActivity.this,ContactAuthorActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.rl_introduction:{
                //功能介绍
                Intent intent = new Intent(AboutActivity.this,IntroductionActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.rl_permission:{
                //开源许可
                Intent intent = new Intent(AboutActivity.this,OpenSourceActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.rl_version:{
                //版本检查
                final ProgressDialog progressDialog = new ProgressDialog(AboutActivity.this);

//                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("正在检查新版本");
                progressDialog.show();
                // TODO: 2017/5/17 应该指定网络访问最新版本逻辑 
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(mContext,"你安装的已经是最新版本了",Toast.LENGTH_SHORT).show();
                            }
                        }, 1000);
                break;
            }
            case R.id.rl_rating:{
                //去评分
                Toast.makeText(AboutActivity.this,"稍候会实现，还未发布",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
