package com.tsiro.dogvip.adapters;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tsiro.dogvip.interfaces.RecyclerCallback;

import java.util.List;

/**
 * Created by giannis on 24/6/2017.
 */

public abstract class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private int layoutid;

    public RecyclerViewAdapter(int layoutId) {
        this.layoutid = layoutId;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding mBinding = DataBindingUtil.inflate(layoutInflater, layoutid, parent, false);

        return new RecyclerViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        Object item = getObjForPosition(position);
        holder.bind(item, getClickListenerObject(), position);
    }

    @Override
    public int getItemViewType(int position) {
        return getLayoutIdForPosition(position);
    }

    @Override
    public int getItemCount() {
        return getTotalItems();
    }

    @BindingAdapter("bind:imageUrl")
    public static void loadImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext()).load(url).into(imageView);
    }

//    protected abstract int getsItemCount();

    protected abstract Object getObjForPosition(int position);

    protected abstract Object getClickListenerObject();

    protected abstract int getLayoutIdForPosition(int position);

    protected abstract int getTotalItems();
}
