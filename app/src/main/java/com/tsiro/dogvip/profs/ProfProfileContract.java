package com.tsiro.dogvip.profs;

import com.tsiro.dogvip.POJO.petsitter.BookingObj;
import com.tsiro.dogvip.POJO.petsitter.OwnerSitterBookingsRequest;
import com.tsiro.dogvip.POJO.petsitter.OwnerSitterBookingsResponse;
import com.tsiro.dogvip.POJO.petsitter.RateBookingRequest;
import com.tsiro.dogvip.POJO.profs.GetUserProfRequest;
import com.tsiro.dogvip.POJO.profs.GetUserProfResponse;
import com.tsiro.dogvip.app.Lifecycle;

/**
 * Created by giannis on 30/9/2017.
 */

public interface ProfProfileContract {

    interface View extends Lifecycle.View {
        void onSuccess(GetUserProfResponse response);
        void onError(int resource);
    }

    interface ViewModel extends Lifecycle.ViewModel {
        void getUserProf(GetUserProfRequest request);
        void setRequestState(int state);
    }

}
