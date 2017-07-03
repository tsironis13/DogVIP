package com.tsiro.dogvip.retrofit;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.forgotpasswrd.ForgotPaswrdObj;
import com.tsiro.dogvip.POJO.logout.LogoutRequest;
import com.tsiro.dogvip.POJO.logout.LogoutResponse;
import com.tsiro.dogvip.POJO.lovematch.LoveMatchRequest;
import com.tsiro.dogvip.POJO.lovematch.LoveMatchResponse;
import com.tsiro.dogvip.POJO.mypets.OwnerRequest;
import com.tsiro.dogvip.POJO.mypets.owner.OwnerObj;
import com.tsiro.dogvip.POJO.mypets.pet.PetObj;
import com.tsiro.dogvip.POJO.registration.RegistrationRequest;
import com.tsiro.dogvip.POJO.registration.AuthenticationResponse;
import com.tsiro.dogvip.POJO.signin.SignInRequest;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

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
    Flowable<OwnerObj> getOwnerDetails(@Body OwnerRequest request);

    @POST("actions.php")
    Flowable<OwnerObj> submitOwner(@Body OwnerObj request);

    @Multipart
    @POST("upload.php")
    Flowable<Image> uploadImage(@Part("action") RequestBody action, @Part("token") RequestBody token, @Part("id") RequestBody id, @Part MultipartBody.Part image);

    @Multipart
    @POST("upload.php")
    Flowable<Image> uploadPetImage(@Part("action") RequestBody action, @Part("token") RequestBody token, @Part("user_role_id") RequestBody user_role_id, @Part("pet_id") RequestBody pet_id, @Part MultipartBody.Part image, @Part("index") RequestBody index);

    @POST("actions.php")
    Flowable<Image> deleteImage(@Body Image image);

    @POST("actions.php")
    Flowable<OwnerRequest> deleteOwner(@Body OwnerRequest request);

    @POST("actions.php")
    Flowable<OwnerObj> submitPet(@Body PetObj request);

    @POST("actions.php")
    Flowable<Image> deletePetImage(@Body Image request);

    @POST("actions.php")
    Flowable<LoveMatchResponse> getPetsByFilter(@Body LoveMatchRequest request);
//    @Multipart
//    @POST("actions.php")
//    Flowable<GetOwnerResponse> submitOwner(@Part("body") RequestBody body, @Part MultipartBody.Part image);
//    Flowable<GetOwnerResponse> submitOwner(@Part MultipartBody.Part image);


}
