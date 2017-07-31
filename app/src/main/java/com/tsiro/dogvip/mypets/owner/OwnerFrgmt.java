package com.tsiro.dogvip.mypets.owner;

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
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

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
import com.tsiro.dogvip.mypets.MyPetsActivity;
import com.tsiro.dogvip.POJO.DialogActions;
import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.mypets.owner.OwnerObj;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.mypets.ownerprofile.OwnerProfileActivity;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.BaseFragment;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.OwnerFrgmtBinding;
import com.tsiro.dogvip.requestmngrlayer.MyPetsRequestManager;
import com.tsiro.dogvip.utilities.DialogPicker;
import com.tsiro.dogvip.utilities.common.CommonUtls;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;

/**
 * Created by giannis on 4/6/2017.
 */

public class OwnerFrgmt extends BaseFragment implements OwnerContract.View {

    private View mView;
    private OwnerFrgmtBinding mBinding;
    private OwnerContract.ViewModel mOwnerFrgmtViewModel;
    private CommonUtls mCommonUtls;
    private File output, filetToUpload;
    private Uri photoURI, galleryURI;
    private int state;
    private OwnerObj ownerObj;
    private boolean addowner, initializeImagePickerDialog;
    private String mToken, imageAction;

    public static OwnerFrgmt newInstance(boolean addNew, String token, OwnerObj ownerObj) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("add_owner", addNew);
        bundle.putString("token", token);

