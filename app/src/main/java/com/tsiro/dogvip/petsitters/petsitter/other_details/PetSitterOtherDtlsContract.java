package com.tsiro.dogvip.petsitters.petsitter.other_details;

import android.view.View;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.petsitter.PetSitterObj;
import com.tsiro.dogvip.app.Lifecycle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giannis on 9/9/2017.
 */

public interface PetSitterOtherDtlsContract {

    interface Presenter {
        void onServiceCheckBoxClick(android.view.View view);
    }

    interface View extends Lifecycle.View {
        void onOtherDetailsSubmit(PetSitterObj petSitterObj);
        void onNextClick(int position, PetSitterObj petSitterObj);
        void updatePetSitterPlaceImages(ArrayList<Image> urls);
        void onPreviousClick(int position);
    }

    interface ViewFragment {
        void onServiceCheckBoxClick(android.view.View view);
    }

}
