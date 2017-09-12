package com.tsiro.dogvip.petsitters.sitter_assignment;

import android.view.View;

import com.tsiro.dogvip.petsitters.petsitter.other_details.PetSitterOtherDtlsContract;

/**
 * Created by thomatou on 9/12/17.
 */

public class SitterAssignmentPresenter implements SitterAssignmentContract.Presenter {

    private SitterAssignmentContract.View mView;

    public SitterAssignmentPresenter(SitterAssignmentContract.View view) {
        mView = view;
    }

    @Override
    public void onServiceCheckBoxClick(View view) {
        mView.onServiceCheckBoxClick(view);
    }

}
