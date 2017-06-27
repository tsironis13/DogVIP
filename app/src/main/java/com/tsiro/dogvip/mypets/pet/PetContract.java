package com.tsiro.dogvip.mypets.pet;

import com.tsiro.dogvip.POJO.mypets.owner.OwnerObj;
import com.tsiro.dogvip.POJO.mypets.pet.PetObj;
import com.tsiro.dogvip.app.Lifecycle;

/**
 * Created by giannis on 4/6/2017.
 */

public class PetContract {

    interface View extends Lifecycle.View {
        void onSuccess(OwnerObj response);
        void onError(int resource, boolean msglength);
    }

    interface ViewModel extends Lifecycle.ViewModel {
        void submitPet(PetObj request);
        void setRequestState(int state);
    }

}
