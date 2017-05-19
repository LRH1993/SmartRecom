package com.lvr.threerecom.ui.home;

import android.content.Intent;

import com.lvr.threerecom.R;
import com.lvr.threerecom.base.BaseActivity;
import com.lvr.threerecom.ui.MainActivity;

/**
 * Created by lvr on 2017/5/19.
 */

public class NotificationShowActivity extends BaseActivity{
    @Override
    public int getLayoutId() {
        return R.layout.activity_notification_show;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
