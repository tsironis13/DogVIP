package com.tsiro.dogvip.profs;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tsiro.dogvip.POJO.profs.GetUserProfRequest;
import com.tsiro.dogvip.POJO.profs.GetUserProfResponse;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivityProfProfileBinding;
import com.tsiro.dogvip.requestmngrlayer.ProfRequestManager;

/**
 * Created by giannis on 30/9/2017.
 */

public class ProfProfileActivity extends BaseActivity implements ProfProfileContract.View {

    private ActivityProfProfileBinding mBinding;
    private ProfProfileContract.ViewModel mViewModel;
    private String mToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_prof_profile);
        mViewModel = new ProfProfileViewModel(ProfRequestManager.getInstance());
        mToken = getMyAccountManager().getAccountDetails().getToken();

        if (getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            setTitle(getResources().getString(R.string.pet_sitters_title));
        }
        initializeView();
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void onSuccess(GetUserProfResponse response) {
        dismissDialog();
    }

    @Override
    public void onError(int resource) {
        dismissDialog();
    }

    private void initializeView() {
        if (isNetworkAvailable()) {
            GetUserProfRequest request = new GetUserProfRequest();
            request.setAction(getResources().getString(R.string.get_user_prof));
            request.setAuthtoken(mToken);
            initializeProgressDialog(getResources().getString(R.string.please_wait));
            mViewModel.getUserProf(request);
        } else {

        }
    }
}
