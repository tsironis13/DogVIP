package com.tsiro.dogvip.petsitters;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tsiro.dogvip.POJO.lostfound.LostFoundObj;
import com.tsiro.dogvip.POJO.petsitter.OwnerSitterBookingsResponse;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.adapters.RecyclerViewAdapter;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.databinding.MyPetAssignmentsFrgmtBinding;
import com.tsiro.dogvip.lostfound.LostEntriesFrgmt;
import com.tsiro.dogvip.lostfound.LostFoundContract;

import java.util.ArrayList;

/**
 * Created by giannis on 3/9/2017.
 */

public class MyPetAssignmentsFrgmt extends Fragment implements PetSittersContract.FrgmtView {

    private static final String debugTag = MyPetAssignmentsFrgmt.class.getSimpleName();
    private MyPetAssignmentsFrgmtBinding mBinding;
    private View mView;
    private OwnerSitterBookingsResponse data;
    private RecyclerViewAdapter rcvAdapter;
    private PetSittersContract.View viewContract;
    private BookingsPresenter presenter;

    public static MyPetAssignmentsFrgmt newInstance(OwnerSitterBookingsResponse data) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("parcelable_obj", data);
        MyPetAssignmentsFrgmt frgmt = new MyPetAssignmentsFrgmt();
        frgmt.setArguments(bundle);
        return frgmt;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if ( context instanceof Activity) this.viewContract = (PetSittersContract.View) context;
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
        if (getArguments() != null) {
            data = getArguments().getParcelable(getResources().getString(R.string.parcelable_obj));
        }
        initializeView(data);
    }

    @Override
    public void onBaseViewClick(View view) {
        viewContract.onFragmentRcvItemClick(data.getOwner_bookings().get((int)view.getTag()), 1);
    }

    private void initializeView(final OwnerSitterBookingsResponse data) {
        if (data.getCode() == AppConfig.STATUS_OK || data.getCode() == AppConfig.ERROR_NO_SITTER_EXISTS) {
            if (data.getOwner_bookings().isEmpty()) {
                mBinding.setHaserror(true);
                mBinding.setErrortext(getResources().getString(R.string.no_data));
            } else {
                presenter = new BookingsPresenter(this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                rcvAdapter = new RecyclerViewAdapter(R.layout.my_pet_assignments_rcv_row) {
                    @Override
                    protected Object getObjForPosition(int position, ViewDataBinding mBinding) {
                        return data.getOwner_bookings().get(position);
                    }

                    @Override
                    protected int getLayoutIdForPosition(int position) {
                        return R.layout.my_pet_assignments_rcv_row;
                    }

                    @Override
                    protected int getTotalItems() {
                        return data.getOwner_bookings().size();
                    }

                    @Override
                    protected Object getClickListenerObject() {
                        return presenter;
                    }
                };
                mBinding.rcv.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                mBinding.rcv.setLayoutManager(linearLayoutManager);
                mBinding.rcv.setNestedScrollingEnabled(false);
                mBinding.rcv.setAdapter(rcvAdapter);
            }
        } else if (data.getCode() == AppConfig.ERROR_NO_OWNER_AND_SITTER_EXIST || data.getCode() == AppConfig.ERROR_NO_OWNER_EXISTS) {
            mBinding.setHaserror(true);
            mBinding.setErrortext(getResources().getString(R.string.no_owner_exists));
        }
    }
}
