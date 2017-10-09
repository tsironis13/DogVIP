package com.tsiro.dogvip.retrofit;

import com.tsiro.dogvip.POJO.BaseResponseObj;
import com.tsiro.dogvip.POJO.FcmTokenUpload;
import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.chat.FetchChatRoomRequest;
import com.tsiro.dogvip.POJO.chat.FetchChatRoomResponse;
import com.tsiro.dogvip.POJO.chat.FetchChatRoomsRequest;
import com.tsiro.dogvip.POJO.chat.FetchChatRoomsResponse;
import com.tsiro.dogvip.POJO.chat.SendMessageRequest;
import com.tsiro.dogvip.POJO.forgotpasswrd.ForgotPaswrdObj;
import com.tsiro.dogvip.POJO.dashboard.DashboardRequest;
import com.tsiro.dogvip.POJO.dashboard.DashboardResponse;
import com.tsiro.dogvip.POJO.lostfound.LostFoundRequest;
import com.tsiro.dogvip.POJO.lostfound.LostFoundResponse;
import com.tsiro.dogvip.POJO.lostfound.ManipulateLostFoundPet;
import com.tsiro.dogvip.POJO.lostfound.ManipulateLostFoundPetResponse;
import com.tsiro.dogvip.POJO.lovematch.LoveMatch;
import com.tsiro.dogvip.POJO.lovematch.LoveMatchRequest;
import com.tsiro.dogvip.POJO.lovematch.LoveMatchResponse;
import com.tsiro.dogvip.POJO.mypets.OwnerRequest;
import com.tsiro.dogvip.POJO.mypets.owner.OwnerObj;
import com.tsiro.dogvip.POJO.mypets.pet.PetObj;
import com.tsiro.dogvip.POJO.petlikes.PetLikesRequest;
import com.tsiro.dogvip.POJO.petlikes.PetLikesResponse;
import com.tsiro.dogvip.POJO.petsitter.BookingObj;
import com.tsiro.dogvip.POJO.petsitter.OwnerSitterBookingsRequest;
import com.tsiro.dogvip.POJO.petsitter.OwnerSitterBookingsResponse;
import com.tsiro.dogvip.POJO.petsitter.PetSitterObj;
import com.tsiro.dogvip.POJO.petsitter.RateBookingRequest;
import com.tsiro.dogvip.POJO.petsitter.SearchedSittersResponse;
import com.tsiro.dogvip.POJO.profs.DeleteProfRequest;
import com.tsiro.dogvip.POJO.profs.GetProfDetailsRequest;
import com.tsiro.dogvip.POJO.profs.ProfDetailsResponse;
import com.tsiro.dogvip.POJO.profs.SaveProfDetailsRequest;
import com.tsiro.dogvip.POJO.profs.SearchProfsRequest;
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
    Observable<DashboardResponse> logout(@Body DashboardRequest request);

    @POST("actions.php")
    Observable<FcmTokenUpload> uploadFcmToken(@Body FcmTokenUpload request);

    @POST("actions.php")
    Flowable<OwnerObj> getOwnerDetails(@Body OwnerRequest request);

    @POST("actions.php")
    Flowable<PetSitterObj> petSitterRelatedActions(@Body PetSitterObj request);

    @POST("actions.php")
    Flowable<SearchedSittersResponse> searchSitters(@Body PetSitterObj request);

    @POST("actions.php")
    Flowable<SearchedSittersResponse> sendBooking(@Body BookingObj request);

    @POST("actions.php")
    Flowable<OwnerObj> submitOwner(@Body OwnerObj request);

    @Multipart
    @POST("upload.php")
    Flowable<Image> uploadImage(@Part("action") RequestBody action, @Part("token") RequestBody token, @Part("id") RequestBody id, @Part MultipartBody.Part image);

    @Multipart
    @POST("upload.php")
    Flowable<Image> uploadPetImage(@Part("action") RequestBody action, @Part("token") RequestBody token, @Part("user_role_id") RequestBody user_role_id, @Part("pet_id") RequestBody pet_id, @Part MultipartBody.Part image, @Part("index") RequestBody index);

    @Multipart
    @POST("upload.php")
    Flowable<Image> uploadPetSitterPlaceImage(@Part("action") RequestBody action, @Part("token") RequestBody token, @Part("id") RequestBody id, @Part MultipartBody.Part image, @Part("index") RequestBody index);

    @POST("actions.php")
    Flowable<Image> deleteImage(@Body Image image);

    @POST("actions.php")
    Flowable<OwnerRequest> deleteOwner(@Body OwnerRequest request);

    @POST("actions.php")
    Flowable<OwnerObj> submitPet(@Body PetObj request);

    @POST("actions.php")
    Flowable<Image> deletePetImage(@Body Image request);

//    @POST("actions.php")
//    Flowable<LoveMatchResponse> getPetsByFilter(@Body LoveMatchRequest request);

    @POST("actions.php")
    Flowable<LoveMatch> getPetsByFilter(@Body LoveMatchRequest request);

    @POST("actions.php")
    Flowable<LoveMatch> likeDislikePet(@Body LoveMatchRequest request);

    @POST("actions.php")
    Flowable<PetLikesResponse> getPetLikes(@Body PetLikesRequest request);

    @POST("actions.php")
    Flowable<LostFoundResponse> getLostFound(@Body LostFoundRequest request);

    @POST("actions.php")
    Flowable<ManipulateLostFoundPetResponse> manipulateLostFound(@Body ManipulateLostFoundPet request);

    @POST("actions.php")
    Flowable<FetchChatRoomResponse> getChatRoomMsgs(@Body FetchChatRoomRequest request);

    @POST("actions.php")
    Flowable<FetchChatRoomResponse> sendMsg(@Body SendMessageRequest request);

    @POST("actions.php")
    Flowable<FetchChatRoomsResponse> getChatRooms(@Body FetchChatRoomsRequest request);

    @POST("actions.php")
    Flowable<DashboardResponse> genericDashboardRequest(@Body DashboardRequest request);

    @POST("actions.php")
    Flowable<OwnerSitterBookingsResponse> getOwnerSitterBookings(@Body OwnerSitterBookingsRequest request);

    @POST("actions.php")
    Flowable<OwnerSitterBookingsResponse> getSitterComments(@Body OwnerSitterBookingsRequest request);

    @POST("actions.php")
    Flowable<OwnerSitterBookingsResponse> rateSitterBooking(@Body RateBookingRequest request);

    @POST("actions.php")
    Flowable<ProfDetailsResponse> getProfDetails(@Body GetProfDetailsRequest request);

    @POST("actions.php")
    Flowable<ProfDetailsResponse> saveProfDetails(@Body SaveProfDetailsRequest request);

    @POST("actions.php")
    Flowable<ProfDetailsResponse> deleteProf(@Body DeleteProfRequest request);

    @POST("actions.php")
    Flowable<ProfDetailsResponse> searchProf(@Body SearchProfsRequest request);
//    @Multipart
//    @POST("actions.php")
//    Flowable<GetOwnerResponse> submitOwner(@Part("body") RequestBody body, @Part MultipartBody.Part image);
//    Flowable<GetOwnerResponse> submitOwner(@Part MultipartBody.Part image);


}
