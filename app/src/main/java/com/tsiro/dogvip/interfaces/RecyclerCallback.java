package com.tsiro.dogvip.interfaces;

import android.databinding.ViewDataBinding;

/**
 * Created by giannis on 24/6/2017.
 */

public interface RecyclerCallback<VM extends ViewDataBinding, T extends Object> {

    void bindData(VM vm, Object model);

}
