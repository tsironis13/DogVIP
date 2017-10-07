package com.tsiro.dogvip.uploadimagecontrol.petsitter_place;

import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.POJO.DialogActions;
import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.ImagePathIndex;
import com.tsiro.dogvip.POJO.mypets.pet.PetObj;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.adapters.ImageGridAdapter;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivityPetsitterPlaceUploadControlBinding;
import com.tsiro.dogvip.requestmngrlayer.ImageUploadControlRequestManager;
import com.tsiro.dogvip.uploadimagecontrol.ImageUploadControlContract;
import com.tsiro.dogvip.uploadimagecontrol.ImageUploadControlPresenter;
import com.tsiro.dogvip.uploadimagecontrol.ImageUploadControlViewModel;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by giannis on 9/9/2017.
 */

public class PetSitterPlaceUploadControlActivity extends BaseActivity implements ImageUploadControlContract.View {

    private static final String debugTag = PetSitterPlaceUploadControlActivity.class.getSimpleName();
    private ActivityPetsitterPlaceUploadControlBinding mBinding;
    private String mToken;
    private ImageUploadControlContract.ViewModel mViewModel;
    private Uri photoURI, galleryURI;
    private int state, petsitter_id, selectedIndex;
    private File output, filetToUpload;
    private boolean initializeImagePickerDialog;
    private ImageGridAdapter imageGridAdapter;
    private ImageUploadControlPresenter imageUploadControlPresenter;
    private ArrayList<Image> urls;
    private Image image;
    private ArrayList<ImagePathIndex> checkedUrls;
    private MenuItem deleteImageItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_petsitter_place_upload_control);
        setSupportActionBar(mBinding.incltoolbar.toolbar);
        mToken = getMyAccountManager().getAccountDetails().getToken();
        mViewModel = new ImageUploadControlViewModel(ImageUploadControlRequestManager.getInstance());
        imageUploadControlPresenter = new ImageUploadControlPresenter(this);
        checkedUrls = new ArrayList<>();

        if (getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(getResources().getString(R.string.photos));
        }
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
            petsitter_id = savedInstanceState.getInt(getResources().getString(R.string.id));
            urls = savedInstanceState.getParcelableArrayList(getResources().getString(R.string.urls));
        } else {
            if (getIntent() != null) {
                petsitter_id = getIntent().getExtras().getInt(getResources().getString(R.string.id));
                urls = getIntent().getExtras().getParcelableArrayList(getResources().getString(R.string.urls));
            }
        }
        configureActivity(urls);
        if (initializeImagePickerDialog) {
            initializeDialog(getResources().getString(R.string.pick_image_dialog), getResources().getString(R.string.new_image_desc), getResources().getString(R.string.new_image_title), getResources().getString(R.string.gallery), getResources().getString(R.string.camera));
            initializeImagePickerDialog = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Disposable disp = RxView.clicks(mBinding.addImageFlbtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    initializeDialog(getResources().getString(R.string.pick_image_dialog), getResources().getString(R.string.new_image_desc), getResources().getString(R.string.new_image_title), getResources().getString(R.string.gallery), getResources().getString(R.string.camera));
                } else {
                    requestUserPermission();
                }
            }
        });
        RxEventBus.add(this, disp);
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
        outState.putInt(getResources().getString(R.string.id), petsitter_id);
        outState.putParcelableArrayList(getResources().getString(R.string.urls), urls);
