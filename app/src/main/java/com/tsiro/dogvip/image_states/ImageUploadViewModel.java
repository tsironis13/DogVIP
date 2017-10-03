package com.tsiro.dogvip.image_states;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.TestImage;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.di.qualifiers.ActivityContext;
import com.tsiro.dogvip.di.qualifiers.ApplicationContext;
import com.tsiro.dogvip.requestmngrlayer.MyPetsRequestManager;
import com.tsiro.dogvip.utilities.GenericImageUploadSubscriber;
import com.tsiro.dogvip.utilities.ImageUploadSubscriber;
import com.tsiro.dogvip.utilities.ImageUtls;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.AsyncProcessor;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by giannis on 1/10/2017.
 */

public class ImageUploadViewModel implements Lifecycle.GenericImageUploadViewModel {

    private static final String debugTag = ImageUploadViewModel.class.getSimpleName();
    private MyPetsRequestManager mMyPetsRequestManager;

    private ImageUtls imageUtls;
    private Lifecycle.ImageUploadView mViewClback;
    private Disposable mUploadDisp;
    private AsyncProcessor<Image> imageProcessor;
    private int requestState;
    private Context mContext;

    @Inject
    public ImageUploadViewModel(MyPetsRequestManager mMyPetsRequestManager, ImageUtls imageUtls, @ApplicationContext Context context) {
        this.mMyPetsRequestManager = mMyPetsRequestManager;
        this.imageUtls = imageUtls;
        this.mContext = context;
    }

    @Override
    public void onViewAttached(Lifecycle.View viewCallback) {
        mViewClback = (Lifecycle.ImageUploadView) viewCallback;
        Log.e(debugTag, mViewClback + " view callback");
    }

    @Override
    public void onViewResumed() {}

    @Override
    public void onViewResumed(State state) {
        if (mUploadDisp != null && requestState != AppConfig.REQUEST_RUNNING && imageProcessor != null)
            imageProcessor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new GenericImageUploadSubscriber(state, this));
    }

    @Override
    public void onViewDetached() {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            Log.e(debugTag, "view detached ");
            mViewClback = null;
            if (mUploadDisp != null) mUploadDisp.dispose();
        }
    }

    public void loadImage(TestImage testImage) {
        Log.e(debugTag, "loadImage");
//        this.testImage = testImage;
        testImage.loadImage(testImage);
    }

    public Intent grantTempFilePermission(Intent intent, File tempFile) {
        if (tempFile != null) {
            Uri photoURI = imageUtls.getUriForFile(tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ClipData clip = ClipData.newUri(mContext.getContentResolver(), "photo", photoURI);
                intent.setClipData(clip);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            else {
                List<ResolveInfo> resInfoList = mContext.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    mContext.grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
            }
            return intent;
        }
        return null;
    }

    public void isGalleryImageValid(TestImage testImage, State state) {
        Log.e(debugTag, imageUtls + " IMAGEUTLS");
        prepareImageToUpload(imageUtls.isGalleryImageValid(testImage.getUri(), testImage), state, testImage.getUri(), testImage.getFile());
    }

    public void isCameraImageValid(TestImage testImage, State state) {
        prepareImageToUpload(imageUtls.isCameraImageValid(testImage.getFile(), testImage), state, testImage.getUri(), testImage.getFile());
    }


    public void loadImageUrl(String url, State state) {
        mViewClback.loadImageUrl(Uri.parse(url), state, null);
    }

    public void setRequestState(int state) {
        this.requestState = state;
    }

    public void noImageUrl() {
        mViewClback.noImageUrl();
    }

    public void deleteImage(State state, Image image) {
        if (requestState != AppConfig.REQUEST_RUNNING) {
            imageProcessor = AsyncProcessor.create();
            mUploadDisp = imageProcessor
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(new GenericImageUploadSubscriber(state, this));

            mMyPetsRequestManager.deleteImage(image, this).subscribe(imageProcessor);
        }
    }

    public void onSuccessUpload(Image image) {
        mUploadDisp = null;
        if (mViewClback !=null) mViewClback.onSuccessUpload(image);
        Log.e(debugTag, "onSuccessUpload");
//        mViewClback.onSuccessUpload();
    }

    public void onErrorUpload() {
        mUploadDisp = null;
        mViewClback.onErrorUpload();
        if (mViewClback != null) requestState = AppConfig.REQUEST_NONE;
//        mViewClback.onErrorUpload();
    }

    public void onSuccessDelete() {
        mUploadDisp = null;
        if (mViewClback !=null) mViewClback.onSuccessDelete();
        Log.e(debugTag, "onSuccessDelete");
//        mViewClback.onSuccessDelete();
    }

    public void onErrorDelete() {
        mUploadDisp = null;
        mViewClback.onErrorDelete();
        if (mViewClback != null) requestState = AppConfig.REQUEST_NONE;
//        mViewClback.onErrorDelete();
    }

    public void uploadImage(State state, String uploadAction, String mToken, int uploadId, File file) {
        MultipartBody.Part mfile;
        try {
            mfile = imageUtls.getRequestFileBody(file);
            RequestBody action = RequestBody.create(okhttp3.MultipartBody.FORM, uploadAction);
            RequestBody token = RequestBody.create(okhttp3.MultipartBody.FORM, mToken);
            RequestBody id = RequestBody.create(okhttp3.MultipartBody.FORM, uploadId+"");
            if (requestState != AppConfig.REQUEST_RUNNING) {
                imageProcessor = AsyncProcessor.create();
                mUploadDisp = imageProcessor.subscribeWith(new GenericImageUploadSubscriber(state, this));
                mViewClback.imageUploading();
                mMyPetsRequestManager.uploadImage(action, token, id, mfile, this).subscribe(imageProcessor);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void prepareImageToUpload(TestImage testImage, State state, Uri uri, File file) {
        if (!testImage.isFileTypeInvalid()) {
            if (testImage.isHasValidSize()) {
                Log.e(debugTag, mViewClback + " null?? here view callback");
                mViewClback.loadImageUrl(uri, state, file);
//                if (img.isDeleteLocalFile()) output = img.getImage();
//                if (isNetworkAvailable()) {
////                    setOwnerProfileImg(uri);
//                } else {
//                    showSnackBar(R.style.SnackBarSingleLine, getResources().getString(R.string.no_internet_connection), "").subscribe();
//                }
            } else {
//                showSnackBar(R.style.SnackBarSingleLine, getResources().getString(R.string.invalid_image_size), "").subscribe();
            }
        } else {
//            showSnackBar(R.style.SnackBarSingleLine, getResources().getString(R.string.invalid_file_type), "").subscribe();
        }
    }

//    @Override
//    public void onSuccessImageAction(Image image) {
//        mUploadDisp = null;
////        Log.e(debugTag, "kjsdsd "+testImage.getState());
////        if (mViewClback != null) testImage.getState().onSuccess(this);
//        if (mViewClback !=null) mViewClback.onSuccessImageAction(image);
//    }
//
//    @Override
//    public void onErrorImageAction() {
//        Log.e(debugTag, "onerror");
//        mUploadDisp = null;
//        mViewClback.onErrorImageAction();
////        testImage.getState().onError(this);
//        if (mViewClback != null) requestState = AppConfig.REQUEST_NONE;
//
//    }
}
