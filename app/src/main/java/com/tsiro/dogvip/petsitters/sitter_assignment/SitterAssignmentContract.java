package com.tsiro.dogvip.petsitters.sitter_assignment;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.lovematch.LoveMatchResponse;
import com.tsiro.dogvip.POJO.petsitter.BookingObj;
import com.tsiro.dogvip.POJO.petsitter.PetSitterObj;
import com.tsiro.dogvip.POJO.petsitter.SearchedSittersResponse;
import com.tsiro.dogvip.app.Lifecycle;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by thomatou on 9/12/17.
 */

public interface SitterAssignmentContract {

    interface Presenter {
        void onServiceCheckBoxClick(android.view.View view);
        void onPhoneImageViewClick(android.view.View view);
        void onBaseViewClick(android.view.View view);
    }

    interface ViewModel extends Lifecycle.ViewModel {
        void searchSitters(PetSitterObj petSitterObj);
        void sendBooking(BookingObj bookingObj);
        void setRequestState(int state);
    }

    interface View {
        void onSuccess(SearchedSittersResponse response);
        void onError(int resource);
        void onServiceCheckBoxClick(android.view.View view);
        void onPhoneImageViewClick(android.view.View view);
        void onBaseViewClick(android.view.View view);
    }

}
