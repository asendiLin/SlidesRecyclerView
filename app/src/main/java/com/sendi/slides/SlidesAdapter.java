package com.sendi.slides;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/2/4.
 */

public class SlidesAdapter extends RecyclerView.Adapter<SlidesAdapter.SlidesViewHolder> {


    private List<String> mList=new ArrayList<>();
    private Context mContext;
    public SlidesAdapter(Context context){
        this.mContext=context;
        for (int i = 0; i < 10; i++) {
            mList.add("slides::"+i);
        }
    }


    @Override
    public SlidesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(mContext).inflate(R.layout.item_layout,parent,false);

        return new SlidesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SlidesViewHolder holder, int position) {
        holder.txtContent.setText(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class SlidesViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout llContent;
        public ImageView ivFun;
        public TextView txtContent;
        public SlidesViewHolder(View itemView) {
            super(itemView);
            llContent=itemView.findViewById(R.id.ll_content);
            ivFun=itemView.findViewById(R.id.iv_fun);
            txtContent=itemView.findViewById(R.id.txtContent);
        }
    }
}
