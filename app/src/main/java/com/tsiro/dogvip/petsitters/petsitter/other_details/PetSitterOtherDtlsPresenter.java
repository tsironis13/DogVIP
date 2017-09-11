package com.tsiro.dogvip.petsitters.petsitter.other_details;

import android.view.View;

import com.tsiro.dogvip.petprofile.PetProfileContract;
import com.tsiro.dogvip.petsitters.petsitter.PetSitterContract;

/**
 * Created by giannis on 9/9/2017.
 */

public class PetSitterOtherDtlsPresenter implements PetSitterOtherDtlsContract.Presenter {

    private PetSitterOtherDtlsContract.ViewFragment mView;

    public PetSitterOtherDtlsPresenter(PetSitterOtherDtlsContract.ViewFragment view) {
        mView = view;
    }

    @Override
    public void onServiceCheckBoxClick(View view) {
        mView.onServiceCheckBoxClick(view);
    }
}
