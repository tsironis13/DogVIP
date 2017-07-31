package com.tsiro.dogvip.lostfound.manipulatefoundpet;

import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.POJO.DialogActions;
import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.lostfound.LostFoundObj;
import com.tsiro.dogvip.POJO.lostfound.ManipulateLostFoundPet;
import com.tsiro.dogvip.POJO.lostfound.ManipulateLostFoundPetResponse;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivityManipulateFoundPetBinding;
import com.tsiro.dogvip.lostfound.LostActivity;
import com.tsiro.dogvip.lostfound.manipulatelostpet.ManipulateLostPetContract;
import com.tsiro.dogvip.lostfound.manipulatelostpet.ManipulateLostPetViewModel;
import com.tsiro.dogvip.mypets.MyPetsActivity;
import com.tsiro.dogvip.requestmngrlayer.ManipulateLostFoundRequestManager;
import com.tsiro.dogvip.utilities.DialogPicker;
import com.tsiro.dogvip.utilities.common.CommonUtls;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by giannis on 13/7/2017.
 */

public class ManipulateFoundPetActivity extends BaseActivity implements ManipulateFoundPetContract.View {

    private ActivityManipulateFoundPetBinding mBinding;
    private String mToken, action;
    private Bundle bundle;
    private ManipulateLostFoundPet baseObj;
    private LostFoundObj lostFoundObj;
    private ManipulateFoundPetContract.ViewModel mViewModel;
    private File output, filetToUpload;
    private Uri photoURI, galleryURI;
    private int state;
    private CommonUtls mCommonUtls;
    private boolean initializeImagePickerDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_manipulate_found_pet);
        mToken = getMyAccountManager().getAccountDetails().getToken();
        mViewModel = new ManipulateFoundPetViewModel(ManipulateLostFoundRequestManager.getInstance());
        mCommonUtls = getCommonUtls();
        setSupportActionBar(mBinding.incltoolbar.toolbar);
        if (savedInstanceState != null) {
            action = savedInstanceState.getString(getResources().getString(R.string.action));
            state = savedInstanceState.getInt(getResources().getString(R.string.imageview_state));
            if (state == 1) {
                String strUri = savedInstanceState.getString(getResources().getString(R.string.gallery_uri));
                if (strUri != null) {
                    galleryURI = Uri.parse(strUri);
//                    isImageValid(galleryURI, state);
                }
            } else if (state == 2) {
                output=(File) savedInstanceState.getSerializable(getResources().getString(R.string.image_output));
            }
        } else {
            if (getIntent() != null) {
                action = getIntent().getExtras().getString(getResources().getString(R.string.action));
            }
        }
        if (getSupportActionBar()!= null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        configureActivity(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Bundle args = new Bundle();
        Disposable disp = RxView.clicks(mBinding.dateLostEdt).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Object o) throws Exception {
                args.putInt(getResources().getString(R.string.dialog_type), 0);
                DialogFragment dialogFragment = new DialogPicker();
                dialogFragment.setArguments(args);
                dialogFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });
        RxEventBus.add(this, disp);
        Disposable disp1 = RxView.clicks(mBinding.timeLostEdt).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Object o) throws Exception {
                args.putInt(getResources().getString(R.string.dialog_type), 1);
                DialogFragment dialogFragment = new DialogPicker();
                dialogFragment.setArguments(args);
                dialogFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        RxEventBus.add(this, disp1);
        Disposable disp2 = RxEventBus.createSubject(AppConfig.DIALOG_ACTION, 0).observeEvents(DialogActions.class).subscribe(new Consumer<DialogActions>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull DialogActions obj) throws Exception {
                if (obj.getAction().equals(getResources().getString(R.string.date_pick_action))) {
                    lostFoundObj.setDisplaydate(obj.getDisplay_date());
                    lostFoundObj.setDate_lost(obj.getDate());
                } else if (obj.getAction().equals(getResources().getString(R.string.time_pick_action))) {
                    lostFoundObj.setTime_lost(obj.getDisplay_date());
                } else if (obj.getAction().equals(getResources().getString(R.string.dialog_cancel_date_action))) {
                    lostFoundObj.setDisplaydate("");
                } else {
                    lostFoundObj.setTime_lost("");
                }
            }
        });
        RxEventBus.add(this, disp2);
        Disposable disp3 = RxView.clicks(mBinding.profileImgv).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                if (!mBinding.getProcessing()) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        initializeDialog(getResources().getString(R.string.pick_image_dialog), getResources().getString(R.string.new_image_desc), getResources().getString(R.string.new_image_title), getResources().getString(R.string.gallery), getResources().getString(R.string.camera));
                    } else {
                        requestUserPermission();
                    }
                }
            }
        });
        RxEventBus.add(this, disp3);
        Disposable disp4 = RxView.clicks(mBinding.clearImgv).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Object o) throws Exception {
                initializeDialog(getResources().getString(R.string.clear_image_dialog), getResources().getString(R.string.clear_image_desc), getResources().getString(R.string.clear_image_title), getResources().getString(R.string.cancel), getResources().getString(R.string.confirm));
            }
        });
        RxEventBus.add(this, disp4);
    }

    @Override
    protected void onPause() {
        super.onPause();
        RxEventBus.unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /* 4 imageview states to handle
         * state 0, no image selected,
         * state 1, gallery image selected,
         * state 2, camera image selected,
         * state 3, url image fetched
         */
        if (state == 1) {
            if (galleryURI != null) outState.putString(getResources().getString(R.string.gallery_uri), galleryURI.toString());
        } else if (state == 2){
            if (output != null) outState.putSerializable(getResources().getString(R.string.image_output), output);
        }
        outState.putInt(getResources().getString(R.string.imageview_state), state);
        outState.putParcelable(getResources().getString(R.string.parcelable_obj), lostFoundObj);
        outState.putString(getResources().getString(R.string.action), action);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        bundle = new Bundle();
        bundle.putInt(getResources().getString(R.string.type), 1);
        startActivity(new Intent(this, LostActivity.class).putExtras(bundle));
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case AppConfig.READ_EXTERNAL_STORAGE_PERMISSION_RESULT_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) initializeImagePickerDialog = true;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConfig.EXTERNAL_CONTENT_URI || requestCode == AppConfig.ACTION_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Uri uri = null;
                if (requestCode == AppConfig.ACTION_IMAGE_CAPTURE) {
                    uri = mCommonUtls.getUriForFile(output);
                    state = 2;
                } else {
                    state = 1;
                    uri = data.getData();
                    galleryURI = uri;
                }
                isImageValid(uri, state);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.submit_form_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_item:
                submitForm();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void onImageActionSuccess(Image image) {
        mBinding.setProcessing(false);
        if (image.getCode() == AppConfig.STATUS_OK) {
            if (image.getAction().equals(getResources().getString(R.string.delete_found_pet_img))) {
                state = 0;
                mBinding.setImgstate(state);
                setOwnerProfileImg(null);
                lostFoundObj.setMain_image("");
//                imageAction = "";
            } else {
                lostFoundObj.setMain_image(image.getImageurl());
                mBinding.setImgstate(state);
                mCommonUtls.deleteAppStorage(output);
            }
        } else {
            showSnackBar(R.style.SnackBarWithAction, getResources().getString(R.string.error), "");
        }
    }

    @Override
    public void onImageActionError() {
        mBinding.setProcessing(false);
        showSnackBar(R.style.SnackBarWithAction, getResources().getString(R.string.error), "");
    }

    @Override
    public void onSuccess(ManipulateLostFoundPetResponse response) {
        dismissDialog();
        bundle = new Bundle();
        bundle.putInt(getResources().getString(R.string.type), 1);
        startActivity(new Intent(this, LostActivity.class).putExtras(bundle));
        finish();
    }

    @Override
    public void onError(int resource) {
        dismissDialog();
        showSnackBar(R.style.SnackBarSingleLine, getResources().getString(resource), "");
    }

    private void configureActivity(Bundle savedInstanceState) {
        baseObj = new ManipulateLostFoundPet();
        if (action.equals(getResources().getString(R.string.add_ownr))) {
            mBinding.setAdd(true);
            setTitle(getResources().getString(R.string.add_owner));
            lostFoundObj = new LostFoundObj();
            baseObj.setAction(getResources().getString(R.string.add_found_pet));
            baseObj.setLostfoundobj(lostFoundObj);
        } else {
            setTitle(getResources().getString(R.string.edit));
            baseObj.setAction(getResources().getString(R.string.edit_found_pet));
            if (savedInstanceState != null) {
                lostFoundObj = savedInstanceState.getParcelable(getResources().getString(R.string.parcelable_obj));
            } else {
                lostFoundObj = getIntent().getExtras().getParcelable(getResources().getString(R.string.parcelable_obj));
            }
            if (lostFoundObj.getMain_image() != null && !lostFoundObj.getMain_image().equals("")) {
                state = 3;
                mBinding.setImgstate(state);
                setOwnerProfileImg(Uri.parse(lostFoundObj.getMain_image()));
            }
            baseObj.setLostfoundobj(lostFoundObj);
        }
        mBinding.setObj(baseObj);
    }

    private void submitForm() {
        hideSoftKeyboard();
        AwesomeValidation mAwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        mAwesomeValidation.addValidation(mBinding.dateLostEdt, ".*\\S.*", getResources().getString(R.string.required_field));
        mAwesomeValidation.addValidation(mBinding.timeLostEdt, ".*\\S.*", getResources().getString(R.string.required_field));
        mAwesomeValidation.addValidation(mBinding.locationEdt, ".*\\S.*", getResources().getString(R.string.required_field));
        mAwesomeValidation.addValidation(mBinding.contactPhoneEdt, ".*\\S.*", getResources().getString(R.string.required_field));
        if (mAwesomeValidation.validate()) {
            if (isNetworkAvailable()) {
                baseObj.setAuthtoken(mToken);
                lostFoundObj.setLocation(mBinding.locationEdt.getText().toString());
                lostFoundObj.setPhone(mBinding.contactPhoneEdt.getText().toString());
                lostFoundObj.setInfo(mBinding.moreInfoEdt.getText().toString());
                mViewModel.manipulateFoundPet(baseObj);
                initializeProgressDialog(getResources().getString(R.string.please_wait));
            } else {
                showSnackBar(R.style.SnackBarSingleLine, getResources().getString(R.string.no_internet_connection), "");
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void requestUserPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showSnackBar(R.style.SnackBarMultiLine, getResources().getString(R.string.grant_permission), "");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, AppConfig.READ_EXTERNAL_STORAGE_PERMISSION_RESULT_CODE);
            }
        } else {
            initializeDialog(getResources().getString(R.string.pick_image_dialog), getResources().getString(R.string.new_image_desc), getResources().getString(R.string.new_image_title), getResources().getString(R.string.gallery), getResources().getString(R.string.camera));
        }
    }

    private void isImageValid(Uri uri, int state) {
        Image img = mCommonUtls.isImageSizeValid(uri, state, output);
        filetToUpload = img.getImage();
        if (img.getSize()) {
            if (isNetworkAvailable()) {
                setOwnerProfileImg(uri);
            } else {
                showSnackBar(R.style.SnackBarSingleLine, getResources().getString(R.string.no_internet_connection), "");
            }
        } else {
            showSnackBar(R.style.SnackBarSingleLine, getResources().getString(R.string.invalid_image_size), "");
            output = null;
        }
    }

    private void uploadImage() {
        MultipartBody.Part mfile;
        try {
            mfile = mCommonUtls.getRequestFileBody(filetToUpload);
            RequestBody action = RequestBody.create(okhttp3.MultipartBody.FORM, getResources().getString(R.string.upload_found_pet_img));
            RequestBody token = RequestBody.create(okhttp3.MultipartBody.FORM, mToken);
            RequestBody id = RequestBody.create(okhttp3.MultipartBody.FORM, lostFoundObj.getId()+"");
//            imageAction = getResources().getString(R.string.upload_ownr_img);
            mViewModel.uploadImage(action, token, id, mfile);
            mBinding.setProcessing(true);
//            ((MyPetsActivity)getActivity()).setImageuploading(true);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void setOwnerProfileImg(final Uri uri) {
        Object object = uri;
        if (state == 0) object = R.drawable.ic_pets;
        if (object != null) {
            Glide.with(this)
                    .load(object)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }
                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            if (state == 3) return false;
                            if (state == 1 || state == 2) uploadImage();
                            if (state == 0) output = null;
                            return false;
                        }
                    })
                    .transition(withCrossFade())
                    .apply(new RequestOptions().centerCrop().error(R.drawable.ic_pets))
                    .into(mBinding.profileImgv);
        }
    }

    private void deleteImg(int id) {
        Image image = new Image();
        image.setAction(getResources().getString(R.string.delete_found_pet_img));
        image.setAuthtoken(mToken);
        image.setId(id);
//        imageAction = getResources().getString(R.string.delete_owner_image);
        mBinding.setProcessing(true);
        mViewModel.deleteImage(image);
    }

    private void initializeDialog(String action, final String desc, String title, String positiveBtnTxt, String negativeBtnTxt) {
        Disposable dialogDisp = initializeGenericDialog(action, desc, title, positiveBtnTxt, negativeBtnTxt).subscribe(new Consumer<DialogActions>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull DialogActions obj) {
                if (obj.getAction().equals(getResources().getString(R.string.pick_image_dialog))) {
                    if (obj.getSelected_action() == 1) {//positive action (camera image)
                        state = 2;
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // Ensure that there's a camera activity to handle the intent
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            try {
                                output = mCommonUtls.createImageFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (output != null) {
                                photoURI = mCommonUtls.getUriForFile(output);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                }
                                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    ClipData clip = ClipData.newUri(getContentResolver(), "photo", photoURI);
                                    intent.setClipData(clip);
                                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                }
                                else {
                                    List<ResolveInfo> resInfoList= getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                                    for (ResolveInfo resolveInfo : resInfoList) {
                                        String packageName = resolveInfo.activityInfo.packageName;
                                        grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                    }
                                }
                                startActivityForResult(intent, AppConfig.ACTION_IMAGE_CAPTURE);
                            }
                        } else {
                            showSnackBar(R.style.SnackBarMultiLine, getResources().getString(R.string.no_camera_available), "");
                        }
                    } else {//negative action
                        state = 1;
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent , AppConfig.EXTERNAL_CONTENT_URI);
                    }
                } else if (obj.getAction().equals(getResources().getString(R.string.clear_image_dialog))) {
                    if (obj.getSelected_action() == 1) {//positive action
                        deleteImg(lostFoundObj.getId());
                    }
                }
            }
        });
        RxEventBus.add(this, dialogDisp);
    }

    private void showSnackBar(final int style, final String msg, final String action) {
        if (mBinding.snckBr != null) {
            mBinding.snckBr.applyStyle(style);
            mBinding.snckBr.text(msg);
            mBinding.snckBr.show();
        }
    }

}
