package com.lvr.threerecom.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lvr.threerecom.R;
import com.lvr.threerecom.bean.InformationBean;
import com.lvr.threerecom.utils.BitmapUtils;
import com.lvr.threerecom.utils.ImageLoaderUtils;

import java.io.File;
import java.util.List;

/**
 * Created by lvr on 2017/5/15.
 */

public class InformationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private final Context context;
    private onItemClickListenr mOnItemClickListenr;
    private final LayoutInflater inflater;
    private List<InformationBean> list;
    private int TYPE_SPECIAL = 0;
    private int TYPE_COMMON =1;
    private int mHeight;
    private int mWidth;


    public InformationAdapter(Context context, List<InformationBean> list) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;

    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return TYPE_SPECIAL;
        }else {
            return TYPE_COMMON;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_SPECIAL){
            return new SpecialViewHolder(inflater.inflate(R.layout.item_information_special, parent, false));
        }else if(viewType==TYPE_COMMON){
           return new CommonViewHolder(inflater.inflate(R.layout.item_information_common, parent, false));
        }
        return null;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof SpecialViewHolder){
            InformationBean bean = list.get(position);
            ((SpecialViewHolder) holder).mTextView.setText(bean.getTitle());
            if(bean.isSet()){
                String content = bean.getContent();
                if(content.startsWith("http")){
                    //网络图片
                    ImageLoaderUtils.displayRound(context, ((SpecialViewHolder) holder).mImageView,content);
                }else{
                    final File file = new File(content);
                    ((SpecialViewHolder) holder).mImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            ((SpecialViewHolder) holder).mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            mWidth = ((SpecialViewHolder) holder).mImageView.getMeasuredWidth();
                            mHeight = ((SpecialViewHolder) holder).mImageView.getMeasuredHeight();
                            Bitmap bitmap = BitmapUtils.decodeBitmapFromFile(file, mWidth, mHeight);
                            if (bitmap != null) {
                                //检查是否有被旋转，并进行纠正
                                int degree = BitmapUtils.getBitmapDegree(file.getAbsolutePath());
                                if (degree != 0) {
                                    bitmap = BitmapUtils.rotateBitmapByDegree(bitmap, degree);
                                }
                                ((SpecialViewHolder) holder).mImageView.setImageBitmap(bitmap);
                            }

                        }
                    });
                }
            }else{
                ((SpecialViewHolder) holder).mImageView.setImageResource(R.drawable.nav_photo);
            }
            ((SpecialViewHolder) holder).mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListenr.onItemClick(position);
                }
            });

        }else{
            InformationBean bean = list.get(position);
            ((CommonViewHolder) holder).mTitle.setText(bean.getTitle());
            if(bean.isSet()){
                ((CommonViewHolder) holder).mContent.setText(bean.getContent());
                ((CommonViewHolder) holder).mContent.setTextColor(context.getResources().getColor(R.color.text_color));
            }else{
                ((CommonViewHolder) holder).mContent.setText("未设置");
                ((CommonViewHolder) holder).mContent.setTextColor(context.getResources().getColor(R.color.time_line));
            }
            ((CommonViewHolder) holder).mContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListenr.onItemClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SpecialViewHolder extends RecyclerView.ViewHolder{
        private TextView mTextView;
        private ImageView mImageView;
        private RelativeLayout mLayout;
        public SpecialViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.tv_title);
            mImageView = (ImageView) itemView.findViewById(R.id.iv_content);
            mLayout = (RelativeLayout) itemView.findViewById(R.id.rl_root);

        }
    }
    public class CommonViewHolder extends RecyclerView.ViewHolder{
        private TextView mTitle;
        private TextView mContent;
        public CommonViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mContent = (TextView) itemView.findViewById(R.id.tv_content);

        }
    }
    public interface onItemClickListenr{
        void onItemClick(int position);
    }
    public void setOnItemClickListenr(onItemClickListenr onItemClickListenr){
        this.mOnItemClickListenr = onItemClickListenr;
    }
}
