package com.tsiro.dogvip.app;


import android.support.v4.app.Fragment;


/**
 * Created by giannis on 22/5/2017.
 */

public abstract class BaseFragment extends Fragment implements Lifecycle.View {

    public abstract Lifecycle.ViewModel getViewModel();

    @Override
    public void onStart() {
        super.onStart();
        getViewModel().onViewAttached(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getViewModel().onViewResumed();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        getViewModel().onViewDetached();
    }

}
