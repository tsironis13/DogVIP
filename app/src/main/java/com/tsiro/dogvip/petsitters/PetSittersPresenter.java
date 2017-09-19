package com.tsiro.dogvip.petsitters;

import android.view.View;

import com.tsiro.dogvip.petsitters.sitter_assignment.SitterAssignmentContract;

/**
 * Created by thomatou on 9/19/17.
 */

public class PetSittersPresenter implements PetSittersContract.Presenter {

    private PetSittersContract.View mView;

    public PetSittersPresenter(PetSittersContract.View view) {
        mView = view;
    }

    @Override
    public void onBaseViewClick(View view) {
        mView.onBaseViewClick(view);
    }
}
