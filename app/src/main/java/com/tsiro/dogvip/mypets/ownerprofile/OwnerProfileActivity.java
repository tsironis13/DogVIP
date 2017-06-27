package com.tsiro.dogvip.mypets.ownerprofile;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.jakewharton.rxbinding2.view.RxView;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;
import com.tsiro.dogvip.DashboardActivity;
import com.tsiro.dogvip.uploadimagecontrol.ImageUploadControlActivity;
import com.tsiro.dogvip.adapters.RecyclerViewAdapter;
import com.tsiro.dogvip.mypets.MyPetsActivity;
import com.tsiro.dogvip.POJO.DialogActions;
import com.tsiro.dogvip.POJO.mypets.OwnerRequest;
import com.tsiro.dogvip.POJO.mypets.owner.OwnerObj;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivityOwnerprofileBinding;
import com.tsiro.dogvip.mypets.pet.PetActivity;
import com.tsiro.dogvip.requestmngrlayer.MyPetsRequestManager;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by giannis on 17/6/2017.
 */

public class OwnerProfileActivity extends BaseActivity implements OwnerProfileContract.View {

    private static final String debugTag = OwnerProfileActivity.class.getSimpleName();
    private ActivityOwnerprofileBinding mBinding;
    private OwnerObj ownerObj;
    private Intent intent;
    private String mToken, imageurl;
    private OwnerProfileContract.ViewModel mOwnerProfileViewModel;
    private OwnerProfilePresenter ownerProfilePresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_ownerprofile);
        setSupportActionBar(mBinding.toolbar);
        mBinding.colTlbrLyt.setExpandedTitleColor(Color.parseColor("#00FFFFFF"));
        mOwnerProfileViewModel = new OwnerProfileViewModel(MyPetsRequestManager.getInstance());
