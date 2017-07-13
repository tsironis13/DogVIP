package com.tsiro.dogvip.lostfound;

import android.content.Intent;
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

import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;
import com.tsiro.dogvip.POJO.lostfound.LostFoundObj;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.adapters.RecyclerViewAdapter;
import com.tsiro.dogvip.databinding.LostEntriesFrgmtBinding;
import com.tsiro.dogvip.lostfound.manipulatelostpet.ManipulateLostPetActivity;

import java.util.ArrayList;

/**
 * Created by giannis on 10/7/2017.
 */

public class LostEntriesFrgmt extends Fragment {

    private static final String debugTag = LostEntriesFrgmt.class.getSimpleName();
    private View mView;
    private LostEntriesFrgmtBinding mBinding;
    private ArrayList<LostFoundObj> data;
    private RecyclerViewAdapter rcvAdapter;

    public static LostEntriesFrgmt newInstance(ArrayList<LostFoundObj> list) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("data", list);
        LostEntriesFrgmt frgmt = new LostEntriesFrgmt();
        frgmt.setArguments(bundle);
        return frgmt;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.lost_entries_frgmt, container, false);
            mView = mBinding.getRoot();
        }
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            data = savedInstanceState.getParcelableArrayList(getResources().getString(R.string.data));
        } else {
            if (getArguments() != null) {
                data = getArguments().getParcelableArrayList(getResources().getString(R.string.data));
            }
        }
        Log.e(debugTag, data+"");
        if (data != null) {
            if (data.isEmpty()) {
                mBinding.setHaserror(true);
                mBinding.setErrortext(getResources().getString(R.string.no_data));
            } else {
                initializeRcV();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(getResources().getString(R.string.data), data);
    }

    private void initializeRcV() {
//        presenter = new LostActivityPresenter(this);
        mBinding.rcv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.rcv.setNestedScrollingEnabled(false);
        mBinding.rcv.setHasFixedSize(true);
        rcvAdapter = new RecyclerViewAdapter(R.layout.my_lost_pet_rcv_row) {
            @Override
            protected Object getObjForPosition(int position, ViewDataBinding mBinding) {
                return data.get(position);
            }
            @Override
            protected int getLayoutIdForPosition(int position) {
                return R.layout.my_lost_pet_rcv_row;
            }
            @Override
            protected int getTotalItems() {
                return data.size();
            }

            @Override
            protected Object getClickListenerObject() {
                return null;
            }
        };
        mBinding.rcv.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mBinding.rcv.setAdapter(rcvAdapter);
    }
}
