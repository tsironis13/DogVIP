package com.tsiro.dogvip.retrofit;

import com.tsiro.dogvip.POJO.forgotpasswrd.ForgotPaswrdObj;
import com.tsiro.dogvip.POJO.logout.LogoutRequest;
import com.tsiro.dogvip.POJO.logout.LogoutResponse;
import com.tsiro.dogvip.POJO.mypets.GetOwnerRequest;
import com.tsiro.dogvip.POJO.mypets.GetOwnerResponse;
import com.tsiro.dogvip.POJO.registration.RegistrationRequest;
import com.tsiro.dogvip.POJO.registration.AuthenticationResponse;
import com.tsiro.dogvip.POJO.signin.SignInRequest;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by giannis on 23/5/2017.
 */

public interface ServiceAPI {

    @POST("actions.php")
    Flowable<AuthenticationResponse> signin(@Body SignInRequest request);

    //register user
    @POST("actions.php")
    Flowable<AuthenticationResponse> register(@Body RegistrationRequest request);

    //register user
    @POST("actions.php")
    Flowable<ForgotPaswrdObj> forgotPaswrd(@Body ForgotPaswrdObj request);

    //logout user
    @POST("actions.php")
    Observable<LogoutResponse> logout(@Body LogoutRequest request);

    @POST("actions.php")
    Flowable<GetOwnerResponse> getOwnerDetails(@Body GetOwnerRequest request);

}
