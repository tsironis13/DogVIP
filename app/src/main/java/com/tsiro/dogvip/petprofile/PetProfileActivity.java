package com.tsiro.dogvip.petprofile;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.ImageViewPagerActivity;
import com.tsiro.dogvip.POJO.lovematch.LoveMatchRequest;
import com.tsiro.dogvip.POJO.lovematch.LoveMatchResponse;
import com.tsiro.dogvip.POJO.mypets.pet.PetObj;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.chatroom.ChatRoomActivity;
import com.tsiro.dogvip.databinding.ActivityPetProfileBinding;
import com.tsiro.dogvip.ownerpets.OwnerPetsActivity;
import com.tsiro.dogvip.petlikes.PetLikesActivity;
import com.tsiro.dogvip.retrofit.RetrofitFactory;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import org.reactivestreams.Subscription;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by giannis on 4/7/2017.
 */

public class PetProfileActivity extends BaseActivity implements PetProfileContract.View {

//    private static final String debugTag = PetProfileActivity.class.getSimpleName();
    private ActivityPetProfileBinding mBinding;
    private String[] petUrls;
    private PetObj petObj;
    //use this option to hide message icon and disable owner profile photo click when current activity is called from OwnerProfileActivity
    private int viewFrom;
    private MenuItem lovedItem;
    private boolean processing;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_pet_profile);
        setSupportActionBar(mBinding.toolbar);
        mBinding.colTlbrLyt.setExpandedTitleColor(Color.parseColor("#00FFFFFF"));
        PetProfilePresenter mPresenter = new PetProfilePresenter(this);
        if (savedInstanceState != null) {
            petObj = savedInstanceState.getParcelable(getResources().getString(R.string.pet_obj));
            petUrls = savedInstanceState.getStringArray(getResources().getString(R.string.urls));
            viewFrom = savedInstanceState.getInt(getResources().getString(R.string.view_from));
        } else {
            if (getIntent() != null) {
                if (getIntent().getExtras().getInt(getResources().getString(R.string.view_from)) != 0) viewFrom = 2020;
                petObj = getIntent().getExtras().getParcelable(getResources().getString(R.string.pet_obj));
                petUrls = getIntent().getExtras().getStringArray(getResources().getString(R.string.urls));
            }
        }
        if (getSupportActionBar()!= null) {
            if (viewFrom != 2020) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(petObj.getP_name());
        }
        mBinding.setPetobj(petObj);
        mBinding.setPresenter(mPresenter);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Disposable disp = RxView.clicks(mBinding.petImgv).subscribe(new Consumer<Object>() {
//            @Override
//            public void accept(@NonNull Object o) throws Exception {
//
//            }
//        });
//        RxEventBus.add(this, disp);
        Disposable disp1 = RxView.clicks(mBinding.profileImgv).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                if (viewFrom != 2020) {
                    Intent intent = new Intent(PetProfileActivity.this, OwnerPetsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt(getResources().getString(R.string.user_role_id), petObj.getUser_role_id());
                    startActivity(intent.putExtras(bundle));
                }
            }
        });
        RxEventBus.add(this, disp1);
        Disposable disp2 = RxView.clicks(mBinding.loveTtv).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                Intent intent = new Intent(PetProfileActivity.this, PetLikesActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(getResources().getString(R.string.pet_obj), petObj);
                startActivity(intent.putExtras(bundle));
            }
        });
        RxEventBus.add(this, disp2);
    }

    @Override
    protected void onPause() {
        super.onPause();
        RxEventBus.unregister(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getResources().getString(R.string.pet_obj), petObj);
        outState.putStringArray(getResources().getString(R.string.urls), petUrls);
        outState.putInt(getResources().getString(R.string.view_from), viewFrom);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (viewFrom == 2020) return false;
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        lovedItem = menu.findItem(R.id.lovedItem);
        if (petObj.isLiked() == 1) {
            lovedItem.setIcon(R.drawable.ic_favorite_white);
        } else {
            lovedItem.setIcon(R.drawable.ic_favorite_border_white);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chatItem:
                sendMsg();
                return true;
            case R.id.lovedItem:
                likedislikePet();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return null;
    }

    @Override
    public void onPetImgClick() {
        if (petUrls.length > 0) {
            Intent intent = new Intent(PetProfileActivity.this, ImageViewPagerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putStringArray(getResources().getString(R.string.urls), petUrls);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private void likedislikePet() {
//        if (isNetworkAvailable()) {
//            String subaction = petObj.isLiked() == 0 ? getResources().getString(R.string.like_pet) : getResources().getString(R.string.dislike_pet);
//            LoveMatchRequest request = new LoveMatchRequest();
//            request.setAction(getResources().getString(R.string.like_dislike_pet));
//            request.setSubaction(subaction);
//            request.setP_id(petObj.getId());
//            request.setAuthtoken(getMyAccountManager().getAccountDetails().getToken());
//            if (!processing) RetrofitFactory.getInstance().getServiceAPI().getPetsByFilter(request)
//                    .doOnSubscribe(new Consumer<Subscription>() {
//                        @Override
//                        public void accept(@NonNull Subscription subscription) throws Exception {
//                            processing = true;
//                        }
//                    })
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .doOnError(new Consumer<Throwable>() {
//                        @Override
//                        public void accept(@NonNull Throwable throwable) throws Exception {
//                            processing = false;
//                            showSnackBar(getResources().getString(R.string.error), getResources().getString(R.string.close), Snackbar.LENGTH_SHORT);
//                        }
//                    })
//                    .doOnNext(new Consumer<LoveMatchResponse>() {
//                        @Override
//                        public void accept(@NonNull LoveMatchResponse response) throws Exception {
//                            processing = false;
//                            int total_likes = petObj.getTotal_likes();
//                            if (response.getSubaction().equals(getResources().getString(R.string.like_pet))) {
//                                total_likes++;
//                                petObj.setTotal_likes(total_likes);
//                                petObj.setLiked(1);
//                                lovedItem.setIcon(R.drawable.ic_favorite_white);
//                            } else {
//                                total_likes--;
//                                petObj.setTotal_likes(total_likes);
//                                petObj.setLiked(0);
//                                lovedItem.setIcon(R.drawable.ic_favorite_border_white);
//                            }
//                            showSnackBar(getResources().getString(R.string.success_action), getResources().getString(R.string.close), Snackbar.LENGTH_SHORT);
//                        }
//                    }).subscribe();
//        } else {
//            showSnackBar(getResources().getString(R.string.no_internet_connection), getResources().getString(R.string.retry), Snackbar.LENGTH_SHORT);
//        }
    }

    private void sendMsg() {
        Bundle bundle = new Bundle();
        bundle.putInt(getResources().getString(R.string.role), 1);
        bundle.putInt(getResources().getString(R.string.user_role_id), petObj.getUser_role_id());
        bundle.putInt(getResources().getString(R.string.pet_id), petObj.getId());
        bundle.putString(getResources().getString(R.string.action), getResources().getString(R.string.get_chat_rooom_msgs_by_participants));
        bundle.putString(getResources().getString(R.string.receiver), petObj.getOwnername());
        bundle.putString(getResources().getString(R.string.receiver_surname), petObj.getSurname());
        bundle.putString(getResources().getString(R.string.pet_name), petObj.getP_name());
        startActivity(new Intent(this, ChatRoomActivity.class).putExtras(bundle));
    }

    public void showSnackBar(final String msg, final String action, int length_code) {
        Snackbar snackbar = Snackbar
                .make(mBinding.coordlt, msg, length_code)
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
