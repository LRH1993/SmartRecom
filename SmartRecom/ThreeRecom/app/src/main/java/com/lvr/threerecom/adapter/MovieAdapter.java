package com.lvr.threerecom.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lvr.threerecom.R;
import com.lvr.threerecom.bean.MovieInfo;
import com.lvr.threerecom.utils.DisplayUtil;
import com.lvr.threerecom.utils.ImageLoaderUtils;

import java.util.List;

/**
 * Created by lvr on 2017/4/22.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private Context mContext;
    private List<MovieInfo> mData;
    private final LayoutInflater inflater;
    private onItemClickListenr mOnItemClickListenr;

    public MovieAdapter(Context context, List<MovieInfo> list) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.mData = list;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MovieViewHolder(inflater.inflate(R.layout.item_movie,parent,false));
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, final int position) {
        MovieInfo info = mData.get(position);
        double score = info.getMovie_average_score();
        holder.mTvRating.setText(score+"");
        String name = info.getMovie_name();
        holder.mTvMovieName.setText(name);
        String url = info.getMovie_picture_url();
        ImageLoaderUtils.display(mContext,holder.mIvPhoto,url);
        boolean hot = info.isMovie_is_hot();
        if(hot){
            holder.mIvHot.setVisibility(View.VISIBLE);
        }else{
            holder.mIvHot.setVisibility(View.GONE);
        }
        holder.mRlRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListenr.onItemClick(position,holder.mIvPhoto);
            }
        });
        int width = DisplayUtil.getScreenWidth(mContext);
        ViewGroup.LayoutParams params = holder.mRlRoot.getLayoutParams();
        params.width = width/2-40;
        holder.mRlRoot.setLayoutParams(params);


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        private ImageView mIvPhoto;
        private RelativeLayout mRlRoot;
        private ImageView mIvHot;
        private TextView mTvRating;
        private TextView mTvMovieName;
        public MovieViewHolder(View itemView) {
            super(itemView);
            mIvHot = (ImageView) itemView.findViewById(R.id.iv_hot);
            mIvPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
            mTvRating  = (TextView) itemView.findViewById(R.id.tv_rating);
            mTvMovieName = (TextView) itemView.findViewById(R.id.tv_name);
            mRlRoot = (RelativeLayout) itemView.findViewById(R.id.rl_root);
        }
    }
    public interface onItemClickListenr{
        void onItemClick(int position,ImageView imageView);
    }
    public void setOnItemClickListenr(onItemClickListenr onItemClickListenr){
        this.mOnItemClickListenr = onItemClickListenr;
    }



}
