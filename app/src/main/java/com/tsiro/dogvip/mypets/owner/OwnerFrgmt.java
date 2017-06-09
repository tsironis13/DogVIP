package com.tsiro.dogvip.mypets.owner;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.jakewharton.rxbinding2.view.RxView;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.tsiro.dogvip.Manifest;
import com.tsiro.dogvip.MyPetsActivity;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.BaseFragment;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.OwnerFrgmtBinding;
import com.tsiro.dogvip.requestmngrlayer.MyPetsRequestManager;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static android.app.Activity.RESULT_OK;

/**
 * Created by giannis on 4/6/2017.
 */

public class OwnerFrgmt extends BaseFragment implements OwnerContract.View {

    private static final String debugTag = OwnerFrgmt.class.getSimpleName();
    private View mView;
    private OwnerFrgmtBinding mBinding;
    private OwnerContract.ViewModel mOwnerFrgmtViewModel;
    private Dialog.Builder mBuilder;
    private Uri mUserProfileSelectedImg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.owner_frgmt, container, false);
            mView = mBinding.getRoot();
        }
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mOwnerFrgmtViewModel = new OwnerViewModel(MyPetsRequestManager.getInstance());
    }

    @Override
    public void onResume() {
        super.onResume();
        Disposable disp = RxView.clicks(mBinding.profilePhoneLlt).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                initializePickProfilePhotoDialog();
            }
        });
        RxEventBus.add(this, disp);
    }

    @Override
    public void onPause() {
        super.onPause();
        RxEventBus.unregister(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case AppConfig.READ_EXTERNAL_STORAGE_PERMISSION_RESULT_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setUserProfileImg(mUserProfileSelectedImg);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConfig.EXTERNAL_CONTENT_URI || requestCode == AppConfig.ACTION_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                mUserProfileSelectedImg = data.getData();
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    setUserProfileImg(mUserProfileSelectedImg);
                } else {
                    requestUserPermission(mUserProfileSelectedImg);
                }
            }
        }
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return mOwnerFrgmtViewModel;
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onError(int code) {

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestUserPermission(Uri uri) {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ((MyPetsActivity)getActivity()).showSnackBar(R.style.SnackBarMultiLine, getResources().getString(R.string.grant_permission));
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, AppConfig.READ_EXTERNAL_STORAGE_PERMISSION_RESULT_CODE);
            }
        } else {
            setUserProfileImg(uri);
        }
    }

    private void setUserProfileImg(final Uri uri) {
        if (uri != null) {
            Glide.with(getActivity())
                    .load(uri)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .circleCrop())
                    .into(mBinding.profileImgv);
        }
    }

    private void initializePickProfilePhotoDialog() {
        mBuilder = new SimpleDialog.Builder(R.style.SimpleDialogLight){
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                super.onPositiveActionClicked(fragment);
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, AppConfig.ACTION_IMAGE_CAPTURE);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , AppConfig.EXTERNAL_CONTENT_URI);
            }
        };

        ((SimpleDialog.Builder)mBuilder).message("Let Google help apps determine location. This means sending anonymous location data to Google, even when no apps are running.")
                .title("Use Google's location service?")
                .positiveAction("AGREE")
                .negativeAction("DISAGREE");
        DialogFragment fragment = DialogFragment.newInstance(mBuilder);
        fragment.show(getFragmentManager(), null);
    }
}
