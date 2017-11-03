package com.tsiro.dogvip.login.signin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import javax.inject.Inject;

/**
 * Created by giannis on 30/10/2017.
 */

public class SignInRetainFragment extends Fragment {

    private SignInViewModel mSignInViewModel;

    @Inject
    public SignInRetainFragment () {}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    public void retainViewModel(SignInViewModel signInViewModel) {
        this.mSignInViewModel = signInViewModel;
    }

    public SignInViewModel getViewModel() {
        return this.mSignInViewModel;
    }

}
