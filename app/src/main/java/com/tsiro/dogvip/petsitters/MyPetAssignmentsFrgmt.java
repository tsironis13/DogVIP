package com.tsiro.dogvip.petsitters;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tsiro.dogvip.POJO.lostfound.LostFoundObj;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.databinding.MyPetAssignmentsFrgmtBinding;
import com.tsiro.dogvip.lostfound.LostEntriesFrgmt;

import java.util.ArrayList;

/**
 * Created by giannis on 3/9/2017.
 */

public class MyPetAssignmentsFrgmt extends Fragment {

    private MyPetAssignmentsFrgmtBinding mBinding;
    private View mView;

    public static MyPetAssignmentsFrgmt newInstance() {
        Bundle bundle = new Bundle();
//        bundle.putParcelableArrayList("data", list);
        MyPetAssignmentsFrgmt frgmt = new MyPetAssignmentsFrgmt();
        frgmt.setArguments(bundle);
        return frgmt;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.my_pet_assignments_frgmt, container, false);
            mView = mBinding.getRoot();
        }
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
