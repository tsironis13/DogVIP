package com.tsiro.dogvip.petsitters.petsitter.other_details;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;

import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.POJO.petsitter.PetSitterObj;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.databinding.BaseInfoFrgmtBinding;
import com.tsiro.dogvip.lostfound.LostFoundContract;
import com.tsiro.dogvip.petsitters.petsitter.PetSitterActivity;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by giannis on 8/9/2017.
 */

public class BaseInfoFrgmt extends Fragment {

    private static final String debugTag = BaseInfoFrgmt.class.getSimpleName();
    private BaseInfoFrgmtBinding mBinding;
    private View mView;
    private PetSitterOtherDtlsContract.View viewContract;
    private PetSitterObj petSitterObj;
    private int petSizeIndex;

    public static BaseInfoFrgmt newInstance(PetSitterObj petSitterObj) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("parcelable_obj", petSitterObj);
        BaseInfoFrgmt baseInfoFrgmt = new BaseInfoFrgmt();
        baseInfoFrgmt.setArguments(bundle);
        return baseInfoFrgmt;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if ( context instanceof Activity) this.viewContract = (PetSitterOtherDtlsContract.View) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.base_info_frgmt, container, false);
            mView = mBinding.getRoot();
        }
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            petSitterObj = getArguments().getParcelable(getResources().getString(R.string.parcelable_obj));
            mBinding.setObj(petSitterObj);
            Log.e(debugTag, mBinding.getObj().getPetsize() + " petsize");
            if (mBinding.getObj().getPetsize() == 1) {
                mBinding.smallSizeRdBtn.setChecked(true);
            } else if (mBinding.getObj().getPetsize() == 2) {
                mBinding.normalSizeRdBtn.setChecked(true);
            }
//            if (mBinding.getObj().getPetsize() == 1) {
//                mBinding.petSizeNormalChbx.setChecked(true);
//            } else if (mBinding.getObj().getPetsize() == 2) {
//                mBinding.petSizeSmallChbx.setChecked(true);
//            } else if (mBinding.getObj().getPetsize() == 3) {
//                mBinding.petSizeSmallChbx.setChecked(true);
//                mBinding.petSizeNormalChbx.setChecked(true);
//            }
//            Log.e(debugTag, petSitterObj.getId() + " ID, NAME: " + petSitterObj.getName());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Disposable disp = RxView.clicks(mBinding.nextBtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                saveDetails();
            }
        });
        RxEventBus.add(this, disp);
        Disposable disp1 = RxView.clicks(mBinding.previousBtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                startActivity(new Intent(getActivity(), PetSitterActivity.class));
                getActivity().finish();
            }
        });
        RxEventBus.add(this, disp1);
        Disposable disp2 = RxView.clicks(mBinding.petSizeSmallChbx).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
//                checkPetSize();
            }
        });
        RxEventBus.add(this, disp2);
        Disposable disp3 = RxView.clicks(mBinding.petSizeNormalChbx).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
//                checkPetSize();
            }
        });
//        mBinding.getObj().setPetsize(0);
        mBinding.petSizeRadioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (i == R.id.smallSizeRdBtn) {
                    mBinding.getObj().setPetsize(1);
                } else {
                    mBinding.getObj().setPetsize(2);
                }
                Log.e(debugTag, mBinding.getObj().getPetsize() + " pet size");
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        RxEventBus.unregister(this);
    }

    private void checkPetSize() {
        if (mBinding.petSizeSmallChbx.isChecked() && mBinding.petSizeNormalChbx.isChecked()) {
            mBinding.getObj().setPetsize(3); //both small and normal size
        } else if (mBinding.petSizeSmallChbx.isChecked() && !mBinding.petSizeNormalChbx.isChecked()) {
            mBinding.getObj().setPetsize(2); //small size
        } else if (!mBinding.petSizeSmallChbx.isChecked() && mBinding.petSizeNormalChbx.isChecked()) {
            mBinding.getObj().setPetsize(1); //normal size
        } else {
            mBinding.getObj().setPetsize(0);
        }
    }

    private void saveDetails() {
        hideSoftKeyboard();
        viewContract.onNextClick(1, mBinding.getObj());
    }

    public void hideSoftKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
