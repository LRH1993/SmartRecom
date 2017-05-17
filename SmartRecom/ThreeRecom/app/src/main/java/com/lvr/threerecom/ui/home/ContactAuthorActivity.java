package com.lvr.threerecom.ui.home;

import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lvr.threerecom.R;
import com.lvr.threerecom.base.BaseActivity;

import butterknife.BindView;

/**
 * Created by lvr on 2017/5/17.
 */

public class ContactAuthorActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;


    @Override
    public int getLayoutId() {
        return R.layout.activity_contact_author;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        mToolbar.setTitle("联系作者");
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
