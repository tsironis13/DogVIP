package com.tsiro.dogvip.petsitters.petsitter;

import android.app.Activity;
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
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
import com.rey.material.widget.SnackBar;
import com.tsiro.dogvip.POJO.DialogActions;
import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.lovematch.LoveMatchRequest;
import com.tsiro.dogvip.POJO.mypets.owner.OwnerObj;
import com.tsiro.dogvip.POJO.petsitter.PetSitterObj;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.dashboard.DashboardActivity;
import com.tsiro.dogvip.databinding.ActivityPetSitterBinding;
import com.tsiro.dogvip.databinding.ActivityPetSittersBinding;
import com.tsiro.dogvip.petsitters.PetSittersActivity;
import com.tsiro.dogvip.petsitters.petsitter.other_details.PetSitterOtherDtlsActivity;
import com.tsiro.dogvip.requestmngrlayer.PetSitterRequestManager;
import com.tsiro.dogvip.utilities.DialogPicker;
import com.tsiro.dogvip.utilities.common.CommonUtls;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by giannis on 6/9/2017.
 */

public class PetSitterActivity extends BaseActivity implements PetSitterContract.View {

    private static final String debugTag = PetSitterActivity.class.getSimpleName();
    private PetSitterContract.ViewModel mViewModel;
    private ActivityPetSitterBinding mBinding;
    private String mToken, imageAction;
    private PetSitterObj petSitterObj;
    private Snackbar mSnackBar;
    private AwesomeValidation mAwesomeValidation;
    private CommonUtls mCommonUtls;
    private File output, fileToUpload;
    private Uri photoURI, galleryURI;
    private int state;
    private boolean initializeImagePickerDialog, addPetSitter, imageuploading, viewLoadedFirstTime;//use this to hide the form when an error occurs for the first time

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_pet_sitter);
        setSupportActionBar(mBinding.incltoolbar.toolbar);
