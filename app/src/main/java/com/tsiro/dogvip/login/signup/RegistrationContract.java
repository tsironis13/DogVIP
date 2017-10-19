package com.tsiro.dogvip.login.signup;


import com.tsiro.dogvip.POJO.registration.AuthenticationResponse;
import com.tsiro.dogvip.POJO.registration.RegistrationRequest;
import com.tsiro.dogvip.app.Lifecycle;

/**
 * Created by giannis on 22/5/2017.
 */

public interface RegistrationContract {

    interface View extends Lifecycle.View {
        void onSuccess(AuthenticationResponse response);
        void onError(int resource, boolean msglength);
    }

    interface ViewModel extends Lifecycle.ViewModel {
        void register(RegistrationRequest request);
        void setRequestState(int state);
    }

}
