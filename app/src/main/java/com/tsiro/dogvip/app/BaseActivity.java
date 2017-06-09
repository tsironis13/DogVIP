package com.tsiro.dogvip.app;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by giannis on 4/6/2017.
 */

public abstract class BaseActivity extends AppCompatActivity implements Lifecycle.View {

    public abstract Lifecycle.ViewModel getViewModel();

    @Override
    protected void onStart() {
        super.onStart();
        getViewModel().onViewAttached(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getViewModel().onViewResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getViewModel().onViewDetached();
    }
}
