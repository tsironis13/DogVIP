package com.tsiro.dogvip.uploadimagecontrol;

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
import com.tsiro.dogvip.ImageViewPagerActivity;
import com.tsiro.dogvip.POJO.DialogActions;
import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.ImagePathIndex;
import com.tsiro.dogvip.POJO.mypets.pet.PetObj;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.adapters.ImageGridAdapter;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivityImageUploadControlBinding;
import com.tsiro.dogvip.mypets.MyPetsActivity;
import com.tsiro.dogvip.requestmngrlayer.ImageUploadControlRequestManager;
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
 * Created by giannis on 25/6/2017.
 */

public class ImageUploadControlActivity extends BaseActivity implements ImageUploadControlContract.View {

    private ActivityImageUploadControlBinding mBinding;
    private ArrayList<Image> urls;
    //index of recycler view row clicked
    private int state, petid, userRoleId, selectedIndex, oldMainImageIndex, setMainImageIndex, index;
    private Uri photoURI, galleryURI;
    private File output, filetToUpload;
    private ImageUploadControlContract.ViewModel mViewModel;
    private boolean initializeImagePickerDialog;
    private String mToken, mainImageUrl;
    private ImageGridAdapter imageGridAdapter;
    private ImageUploadControlPresenter imageUploadControlPresenter;
    private ArrayList<ImagePathIndex> checkedUrls;
    private MenuItem mainImageItem, deleteImageItem;
    private Image image;
    private PetObj petObj;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_image_upload_control);
        mViewModel = new ImageUploadControlViewModel(ImageUploadControlRequestManager.getInstance());
        setSupportActionBar(mBinding.incltoolbar.toolbar);
        mToken = getMyAccountManager().getAccountDetails().getToken();
        imageUploadControlPresenter = new ImageUploadControlPresenter(this);
        checkedUrls = new ArrayList<>();

        if (getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(getResources().getString(R.string.photos));
        }
        if (savedInstanceState != null) {
            petObj = savedInstanceState.getParcelable(getResources().getString(R.string.pet_obj));
            userRoleId = savedInstanceState.getInt(getResources().getString(R.string.user_role_id));
            index = savedInstanceState.getInt(getResources().getString(R.string.index));
            state = savedInstanceState.getInt(getResources().getString(R.string.imageview_state));
            mainImageUrl = savedInstanceState.getString(getResources().getString(R.string.main_image));
            if (state == 1) {
                String strUri = savedInstanceState.getString(getResources().getString(R.string.gallery_uri));
                if (strUri != null) {
                    galleryURI = Uri.parse(strUri);
//                    isImageValid(galleryURI, state);
                }
            } else if (state == 2) {
                output=(File) savedInstanceState.getSerializable(getResources().getString(R.string.image_output));
            }
            configureActivity(petObj);
        } else {
            if (getIntent() != null) {
                petObj = getIntent().getExtras().getParcelable(getResources().getString(R.string.pet_obj));
                userRoleId = getIntent().getExtras().getInt(getResources().getString(R.string.user_role_id));
                index = getIntent().getExtras().getInt(getResources().getString(R.string.index));//recycler view row index clicked
            }
            configureActivity(petObj);
        }
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
        outState.putParcelable(getResources().getString(R.string.pet_obj), petObj);
        outState.putInt(getResources().getString(R.string.user_role_id), userRoleId);
        outState.putInt(getResources().getString(R.string.index), index);
        outState.putString(getResources().getString(R.string.main_image), mainImageUrl);
        outState.putInt(getResources().getString(R.string.imageview_state), state);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog();
    }

    @Override
    protected void onPause() {
        super.onPause();
        RxEventBus.unregister(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        petObj.setMain_url(mainImageUrl);
        petObj.setUrls(urls);
        Bundle bundle = new Bundle();
        bundle.putParcelable(getResources().getString(R.string.pet_obj), petObj);
        bundle.putInt(getResources().getString(R.string.index), index);
        setResult(RESULT_OK, new Intent().putExtras(bundle));
        finish();
        super.onBackPressed();
    }

    @Override
    public void onImageClick(View view) {
        int pos = (int) view.getTag();
        if (urls.get(pos).getImageurl() != null) {
            String[] petUrls = new String[] {urls.get(pos).getImageurl()};
            Intent intent = new Intent(this, ImageViewPagerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putStringArray(getResources().getString(R.string.urls), petUrls);
            intent.putExtras(bundle);
            startActivity(intent);
        }
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
            if (!urls.get(selectedIndex).isMain()) mainImageItem.setVisible(true);
            deleteImageItem.setVisible(true);
        }
        if (checkedUrls.size() > 1) {
            mainImageItem.setVisible(false);
        }
        if (checkedUrls.size() == 0) {
            mainImageItem.setVisible(false);
            deleteImageItem.setVisible(false);
        }
    }

    @Override
    public void onSuccess(Image image) {
        dismissDialog();
        mainImageUrl = image.getMainurl();
        if (image.getAction().equals(getResources().getString(R.string.delete_pet_image))) {
            Iterator<Image> iter = urls.iterator();
            while (iter.hasNext()) {
                Image obj = iter.next();
                if (obj.isChecked()) iter.remove();
            }
            checkedUrls.clear();
            if (urls.size() == 1) urls.get(0).setCanchecked(true);
            notifyGridAdapter(imageGridAdapter);
            if (urls.size() == 0) {
                mBinding.setImagelimit(0);
            }
            if (urls.size() > 0) mBinding.setImagelimit(2);
        } else if (image.getAction().equals(getResources().getString(R.string.set_pet_main_image))) {
//            if (checkBox != null) unCheckItem();
            petObj.setMain_url(mainImageUrl);
            urls.get(setMainImageIndex).setChecked(false);
            checkedUrls.clear();
            urls.get(oldMainImageIndex).setMain(false);
            urls.get(oldMainImageIndex).setCanchecked(true);
            urls.get(setMainImageIndex).setMain(true);
            urls.get(setMainImageIndex).setCanchecked(false);
            oldMainImageIndex = setMainImageIndex;
            notifyGridAdapter(imageGridAdapter);
        } else {
            if (urls != null) {
                getCommonUtls().deleteAppStorage(output);
                if (image.getId() == 0) { //first pet image just added
                    image.setMain(true);
                    urls.add(image);
                    mBinding.setImagelimit(2);
                    imageGridAdapter = new ImageGridAdapter(this, R.layout.image_grid_item, urls, imageUploadControlPresenter);
                    notifyGridAdapter(imageGridAdapter);
                } else {
                    if (image.getAction().equals(getResources().getString(R.string.upload_pet_img))) {
                        petObj.setMain_url(mainImageUrl);
                        if (urls.size() == 1) urls.get(0).setCanchecked(false);
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
//        checkedUrls.clear();
//        clearCheckUrls();
        showSnackBar(getResources().getString(resource), getResources().getString(R.string.close));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_control_menu, menu);
        mainImageItem = menu.findItem(R.id.mainImageItem);
        deleteImageItem = menu.findItem(R.id.deleteImageItem);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mainImageItem:
                setMainPetImage();
                return true;
            case R.id.deleteImageItem:
                deletePetImage();
            default:
                return super.onOptionsItemSelected(item);
        }
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

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    private void clearCheckUrls() {
        for (Image list : urls) {
            if (list.isChecked()) list.setChecked(false);
        }
        mainImageItem.setVisible(false);
        deleteImageItem.setVisible(false);
    }

    private void configureActivity(PetObj petObj) {
        if (petObj != null) {
            urls = petObj.getUrls();
            petid = petObj.getId();
        }
        if (urls != null) {
            if (urls.isEmpty()) {
                mBinding.setImagelimit(0);
            } else {
//                mainImageUrl = petObj.getMain_url();
                imageGridAdapter = new ImageGridAdapter(this, R.layout.image_grid_item, urls, imageUploadControlPresenter);
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

    private void setMainPetImage() {
        for (int i = 0; i <urls.size(); i++) {
            if (urls.get(i).isMain()) oldMainImageIndex = i;
        }
        for (int i = 0; i <checkedUrls.size(); i++) {
            setMainImageIndex = checkedUrls.get(i).getIndex();
        }
        image = new Image();
        image.setAction(getResources().getString(R.string.set_pet_main_image));
        image.setAuthtoken(mToken);
        image.setId(petid);
        image.setUser_role_id(userRoleId);
        image.setImageurl(urls.get(setMainImageIndex).getImageurl());
        if (isNetworkAvailable()) {
            mViewModel.manipulatePetImage(image);
            initializeProgressDialog(getResources().getString(R.string.please_wait));
        } else {
            showSnackBar(getResources().getString(R.string.no_internet_connection), getResources().getString(R.string.close));
        }
    }

    private void deletePetImage() {
        Disposable disp = initializeGenericDialog("", getResources().getString(R.string.clear_images_desc), getResources().getString(R.string.clear_image_title), getResources().getString(R.string.cancel), getResources().getString(R.string.confirm)).subscribe(new Consumer<DialogActions>() {
            @Override
            public void accept(@NonNull DialogActions obj) throws Exception {
                if (obj.getSelected_action() == 1) {//positive action (camera image)
                    image = new Image();
                    image.setAction(getResources().getString(R.string.delete_pet_image));
                    image.setAuthtoken(mToken);
                    image.setId(petid);
                    image.setUser_role_id(userRoleId);
                    image.setChecked_urls(checkedUrls);
                    image.setMain(urls.get(selectedIndex).isMain());
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
            RequestBody action = RequestBody.create(okhttp3.MultipartBody.FORM, getResources().getString(R.string.upload_pet_img));
            RequestBody token = RequestBody.create(okhttp3.MultipartBody.FORM, mToken);
            RequestBody user_role_id = RequestBody.create(okhttp3.MultipartBody.FORM, userRoleId+"");
            RequestBody pet_id = RequestBody.create(okhttp3.MultipartBody.FORM, petid+"");
            RequestBody index = RequestBody.create(okhttp3.MultipartBody.FORM, urls.size()+"");
            mViewModel.uploadPetImage(action, token, user_role_id, pet_id, mfile, index);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
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
