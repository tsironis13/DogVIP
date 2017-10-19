package com.tsiro.dogvip.profs.prof;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxCompoundButton;
import com.rey.material.widget.SnackBar;
import com.tsiro.dogvip.POJO.DialogActions;
import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.TestImage;
import com.tsiro.dogvip.POJO.profs.ProfDetailsResponse;
import com.tsiro.dogvip.POJO.profs.ProfObj;
import com.tsiro.dogvip.POJO.profs.SaveProfDetailsRequest;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.base.activity.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivityProfBinding;
import com.tsiro.dogvip.image_states.CameraImageState;
import com.tsiro.dogvip.image_states.GalleryImageState;
import com.tsiro.dogvip.image_states.ImageUploadViewModel;
import com.tsiro.dogvip.image_states.NoImageState;
import com.tsiro.dogvip.image_states.State;
import com.tsiro.dogvip.image_states.UrlImageState;
import com.tsiro.dogvip.profs.profprofile.ProfProfileActivity;
import com.tsiro.dogvip.profs.profprofile.ProfProfileContract;
import com.tsiro.dogvip.profs.profprofile.ProfProfileViewModel;
import com.tsiro.dogvip.requestmngrlayer.ProfRequestManager;
import com.tsiro.dogvip.utilities.ImageUtls;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

/**
 * Created by giannis on 4/10/2017.
 */

public class ProfActivity extends BaseActivity implements ProfProfileContract.View, Lifecycle.ImageUploadView {

    private static final String debugTag = ProfActivity.class.getSimpleName();
    private ActivityProfBinding mBinding;
    private List<Integer> categoriesList = new ArrayList<>();
    private ProfObj profObj;
    private Observable<Integer> indexes = Observable.fromIterable(Arrays.asList(1,2,3,4,5,6));
    private String mToken;
    private SnackBar mSnackBar;
    private SaveProfDetailsRequest saveProfDetailsRequest;
    private ProfProfileContract.ViewModel mViewModel;
//    private ImageUploadViewModel mImageUploadViewModel;
//    private ImageUtls imageUtls;
    private TestImage image;
    private boolean initializeImagePickerDialog;
    @Inject
    ImageUploadViewModel mImageUploadViewModel;
    @Inject
    ImageUtls imageUtls;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_prof);
        mSnackBar = mBinding.snckBr;
        setSupportActionBar(mBinding.incltoolbar.toolbar);
        if (getSupportActionBar()!= null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToken = getMyAccountManager().getAccountDetails().getToken();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, AppConfig.cities);
        mBinding.cityEdt.setAdapter(adapter);
