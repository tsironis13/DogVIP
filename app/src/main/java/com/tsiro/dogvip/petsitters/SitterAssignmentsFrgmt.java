package com.tsiro.dogvip.petsitters;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tsiro.dogvip.POJO.lostfound.LostFoundObj;
import com.tsiro.dogvip.POJO.petsitter.OwnerSitterBookingsResponse;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.databinding.SitterAssignmentsFrgmtBinding;
import com.tsiro.dogvip.lostfound.LostEntriesFrgmt;

import java.util.ArrayList;

/**
 * Created by giannis on 3/9/2017.
 */

public class SitterAssignmentsFrgmt extends Fragment {

    private static final String debugTag = SitterAssignmentsFrgmt.class.getSimpleName();
    private SitterAssignmentsFrgmtBinding mBinding;
    private View mView;
    private OwnerSitterBookingsResponse data;

    public static SitterAssignmentsFrgmt newInstance(OwnerSitterBookingsResponse data) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("parcelable_obj", data);
        SitterAssignmentsFrgmt frgmt = new SitterAssignmentsFrgmt();
        frgmt.setArguments(bundle);
        return frgmt;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.sitter_assignments_frgmt, container, false);
            mView = mBinding.getRoot();
        }
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            data = savedInstanceState.getParcelable(getResources().getString(R.string.parcelable_obj));
        } else {
            if (getArguments() != null) {
                data = getArguments().getParcelable(getResources().getString(R.string.parcelable_obj));
            }
        }
        initializeView(data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getResources().getString(R.string.parcelable_obj), data);
    }

    private void initializeView(OwnerSitterBookingsResponse data) {
        if (data.getCode() == AppConfig.STATUS_OK || data.getCode() == AppConfig.ERROR_NO_OWNER_EXISTS) {
            Log.e(debugTag, "sitter assignemnts");
        } else if (data.getCode() == AppConfig.ERROR_NO_OWNER_AND_SITTER_EXIST || data.getCode() == AppConfig.ERROR_NO_SITTER_EXISTS) {
            mBinding.setHaserror(true);
            mBinding.setErrortext(getResources().getString(R.string.no_petsitter_exists));
        }
    }
}
