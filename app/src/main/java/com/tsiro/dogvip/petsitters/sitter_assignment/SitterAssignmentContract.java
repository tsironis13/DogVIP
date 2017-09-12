package com.tsiro.dogvip.petsitters.sitter_assignment;

/**
 * Created by thomatou on 9/12/17.
 */

public interface SitterAssignmentContract {

    interface Presenter {
        void onServiceCheckBoxClick(android.view.View view);
    }

    interface View {
        void onServiceCheckBoxClick(android.view.View view);
    }

}
