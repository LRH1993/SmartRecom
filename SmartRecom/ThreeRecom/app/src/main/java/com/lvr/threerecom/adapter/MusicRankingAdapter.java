package com.lvr.threerecom.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lvr.threerecom.R;
import com.lvr.threerecom.bean.RankingListItem;
import com.lvr.threerecom.ui.music.MusicSongListDetailActivity;
import com.lvr.threerecom.utils.ImageLoaderUtils;

import java.util.List;

/**
 * Created by lvr on 2017/4/27.
 */

public class MusicRankingAdapter extends RecyclerView.Adapter<MusicRankingAdapter.RankingViewHolder> {
    private final Context context;
    private final LayoutInflater inflater;
    private List<RankingListItem.RangkingDetail> list;
    public MusicRankingAdapter(Context context, List<RankingListItem.RangkingDetail> list) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public RankingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RankingViewHolder(inflater.inflate(R.layout.item_rangking, parent, false));
    }

    @Override
    public void onBindViewHolder(RankingViewHolder holder, int position) {
        final RankingListItem.RangkingDetail detail = list.get(position);
        String url = detail.getPic_s192();
        ImageLoaderUtils.display(context,holder.mIvPhoto,url);
        final List<RankingListItem.RangkingDetail.SongInfo> content = detail.getContent();
        String name = detail.getName();
        holder.mTvName.setText(name);
        RankingListItem.RangkingDetail.SongInfo info1 = content.get(0);
        String title1 = info1.getTitle();
        String author1 = info1.getAuthor();
        holder.mTvFirst.setText("1."+title1+"-"+author1);
        RankingListItem.RangkingDetail.SongInfo info2 = content.get(1);
        String title2 = info2.getTitle();
        String author2 = info2.getAuthor();
        holder.mTvSecond.setText("2."+title2+"-"+author2);
        RankingListItem.RangkingDetail.SongInfo info3 = content.get(2);
        String title3 = info3.getTitle();
        String author3 = info3.getAuthor();
        holder.mTvThird.setText("3."+title3+"-"+author3);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class RankingViewHolder extends RecyclerView.ViewHolder{
        private ImageView mIvPhoto;
        private TextView mTvFirst;
        private TextView mTvSecond;
        private TextView mTvThird;
        private TextView mTvName;
        public RankingViewHolder(View itemView) {
            super(itemView);
            mIvPhoto = (ImageView) itemView.findViewById(R.id.iv_ranking_photo);
            mTvFirst = (TextView) itemView.findViewById(R.id.tv_rank_first);
            mTvSecond = (TextView) itemView.findViewById(R.id.tv_rank_second);
            mTvThird = (TextView) itemView.findViewById(R.id.tv_rank_third);
            mTvName = (TextView) itemView.findViewById(R.id.tv_rank_name);
        }
    }
}
