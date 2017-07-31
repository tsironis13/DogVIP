package com.tsiro.dogvip.lostfound;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;
import com.tsiro.dogvip.POJO.DialogActions;
import com.tsiro.dogvip.POJO.lostfound.LostFoundObj;
import com.tsiro.dogvip.POJO.lostfound.LostFoundRequest;
import com.tsiro.dogvip.POJO.lostfound.LostFoundResponse;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.adapters.RecyclerViewAdapter;
import com.tsiro.dogvip.databinding.MyLostEntriesFrgmtBinding;
import com.tsiro.dogvip.lostfound.manipulatefoundpet.ManipulateFoundPetActivity;
import com.tsiro.dogvip.lostfound.manipulatelostpet.ManipulateLostPetActivity;
import com.tsiro.dogvip.retrofit.RetrofitFactory;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by giannis on 12/7/2017.
 */

public class MyFoundEntriesFrgmt extends Fragment {

    private View mView;
    private MyLostEntriesFrgmtBinding mBinding;
    private ArrayList<LostFoundObj> mydata;
    private RecyclerTouchListener listener;
    private RecyclerViewAdapter rcvAdapter;
    private int index;
    private LostFoundContract.View viewContract;

    public static MyFoundEntriesFrgmt newInstance(ArrayList<LostFoundObj> list) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("data", list);
        MyFoundEntriesFrgmt frgmt = new MyFoundEntriesFrgmt();
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
            mBinding = DataBindingUtil.inflate(inflater, R.layout.my_lost_entries_frgmt, container, false);
            mView = mBinding.getRoot();
        }
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mydata = savedInstanceState.getParcelableArrayList(getResources().getString(R.string.data));
        } else {
            if (getArguments() != null) {
                mydata = getArguments().getParcelableArrayList(getResources().getString(R.string.data));
            }
        }
        if (mydata != null) {
            if (mydata.isEmpty()) {
                mBinding.setHaserror(true);
                mBinding.setErrortext(getResources().getString(R.string.no_data));
            } else {
                initializeRcV();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mBinding.rcv.addOnItemTouchListener(listener);
        Disposable disp = RxView.clicks(mBinding.addLostPetFlbtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                Bundle bundle = new Bundle();
                bundle.putString(getResources().getString(R.string.action), getResources().getString(R.string.add_ownr));
                startActivity(new Intent(getActivity(), ManipulateFoundPetActivity.class).putExtras(bundle));
            }
        });
        RxEventBus.add(this ,disp);
    }

    @Override
    public void onPause() {
        super.onPause();
        RxEventBus.unregister(this);
        if (listener != null) mBinding.rcv.removeOnItemTouchListener(listener);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(getResources().getString(R.string.data), mydata);
    }

    private void initializeRcV() {
        mBinding.rcv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.rcv.setNestedScrollingEnabled(false);
        mBinding.rcv.setHasFixedSize(true);
        rcvAdapter = new RecyclerViewAdapter(R.layout.my_found_pet_rcv_row) {
            @Override
            protected Object getObjForPosition(int position, ViewDataBinding mBinding) {
                return mydata.get(position);
            }
            @Override
            protected int getLayoutIdForPosition(int position) {
                return R.layout.my_found_pet_rcv_row;
            }
            @Override
            protected int getTotalItems() {
                return mydata.size();
            }

            @Override
            protected Object getClickListenerObject() {
                return null;
            }
        };
        mBinding.rcv.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mBinding.rcv.setAdapter(rcvAdapter);
        listener = new RecyclerTouchListener(getActivity(), mBinding.rcv)
                .setSwipeOptionViews(R.id.share, R.id.edit, R.id.delete)
                .setSwipeable(R.id.baseRlt, R.id.revealRowLlt, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @Override
                    public void onSwipeOptionClicked(int viewID, int position) {
                        if (viewID == R.id.edit) {
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(getResources().getString(R.string.parcelable_obj), mydata.get(position));
                            bundle.putString(getResources().getString(R.string.action), getResources().getString(R.string.edit_ownr));
                            startActivity(new Intent(getActivity(), ManipulateFoundPetActivity.class).putExtras(bundle));
                        } else if (viewID == R.id.share) {
                            viewContract.onShareIconClick(mydata.get(position));
                        } else {
                            deleteLostPet(mydata.get(position).getId());
                            index = position;
                        }
                    }
                });
        mBinding.rcv.addOnItemTouchListener(listener);
    }

    private void deleteLostPet(final int entry_id) {
        Disposable disp =  ((LostActivity)getActivity()).initializeGenericDialog("", getResources().getString(R.string.delete_lost_found_item_desc), getResources().getString(R.string.delete_lost_found_item_title), getResources().getString(R.string.cancel), getResources().getString(R.string.confirm)).subscribe(new Consumer<DialogActions>() {
            @Override
            public void accept(@NonNull DialogActions obj) throws Exception {
                if (obj.getSelected_action() == 1) {
                    LostFoundRequest request = new LostFoundRequest();
                    request.setAction(getResources().getString(R.string.delete_found_entry));
                    request.setAuthtoken(((LostActivity)getActivity()).getmToken());
                    request.setEntry_id(entry_id);
                    if (((LostActivity)getActivity()).isNetworkAvailable()) {
//                        LostFoundViewModel viewModel = (LostFoundViewModel) ((LostActivity)getActivity()).getViewModel();
//                        viewModel.getLostPets(request);
                        RetrofitFactory.getInstance().getServiceAPI().getLostFound(request)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnError(new Consumer<Throwable>() {
                                    @Override
                                    public void accept(@NonNull Throwable throwable) throws Exception {
                                        ((LostActivity)getActivity()).dismissDialog();
                                        showSnackBar(getResources().getString(R.string.error), getResources().getString(R.string.close));
                                    }
                                })
                                .doOnNext(new Consumer<LostFoundResponse>() {
                                    @Override
                                    public void accept(@NonNull LostFoundResponse response) throws Exception {
                                        ((LostActivity)getActivity()).dismissDialog();
                                        mydata.remove(index);
                                        rcvAdapter.notifyItemRemoved(index);
                                        rcvAdapter.notifyDataSetChanged();
                                        if (mydata.size() == 0) {
                                            mBinding.setHaserror(true);
                                            mBinding.setErrortext(getResources().getString(R.string.no_data));
                                        }
                                    }
                                }).delay(400, TimeUnit.MILLISECONDS).subscribe();
                        ((LostActivity)getActivity()).initializeProgressDialog(getResources().getString(R.string.please_wait));
                    } else {
                        showSnackBar(getResources().getString(R.string.no_internet_connection), getResources().getString(R.string.close));
                    }
                }}
        });
        RxEventBus.add(this, disp);
    }

    private void showSnackBar(final String msg, final String action) {
        Snackbar snackbar = Snackbar
                .make(mBinding.coordlt, msg, Snackbar.LENGTH_LONG)
                .setAction(action, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

        snackbar.setActionTextColor(ContextCompat.getColor(getActivity(), android.R.color.black));
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.white));
        textView.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.holo_red_light));
        snackbar.show();
    }

}
