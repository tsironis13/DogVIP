package com.tsiro.dogvip.lostfound;

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
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.adapters.RecyclerViewAdapter;
import com.tsiro.dogvip.databinding.LostEntriesFrgmtBinding;

import java.util.ArrayList;

/**
 * Created by giannis on 12/7/2017.
 */

public class FoundEntriesFrgmt extends Fragment implements LostFoundContract.FrgmtView {

    private View mView;
    private LostEntriesFrgmtBinding mBinding;
    private ArrayList<LostFoundObj> data;
    private LostActivityPresenter presenter;
    private LostFoundContract.View viewContract;

    public static FoundEntriesFrgmt newInstance(ArrayList<LostFoundObj> list) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("data", list);
        FoundEntriesFrgmt frgmt = new FoundEntriesFrgmt();
        frgmt.setArguments(bundle);
        return frgmt;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if ( context instanceof Activity) this.viewContract = (LostFoundContract.View) context;
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

    @Override
    public void onBaseViewClick(View view) {
        int position = (int) view.getTag();
        viewContract.onBaseViewClick(data.get(position), 1);
    }

    @Override
    public void onShareIconClick(View view) {
        int position = (int) view.getTag();
        viewContract.onShareIconClick(data.get(position));
    }

    private void initializeRcV() {
        presenter = new LostActivityPresenter(this);
        mBinding.rcv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.rcv.setNestedScrollingEnabled(false);
        mBinding.rcv.setHasFixedSize(true);
        RecyclerViewAdapter rcvAdapter = new RecyclerViewAdapter(R.layout.found_pet_rcv_row) {
            @Override
            protected Object getObjForPosition(int position, ViewDataBinding mBinding) {
                return data.get(position);
            }

            @Override
            protected int getLayoutIdForPosition(int position) {
                return R.layout.found_pet_rcv_row;
            }

            @Override
            protected int getTotalItems() {
                return data.size();
            }

            @Override
            protected Object getClickListenerObject() {
                return presenter;
            }
        };
        mBinding.rcv.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mBinding.rcv.setAdapter(rcvAdapter);
    }
}
