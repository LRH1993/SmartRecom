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
import com.lvr.threerecom.bean.WrapperSongListInfo;
import com.lvr.threerecom.ui.music.MusicSongListDetailActivity;
import com.lvr.threerecom.utils.DisplayUtil;
import com.lvr.threerecom.utils.ImageLoaderUtils;

import java.util.List;

/**
 * Created by lvr on 2017/4/27.
 */
// TODO: 2017/4/27 错位现象 还未解决
public class MusicSongListAdapter extends RecyclerView.Adapter<MusicSongListAdapter.SongListViewHolder> {
    private final Context context;
    private final LayoutInflater inflater;
    private List<WrapperSongListInfo.SongListInfo> list;
    public MusicSongListAdapter(Context context, List<WrapperSongListInfo.SongListInfo> list) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public SongListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SongListViewHolder(inflater.inflate(R.layout.item_songlist, parent, false));
    }

    @Override
    public void onBindViewHolder(SongListViewHolder holder, int position) {
        final WrapperSongListInfo.SongListInfo info = list.get(position);
        int count = Integer.parseInt(info.getListenum());
        String name = info.getTitle();
        String url = info.getPic_300();
        if (count > 10000) {
            count = count / 10000;
             holder.mTvCount.setText(count+"万");
        } else {
            holder.mTvCount.setText(info.getListenum());
        }
        holder.mTvName.setText(name);
        ImageLoaderUtils.display(context,holder.mIvPhoto,url);
        int width = DisplayUtil.getScreenWidth(context);
        ViewGroup.LayoutParams params = holder.mRlRoot.getLayoutParams();
        params.width = width/2-40;
        holder.mRlRoot.setLayoutParams(params);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MusicSongListDetailActivity.class);
                intent.putExtra("songListId",info.getListid());
                intent.putExtra("islocal", false);
                intent.putExtra("songListPhoto", info.getPic_300());
                intent.putExtra("songListname", info.getTitle());
                intent.putExtra("songListTag", info.getTag());
                intent.putExtra("songListCount", info.getListenum());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size()-2;
    }


    public class SongListViewHolder extends RecyclerView.ViewHolder{
        private ImageView mIvPhoto;
        private TextView mTvName;
        private RelativeLayout mRlRoot;
        private TextView mTvCount;
        public SongListViewHolder(View itemView) {
            super(itemView);
            mIvPhoto = (ImageView) itemView.findViewById(R.id.iv_songlist_photo);
            mTvCount = (TextView) itemView.findViewById(R.id.tv_songlist_count);
            mTvName = (TextView) itemView.findViewById(R.id.tv_songlist_name);
            mRlRoot = (RelativeLayout) itemView.findViewById(R.id.rl_root);
        }
    }
}
