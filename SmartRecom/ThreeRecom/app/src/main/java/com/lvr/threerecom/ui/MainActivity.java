package com.lvr.threerecom.ui;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnRefreshListener;
import com.lvr.threerecom.R;
import com.lvr.threerecom.adapter.MainAdapter;
import com.lvr.threerecom.anims.LandingAnimator;
import com.lvr.threerecom.anims.ScaleInAnimationAdapter;
import com.lvr.threerecom.app.AppConstantValue;
import com.lvr.threerecom.base.BaseActivity;
import com.lvr.threerecom.bean.MovieInfo;
import com.lvr.threerecom.ui.home.presenter.impl.MainPresenterImpl;
import com.lvr.threerecom.ui.home.view.MainView;
import com.lvr.threerecom.ui.login.LoginActivity;
import com.lvr.threerecom.ui.movie.MovieDetailActivity;
import com.lvr.threerecom.ui.movie.MovieDisplayActivity;
import com.lvr.threerecom.ui.music.MusicActivity;
import com.lvr.threerecom.utils.StatusBarSetting;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, MainView, OnRefreshListener, MainAdapter.onItemClickListenr {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.navigation)
    NavigationView mNavigation;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.rv_content)
    IRecyclerView mRvContent;
    private MainAdapter mMainAdapter;
    private List<MovieInfo> mList = new ArrayList<>();
    private MainPresenterImpl mPresenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initPresenter() {
        mPresenter = new MainPresenterImpl(this, this);
    }

    @Override
    public void initView() {
        StatusBarSetting.setColorForDrawerLayout(this, mDrawerLayout, getResources().getColor(R.color.colorPrimary), StatusBarSetting.DEFAULT_STATUS_BAR_ALPHA);
        setToolBar();
        setNavigationView();
        initData();
        mPresenter.requestHotMoviee();
        initSensorService();
    }

    /**
     * 开启传感器采集数据的远程进程
     */
    private void initSensorService() {
        Intent intent = new Intent();
        intent.setAction("com.sensor.service");
        intent.setPackage(getPackageName());
        startService(intent);
    }

    /**
     * 设置RecyclerView相关数据
     */
    private void initData() {
        mMainAdapter = new MainAdapter(mContext, mList);
        GridLayoutManager manager = new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0 || position == 1 || position == mList.size() + 2) {
                    return 2;
                } else {
                    return 1;
                }
            }
        });
        mRvContent.setLayoutManager(manager);
        mRvContent.setOnRefreshListener(this);
        mMainAdapter.setOnItemClickListenr(this);
        mRvContent.setItemAnimator(new LandingAnimator());
        mRvContent.setIAdapter(new ScaleInAnimationAdapter(mMainAdapter));
    }

    private void setNavigationView() {
        //NavigationView初始化
        mNavigation.setItemIconTintList(null);
        View headerView = mNavigation.getHeaderView(0);
        mNavigation.setNavigationItemSelectedListener(this);
        setHomeItemState();
        login(headerView);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setHomeItemState();
    }

    /**
     * 设置首页默认被选中的状态
     */
    private void setHomeItemState() {
        Menu menu = mNavigation.getMenu();
        MenuItem item = menu.getItem(0);
        item.setChecked(true);
    }

    /**
     * 跳转到登录界面
     *
     * @param view
     */
    private void login(View view) {
        ImageView iv_photo = (ImageView) view.findViewById(R.id.iv_user_photo);
        iv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.startAction(MainActivity.this);
            }
        });
    }

    private void setToolBar() {
        mToolbar.setTitle("首页");//设置标题
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        //菜单按钮可用
        actionBar.setHomeButtonEnabled(true);
        //回退按钮可用
        actionBar.setDisplayHomeAsUpEnabled(true);
        //将drawlayout与toolbar绑定在一起
        ActionBarDrawerToggle abdt = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
        abdt.syncState();//初始化状态
        //设置drawlayout的监听事件 打开/关闭
        mDrawerLayout.setDrawerListener(abdt);
        //actionbar中的内容进行初始化
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.mn_home: {
                //不需要做
                break;
            }
            case R.id.mn_movie: {
                MovieDisplayActivity.startAction(MainActivity.this);
                break;
            }
            case R.id.mn_music: {
                MusicActivity.startAction(MainActivity.this);
                break;
            }

        }
        item.setChecked(true);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void returnMovieInfos(List<MovieInfo> movieInfoList) {
        mRvContent.setRefreshing(false);
        mList.clear();
        mList.addAll(movieInfoList);
        mMainAdapter.notifyDataSetChanged();
    }


    @Override
    public void onRefresh() {
        mRvContent.setRefreshing(true);
        mPresenter.requestHotMoviee();
    }

    @Override
    public void onItemClick(int position, ImageView imageView) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra("movie", mList.get(position));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation((Activity) mContext, imageView, AppConstantValue.TRANSITION_IMAGE_MOVIE);
            mContext.startActivity(intent, options.toBundle());
        } else {
            //让新的Activity从一个小的范围扩大到全屏
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeScaleUpAnimation(imageView, imageView.getWidth() / 2, imageView.getHeight() / 2, 0, 0);
            ActivityCompat.startActivity((Activity) mContext, intent, options.toBundle());
        }
    }
}
