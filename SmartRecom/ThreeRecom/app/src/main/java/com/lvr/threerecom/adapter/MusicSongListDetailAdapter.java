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
import com.lvr.threerecom.bean.SongListDetail;

import java.util.List;

/**
 * Created by lvr on 2017/4/27.
 */

public class MusicSongListDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private final LayoutInflater inflater;
    private List<SongListDetail.SongDetail> list;
    private static final int TYPE_HEAD = 0;
    private static final int TYPE_LIST = 1;
    private onItemClickListener mOnItemClickListener;
    private onPlayAllClickListener mOnPlayAllClickListener;
    public MusicSongListDetailAdapter(Context context, List<SongListDetail.SongDetail> list) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return TYPE_HEAD;
        }else {
            return TYPE_LIST;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==TYPE_HEAD){
            return new HeaderViewHolder(inflater.inflate(R.layout.item_songlist_detail_header,parent,false));
        }else{
            return new ListViewHolder(inflater.inflate(R.layout.item_songlist_detail_list,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof HeaderViewHolder){
            ((HeaderViewHolder) holder).mTvPlayNumber.setText("(共"+list.size()+"首)");
        }else {
            final SongListDetail.SongDetail detail = list.get(position - 1);
            String title = detail.getTitle();
            ((ListViewHolder) holder).mTvName.setText(title);
            String author = detail.getAuthor();
            ((ListViewHolder) holder).mTvAuthor.setText(author);
            ((ListViewHolder) holder).mTvNumber.setText(position+"");
            ((ListViewHolder) holder).mRlSong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(position-1);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size()+1;
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder{
        private RelativeLayout mRlPlayAll;
        private ImageView mIvSetting;
        private TextView mTvPlayNumber;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            mRlPlayAll = (RelativeLayout) itemView.findViewById(R.id.rl_play_all_layout);
            mIvSetting = (ImageView) itemView.findViewById(R.id.iv_detail_select);
            mTvPlayNumber = (TextView) itemView.findViewById(R.id.tv_play_all_number);
            mRlPlayAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnPlayAllClickListener.onItemClick(list);
                }
            });
            mIvSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
    public class ListViewHolder extends RecyclerView.ViewHolder{
        private TextView mTvNumber;
        private TextView mTvName;
        private TextView mTvAuthor;
        private ImageView mIvMore;
        private RelativeLayout mRlSong;

        public ListViewHolder(View itemView) {
            super(itemView);
            mTvNumber = (TextView) itemView.findViewById(R.id.tv_trackNumber);
            mTvName = (TextView) itemView.findViewById(R.id.tv_song_title);
            mTvAuthor = (TextView) itemView.findViewById(R.id.tv_song_artist);
            mIvMore = (ImageView) itemView.findViewById(R.id.popup_menu);
            mRlSong = (RelativeLayout) itemView.findViewById(R.id.rl_song);
            mIvMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });


        }
    }
    public void setOnItemClickListener(onItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }
    public interface  onItemClickListener{
        void onItemClick(int position);
    }
    public void setOnPlayAllClickListener(onPlayAllClickListener onPlayAllClickListener){
        this.mOnPlayAllClickListener = onPlayAllClickListener;
    }
    public interface  onPlayAllClickListener{
        void onItemClick(List<SongListDetail.SongDetail> songDetails);
    }
}
