package com.tsiro.dogvip.login.signup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.tsiro.dogvip.login.signin.SignInViewModel;

import javax.inject.Inject;

/**
 * Created by giannis on 30/10/2017.
 */

public class RegisterRetainFragment extends Fragment {

    private RegistrationViewModel mRegistrationViewModel;

    @Inject
    public RegisterRetainFragment () {}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    public void retainViewModel(RegistrationViewModel registrationViewModel) {
        this.mRegistrationViewModel = registrationViewModel;
    }

    public RegistrationViewModel getViewModel() {
        return this.mRegistrationViewModel;
    }

}
