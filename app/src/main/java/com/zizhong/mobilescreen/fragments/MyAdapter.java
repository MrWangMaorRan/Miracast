package com.zizhong.mobilescreen.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hpplay.sdk.source.browse.api.LelinkServiceInfo;
import com.zizhong.mobilescreen.R;

import java.util.ArrayList;
import java.util.List;

import static androidx.recyclerview.widget.RecyclerView.*;

public class MyAdapter extends   RecyclerView.Adapter<MyAdapter.Hokder>{

    private LiveFragment context;
    private List<LelinkServiceInfo> mDatas;
    public MyAdapter(LiveFragment liveFragment, ArrayList<LelinkServiceInfo> lelinkServiceInfo) {
        this.mDatas = lelinkServiceInfo;
        this.context =liveFragment;
    }

    @NonNull
    @Override
    public MyAdapter.Hokder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv, parent, false);
        return new Hokder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.Hokder holder, int position) {
        LelinkServiceInfo lelinkServiceInfo = mDatas.get(position);
        holder.tv.setText(lelinkServiceInfo.getName());
        holder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickItem.onClickListenerItem(lelinkServiceInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public class Hokder extends ViewHolder {
        private final TextView tv;


        public Hokder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv);
        }
    }
    OnClickItem onClickItem;
    public interface OnClickItem{
        void onClickListenerItem(LelinkServiceInfo position);
    }
    public OnClickItem getOnClickItem() {
        return onClickItem;
    }

    public void setOnClickItem(OnClickItem onClickItem) {
        this.onClickItem = onClickItem;
    }
}