//        petObjList = new ArrayList<>();
        if (getIntent() != null) {
            ownerObj = getIntent().getExtras().getParcelable(getResources().getString(R.string.parcelable_obj));
//            Log.e(debugTag, ownerObj.getImageurl()+" IMAGE HERE");
            mBinding.setOwner(ownerObj);
            if (ownerObj.getPets().isEmpty()) {
                mBinding.setHaspets(false);
            } else {
                ownerProfilePresenter = new OwnerProfilePresenter(this);
                mBinding.setHaspets(true);
                mBinding.rcv.setLayoutManager(new LinearLayoutManager(this));
                mBinding.rcv.setNestedScrollingEnabled(false);
                final RecyclerTouchListener listener = new RecyclerTouchListener(this, mBinding.rcv);
                RecyclerViewAdapter adapter = new RecyclerViewAdapter(R.layout.owner_pet_rcv_row) {
                    @Override
                    protected Object getObjForPosition(int position) {
//                        Log.e(debugTag, ownerObj.getPets().get(position).getName()+"");
                        return ownerObj.getPets().get(position);
                    }
                    @Override
                    protected int getLayoutIdForPosition(int position) {
                        return R.layout.owner_pet_rcv_row;
                    }
                    @Override
                    protected int getTotalItems() {
                        return ownerObj.getPets().size();
                    }

                    @Override
                    protected Object getClickListenerObject() {
                        return ownerProfilePresenter;
                    }
                };
//                mBinding.rcv.addOnItemTouchListener(new RecyclerViewTouchListener(this, mBinding.rcv, new RecyclerViewClickListener() {
//                    @Override
//                    public void onClick(View view, int position) {
//                        Log.e(debugTag, view+""+ownerObj.getPets().get(position).getName());
//
//                    }
//                }));
                mBinding.rcv.setAdapter(adapter);
                mBinding.rcv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
                listener.setSwipeOptionViews(R.id.edit, R.id.delete)
                        .setSwipeable(R.id.baseRlt, R.id.revealRowLlt, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                            @Override
                            public void onSwipeOptionClicked(int viewID, int position) {
                                if (viewID == R.id.edit) {
                                    startPetActivity(false, position);
                                    Log.e(debugTag, position+"");
                                } else {
                                    Log.e(debugTag, "delete");
                                }
                            }
                        });
                mBinding.rcv.addOnItemTouchListener(listener);
                mBinding.setOwner(ownerObj);
            }
            if (ownerObj.getImageurl() != null && !ownerObj.getImageurl().equals("")) {
                setOwnerProfileImg(ownerObj.getImageurl());
            }
        }
        mToken = getMyAccountManager().getAccountDetails().getToken();

        if (getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(ownerObj.getName()+" "+ownerObj.getSurname());
//            setTitle(mBinding.getOwner().getName()+" "+mBinding.getOwner().getSurname());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        intent = new Intent(this, MyPetsActivity.class);
        Disposable disp1 = RxView.clicks(mBinding.profileImgv).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                editProfile();
            }
        });
        RxEventBus.add(this, disp1);
        Disposable disp3 = RxView.clicks(mBinding.addPetFlbtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                startPetActivity(true, -1);
            }
        });
        RxEventBus.add(this, disp3);
    }

    @Override
    protected void onPause() {
        super.onPause();
        RxEventBus.unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppConfig.OWNER_ACTIVITY_FOR_RESULT) {
            if (resultCode == RESULT_OK) {
                imageurl = data.getExtras().getString(getResources().getString(R.string.imageurl));
                setOwnerProfileImg(imageurl);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                editProfile();
                return true;
            case R.id.delete:
                initializeDialog(getResources().getString(R.string.delete_owner), getResources().getString(R.string.delete_owner_desc), getResources().getString(R.string.delete_owner_title), getResources().getString(R.string.cancel), getResources().getString(R.string.confirm));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBaseViewClick(View view) {
        startPetActivity(false, (int)view.getTag());
    }

    @Override
    public void onPetImgViewClick(View view) {
        int position = (int)view.getTag();
        Intent intent = new Intent(OwnerProfileActivity.this, ImageUploadControlActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(getResources().getString(R.string.urls), ownerObj.getPets().get(position).getUrls());
        bundle.putInt(getResources().getString(R.string.pet_id), ownerObj.getPets().get(position).getId());
        bundle.putInt(getResources().getString(R.string.user_role_id), ownerObj.getId());
        startActivity(intent.putExtras(bundle));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //go always to Dashboard activity on back button pressed
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra(getResources().getString(R.string.token), mToken);
        startActivity(intent);
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return mOwnerProfileViewModel;
    }

    @Override
    public void onSuccess(OwnerRequest response) {
        dismissDialog();
        if (response.getCode() == AppConfig.STATUS_OK) {
            startActivity(new Intent(this, DashboardActivity.class));
        } else {
            showSnackBar(getResources().getString(R.string.error), getResources().getString(R.string.close));
        }
    }

    @Override
    public void onError(int resource) {
        dismissDialog();
        showSnackBar(getResources().getString(R.string.error), getResources().getString(R.string.close));
    }

    private void startPetActivity(boolean addpet, int position) {
        Intent intent = new Intent(OwnerProfileActivity.this, PetActivity.class);
        Bundle bundle = new Bundle();
        if (!addpet) bundle.putParcelable(getResources().getString(R.string.parcelable_obj), ownerObj.getPets().get(position));
        bundle.putBoolean(getResources().getString(R.string.add_pet), addpet);
        bundle.putInt(getResources().getString(R.string.user_role_id), ownerObj.getId());
        startActivity(intent.putExtras(bundle));
    }

    private void deleteOwner() {
        initializeProgressDialog(getResources().getString(R.string.please_wait));
        OwnerRequest request = new OwnerRequest();
        request.setAction(getResources().getString(R.string.delete_owner));
        request.setAuthtoken(mToken);
        request.setId(ownerObj.getId());
        mOwnerProfileViewModel.deleteOwner(request);
    }

    private void editProfile() {
        intent.putExtra(getResources().getString(R.string.token), mToken);
        intent.putExtra(getResources().getString(R.string.edit_ownr), true);
        if (imageurl !=null) {
            if (!imageurl.equals("")) {
                ownerObj.setImageurl(imageurl);
            } else {
                ownerObj.setImageurl("");
            }
        }
        intent.putExtra(getResources().getString(R.string.parcelable_obj), ownerObj);
        startActivityForResult(intent, AppConfig.OWNER_ACTIVITY_FOR_RESULT);
    }

    private void setOwnerProfileImg(final String url) {
//        Log.e(debugTag, uri+"");
        if (url != null) {
            Glide.with(this)
                    .load(url)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }
                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .transition(withCrossFade())
                    .apply(new RequestOptions().circleCrop().error(R.drawable.default_person).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE))
                    .into(mBinding.profileImgv);
        }
    }

    private void initializeDialog(String action, final String desc, String title, String positiveBtnTxt, String negativeBtnTxt) {
        Disposable dialogDisp = initializeGenericDialog(action, desc, title, positiveBtnTxt, negativeBtnTxt).subscribe(new Consumer<DialogActions>() {
            @Override
            public void accept(@NonNull DialogActions obj) throws Exception {
                if (obj.getAction().equals(getResources().getString(R.string.delete_owner))) {
                    if (obj.getSelected_action() == 1) {//positive action
                        deleteOwner();
                    }
                }
            }
        });
        RxEventBus.add(this, dialogDisp);
    }

    public void showSnackBar(final String msg, final String action) {
//        return io.reactivex.Observable.create(new ObservableOnSubscribe<String>() {
//            @Override
//            public void subscribe(@NonNull final ObservableEmitter<String> subscriber) throws Exception {
//                if (mSnackBar != null) {
//                    mSnackBar.applyStyle(style);
//                    mSnackBar.text(msg);
//                    mSnackBar.actionClickListener(new SnackBar.OnActionClickListener() {
//                        @Override
//                        public void onActionClick(SnackBar sb, int actionId) {
//                            subscriber.onNext(action);
//                        }
//                    });
//                    mSnackBar.show();
//                }
//            }
//        });
        Snackbar snackbar = Snackbar
                .make(mBinding.coordlt, msg, Snackbar.LENGTH_LONG)
                .setAction(action, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

        snackbar.setActionTextColor(ContextCompat.getColor(this, android.R.color.black));
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        sbView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        textView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
        snackbar.show();
    }

}
