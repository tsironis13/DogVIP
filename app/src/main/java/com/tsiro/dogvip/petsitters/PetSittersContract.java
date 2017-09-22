package com.tsiro.dogvip.petsitters;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.petsitter.BookingObj;
import com.tsiro.dogvip.POJO.petsitter.OwnerSitterBookingsRequest;
import com.tsiro.dogvip.POJO.petsitter.OwnerSitterBookingsResponse;
import com.tsiro.dogvip.POJO.petsitter.PetSitterObj;
import com.tsiro.dogvip.POJO.petsitter.RateBookingRequest;
import com.tsiro.dogvip.app.Lifecycle;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by giannis on 14/9/2017.
 */

public interface PetSittersContract {

    interface Presenter {
        void onBaseViewClick(android.view.View view);
    }

    interface FrgmtView {
        void onBaseViewClick(android.view.View view);
    }

    interface View extends Lifecycle.View {
        void onBaseViewClick(android.view.View view);
        void onFragmentRcvItemClick(BookingObj bookingObj, int type);//0 sitter booking, 1 owner booking
        void onSuccess(OwnerSitterBookingsResponse response);
        void onError(int resource);
    }

    interface ViewModel extends Lifecycle.ViewModel {
        void getSitterComments(OwnerSitterBookingsRequest request);
        void getPendingBookings(OwnerSitterBookingsRequest request);
        void getBookingDetails(OwnerSitterBookingsRequest request);
        void getOwnerSitterBookings(OwnerSitterBookingsRequest request);
        void rateSitterBoooking(RateBookingRequest request);
        void setRequestState(int state);
    }

}
