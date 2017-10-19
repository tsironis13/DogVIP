package com.tsiro.dogvip.lovematch;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import javax.inject.Inject;

/**
 * Created by giannis on 13/10/2017.
 */

public class LoveMatchActivityRetainFragment extends Fragment {

    private LoveMatchViewModel loveMatchViewModel;

    @Inject
    public LoveMatchActivityRetainFragment() {}

    //    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        Log.e("onCreateView", this + ": onCreateView()");
//        return null;
//    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    public void retainViewModel(LoveMatchViewModel loveMatchViewModel) {
        Log.e("df", loveMatchViewModel + " ");
        this.loveMatchViewModel = loveMatchViewModel;
    }

    public LoveMatchViewModel getViewModel() {
        return this.loveMatchViewModel;
    }

}
