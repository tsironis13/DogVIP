package com.tsiro.dogvip.lostfound.manipulatelostpet;

import com.tsiro.dogvip.POJO.lostfound.LostFoundRequest;
import com.tsiro.dogvip.POJO.lostfound.LostFoundResponse;
import com.tsiro.dogvip.POJO.lostfound.ManipulateLostFoundPet;
import com.tsiro.dogvip.POJO.lostfound.ManipulateLostFoundPetResponse;
import com.tsiro.dogvip.app.Lifecycle;

/**
 * Created by giannis on 11/7/2017.
 */

public interface ManipulateLostPetContract {

    interface View extends Lifecycle.View {
        void onSuccess(ManipulateLostFoundPetResponse response);
        void onError(int resource);
        void onBaseViewClick(android.view.View view);
    }

    interface ViewModel extends Lifecycle.ViewModel {
        void manipulateLostPet(ManipulateLostFoundPet request);
        void setRequestState(int state);
    }

}
