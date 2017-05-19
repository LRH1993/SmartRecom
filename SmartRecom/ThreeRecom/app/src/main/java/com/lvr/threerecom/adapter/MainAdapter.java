package com.lvr.threerecom.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lvr.threerecom.R;
import com.lvr.threerecom.bean.MovieInfo;
import com.lvr.threerecom.ui.movie.MovieDetailActivity;
import com.lvr.threerecom.ui.movie.MovieDisplayActivity;
import com.lvr.threerecom.utils.DisplayUtil;
import com.lvr.threerecom.utils.GlideLoader;
import com.lvr.threerecom.utils.ImageLoaderUtils;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lvr on 2017/4/24.
 */

public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private onItemClickListenr mOnItemClickListenr;
    private final Context context;
    private final LayoutInflater inflater;
    private List<MovieInfo> list;
    private static final int TYPE_BANNER = 0;
    private static final int TYPE_MOVIE_DES = 1;
    private static final int TYPE_MOVIE_DETAIL = 2;
    private static final int TYPE_FOOT =3;
    private List<String> mImages = new ArrayList<>();

    public MainAdapter(Context context, List<MovieInfo> list) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;


    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_BANNER;
        } else if (position == 1) {
            return TYPE_MOVIE_DES;
        } else if(position== list.size()+2){
            return TYPE_FOOT;
        }else {
            return TYPE_MOVIE_DETAIL;
        }


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_BANNER:
                return new BannerViewHolder(inflater.inflate(R.layout.item_home_banner, parent, false));
            case TYPE_MOVIE_DES:
                return new MovieDesViewHolder(inflater.inflate(R.layout.item_home_movie_des, parent, false));
            case TYPE_MOVIE_DETAIL:
                return new MovieDetailViewHolder(inflater.inflate(R.layout.item_movie, parent, false));
            case TYPE_FOOT:
                return new BottomViewHolder(inflater.inflate(R.layout.item_home_bottom, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BannerViewHolder) {
            setBannerItemValues((BannerViewHolder) holder, position);
        } else if (holder instanceof MovieDetailViewHolder) {
            setMovieDetailItemValues((MovieDetailViewHolder) holder, position);
        }
    }

    private void setMovieDetailItemValues(final MovieDetailViewHolder holder, final int position) {
        MovieInfo info = list.get(position - 2);
        double score = info.getMovie_average_score();
        holder.mTvRating.setText(score + "");
        String name = info.getMovie_name();
        holder.mTvMovieName.setText(name);
        String url = info.getMovie_picture_url();
        ImageLoaderUtils.display(context, holder.mIvPhoto, url);
        boolean hot = info.isMovie_is_hot();
        if (hot) {
            holder.mIvHot.setVisibility(View.VISIBLE);
        } else {
            holder.mIvHot.setVisibility(View.GONE);
        }
        holder.mRlRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListenr.onItemClick(position-2,holder.mIvPhoto);
            }
        });
        int width = DisplayUtil.getScreenWidth(context);
        ViewGroup.LayoutParams params = holder.mRlRoot.getLayoutParams();
        params.width = width / 2 - 40;
        holder.mRlRoot.setLayoutParams(params);
    }


    private void setBannerItemValues(final BannerViewHolder holder, int position) {
        if (mImages.size() == 0 && list.size() >= 4) {
            for (int i = 0; i < 4; i++) {
                MovieInfo info = list.get(i);
                mImages.add(info.getMovie_picture_url());
            }
        }
        holder.mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        holder.mBanner.setImageLoader(new GlideLoader());
        holder.mBanner.setImages(mImages);
        holder.mBanner.setBannerAnimation(Transformer.DepthPage);
        holder.mBanner.isAutoPlay(true);
        holder.mBanner.setDelayTime(1500);
        holder.mBanner.setIndicatorGravity(BannerConfig.CENTER);
        holder.mBanner.start();
        holder.mBanner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                Intent intent = new Intent(context, MovieDetailActivity.class);
                intent.putExtra("movie", list.get(position));
                context.startActivity(intent);
            }
        });
    }

  @Override
    public int getItemCount() {
        return list.size() + 3;
    }


    public class BannerViewHolder extends RecyclerView.ViewHolder {
        private Banner mBanner;

        public BannerViewHolder(View itemView) {
            super(itemView);
            mBanner = (Banner) itemView.findViewById(R.id.banner_home);
        }
    }

    public class MovieDesViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvMore;
        public MovieDesViewHolder(View itemView) {
            super(itemView);
            mTvMore = (TextView) itemView.findViewById(R.id.tv_more);
            mTvMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MovieDisplayActivity.class);
                    context.startActivity(intent);
                }
            });
        }

    }

    public class MovieDetailViewHolder extends RecyclerView.ViewHolder {
        private ImageView mIvPhoto;
        private RelativeLayout mRlRoot;
        private ImageView mIvHot;
        private TextView mTvRating;
        private TextView mTvMovieName;

        public MovieDetailViewHolder(View itemView) {
            super(itemView);
            mIvHot = (ImageView) itemView.findViewById(R.id.iv_hot);
            mIvPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
            mTvRating = (TextView) itemView.findViewById(R.id.tv_rating);
            mTvMovieName = (TextView) itemView.findViewById(R.id.tv_name);
            mRlRoot = (RelativeLayout) itemView.findViewById(R.id.rl_root);
        }
    }
    public class BottomViewHolder extends RecyclerView.ViewHolder {

        public BottomViewHolder(View itemView) {
            super(itemView);

        }
    }

    public interface onItemClickListenr{
        void onItemClick(int position,ImageView imageView);
    }
    public void setOnItemClickListenr(onItemClickListenr onItemClickListenr){
        this.mOnItemClickListenr = onItemClickListenr;
    }
}
