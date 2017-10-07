package com.tsiro.dogvip.petsitters.petsitter.other_details;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;

import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.petsitter.PetSitterObj;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.databinding.PlaceInfoFrgmtBinding;
import com.tsiro.dogvip.uploadimagecontrol.petsitter_place.PetSitterPlaceUploadControlActivity;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import java.util.ArrayList;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static android.app.Activity.RESULT_OK;

/**
 * Created by giannis on 8/9/2017.
 */

public class PlaceInfoFrgmt extends Fragment {

    private static final String debugTag = PlaceInfoFrgmt.class.getSimpleName();
    private PlaceInfoFrgmtBinding mBinding;
    private View mView;
    private PetSitterOtherDtlsContract.View viewContract;
    private PetSitterObj petSitterObj;

    public static PlaceInfoFrgmt newInstance(PetSitterObj petSitterObj) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("parcelable_obj", petSitterObj);
        PlaceInfoFrgmt placeInfoFrgmt = new PlaceInfoFrgmt();
        placeInfoFrgmt.setArguments(bundle);
        return placeInfoFrgmt;
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
            mBinding = DataBindingUtil.inflate(inflater, R.layout.place_info_frgmt, container, false);
            mView = mBinding.getRoot();
        }
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_row, getResources().getStringArray(R.array.pet_place));
        mBinding.petplaceSpnr.setAdapter(adapter);

        ArrayAdapter<String> cadapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_row, getResources().getStringArray(R.array.place_type));
        mBinding.placetypeSpnr.setAdapter(cadapter);
        if (savedInstanceState != null) {
            petSitterObj = savedInstanceState.getParcelable(getResources().getString(R.string.parcelable_obj));
            mBinding.setObj(petSitterObj);
            if (mBinding.getObj().getPetplace() == 1) mBinding.petplaceSpnr.setSelection(1);
            if (mBinding.getObj().getPlacetype() == 1) mBinding.placetypeSpnr.setSelection(1);
        } else {
            if (getArguments() != null) {
                petSitterObj = getArguments().getParcelable(getResources().getString(R.string.parcelable_obj));
                mBinding.setObj(petSitterObj);
                if (mBinding.getObj().getPetplace() == 1) mBinding.petplaceSpnr.setSelection(1);
                if (mBinding.getObj().getPlacetype() == 1) mBinding.placetypeSpnr.setSelection(1);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Disposable disp = RxView.clicks(mBinding.previousBtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                viewContract.onPreviousClick(1);
            }
        });
        RxEventBus.add(this, disp);
        Disposable disp1 = RxView.clicks(mBinding.placeImgBtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                Bundle bundle = new Bundle();
                bundle.putInt(getResources().getString(R.string.id), petSitterObj.getId());
                bundle.putParcelableArrayList(getResources().getString(R.string.urls), petSitterObj.getUrls());
                Intent intent = new Intent(getActivity(), PetSitterPlaceUploadControlActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, AppConfig.REFRESH_PETSITTER_PLACE_IMAGES);
            }
        });
        RxEventBus.add(this, disp1);
    }

    @Override
    public void onPause() {
        super.onPause();
        RxEventBus.unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getResources().getString(R.string.parcelable_obj), petSitterObj);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == AppConfig.REFRESH_PETSITTER_PLACE_IMAGES) {
                ArrayList<Image> urls = data.getExtras().getParcelableArrayList(getResources().getString(R.string.urls));
                if (urls != null) {
                    petSitterObj.setUrls(urls);
                    viewContract.updatePetSitterPlaceImages(urls);
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.submit_form_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_item:
                submitDetails();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void submitDetails() {
        hideSoftKeyboard();
        petSitterObj.setPetplace(mBinding.petplaceSpnr.getSelectedItemPosition());
        petSitterObj.setPlacetype(mBinding.placetypeSpnr.getSelectedItemPosition());
        petSitterObj.setAddress(mBinding.placeaddressEdt.getText().toString());
        viewContract.onOtherDetailsSubmit(petSitterObj);
    }

    public void hideSoftKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