//        imageUtls = new ImageUtls(this);
//        mImageUploadViewModel = new ImageUploadViewModel(new ImageActionsRequestManager(new ImageActionsAPIService(RetrofitFactory.getInstance().getServiceAPI())), imageUtls, this);
        mViewModel = new ProfProfileViewModel(ProfRequestManager.getInstance());
        saveProfDetailsRequest = new SaveProfDetailsRequest();
        mImageUploadViewModel.onViewAttached(this);

        configureActivity(savedInstanceState);
        Observable checkBxsObservable = Observable.fromIterable(Arrays.asList(
                RxCompoundButton.checkedChanges(mBinding.category1CheckBx), RxCompoundButton.checkedChanges(mBinding.category2CheckBx), RxCompoundButton.checkedChanges(mBinding.category3CheckBx), RxCompoundButton.checkedChanges(mBinding.category4CheckBx), RxCompoundButton.checkedChanges(mBinding.category5CheckBx), RxCompoundButton.checkedChanges(mBinding.category6CheckBx)));

        Observable.zip(indexes, checkBxsObservable, new BiFunction<Integer, Observable<Boolean>, Integer>(){
            @Override
            public Integer apply(@NonNull final Integer index, @NonNull Observable<Boolean> checked) throws Exception {
                checked.subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            categoriesList.add(index);
                        } else {
                            categoriesList.remove(index);
                        }

                    }
                });
                return 0;
            }
        }).subscribe();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mImageUploadViewModel.onViewResumed(image.getState());
        Disposable disp = RxView.clicks(mBinding.profileImgv).subscribe(new Consumer<Object>() {
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
        RxEventBus.add(this, disp);
        Disposable disp1 = RxView.clicks(mBinding.clearImgv).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Object o) throws Exception {
                initializeDialog(getResources().getString(R.string.clear_image_dialog), getResources().getString(R.string.clear_image_desc), getResources().getString(R.string.clear_image_title), getResources().getString(R.string.cancel), getResources().getString(R.string.confirm));
            }
        });
        RxEventBus.add(this, disp1);
        if (initializeImagePickerDialog) {
            initializeDialog(getResources().getString(R.string.pick_image_dialog), getResources().getString(R.string.new_image_desc), getResources().getString(R.string.new_image_title), getResources().getString(R.string.gallery), getResources().getString(R.string.camera));
            initializeImagePickerDialog = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImageUploadViewModel.onViewDetached();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getResources().getString(R.string.image), image);
        outState.putParcelable(getResources().getString(R.string.parcelable_obj), mBinding.getProf());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConfig.EXTERNAL_CONTENT_URI || requestCode == AppConfig.ACTION_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                if (requestCode == AppConfig.ACTION_IMAGE_CAPTURE) {
                    image.setState(new CameraImageState());
                    image.setUri(imageUtls.getUriForFile(image.getFile()));
                    mImageUploadViewModel.loadImage(image);
                } else {
                    image.setState(new GalleryImageState());
                    image.setUri(data.getData());
                    mImageUploadViewModel.loadImage(image);
                }
            }
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
    public void onSuccess(ProfDetailsResponse response) {
        dismissDialog();
        mBinding.scrollView.smoothScrollTo(0, 0);
        mBinding.setProf(response.getProf());
        startActivity(new Intent(this, ProfProfileActivity.class));
    }

    @Override
    public void onError(int resource) {
        dismissDialog();
    }

    @Override
    public void onPhoneIconViewClick(View view) {

    }

    @Override
    public void onMessageIconClick(View view) {

    }

    @Override
    public void onViewClick(View view) {

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void requestUserPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showSnackBar(R.style.SnackBarMultiLine, getResources().getString(R.string.grant_permission), "").subscribe();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, AppConfig.READ_EXTERNAL_STORAGE_PERMISSION_RESULT_CODE);
            }
        } else {
            initializeDialog(getResources().getString(R.string.pick_image_dialog), getResources().getString(R.string.new_image_desc), getResources().getString(R.string.new_image_title), getResources().getString(R.string.gallery), getResources().getString(R.string.camera));
        }
    }

    @Override
    public void noImageUrl() {
//        mBinding.setNoimagestate(true);
    }

    @Override
    public void loadImageUrl(Object obj, State state, File file) {
        imageUtls.loadImageWithGlide(obj, state, file, mBinding.profileImgv, mImageUploadViewModel, getResources().getString(R.string.upload_prof_img), mToken, profObj.getId(), R.drawable.default_person);
    }

    @Override
    public void imageUploading() {
        mBinding.setProcessing(true);
    }

    @Override
    public void onSuccessUpload(Image image) {
        mBinding.setProcessing(false);
        this.image.setState(new UrlImageState(image.getImageurl()));
        profObj.setImage_url(image.getImageurl());
    }

    @Override
    public void onErrorUpload() {
        mBinding.setProcessing(false);
        showSnackBar(R.style.SnackBarWithAction, getResources().getString(R.string.error), "aa").delay(400, TimeUnit.MILLISECONDS).subscribe(new Consumer<String>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull String action) throws Exception {
                if (action.equals(getResources().getString(R.string.delete_owner_image))) {
//                    imageAction = action;
//                    deleteImg(ownerObj.getId());
                } else {
//                        uploadImage();
                }
            }
        });
    }

    @Override
    public void onSuccessDelete() {
        mBinding.setProcessing(false);
        imageUtls.clearImageWithGlide(mBinding.profileImgv, R.drawable.default_person);
        this.image.setState(new NoImageState());
        profObj.setImage_url("");
    }

    @Override
    public void onErrorDelete() {
        mBinding.setProcessing(false);
    }

    private void configureActivity(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            profObj = savedInstanceState.getParcelable(getResources().getString(R.string.parcelable_obj));
            image = savedInstanceState.getParcelable(getResources().getString(R.string.image));
//            mImageUploadViewModel.onRetainInstanceState(image.getState());
        } else {
            if (getIntent().getExtras() != null) {
                profObj = getIntent().getExtras().getParcelable(getResources().getString(R.string.parcelable_obj));
            }
            image = new TestImage(profObj.getImage_url());
        }
        mImageUploadViewModel.loadImage(image);
        mBinding.setProf(profObj);
        if (mBinding.getProf().getId() == 0) {
            setTitle(getResources().getString(R.string.add_owner));
            saveProfDetailsRequest.setAction(getResources().getString(R.string.add_prof));
        } else {
            setTitle(getResources().getString(R.string.edit_owner));
            saveProfDetailsRequest.setAction(getResources().getString(R.string.edit_prof));
            ViewGroup container = mBinding.containerRlt;
            for (Integer category : mBinding.getProf().getCategories()) {
                CheckBox chechBx = (CheckBox) container.findViewWithTag(String.valueOf(category));
                chechBx.setChecked(true);
            }
        }

    }

    private void deleteImg(int id) {
        Image image = new Image();
        image.setAction(getResources().getString(R.string.delete_prof_image));
        image.setAuthtoken(mToken);
        image.setId(id);
        mImageUploadViewModel.deleteImage(this.image.getState(), image);
    }

    private void submitForm() {
        hideSoftKeyboard();
        AwesomeValidation mAwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        mAwesomeValidation.addValidation(mBinding.nameEdt, ".*\\S.*", getResources().getString(R.string.required_field));
        mAwesomeValidation.addValidation(mBinding.addressEdt, ".*\\S.*", getResources().getString(R.string.required_field));
        if (mAwesomeValidation.validate()) {
            if (isNetworkAvailable()) {
//                ownerObj.setState(state);
                if (!Arrays.asList(AppConfig.cities).contains(mBinding.cityEdt.getText().toString())) {
                    RxEventBus.add(this, showSnackBar(R.style.SnackBarMultiLine, getResources().getString(R.string.city_no_match), "").subscribe());
                } else if (categoriesList.isEmpty()) {
                    RxEventBus.add(this, showSnackBar(R.style.SnackBarMultiLine, getResources().getString(R.string.prof_categories_list_empty), "").subscribe());
                } else {
                    saveProfDetailsRequest.setAuthtoken(mToken);
                    mBinding.getProf().setName(mBinding.nameEdt.getText().toString());
                    mBinding.getProf().setCity(mBinding.cityEdt.getText().toString());
                    mBinding.getProf().setPlace_address(mBinding.addressEdt.getText().toString());
                    mBinding.getProf().setMobile_number(mBinding.phoneEdt.getText().toString());
                    mBinding.getProf().setWebsite(mBinding.websiteEdt.getText().toString());
                    mBinding.getProf().setCategories(categoriesList);
                    saveProfDetailsRequest.setData(mBinding.getProf());
                    initializeProgressDialog(getResources().getString(R.string.please_wait));
                    mViewModel.saveProfDetails(saveProfDetailsRequest);
                }
            }
        }
    }

    private void initializeDialog(String action, final String desc, String title, String positiveBtnTxt, String negativeBtnTxt) {
        Disposable dialogDisp = initializeGenericDialog(action, desc, title, positiveBtnTxt, negativeBtnTxt).subscribe(new Consumer<DialogActions>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull DialogActions obj) {
                if (obj.getAction().equals(getResources().getString(R.string.pick_image_dialog))) {
                    if (obj.getSelected_action() == 1) {//positive action (camera image)
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // Ensure that there's a camera activity to handle the intent
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            File tempFile = null;
                            try {
                                tempFile = imageUtls.createTempImageFile(".jpg");
                                image.setFile(tempFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Intent resultIntent = mImageUploadViewModel.grantTempFilePermission(intent, tempFile);
                            if (resultIntent != null) startActivityForResult(resultIntent, AppConfig.ACTION_IMAGE_CAPTURE);
                        } else {
                            showSnackBar(R.style.SnackBarMultiLine, getResources().getString(R.string.no_camera_available), "").subscribe();
                        }
                    } else {//negative action (gallery image)
//                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.image_choose_label)), AppConfig.EXTERNAL_CONTENT_URI);
                    }
                } else if (obj.getAction().equals(getResources().getString(R.string.clear_image_dialog))) {
                    if (obj.getSelected_action() == 1) {//positive action
                        deleteImg(profObj.getId());
                    }
                }
            }
        });
        RxEventBus.add(this, dialogDisp);
    }

    private io.reactivex.Observable<String> showSnackBar(final int style, final String msg, final String action) {
        return io.reactivex.Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@io.reactivex.annotations.NonNull final ObservableEmitter<String> subscriber) throws Exception {
                if (mSnackBar != null) {
                    mSnackBar.applyStyle(style);
                    mSnackBar.text(msg);
                    mSnackBar.actionClickListener(new SnackBar.OnActionClickListener() {
                        @Override
                        public void onActionClick(SnackBar sb, int actionId) {
                            subscriber.onNext(action);
                        }
                    });
                    mSnackBar.show();
                }
            }
        });
    }

    private void dismissSnackBar() {
        if (mSnackBar != null && mSnackBar.isShown()) mSnackBar.dismiss();
    }
}
