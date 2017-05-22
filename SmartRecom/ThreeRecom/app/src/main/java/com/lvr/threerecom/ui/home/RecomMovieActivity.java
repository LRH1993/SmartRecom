package com.lvr.threerecom.ui.home;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnLoadMoreListener;
import com.aspsine.irecyclerview.OnRefreshListener;
import com.lvr.threerecom.R;
import com.lvr.threerecom.adapter.MovieAdapter;
import com.lvr.threerecom.anims.LandingAnimator;
import com.lvr.threerecom.anims.ScaleInAnimationAdapter;
import com.lvr.threerecom.app.AppConstantValue;
import com.lvr.threerecom.base.BaseActivity;
import com.lvr.threerecom.bean.MovieInfo;
import com.lvr.threerecom.ui.MainActivity;
import com.lvr.threerecom.ui.home.presenter.impl.RecomMoviePresenterImpl;
import com.lvr.threerecom.ui.home.view.RecomMovieView;
import com.lvr.threerecom.ui.movie.MovieDetailActivity;
import com.lvr.threerecom.widget.LoadMoreFooterView;
import com.lvr.threerecom.widget.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by lvr on 2017/5/19.
 * 暂时并不是个性化推荐
 */

public class RecomMovieActivity extends BaseActivity implements RecomMovieView,OnRefreshListener,OnLoadMoreListener {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.irv_movie)
    IRecyclerView mIrvMovie;
    private String type ="romance";
    private RecomMoviePresenterImpl mPresenter;
    private LoadMoreFooterView mFooterView;
    private MovieAdapter mMovieAdapter;
    private List<MovieInfo> mList = new ArrayList<>();
    private Context mContext;
    private int startPage = 0;
    private boolean isFirst;
    @Override
    public int getLayoutId() {
        return R.layout.activity_recom_movie;
    }

    @Override
    public void initPresenter() {
        mPresenter = new RecomMoviePresenterImpl(this);
    }

    @Override
    public void initView() {
        mContext =this;
        mToolbar.setTitle("电影推荐");
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setRecyclerView();
        mFooterView = (LoadMoreFooterView) mIrvMovie.getLoadMoreFooterView();
        mPresenter.requestMovieByType(AppConstantValue.PAGE_SIZE, startPage, AppConstantValue.TYPE_GENRES, type);
        LoadingDialog.showDialogForLoading(RecomMovieActivity.this);
    }

    private void setRecyclerView() {
        mMovieAdapter = new MovieAdapter(mContext,mList);
        mMovieAdapter.setOnItemClickListenr(new MovieAdapter.onItemClickListenr() {
            @Override
            public void onItemClick(int position, ImageView imageView) {
                Intent intent = new Intent(RecomMovieActivity.this, MovieDetailActivity.class);
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
        });
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        manager.setItemPrefetchEnabled(false);
        mIrvMovie.setLayoutManager(manager);
        mIrvMovie.setOnRefreshListener(this);
        mIrvMovie.setOnLoadMoreListener(this);
        mIrvMovie.setItemAnimator(new LandingAnimator());
        mIrvMovie.setIAdapter(new ScaleInAnimationAdapter(mMovieAdapter));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void returnMovieList(List<MovieInfo> movieInfoList) {
        if(!isFirst) {
            isFirst = true;
            LoadingDialog.cancelDialogForLoading();
        }
        if (movieInfoList.size() != 0) {
            if (startPage == 0) {
                //刷新请求
                mIrvMovie.setRefreshing(false);
                mList.clear();
                mList.addAll(movieInfoList);
                System.out.println("初次加载数据集大小："+mList.size());
                mMovieAdapter.notifyDataSetChanged();
            } else {
                //加载更多
                mList.addAll(movieInfoList);
                System.out.println("数据集大小："+mList.size());
                mMovieAdapter.notifyDataSetChanged();
            }
        } else {
            System.out.println("数据集中数据条目："+movieInfoList.size());
            startPage--;
            mFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
        }
    }

    @Override
    public void onLoadMore() {
        startPage++;
        mPresenter.requestMovieByType(AppConstantValue.PAGE_SIZE, startPage, AppConstantValue.TYPE_GENRES, type);
        mFooterView.setStatus(LoadMoreFooterView.Status.LOADING);
    }

    @Override
    public void onRefresh() {
        startPage = 0;
        mIrvMovie.setRefreshing(true);
        mPresenter.requestMovieByType(AppConstantValue.PAGE_SIZE, startPage, AppConstantValue.TYPE_GENRES, type);
        mFooterView.setStatus(LoadMoreFooterView.Status.GONE);
    }
}