        if (ownerObj != null)bundle.putParcelable("parcelable_obj", ownerObj);
        OwnerFrgmt ownerFrgmt = new OwnerFrgmt();
        ownerFrgmt.setArguments(bundle);
        return ownerFrgmt;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.owner_frgmt, container, false);
            mView = mBinding.getRoot();
            setHasOptionsMenu(true);
        }
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCommonUtls = ((MyPetsActivity)getActivity()).getCommonUtls();
        mOwnerFrgmtViewModel = new OwnerViewModel(MyPetsRequestManager.getInstance());
        mToken = ((MyPetsActivity)getActivity()).getMyAccountManager().getAccountDetails().getToken();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, AppConfig.cities);
        mBinding.cityEdt.setAdapter(adapter);

        if (savedInstanceState != null) {
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
            addowner = savedInstanceState.getBoolean(getResources().getString(R.string.add_ownr));
            mBinding.setAdduser(addowner);
            if (addowner) {//add owner
                getActivity().setTitle(getResources().getString(R.string.add_owner));
                ownerObj = new OwnerObj();
                mBinding.setOwner(ownerObj);
            } else {//edit owner
                getActivity().setTitle(getResources().getString(R.string.edit_owner));
                if (savedInstanceState.getParcelable(getResources().getString(R.string.parcelable_obj)) != null) {
                    ownerObj = savedInstanceState.getParcelable(getResources().getString(R.string.parcelable_obj));
                    mBinding.setOwner(ownerObj);
                    if (ownerObj.getImageurl() != null && !ownerObj.getImageurl().equals("")) {
                        state = 3;
                        mBinding.setImgstate(state);
                        setOwnerProfileImg(Uri.parse(ownerObj.getImageurl()));
                    }
                }
            }
        } else {
            if (getArguments() != null) {//add or edit owner
                addowner = getArguments().getBoolean(getResources().getString(R.string.add_ownr));
                mBinding.setAdduser(addowner);
                if (addowner) {//add owner
                    getActivity().setTitle(getResources().getString(R.string.add_owner));
                    ownerObj = new OwnerObj();
                    mBinding.setOwner(ownerObj);
                } else {//edit owner
                    getActivity().setTitle(getResources().getString(R.string.edit_owner));
                    if (getArguments().getParcelable(getResources().getString(R.string.parcelable_obj)) != null) {
                        ownerObj = getArguments().getParcelable(getResources().getString(R.string.parcelable_obj));
                        mBinding.setOwner(ownerObj);
                        if (ownerObj.getImageurl() != null && !ownerObj.getImageurl().equals("")) {
                            state = 3;
                            mBinding.setImgstate(state);
                            setOwnerProfileImg(Uri.parse(ownerObj.getImageurl()));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Disposable disp = RxView.clicks(mBinding.profileImgv).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                if (!mBinding.getProcessing()) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        initializeDialog(getResources().getString(R.string.pick_image_dialog), getResources().getString(R.string.new_image_desc), getResources().getString(R.string.new_image_title), getResources().getString(R.string.gallery), getResources().getString(R.string.camera));
                    } else {
                        requestUserPermission();
                    }
                    ((MyPetsActivity)getActivity()).dismissSnackBar();
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
        Disposable disp2 = RxView.clicks(mBinding.ageEdt).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Object o) throws Exception {
                Bundle args = new Bundle();
                args.putInt(getResources().getString(R.string.dialog_type), 0);
                DialogFragment dialogFragment = new DialogPicker();
                dialogFragment.setArguments(args);
                dialogFragment.show(getFragmentManager(), "datePicker");
            }
        });
        RxEventBus.add(this, disp2);
        Disposable disp3 = RxEventBus.createSubject(AppConfig.DIALOG_ACTION, 0).observeEvents(DialogActions.class).subscribe(new Consumer<DialogActions>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull DialogActions obj) throws Exception {
                if (obj.getAction().equals(getResources().getString(R.string.date_pick_action))) {
                    if (obj.getCode() == AppConfig.STATUS_OK) {
                        ownerObj.setDisplayage(obj.getDisplay_date());
                        ownerObj.setAge(obj.getDate());
                    } else {
                        ((MyPetsActivity)getActivity()).showSnackBar(R.style.SnackBarSingleLine, getResources().getString(R.string.invalid_date), "").subscribe();
                    }
                } else {
                    ownerObj.setDisplayage("");
                }
            }
        });
        RxEventBus.add(this, disp3);
        if (initializeImagePickerDialog) {
            initializeDialog(getResources().getString(R.string.pick_image_dialog), getResources().getString(R.string.new_image_desc), getResources().getString(R.string.new_image_title), getResources().getString(R.string.gallery), getResources().getString(R.string.camera));
            initializeImagePickerDialog = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        RxEventBus.unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
    public void onSaveInstanceState(Bundle outState) {
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
        outState.putBoolean(getResources().getString(R.string.add_ownr), addowner);
        if (ownerObj != null)outState.putParcelable(getResources().getString(R.string.parcelable_obj), ownerObj);
        outState.putInt(getResources().getString(R.string.imageview_state), state);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.submit_form_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
        return mOwnerFrgmtViewModel;
    }

    @Override
    public void onSuccess(OwnerObj response) {
        ((MyPetsActivity) getActivity()).dismissDialog();
        Intent intent = new Intent(getActivity(), OwnerProfileActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putParcelable(getResources().getString(R.string.parcelable_obj), response);
        startActivity(intent.putExtras(mBundle));
    }

    @Override
    public void onError(int resource, final boolean msglength) {
        ((MyPetsActivity) getActivity()).dismissDialog();
        int style = R.style.SnackBarSingleLine;
        if (msglength) style = R.style.SnackBarMultiLine;
        ((MyPetsActivity)getActivity()).showSnackBar(style, getResources().getString(resource), "").subscribe();
    }

    @Override
    public void onImageActionSuccess(Image image) {
        mBinding.setProcessing(false);
        ((MyPetsActivity)getActivity()).setImageuploading(false);
        if (image.getCode() == AppConfig.STATUS_OK) {
            if (image.getAction().equals(getResources().getString(R.string.delete_owner_image))) {
                state = 0;
                mBinding.setImgstate(state);
                setOwnerProfileImg(null);
                ((MyPetsActivity)getActivity()).setImageProfileUrl("");
                ownerObj.setImageurl("");
                imageAction = "";
            } else {
                mBinding.setImgstate(state);
                ((MyPetsActivity)getActivity()).setImageProfileUrl(image.getImageurl());
                mCommonUtls.deleteAppStorage(output);
            }
        } else {
            ((MyPetsActivity)getActivity()).showSnackBar(R.style.SnackBarWithAction, getResources().getString(R.string.error), imageAction).delay(400, TimeUnit.MILLISECONDS).subscribe(new Consumer<String>() {
                @Override
                public void accept(@io.reactivex.annotations.NonNull String action) throws Exception {
                    if (action.equals(getResources().getString(R.string.delete_owner_image))) {
                        imageAction = action;
                        deleteImg(ownerObj.getId());
                    } else {
                        uploadImage();
                    }
                }
            });
        }
    }

    @Override
    public void onImageActionError() {
        mBinding.setProcessing(false);
        ((MyPetsActivity)getActivity()).setImageuploading(false);
        ((MyPetsActivity)getActivity()).showSnackBar(R.style.SnackBarWithAction, getResources().getString(R.string.error), imageAction).delay(200, TimeUnit.MILLISECONDS).subscribe(new Consumer<String>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull String action) throws Exception {
                if (action.equals(getResources().getString(R.string.delete_owner_image))) {
                    imageAction = action;
                    deleteImg(ownerObj.getId());
                } else {
                    uploadImage();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void requestUserPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ((MyPetsActivity)getActivity()).showSnackBar(R.style.SnackBarMultiLine, getResources().getString(R.string.grant_permission), "").subscribe();
            } else {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, AppConfig.READ_EXTERNAL_STORAGE_PERMISSION_RESULT_CODE);
            }
        } else {
            initializeDialog(getResources().getString(R.string.pick_image_dialog), getResources().getString(R.string.new_image_desc), getResources().getString(R.string.new_image_title), getResources().getString(R.string.gallery), getResources().getString(R.string.camera));
        }
    }

    private void deleteImg(int id) {
        Image image = new Image();
        image.setAction(getResources().getString(R.string.delete_owner_image));
        image.setAuthtoken(mToken);
        image.setId(id);
        imageAction = getResources().getString(R.string.delete_owner_image);
        mBinding.setProcessing(true);
        mOwnerFrgmtViewModel.deleteImage(image);
    }

    private void submitForm() {
        ((MyPetsActivity)getActivity()).hideSoftKeyboard();
        AwesomeValidation mAwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        mAwesomeValidation.addValidation(mBinding.nameEdt, ".*\\S.*", getResources().getString(R.string.required_field));
        mAwesomeValidation.addValidation(mBinding.surnameEdt, ".*\\S.*", getResources().getString(R.string.required_field));
        mAwesomeValidation.addValidation(mBinding.ageEdt, ".*\\S.*", getResources().getString(R.string.required_field));
//        mAwesomeValidation.addValidation(mBinding.ageEdt, "^(0?[1-9]|[1-9][0-9])$", getResources().getString(R.string.required_field));
        if (mAwesomeValidation.validate()) {
            if ((((MyPetsActivity)getActivity()).isNetworkAvailable())) {
//                ownerObj.setState(state);
                if (!Arrays.asList(AppConfig.cities).contains(mBinding.cityEdt.getText().toString())) {
                    RxEventBus.add(this, ((MyPetsActivity) getActivity()).showSnackBar(R.style.SnackBarMultiLine, getResources().getString(R.string.city_no_match), "").subscribe());
                } else {
                    ownerObj.setAuthtoken(mToken);
                    ownerObj.setName(mBinding.nameEdt.getText().toString());
                    ownerObj.setSurname(mBinding.surnameEdt.getText().toString());
                    ownerObj.setDisplayage(mBinding.ageEdt.getText().toString());
                    ownerObj.setCity(mBinding.cityEdt.getText().toString());
                    ownerObj.setPhone(mBinding.phoneEdt.getText().toString());
                    if (addowner) {
                        ownerObj.setAction(getResources().getString(R.string.add_ownr));
                        ((MyPetsActivity) getActivity()).initializeProgressDialog(getResources().getString(R.string.please_wait));
                        mOwnerFrgmtViewModel.submitOwner(ownerObj);
                    } else {
                        if (!mBinding.getProcessing()) {
                            ownerObj.setAction(getResources().getString(R.string.edit_ownr));
                            if (ownerObj.getImageurl() !=null && !ownerObj.getImageurl().equals("")) ownerObj.setImageurl(ownerObj.getImageurl());
                            if (((MyPetsActivity)getActivity()).getImageurl() != null && !((MyPetsActivity)getActivity()).getImageurl().equals("")) ownerObj.setImageurl(((MyPetsActivity)getActivity()).getImageurl());
//                        ownerObj.setId(ownerObj.getId());
                            ((MyPetsActivity) getActivity()).initializeProgressDialog(getResources().getString(R.string.please_wait));
                            mOwnerFrgmtViewModel.submitOwner(ownerObj);
                        }
                    }
                }
            } else {
                ((MyPetsActivity)getActivity()).showSnackBar(R.style.SnackBarSingleLine, getResources().getString(R.string.no_internet_connection), "").subscribe();
            }
        }
    }

    private void isImageValid(Uri uri, int state) {
        Image img = mCommonUtls.isImageSizeValid(uri, state, output);
        filetToUpload = img.getImage();
        if (img.getSize()) {
            if (((MyPetsActivity)getActivity()).isNetworkAvailable()) {
                setOwnerProfileImg(uri);
            } else {
                ((MyPetsActivity)getActivity()).showSnackBar(R.style.SnackBarSingleLine, getResources().getString(R.string.no_internet_connection), "").subscribe();
            }
        } else {
            ((MyPetsActivity)getActivity()).showSnackBar(R.style.SnackBarSingleLine, getResources().getString(R.string.invalid_image_size), "").subscribe();
            output = null;
        }
    }

    private void uploadImage() {
        MultipartBody.Part mfile;
        try {
            mfile = mCommonUtls.getRequestFileBody(filetToUpload);
            RequestBody action = RequestBody.create(okhttp3.MultipartBody.FORM, getResources().getString(R.string.upload_ownr_img));
            RequestBody token = RequestBody.create(okhttp3.MultipartBody.FORM, mToken);
            RequestBody id = RequestBody.create(okhttp3.MultipartBody.FORM, ownerObj.getId()+"");
            imageAction = getResources().getString(R.string.upload_ownr_img);
            mOwnerFrgmtViewModel.uploadImage(action, token, id, mfile);
            mBinding.setProcessing(true);
            ((MyPetsActivity)getActivity()).setImageuploading(true);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void setOwnerProfileImg(final Uri uri) {
        Object object = uri;
        if (state == 0) object = R.drawable.default_person;
        if (object != null) {
            Glide.with(getActivity())
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
                    .apply(new RequestOptions().centerCrop().error(R.drawable.default_person).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE))
                    .into(mBinding.profileImgv);
        }
    }

    private void initializeDialog(String action, final String desc, String title, String positiveBtnTxt, String negativeBtnTxt) {
        Disposable dialogDisp = ((MyPetsActivity)getActivity()).initializeGenericDialog(action, desc, title, positiveBtnTxt, negativeBtnTxt).subscribe(new Consumer<DialogActions>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull DialogActions obj) {
                if (obj.getAction().equals(getResources().getString(R.string.pick_image_dialog))) {
                    if (obj.getSelected_action() == 1) {//positive action (camera image)
                        state = 2;
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // Ensure that there's a camera activity to handle the intent
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
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
                                    ClipData clip = ClipData.newUri(getActivity().getContentResolver(), "photo", photoURI);
                                    intent.setClipData(clip);
                                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                }
                                else {
                                    List<ResolveInfo> resInfoList= getActivity().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                                    for (ResolveInfo resolveInfo : resInfoList) {
                                        String packageName = resolveInfo.activityInfo.packageName;
                                        getActivity().grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                    }
                                }
                                startActivityForResult(intent, AppConfig.ACTION_IMAGE_CAPTURE);
                            }
                        } else {
                            ((MyPetsActivity)getActivity()).showSnackBar(R.style.SnackBarMultiLine, getResources().getString(R.string.no_camera_available), "").subscribe();
                        }
                    } else {//negative action
                        state = 1;
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent , AppConfig.EXTERNAL_CONTENT_URI);
                    }
                } else if (obj.getAction().equals(getResources().getString(R.string.clear_image_dialog))) {
                    if (obj.getSelected_action() == 1) {//positive action
                        deleteImg(ownerObj.getId());
                    }
                }
            }
        });
        RxEventBus.add(this, dialogDisp);
    }
}
