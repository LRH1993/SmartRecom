package com.lvr.threerecom.ui.home;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aspsine.irecyclerview.IRecyclerView;
import com.lvr.threerecom.R;
import com.lvr.threerecom.adapter.InformationAdapter;
import com.lvr.threerecom.base.BaseActivity;
import com.lvr.threerecom.bean.InformationBean;
import com.lvr.threerecom.ui.home.presenter.impl.InformationPresenterImpl;
import com.lvr.threerecom.ui.home.view.InformationView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by lvr on 2017/5/15.
 */

public class MyInformationActivity extends BaseActivity implements InformationView,InformationAdapter.onItemClickListenr {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.irv_information)
    IRecyclerView mIrvInformation;
    private ActionBar mBar;
    private InformationPresenterImpl mPresenter;
    private List<InformationBean> mList = new ArrayList<>();
    private InformationAdapter mAdapter;
    private Context mContext;


    @Override
    public int getLayoutId() {
        return R.layout.activity_my_information;
    }

    @Override
    public void initPresenter() {
        mPresenter = new InformationPresenterImpl(this);
    }

    @Override
    public void initView() {
        mContext = this;
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBar = getSupportActionBar();
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        mBar.setTitle("完善个人信息");
        setRecycleView();
        mPresenter.requestInformation();

    }

    private void setRecycleView() {
        mAdapter = new InformationAdapter(mContext,mList);
        mAdapter.setOnItemClickListenr(this);
        mIrvInformation.setLayoutManager(new LinearLayoutManager(mContext));
        mIrvInformation.setItemAnimator(new DefaultItemAnimator());
        mIrvInformation.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL));
        mIrvInformation.setIAdapter(mAdapter);
    }


    @Override
    public void returnInformation(List<InformationBean> informationBeanList) {
        mList.addAll(informationBeanList);
        mAdapter.notifyDataSetChanged();
    }



    @Override
    public void onItemClick(int position) {
        if(position==0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("选择图片");
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.item_photo_select, (ViewGroup) findViewById(R.id.ll_root),false);
            builder.setView(view,100,30,100,30);
            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }
}
