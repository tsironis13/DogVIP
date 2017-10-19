package com.tsiro.dogvip.base.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.utilities.UIUtls;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasFragmentInjector;
import dagger.android.support.HasSupportFragmentInjector;


/**
 * Created by giannis on 22/5/2017.
 */

public abstract class BaseFragment extends Fragment implements HasSupportFragmentInjector, Lifecycle.View {

    @Inject
    DispatchingAndroidInjector<Fragment> childFragmentInjector;
    public abstract Lifecycle.ViewModel getViewModel();
    @Inject
    UIUtls uiUtls;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Perform injection here before M, L (API 22) and below because onAttach(Context)
            // is not yet available at L.
            AndroidInjection.inject(activity);
        }
        super.onAttach(activity);
    }

    @Override
    public void onAttach(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Perform injection here for M (API 23) due to deprecation of onAttach(Activity).
            AndroidInjection.inject(getActivity());
        }
        super.onAttach(context);
    }

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

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        Log.e("aaa", uiUtls + " dskjjkdskjdskjdskds");
        return childFragmentInjector;
    }

    //    @Override
//    public AndroidInjector<Fragment> fragmentInjector() {
//        return childFragmentInjector;
//    }
}