//        outState.putInt(getResources().getString(R.string.index), index);
        outState.putInt(getResources().getString(R.string.imageview_state), state);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(getResources().getString(R.string.urls), urls);
        setResult(RESULT_OK, new Intent().putExtras(bundle));
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_control_menu, menu);
        menu.findItem(R.id.mainImageItem).setVisible(false);
        deleteImageItem = menu.findItem(R.id.deleteImageItem);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteImageItem:
                deletePetImage();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void onSuccess(Image image) {
        dismissDialog();
        if (image.getAction().equals(getResources().getString(R.string.delete_petsitter_place_image))) {
            Iterator<Image> iter = urls.iterator();
            while (iter.hasNext()) {
                Image obj = iter.next();
                if (obj.isChecked()) iter.remove();
            }
            checkedUrls.clear();
            notifyGridAdapter(imageGridAdapter);
            if (urls.size() == 0) {
                mBinding.setImagelimit(0);
            }
            if (urls.size() > 0) mBinding.setImagelimit(2);
        } else {
            if (urls != null) {
                getCommonUtls().deleteAppStorage(output);
                if (image.getId() == 0) { //first pet image just added
                    urls.add(image);
                    mBinding.setImagelimit(2);
                    imageGridAdapter = new ImageGridAdapter(this, R.layout.petsitter_image_place_grid_item, urls, imageUploadControlPresenter);
                    notifyGridAdapter(imageGridAdapter);
                } else {
                    if (image.getAction().equals(getResources().getString(R.string.upload_petsitter_place_img))) {
                        urls.add(image);
                        notifyGridAdapter(imageGridAdapter);
                    }
                    if (image.getId() == 5) mBinding.setImagelimit(1);
                }
                checkedUrls.clear();
            }
        }
        clearCheckUrls();
    }

    @Override
    public void onError(int resource) {
        dismissDialog();
        showSnackBar(getResources().getString(resource), getResources().getString(R.string.close));
    }

    @Override
    public void onCheckBoxClick(View view, boolean isChecked) {
        selectedIndex = (int) view.getTag();
        if (!isChecked) {
            ImagePathIndex imagePathIndex = new ImagePathIndex();
            imagePathIndex.setPath(urls.get(selectedIndex).getImageurl());
            imagePathIndex.setIndex(selectedIndex);
            checkedUrls.add(imagePathIndex);
            urls.get(selectedIndex).setChecked(true);
        } else {
            for (int i = 0; i < checkedUrls.size(); i++) {
                int remove =  checkedUrls.get(i).getIndex();
                if (remove == selectedIndex) checkedUrls.remove(i);
            }
            urls.get(selectedIndex).setChecked(false);
        }
        if (checkedUrls.size() == 1) {
            deleteImageItem.setVisible(true);
        }
        if (checkedUrls.size() == 0) {
            deleteImageItem.setVisible(false);
        }
    }

    @Override
    public void onImageClick(View view) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConfig.EXTERNAL_CONTENT_URI || requestCode == AppConfig.ACTION_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Uri uri = null;
                int saveinstance_state = state;
                if (requestCode == AppConfig.ACTION_IMAGE_CAPTURE) {
                    uri = getCommonUtls().getUriForFile(output);
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
    public void onRequestPermissionsResult(int requestCode, @android.support.annotation.NonNull String[] permissions, @android.support.annotation.NonNull int[] grantResults) {
        switch (requestCode) {
            case AppConfig.READ_EXTERNAL_STORAGE_PERMISSION_RESULT_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) initializeImagePickerDialog = true;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void requestUserPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showSnackBar(getResources().getString(R.string.grant_permission), getResources().getString(R.string.close));
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, AppConfig.READ_EXTERNAL_STORAGE_PERMISSION_RESULT_CODE);
            }
        } else {
            initializeDialog(getResources().getString(R.string.pick_image_dialog), getResources().getString(R.string.new_image_desc), getResources().getString(R.string.new_image_title), getResources().getString(R.string.gallery), getResources().getString(R.string.camera));
        }
    }

    private void configureActivity(ArrayList<Image> urls) {
        if (urls != null) {
            if (urls.isEmpty()) {
                mBinding.setImagelimit(0);
            } else {
                imageGridAdapter = new ImageGridAdapter(this, R.layout.petsitter_image_place_grid_item, urls, imageUploadControlPresenter);
                mBinding.imageGrdv.setAdapter(imageGridAdapter);
                if (urls.size() == 6) {
                    mBinding.setImagelimit(1);
                } else {
                    mBinding.setImagelimit(2);
                }
            }
        }
    }

    private void notifyGridAdapter(final ImageGridAdapter imageGridAdptr) {
        if (imageGridAdptr != null) {
            mBinding.imageGrdv.setAdapter(imageGridAdptr);
        } else {
            imageGridAdapter.notifyDataSetChanged();
        }
    }

    private void clearCheckUrls() {
        for (Image list : urls) {
            if (list.isChecked()) list.setChecked(false);
        }
        deleteImageItem.setVisible(false);
    }

    private boolean isImageValid(Uri uri, int state) {
        Image img = getCommonUtls().isImageSizeValid(uri, state, output);
        filetToUpload = img.getImage();
        if (!img.isInvalid_filetype()) {
            if (img.getSize()) {
                if (img.isDeleteLocalFile()) output = img.getImage();
                if (isNetworkAvailable()) {
                    initializeProgressDialog(getResources().getString(R.string.please_wait));
                    uploadImage();
                } else {
                    showSnackBar(getResources().getString(R.string.no_internet_connection), getResources().getString(R.string.close));
                }
            } else {
                showSnackBar(getResources().getString(R.string.invalid_image_size), getResources().getString(R.string.close));
                output = null;
            }
        } else {
            showSnackBar(getResources().getString(R.string.invalid_file_type), getResources().getString(R.string.close));
        }
        return img.isInvalid_filetype();
    }

    private void uploadImage() {
        MultipartBody.Part mfile;
        try {
            mfile = getCommonUtls().getRequestFileBody(filetToUpload);
            RequestBody action = RequestBody.create(okhttp3.MultipartBody.FORM, getResources().getString(R.string.upload_petsitter_place_img));
            RequestBody token = RequestBody.create(okhttp3.MultipartBody.FORM, mToken);
            RequestBody id = RequestBody.create(okhttp3.MultipartBody.FORM, petsitter_id+"");
            RequestBody index = RequestBody.create(okhttp3.MultipartBody.FORM, urls.size()+"");
            mViewModel.uploadPetSitterPlaceImage(action, token, id, mfile, index);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void deletePetImage() {
        Disposable disp = initializeGenericDialog("", getResources().getString(R.string.clear_images_desc), getResources().getString(R.string.clear_image_title), getResources().getString(R.string.cancel), getResources().getString(R.string.confirm)).subscribe(new Consumer<DialogActions>() {
            @Override
            public void accept(@NonNull DialogActions obj) throws Exception {
                if (obj.getSelected_action() == 1) {//positive action (camera image)
                    image = new Image();
                    image.setAction(getResources().getString(R.string.delete_petsitter_place_image));
                    image.setAuthtoken(mToken);
                    image.setId(petsitter_id);
                    image.setChecked_urls(checkedUrls);
                    if (isNetworkAvailable()) {
                        mViewModel.manipulatePetImage(image);
                        initializeProgressDialog(getResources().getString(R.string.please_wait));
                    } else {
                        showSnackBar(getResources().getString(R.string.no_internet_connection), getResources().getString(R.string.close));
                    }
                }
            }
        });
        RxEventBus.add(this, disp);
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
                                output = getCommonUtls().createImageFile(".jpg");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (output != null) {
                                photoURI = getCommonUtls().getUriForFile(output);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    ClipData clip = ClipData.newUri(getContentResolver(), "photo", photoURI);
                                    intent.setClipData(clip);
                                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                } else {
                                    List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                                    for (ResolveInfo resolveInfo : resInfoList) {
                                        String packageName = resolveInfo.activityInfo.packageName;
                                        grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                    }
                                }
                                startActivityForResult(intent, AppConfig.ACTION_IMAGE_CAPTURE);
                            }
                        } else {
                            showSnackBar(getResources().getString(R.string.no_camera_available), getResources().getString(R.string.close));
                        }
                    } else {//negative action
                        state = 1;
//                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.image_choose_label)), AppConfig.EXTERNAL_CONTENT_URI);
                    }
                }
            }
        });
        RxEventBus.add(this, dialogDisp);
    }

    public void showSnackBar(final String msg, final String action) {
        Snackbar snackbar = Snackbar
                .make(mBinding.coordlt, msg, Snackbar.LENGTH_LONG)
                .setAction(action, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {}
                });
        snackbar.setActionTextColor(ContextCompat.getColor(this, android.R.color.black));
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        sbView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        textView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
        snackbar.show();
    }
}
