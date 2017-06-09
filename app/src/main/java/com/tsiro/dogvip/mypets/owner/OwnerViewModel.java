package com.tsiro.dogvip.mypets.owner;

import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.requestmngrlayer.MyPetsRequestManager;

/**
 * Created by giannis on 4/6/2017.
 */

public class OwnerViewModel implements OwnerContract.ViewModel {

    private MyPetsRequestManager mMyPetsRequestManager;

    public OwnerViewModel(MyPetsRequestManager mMyPetsRequestManager) {
        this.mMyPetsRequestManager = mMyPetsRequestManager;
    }

    @Override
    public void onViewAttached(Lifecycle.View viewCallback) {

    }

    @Override
    public void onViewResumed() {

    }

    @Override
    public void onViewDetached() {

    }

    @Override
    public void submitOwner() {

    }

    @Override
    public void setRequestState(int state) {

    }
}
