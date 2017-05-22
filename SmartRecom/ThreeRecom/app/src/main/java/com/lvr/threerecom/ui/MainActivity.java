package com.lvr.threerecom.ui;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnRefreshListener;
import com.lvr.threerecom.R;
import com.lvr.threerecom.adapter.MainAdapter;
import com.lvr.threerecom.anims.LandingAnimator;
import com.lvr.threerecom.anims.ScaleInAnimationAdapter;
import com.lvr.threerecom.app.AppApplication;
import com.lvr.threerecom.app.AppConstantValue;
import com.lvr.threerecom.base.BaseActivity;
import com.lvr.threerecom.bean.LoginBean;
import com.lvr.threerecom.bean.MovieInfo;
import com.lvr.threerecom.ui.home.AboutActivity;
import com.lvr.threerecom.ui.home.MyInformationActivity;
import com.lvr.threerecom.ui.home.presenter.impl.MainPresenterImpl;
import com.lvr.threerecom.ui.home.view.MainView;
import com.lvr.threerecom.ui.login.LoginActivity;
import com.lvr.threerecom.ui.movie.MovieDetailActivity;
import com.lvr.threerecom.ui.movie.MovieDisplayActivity;
import com.lvr.threerecom.ui.music.MusicActivity;
import com.lvr.threerecom.utils.BitmapUtils;
import com.lvr.threerecom.utils.ImageLoaderUtils;
import com.lvr.threerecom.utils.SPUtils;
import com.lvr.threerecom.utils.StatusBarSetting;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

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
    private AlertDialog mDialog;
    private ImageView mIv_photo;
    private TextView mMTv_login;
    private Intent mIntent;

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
        initLoginState();
        initSensorService();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        updateLoginUI();

    }

    /**
     * 判断状态为登录后 更新UI
     */
    private void updateLoginUI() {
        Boolean isLogin = SPUtils.getSharedBooleanData(AppApplication.getAppContext(), "isLogin");
        if(isLogin){
            String nickname = SPUtils.getSharedStringData(AppApplication.getAppContext(), "nickname");
            String userid = SPUtils.getSharedStringData(AppApplication.getAppContext(), "userid");
            String age = SPUtils.getSharedStringData(AppApplication.getAppContext(), "age");
            String gender = SPUtils.getSharedStringData(AppApplication.getAppContext(), "gender");
            String movie_preference = SPUtils.getSharedStringData(AppApplication.getAppContext(), "movie_preference");
            String music_preference = SPUtils.getSharedStringData(AppApplication.getAppContext(), "music_preference");
            String url = SPUtils.getSharedStringData(AppApplication.getAppContext(), "photoUrl");
            LoginBean loginBean = new LoginBean();
            if(!nickname.isEmpty()){
                loginBean.setNickname(nickname);
            }
            if(!age.isEmpty()){
                loginBean.setAge(Integer.parseInt(age));
            }
            if(!movie_preference.isEmpty()){
                loginBean.setMovie_preference(movie_preference);
            }
            if(!music_preference.isEmpty()){
                loginBean.setMusic_preference(music_preference);
            }
            if(!userid.isEmpty()){
                loginBean.setUserid(userid);
            }
            if(!gender.isEmpty()){
                loginBean.setSex(gender);
            }
            if(!url.isEmpty()){
                loginBean.setUser_photo_url(url);
            }
            EventBus.getDefault().post(loginBean);
        }
    }

    /**
     * 初始化登录状态 默认登陆保持7天
     */
    private void initLoginState() {
        Boolean isLogin = SPUtils.getSharedBooleanData(AppApplication.getAppContext(), "isLogin");
        if(isLogin){
            //查看登录日期有没有保持7天
            long date = SPUtils.getSharedlongData(AppApplication.getAppContext(), "loginDate");
            if(System.currentTimeMillis()-date>7*24*3600*1000){
                SPUtils.setSharedBooleanData(AppApplication.getAppContext(), "isLogin",false);
            }
        }
    }

    /**
     * 开启传感器采集数据的远程进程
     */
    private void initSensorService() {
        //已经登录后再开启采集数据
        if(SPUtils.getSharedBooleanData(AppApplication.getAppContext(), "isLogin")){
            mIntent = new Intent();
            mIntent.setAction("com.sensor.service");
            mIntent.setPackage(getPackageName());
            startService(mIntent);
        }
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
        mIv_photo = (ImageView) headerView.findViewById(R.id.iv_user_photo);
        mMTv_login = (TextView) headerView.findViewById(R.id.tv_login);
        mNavigation.setNavigationItemSelectedListener(this);
        setHomeItemState();
        login();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setHomeItemState();
        //在登陆后的状态下，再次开启采集数据
        initSensorService();
    }

    /**
     * 设置首页默认被选中的状态
     */
    private void setHomeItemState() {
        Menu menu = mNavigation.getMenu();
        MenuItem item = menu.getItem(0);
        //更多中  特殊情况  取消选中状态
        menu.getItem(5).getSubMenu().getItem(0).setChecked(false);
        menu.getItem(5).getSubMenu().getItem(1).setChecked(false);
        item.setChecked(true);
    }

    /**
     * 接收到登录成功的消息
     * @param bean
     */
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onLogInEvent(LoginBean bean) {
        String url = bean.getUser_photo_url();
        if(url!=null&&!url.isEmpty()){
            //更新头像
            if(mIv_photo!=null){
                if(url.startsWith("http")){
                    //网络图片
                    ImageLoaderUtils.displayRound(MainActivity.this,mIv_photo,url);
                }else{
                    //本地图片
                    final File file = new File(url);
                    mIv_photo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            mIv_photo.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int mWidth = mIv_photo.getMeasuredWidth();
                            int mHeight = mIv_photo.getMeasuredHeight();
                            Bitmap bitmap = BitmapUtils.decodeBitmapFromFile(file, mWidth, mHeight);
                            if (bitmap != null) {
                                //检查是否有被旋转，并进行纠正
                                int degree = BitmapUtils.getBitmapDegree(file.getAbsolutePath());
                                if (degree != 0) {
                                    bitmap = BitmapUtils.rotateBitmapByDegree(bitmap, degree);
                                }
                                mIv_photo.setImageBitmap(bitmap);
                            }

                        }
                    });
                }
                System.out.println("图片url:"+url);
            }
        }
        String nickname = bean.getNickname();
        if(nickname!=null&&!nickname.isEmpty()){
            if(mMTv_login!=null){
                mMTv_login.setText(nickname);
            }
        }else{
            if(mMTv_login!=null){
                mMTv_login.setText("已登录，完善信息为你提供更多服务");
            }
        }
        mIv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到完善信息页面
                Intent intent = new Intent(MainActivity.this, MyInformationActivity.class);
                startActivity(intent);
            }
        });
    }
    /**
     * 跳转到登录界面
     *
     */
    private void login() {

        mIv_photo.setOnClickListener(new View.OnClickListener() {
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
            case R.id.mn_information:{
                if(SPUtils.getSharedBooleanData(AppApplication.getAppContext(),"isLogin")){
                    //已经登录了
                    Intent intent = new Intent(MainActivity.this, MyInformationActivity.class);
                    startActivity(intent);

                }else{
                    //提示还未登录
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("提示");
                    builder.setMessage("还未登录！\n\n登录后才能完善个人信息！");
                    builder.setPositiveButton("知道", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mDialog.dismiss();
                        }
                    });
                    mDialog = builder.create();
                    mDialog.show();
                }

                break;
            }
            case R.id.mn_about:{
                //跳转到关于页面
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.mn_out:{
                //注销登录
                if(SPUtils.getSharedBooleanData(AppApplication.getAppContext(),"isLogin")){
                    //1.清除SharedPreference中的内容
                    SPUtils.setSharedBooleanData(AppApplication.getAppContext(),"isLogin",false);
                    SPUtils.setSharedStringData(AppApplication.getAppContext(), "nickname",null);
                    SPUtils.setSharedStringData(AppApplication.getAppContext(), "age",null);
                    SPUtils.setSharedStringData(AppApplication.getAppContext(), "gender",null);
                    SPUtils.setSharedStringData(AppApplication.getAppContext(), "movie_preference",null);
                    SPUtils.setSharedStringData(AppApplication.getAppContext(), "music_preference",null);
                    SPUtils.setSharedlongData(AppApplication.getAppContext(),"loginDate",0);
                    SPUtils.setSharedStringData(AppApplication.getAppContext(),"userid",null);
                    SPUtils.setSharedStringData(AppApplication.getAppContext(),"photoUrl",null);
                    //2.关闭服务
                    if(mIntent!=null){
                        stopService(mIntent);
                    }
                    //3.页面回显
                    mMTv_login.setText("点击头像登录,开启更多功能");
                    mIv_photo.setImageResource(R.drawable.nav_photo);
                    mIv_photo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LoginActivity.startAction(MainActivity.this);
                        }
                    });
                    Toast.makeText(MainActivity.this,"注销成功",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,"还未登录",Toast.LENGTH_SHORT).show();
                }


                break;
            }
            case R.id.mn_guess:{
                Toast.makeText(MainActivity.this,"该功能还未实现，敬请期待",Toast.LENGTH_SHORT).show();
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
