package com.tsiro.dogvip.petsitters;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.petsitter.OwnerSitterBookingsRequest;
import com.tsiro.dogvip.POJO.petsitter.OwnerSitterBookingsResponse;
import com.tsiro.dogvip.POJO.petsitter.PetSitterObj;
import com.tsiro.dogvip.app.Lifecycle;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by giannis on 14/9/2017.
 */

public interface PetSittersContract {

    interface View extends Lifecycle.View {
        void onSuccess(OwnerSitterBookingsResponse response);
        void onError(int resource);
    }

    interface ViewModel extends Lifecycle.ViewModel {
        void getOwnerSitterBookings(OwnerSitterBookingsRequest request);
        void setRequestState(int state);
    }

}
