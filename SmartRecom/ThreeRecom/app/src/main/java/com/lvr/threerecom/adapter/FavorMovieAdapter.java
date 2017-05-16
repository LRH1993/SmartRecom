package com.lvr.threerecom.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.flexbox.FlexboxLayoutManager;
import com.lvr.threerecom.R;
import com.lvr.threerecom.widget.SuperTextView;

import java.util.List;

/**
 * Created by lvr on 2017/5/16.
 */

public class FavorMovieAdapter extends RecyclerView.Adapter<FavorMovieAdapter.CommonViewHolder>  {
    private final Context context;
    private final LayoutInflater inflater;
    private List<String> list;
    private List<String> mSelectList ;

    public FavorMovieAdapter(List<String> list, Context context,List<String> selectList) {
        this.list = list;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.mSelectList = selectList;
    }

    @Override
    public FavorMovieAdapter.CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommonViewHolder(inflater.inflate(R.layout.item_favor_common, parent, false));
    }

    @Override
    public void onBindViewHolder(final FavorMovieAdapter.CommonViewHolder holder, int position) {
        holder.mTitle.setText(list.get(position));
        ViewGroup.LayoutParams params = holder.mTitle.getLayoutParams();
        if (params instanceof FlexboxLayoutManager.LayoutParams) {
            FlexboxLayoutManager.LayoutParams flexboxLp = (FlexboxLayoutManager.LayoutParams) holder.mTitle.getLayoutParams();
            flexboxLp.setFlexGrow(1.0f);
        }
        if(mSelectList.contains(holder.mTitle.getText().toString())){
            holder.mTitle.setTextColor(context.getResources().getColor(R.color.colorAccent));
            holder.mTitle.setStrokeColor(context.getResources().getColor(R.color.colorAccent));
        }

        holder.mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = holder.mTitle.getCurrentTextColor();
                if(color==context.getResources().getColor(R.color.colorAccent)){
                    String text = holder.mTitle.getText().toString();
                    mSelectList.remove(text);
                    System.out.println("mSelectList中集合元素大小："+mSelectList.size());
                    holder.mTitle.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                    holder.mTitle.setStrokeColor(context.getResources().getColor(R.color.colorPrimary));
                }else{
                    holder.mTitle.setTextColor(context.getResources().getColor(R.color.colorAccent));
                    holder.mTitle.setStrokeColor(context.getResources().getColor(R.color.colorAccent));
                    String text = holder.mTitle.getText().toString();
                    mSelectList.add(text);
                }


            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class CommonViewHolder extends RecyclerView.ViewHolder{
        private SuperTextView mTitle;
        public CommonViewHolder(View itemView) {
            super(itemView);
            mTitle = (SuperTextView) itemView.findViewById(R.id.stv_title);

        }
    }
    public List<String> getSelectList(){
        return mSelectList;
    }
}
