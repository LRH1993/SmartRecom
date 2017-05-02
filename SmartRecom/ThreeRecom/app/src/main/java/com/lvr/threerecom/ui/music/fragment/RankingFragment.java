package com.lvr.threerecom.ui.music.fragment;

import android.content.Context;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;

import com.aspsine.irecyclerview.IRecyclerView;
import com.lvr.threerecom.R;
import com.lvr.threerecom.adapter.MusicRankingAdapter;
import com.lvr.threerecom.anims.LandingAnimator;
import com.lvr.threerecom.anims.ScaleInAnimationAdapter;
import com.lvr.threerecom.app.AppConstantValue;
import com.lvr.threerecom.base.BaseFragment;
import com.lvr.threerecom.bean.RankingListItem;
import com.lvr.threerecom.ui.music.presneter.MusicRankingPresenter;
import com.lvr.threerecom.ui.music.presneter.impl.MusicRankingPresenterImpl;
import com.lvr.threerecom.ui.music.view.MusicRankingListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by lvr on 2017/4/26.
 */

public class RankingFragment extends BaseFragment implements MusicRankingListView {
    @BindView(R.id.irv_ranking)
    IRecyclerView mIrvRanking;
    private MusicRankingPresenter mPresenter;
    private Context mContext;
    private MusicRankingAdapter mAdapter;
    private List<RankingListItem.RangkingDetail> mList = new ArrayList<>();


    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_music_rank;
    }

    @Override
    protected void initView() {
        mContext = getActivity();
        mPresenter = new MusicRankingPresenterImpl(this,mContext);
        mAdapter = new MusicRankingAdapter(mContext, mList);
        mIrvRanking.setLayoutManager(new LinearLayoutManager(mContext));
        mIrvRanking.setItemAnimator(new LandingAnimator());
        mIrvRanking.setIAdapter(new ScaleInAnimationAdapter(mAdapter));
        mIrvRanking.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL));
        mPresenter.requestRankingListAll(AppConstantValue.MUSIC_URL_FORMAT,AppConstantValue.MUSIC_URL_FROM,AppConstantValue.MUSIC_URL_METHOD_RANKINGLIST,AppConstantValue.MUSIC_URL_RANKINGLIST_FLAG);
    }

    @Override
    public void returnRankingListInfos(List<RankingListItem.RangkingDetail> rangkingListInfo) {
        mList.addAll(rangkingListInfo);
        mAdapter.notifyDataSetChanged();
    }


}
