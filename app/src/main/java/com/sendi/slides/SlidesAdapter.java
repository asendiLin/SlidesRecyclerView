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


    private List<String> mList = new ArrayList<>();
    private Context mContext;
    private OnDeleteListener mDeleteListener;

    public SlidesAdapter(Context context) {
        this.mContext = context;
        for (int i = 0; i < 10; i++) {
            mList.add("slides::" + i);
        }
    }

    public void deleteItem(int position) {
        mList.remove(position);
        notifyDataSetChanged();
    }

    public void setDeleteListener(OnDeleteListener deleteListener) {
        this.mDeleteListener = deleteListener;
    }

    @Override
    public SlidesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_layout, parent, false);

        return new SlidesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SlidesViewHolder holder, final int position) {
        holder.txtContent.setText(mList.get(position));
        holder.deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDeleteListener!=null){
                    mDeleteListener.onDelete(position,holder.itemView);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class SlidesViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout ContentView;
        public ImageView deleteView;
        public TextView txtContent;

        public SlidesViewHolder(View itemView) {
            super(itemView);
            ContentView = itemView.findViewById(R.id.ll_content);
            deleteView = itemView.findViewById(R.id.iv_fun);
            txtContent = itemView.findViewById(R.id.txtContent);
        }
    }

    interface OnDeleteListener {
        void onDelete(int position,View view);
    }
}
