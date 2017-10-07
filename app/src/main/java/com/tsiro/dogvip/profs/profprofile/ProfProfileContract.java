package com.tsiro.dogvip.profs.profprofile;

import com.tsiro.dogvip.POJO.profs.DeleteProfRequest;
import com.tsiro.dogvip.POJO.profs.GetProfDetailsRequest;
import com.tsiro.dogvip.POJO.profs.ProfDetailsResponse;
import com.tsiro.dogvip.POJO.profs.SaveProfDetailsRequest;
import com.tsiro.dogvip.POJO.profs.SearchProfsRequest;
import com.tsiro.dogvip.app.Lifecycle;

/**
 * Created by giannis on 30/9/2017.
 */

public interface ProfProfileContract {

    interface View extends Lifecycle.View {
        void onSuccess(ProfDetailsResponse response);
        void onError(int resource);
        void onPhoneIconViewClick(android.view.View view);
        void onMessageIconClick(android.view.View view);
        void onViewClick(android.view.View view);
    }


    interface ViewModel extends Lifecycle.ViewModel {
        void searchProf(SearchProfsRequest request);
        void getProfDetails(GetProfDetailsRequest request);
        void saveProfDetails(SaveProfDetailsRequest request);
        void deleteProf(DeleteProfRequest request);
        void setRequestState(int state);
        void onPhoneIconViewClick(android.view.View view);
        void onMessageIconClick(android.view.View view);
        void onViewClick(android.view.View view);
    }

}
