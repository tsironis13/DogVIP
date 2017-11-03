package com.tsiro.dogvip.adapters;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;
import com.tsiro.dogvip.R;


/**
 * Created by giannis on 24/6/2017.
 */

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    private ViewDataBinding mBinding;

    public RecyclerViewHolder(ViewDataBinding viewDataBinding) {
        super(viewDataBinding.getRoot());
        this.mBinding = viewDataBinding;
    }

    public void bind(Object obj, Object object, int position) {
        mBinding.setVariable(BR.obj, obj);
        mBinding.setVariable(BR.presenter, object);
        View baseView = mBinding.getRoot().findViewById(R.id.baseRlt);
        if (baseView != null) baseView.setTag(position);
        mBinding.executePendingBindings();
    }
}
