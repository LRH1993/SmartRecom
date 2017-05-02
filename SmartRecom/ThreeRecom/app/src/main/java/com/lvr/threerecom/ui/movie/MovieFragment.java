package com.lvr.threerecom.ui.movie;


import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.ImageView;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnLoadMoreListener;
import com.aspsine.irecyclerview.OnRefreshListener;
import com.lvr.threerecom.R;
import com.lvr.threerecom.adapter.MovieAdapter;
import com.lvr.threerecom.anims.LandingAnimator;
import com.lvr.threerecom.anims.ScaleInAnimationAdapter;
import com.lvr.threerecom.app.AppConstantValue;
import com.lvr.threerecom.base.BaseFragment;
import com.lvr.threerecom.bean.FabSrcollBean;
import com.lvr.threerecom.bean.MovieInfo;
import com.lvr.threerecom.client.RxDisposeManager;
import com.lvr.threerecom.ui.movie.presenter.Impl.MoviePresenterImpl;
import com.lvr.threerecom.ui.movie.view.MovieView;
import com.lvr.threerecom.widget.LoadMoreFooterView;
import com.lvr.threerecom.widget.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;


/**
 * Created by lvr on 2017/4/22.
 */

public class MovieFragment extends BaseFragment implements OnRefreshListener,OnLoadMoreListener,MovieView{

    @BindView(R.id.irv_movie)
    IRecyclerView mIrvMovie;
    private String mMovieGenres;
    private LoadMoreFooterView mFooterView;
    private MovieAdapter mMovieAdapter;
    private List<MovieInfo> mList = new ArrayList<>();
    private MoviePresenterImpl mPresenter;
    private Context mContext;
    private int startPage = 0;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_movie;
    }

    @Override
    protected void initView() {
        if (getArguments() != null) {
            mMovieGenres = getArguments().getString("type");
        }
        mContext = getActivity();
        mPresenter = new MoviePresenterImpl(this,getActivity());
        mMovieAdapter = new MovieAdapter(mContext,mList);
        mMovieAdapter.setOnItemClickListenr(new MovieAdapter.onItemClickListenr() {
            @Override
            public void onItemClick(int position, ImageView imageView) {
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                intent.putExtra("movie", mList.get(position));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions
                            .makeSceneTransitionAnimation((Activity) mContext, imageView,AppConstantValue.TRANSITION_IMAGE_MOVIE);
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
        mFooterView = (LoadMoreFooterView) mIrvMovie.getLoadMoreFooterView();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        System.out.println("当前可见状态："+isVisible+"当前题材："+mMovieGenres);
        if (isVisible) {
            //可见，并且是第一次加载
            mPresenter.requestMovieByType(AppConstantValue.PAGE_SIZE, startPage, AppConstantValue.TYPE_GENRES, mMovieGenres);
            LoadingDialog.showDialogForLoading(getActivity());
        } else {
            //取消加载
            RxDisposeManager.get().cancel("MovieByType");
            LoadingDialog.cancelDialogForLoading();
        }
    }

    @Override
    public void onLoadMore() {
        startPage++;
        mPresenter.requestMovieByType(AppConstantValue.PAGE_SIZE, startPage, AppConstantValue.TYPE_GENRES, mMovieGenres);
        mFooterView.setStatus(LoadMoreFooterView.Status.LOADING);
    }

    @Override
    public void onRefresh() {
        startPage = 0;
        mIrvMovie.setRefreshing(true);
        mPresenter.requestMovieByType(AppConstantValue.PAGE_SIZE, startPage, AppConstantValue.TYPE_GENRES, mMovieGenres);
        mFooterView.setStatus(LoadMoreFooterView.Status.GONE);
    }
    @Override
    public void returnMovieInfos(List<MovieInfo> movieInfoList) {
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
    @Subscribe
    public void onFabScrollEvent(FabSrcollBean event) {
        if(event.isTop()){
            System.out.println("接收到消息");

            mIrvMovie.smoothScrollToPosition(0);
        }
    }
}
