package com.tsiro.dogvip.login.signin;

import com.tsiro.dogvip.POJO.registration.AuthenticationResponse;
import com.tsiro.dogvip.POJO.signin.SignInRequest;
import com.tsiro.dogvip.app.Lifecycle;

/**
 * Created by giannis on 28/5/2017.
 */

public interface SignInContract {

    interface View extends Lifecycle.View {
        void onSuccess(AuthenticationResponse response);
        void onError(int resource, boolean msglength);
    }

    interface ViewModel extends Lifecycle.ViewModel {
        void signin(SignInRequest request);
        void setRequestState(int state);
    }

}