//        mSnackBar = mBinding.snckBr;
        mCommonUtls = getCommonUtls();
        mViewModel = new PetSitterViewModel(PetSitterRequestManager.getInstance());
        mToken = getMyAccountManager().getAccountDetails().getToken();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, AppConfig.cities);
        mBinding.cityEdt.setAdapter(adapter);
        if (getSupportActionBar()!= null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        configureActivity(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Disposable disp = RxView.clicks(mBinding.retryBtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                configureActivity(null);
            }
        });
        RxEventBus.add(this, disp);
        Disposable disp2 = RxView.clicks(mBinding.ageEdt).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Object o) throws Exception {
                Bundle args = new Bundle();
                args.putInt(getResources().getString(R.string.dialog_type), 0);
                DialogFragment dialogFragment = new DialogPicker();
                dialogFragment.setArguments(args);
                dialogFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        RxEventBus.add(this, disp2);
        Disposable disp3 = RxEventBus.createSubject(AppConfig.DIALOG_ACTION, 0).observeEvents(DialogActions.class).subscribe(new Consumer<DialogActions>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull DialogActions obj) throws Exception {
                if (obj.getAction().equals(getResources().getString(R.string.date_pick_action))) {
                    if (obj.getCode() == AppConfig.STATUS_OK) {
                        mBinding.getPetsitter().setDisplayage(obj.getDisplay_date());
                        mBinding.getPetsitter().setAge(obj.getDate());
                    } else {
                        showSnackBar(getResources().getString(R.string.invalid_date), "", Snackbar.LENGTH_LONG,  getResources().getString(R.string.close)).subscribe();
                    }
                } else {
                    petSitterObj.setDisplayage("");
                }
            }
        });
        RxEventBus.add(this, disp3);
        Disposable disp4 = RxView.clicks(mBinding.nextBtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                if (!mBinding.getProcessing()) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(getResources().getString(R.string.parcelable_obj), mBinding.getPetsitter());
                    Intent intent = new Intent(PetSitterActivity.this, PetSitterOtherDtlsActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
            }
        });
        RxEventBus.add(this, disp4);
        Disposable disp5 = RxView.clicks(mBinding.profileImgv).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                if (!mBinding.getProcessing()) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        initializeDialog(getResources().getString(R.string.pick_image_dialog), getResources().getString(R.string.new_image_desc), getResources().getString(R.string.new_image_title), getResources().getString(R.string.gallery), getResources().getString(R.string.camera));
                    } else {
                        requestUserPermission();
                    }
                    dismissSnackBar();
                }
            }
        });
        RxEventBus.add(this, disp5);
        Disposable disp6 = RxView.clicks(mBinding.clearImgv).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Object o) throws Exception {
                initializeDialog(getResources().getString(R.string.clear_image_dialog), getResources().getString(R.string.clear_image_desc), getResources().getString(R.string.clear_image_title), getResources().getString(R.string.cancel), getResources().getString(R.string.confirm));
            }
        });
        RxEventBus.add(this, disp6);
        if (initializeImagePickerDialog) {
            initializeDialog(getResources().getString(R.string.pick_image_dialog), getResources().getString(R.string.new_image_desc), getResources().getString(R.string.new_image_title), getResources().getString(R.string.gallery), getResources().getString(R.string.camera));
            initializeImagePickerDialog = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        RxEventBus.unregister(this);
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
        outState.putBoolean("test", addPetSitter);
        outState.putBoolean(getResources().getString(R.string.view_loaded_first_time), viewLoadedFirstTime);
        if (petSitterObj != null) outState.putParcelable(getResources().getString(R.string.parcelable_obj), mBinding.getPetsitter());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog();
        if (mAwesomeValidation != null) mAwesomeValidation.clear();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (imageuploading) {
            showSnackBar(getResources().getString(R.string.image_uploading_on_progress), "", Snackbar.LENGTH_LONG,  getResources().getString(R.string.close)).subscribe();
        } else {
            startActivity(new Intent(this, PetSittersActivity.class));
            finish();
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @android.support.annotation.NonNull String[] permissions, @android.support.annotation.NonNull int[] grantResults) {
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
                int saveinstance_state = state;
                if (requestCode == AppConfig.ACTION_IMAGE_CAPTURE) {
                    uri = mCommonUtls.getUriForFile(output);
                    state = 2;
                } else {
                    state = 1;
                    uri = data.getData();
                    galleryURI = uri;
                }
                boolean isinvalid = isImageValid(uri, state);
                if (isinvalid && saveinstance_state == 3) state = 3;
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
    public void onSuccess(PetSitterObj response) {
        dismissDialog();
        viewLoadedFirstTime = true;
        if (mBinding.getHasError()) mBinding.setHasError(false);
        mBinding.setPetsitter(response);
        if (response.getId() != 0) {
//            RxEventBus.add(this, showSnackBar(R.style.SnackBarMultiLine, getResources().getString(R.string.success_action), getResources().getString(R.string.close)).subscribe());
            setTitle(getResources().getString(R.string.edit_owner));
            addPetSitter = false;
            mBinding.setAddpetsitter(false);
            mBinding.nestedScrlview.smoothScrollTo(0, 0);
            if (response.getImageurl() != null && !response.getImageurl().equals("")) {
                state = 3;
                mBinding.setImgstate(state);
                petSitterObj.setImageurl(response.getImageurl());
                setPetSitterProfileImg(Uri.parse(response.getImageurl()));
            }
        } else {
            setTitle(getResources().getString(R.string.add_owner));
            addPetSitter = true;
            mBinding.setAddpetsitter(true);
        }
    }

    @Override
    public void onError(int resource, boolean msglength) {
        dismissDialog();
        if (!viewLoadedFirstTime) {
            mBinding.setHasError(true);
            mBinding.setErrorText(getResources().getString(R.string.error));
        } else {
            RxEventBus.add(this, showSnackBar(getResources().getString(resource), "", Snackbar.LENGTH_LONG,  getResources().getString(R.string.close)).subscribe());
        }
//        if (mBinding.getHasError()) {
//            mBinding.setErrorText(getResources().getString(R.string.error));
//        } else {
//
//        }
    }

    @Override
    public void onImageActionSuccess(Image image) {
        mBinding.setProcessing(false);
        imageuploading = false;
        if (image.getCode() == AppConfig.STATUS_OK) {
            if (image.getAction().equals(getResources().getString(R.string.delete_petsitter_image))) {
                state = 0;
                mBinding.setImgstate(state);
                setPetSitterProfileImg(null);
//                petSitterObj.setImageurl("");
                mBinding.getPetsitter().setImageurl("");
                imageAction = "";
            } else {
                mBinding.setImgstate(state);
                petSitterObj.setImageurl(image.getImageurl());
                mCommonUtls.deleteAppStorage(output);
            }
        } else {
            showSnackBar(getResources().getString(R.string.error), imageAction, Snackbar.LENGTH_INDEFINITE, getResources().getString(R.string.retry)).delay(400, TimeUnit.MILLISECONDS).subscribe(new Consumer<String>() {
                @Override
                public void accept(@io.reactivex.annotations.NonNull String action) throws Exception {
                    imageAction = action;
                    if (action.equals(getResources().getString(R.string.delete_petsitter_image))) {
                        deleteImg(mBinding.getPetsitter().getId());
                    } else if (action.equals(getResources().getString(R.string.upload_petsitter_img))){
                        uploadImage();
                    }
                }
            });
        }
    }

    @Override
    public void onImageActionError() {
        mBinding.setProcessing(false);
        imageuploading = false;
        showSnackBar(getResources().getString(R.string.error), imageAction, Snackbar.LENGTH_INDEFINITE, getResources().getString(R.string.retry)).delay(200, TimeUnit.MILLISECONDS).subscribe(new Consumer<String>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull String action) throws Exception {
                imageAction = action;
                if (action.equals(getResources().getString(R.string.delete_petsitter_image))) {
                    imageAction = action;
//                    deleteImg(ownerObj.getId());
                } else if (action.equals(getResources().getString(R.string.upload_petsitter_img))) {
                    uploadImage();
                }
            }
        });
    }

    private void deleteImg(int id) {
        if (isNetworkAvailable()) {
            Image image = new Image();
            image.setAction(getResources().getString(R.string.delete_petsitter_image));
            image.setAuthtoken(mToken);
            image.setId(id);
            imageAction = getResources().getString(R.string.delete_petsitter_image);
            mBinding.setProcessing(true);
            mViewModel.deleteImage(image);
        } else {
            showSnackBar(getResources().getString(R.string.no_internet_connection), "", Snackbar.LENGTH_LONG, getResources().getString(R.string.close)).subscribe();
        }
    }

    private void submitForm() {
        hideSoftKeyboard();
        mAwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        mAwesomeValidation.addValidation(mBinding.nameEdt, ".*\\S.*", getResources().getString(R.string.required_field));
        mAwesomeValidation.addValidation(mBinding.surnameEdt, ".*\\S.*", getResources().getString(R.string.required_field));
        mAwesomeValidation.addValidation(mBinding.ageEdt, ".*\\S.*", getResources().getString(R.string.required_field));
        mAwesomeValidation.addValidation(mBinding.phoneEdt, ".*\\S.*", getResources().getString(R.string.required_field));
//        mAwesomeValidation.addValidation(mBinding.ageEdt, "^(0?[1-9]|[1-9][0-9])$", getResources().getString(R.string.required_field));
        if (mAwesomeValidation.validate()) {
            if (isNetworkAvailable()) {
//                ownerObj.setState(state);
                if (!Arrays.asList(AppConfig.cities).contains(mBinding.cityEdt.getText().toString())) {
                    RxEventBus.add(this, showSnackBar(getResources().getString(R.string.city_no_match), "", Snackbar.LENGTH_LONG, getResources().getString(R.string.close)).subscribe());
                } else {
                    petSitterObj.setAuthtoken(mToken);
                    petSitterObj.setName(mBinding.nameEdt.getText().toString());
                    petSitterObj.setSurname(mBinding.surnameEdt.getText().toString());
                    petSitterObj.setDisplayage(mBinding.ageEdt.getText().toString());
                    petSitterObj.setAge(mBinding.getPetsitter().getAge());
                    petSitterObj.setCity(mBinding.cityEdt.getText().toString());
                    petSitterObj.setPhone(mBinding.phoneEdt.getText().toString());
                    if (mBinding.getAddpetsitter()) {
                        petSitterObj.setAction(getResources().getString(R.string.add_petsitter));
                        initializeProgressDialog(getResources().getString(R.string.please_wait));
                        mViewModel.petSitterRelatedActions(petSitterObj);
                    } else {
                        if (!mBinding.getProcessing()) {
                            petSitterObj.setAction(getResources().getString(R.string.edit_petsitter));
                            petSitterObj.setSubaction(getResources().getString(R.string.edit_base_details));
                            petSitterObj.setId(mBinding.getPetsitter().getId());
//                            if (petSitterObj.getImageurl() !=null && !petSitterObj.getImageurl().equals("")) petSitterObj.setImageurl(petSitterObj.getImageurl());

                            initializeProgressDialog(getResources().getString(R.string.please_wait));
                            mViewModel.petSitterRelatedActions(petSitterObj);
                        }
                    }
                }
            } else {
                showSnackBar(getResources().getString(R.string.no_internet_connection), "", Snackbar.LENGTH_LONG, getResources().getString(R.string.close)).subscribe();
            }
        }
    }

    private void configureActivity(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            viewLoadedFirstTime = savedInstanceState.getBoolean(getResources().getString(R.string.view_loaded_first_time));
            if (viewLoadedFirstTime) {
                petSitterObj = savedInstanceState.getParcelable(getResources().getString(R.string.parcelable_obj));
                addPetSitter = savedInstanceState.getBoolean("test");
                if (addPetSitter) {
                    setTitle(getResources().getString(R.string.add_owner));
                } else {
                    setTitle(getResources().getString(R.string.edit_owner));
                    if (petSitterObj.getImageurl() != null && !petSitterObj.getImageurl().equals("")) {
                        state = 3;
                        mBinding.setImgstate(state);
                        setPetSitterProfileImg(Uri.parse(petSitterObj.getImageurl()));
                    }
                }
                mBinding.setAddpetsitter(addPetSitter);
                mBinding.setPetsitter(petSitterObj);
            } else {
                mBinding.setHasError(true);
                mBinding.setErrorText(getResources().getString(R.string.error));
            }
        } else {
            if (isNetworkAvailable()) {
                mBinding.setAddpetsitter(true);

                initializeProgressDialog(getResources().getString(R.string.please_wait));
                petSitterObj = new PetSitterObj();
                petSitterObj.setAuthtoken(mToken);
                petSitterObj.setAction(getResources().getString(R.string.get_petsitter_details));
                mViewModel.petSitterRelatedActions(petSitterObj);
            } else {
//                menuItem.setVisible(false);
                mBinding.setHasError(true);
                mBinding.setErrorText(getResources().getString(R.string.no_internet_connection));
            }
        }
    }

    private boolean isImageValid(Uri uri, int state) {
        Image img = mCommonUtls.isImageSizeValid(uri, state, output);
        fileToUpload = img.getImage();
        if (!img.isInvalid_filetype()) {
            if (img.getSize()) {
                if (img.isDeleteLocalFile()) output = img.getImage();
                if (isNetworkAvailable()) {
                    setPetSitterProfileImg(uri);
                } else {
                    showSnackBar(getResources().getString(R.string.no_internet_connection), "", Snackbar.LENGTH_LONG, getResources().getString(R.string.close)).subscribe();
                }
            } else {
                showSnackBar(getResources().getString(R.string.invalid_image_size), "", Snackbar.LENGTH_LONG, getResources().getString(R.string.close)).subscribe();
                output = null;
            }
        } else {
            showSnackBar(getResources().getString(R.string.invalid_file_type), "", Snackbar.LENGTH_LONG, getResources().getString(R.string.close)).subscribe();
        }
        return img.isInvalid_filetype();
    }

    private void setPetSitterProfileImg(final Uri uri) {
        Object object = uri;
        if (state == 0) object = R.drawable.default_person;
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
                    .apply(new RequestOptions().centerCrop().error(R.drawable.default_person))
                    .into(mBinding.profileImgv);
        }
    }

    private void uploadImage() {
        MultipartBody.Part mfile;
        try {
            mfile = mCommonUtls.getRequestFileBody(fileToUpload);
            RequestBody action = RequestBody.create(okhttp3.MultipartBody.FORM, getResources().getString(R.string.upload_petsitter_img));
            RequestBody token = RequestBody.create(okhttp3.MultipartBody.FORM, mToken);
            RequestBody id = RequestBody.create(okhttp3.MultipartBody.FORM, mBinding.getPetsitter().getId()+"");
            imageAction = getResources().getString(R.string.upload_petsitter_img);
            mViewModel.uploadImage(action, token, id, mfile);
            mBinding.setProcessing(true);
            imageuploading = true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void requestUserPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showSnackBar(getResources().getString(R.string.grant_permission), "", Snackbar.LENGTH_LONG, getResources().getString(R.string.close)).subscribe();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, AppConfig.READ_EXTERNAL_STORAGE_PERMISSION_RESULT_CODE);
            }
        } else {
            initializeDialog(getResources().getString(R.string.pick_image_dialog), getResources().getString(R.string.new_image_desc), getResources().getString(R.string.new_image_title), getResources().getString(R.string.gallery), getResources().getString(R.string.camera));
        }
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
                                output = mCommonUtls.createImageFile(".jpg");
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
                                    List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                                    for (ResolveInfo resolveInfo : resInfoList) {
                                        String packageName = resolveInfo.activityInfo.packageName;
                                        grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                    }
                                }
                                startActivityForResult(intent, AppConfig.ACTION_IMAGE_CAPTURE);
                            }
                        } else {
                            showSnackBar(getResources().getString(R.string.no_camera_available), "", Snackbar.LENGTH_LONG, getResources().getString(R.string.close)).subscribe();
                        }
                    } else {//negative action
                        state = 1;
//                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.image_choose_label)), AppConfig.EXTERNAL_CONTENT_URI);
                    }
                } else if (obj.getAction().equals(getResources().getString(R.string.clear_image_dialog))) {
                    if (obj.getSelected_action() == 1) {//positive action
                        deleteImg(mBinding.getPetsitter().getId());
                    }
                }
            }
        });
        RxEventBus.add(this, dialogDisp);
    }

    private io.reactivex.Observable<String> showSnackBar(final String msg, final String action, final int length, final String actionText) {
        return io.reactivex.Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@io.reactivex.annotations.NonNull final ObservableEmitter<String> subscriber) throws Exception {
                mSnackBar = Snackbar
                        .make(mBinding.coordlt, msg, length)
                        .setAction(actionText, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                subscriber.onNext(action);
                            }
                        });

                mSnackBar.setActionTextColor(ContextCompat.getColor(PetSitterActivity.this, android.R.color.black));
                View sbView = mSnackBar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                sbView.setBackgroundColor(ContextCompat.getColor(PetSitterActivity.this, android.R.color.white));
                textView.setTextColor(ContextCompat.getColor(PetSitterActivity.this, android.R.color.holo_red_light));
                mSnackBar.show();
            }
        });
    }

    private void dismissSnackBar() {
        if (mSnackBar != null && mSnackBar.isShown()) mSnackBar.dismiss();
    }
}
