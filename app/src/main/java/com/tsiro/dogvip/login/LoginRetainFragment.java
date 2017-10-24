package com.tsiro.dogvip.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.tsiro.dogvip.lovematch.LoveMatchViewModel;

import javax.inject.Inject;

/**
 * Created by giannis on 23/10/2017.
 */

public class LoginRetainFragment extends Fragment {

    private LoginViewModel mLoginViewModel;

    @Inject
    public LoginRetainFragment () {}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    public void retainViewModel(LoginViewModel loginViewModel) {
        this.mLoginViewModel = loginViewModel;
    }

    public LoginViewModel getViewModel() {
        return this.mLoginViewModel;
    }
}
