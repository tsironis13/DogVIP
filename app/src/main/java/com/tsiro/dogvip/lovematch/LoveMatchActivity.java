package com.tsiro.dogvip.lovematch;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.POJO.lovematch.GetPetsResponse;
import com.tsiro.dogvip.POJO.lovematch.GetPetsByFilterRequest;
import com.tsiro.dogvip.POJO.lovematch.LikeDislikeRequest;
import com.tsiro.dogvip.POJO.lovematch.LikeDislikeResponse;
import com.tsiro.dogvip.POJO.lovematch.LoveMatchResponse;
import com.tsiro.dogvip.POJO.mypets.pet.PetObj;
import com.tsiro.dogvip.chatroom.ChatRoomActivity;
import com.tsiro.dogvip.petprofile.PetProfileActivity;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.adapters.RecyclerViewAdapter;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.base.activity.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivityLoveMatchBinding;
import com.tsiro.dogvip.utilities.UIUtls;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by giannis on 1/7/2017.
 */

public class LoveMatchActivity extends BaseActivity implements LoveMatchContract.View {

    private ActivityLoveMatchBinding mBinding;
    private String mToken, city, race;
    private RecyclerViewAdapter rcvAdapter;
    private LinearLayoutManager linearLayoutManager;
    private int page = 1, position;
    private ArrayList<PetObj> data;
    private boolean availableData = true, isLoading, error, hasfilters;
    private static final String debugTag = LoveMatchActivity.class.getSimpleName();

    @Inject
    LoveMatchViewModel mViewModel;
    @Inject
    UIUtls uiUtls;
    @Inject
    LikeDislikeRequest likeDislikeRequest;
    @Inject
    GetPetsByFilterRequest getPetsByFilterRequest;
    @Inject
    LoveMatchActivityRetainFragment mLoveMatchActivityRetainFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_love_match);
        mToken = getMyAccountManager().getAccountDetails().getToken();

        if (getSupportFragmentManager().findFragmentByTag("my_fragment") == null) {
            getSupportFragmentManager().beginTransaction().add(mLoveMatchActivityRetainFragment, "my_fragment").commit();
//            Log.e(debugTag, mLoveMatchActivityRetainFragment + " inside if null");
        } else {
            mLoveMatchActivityRetainFragment = (LoveMatchActivityRetainFragment) getSupportFragmentManager().findFragmentByTag("my_fragment");
//            Log.e(debugTag, mLoveMatchActivityRetainFragment + " RETAIN FRGMNT HERE");
        }

        mLoveMatchActivityRetainFragment.getViewModel();
//        Log.e(debugTag, "HERE IT IS "+mLoveMatchActivityRetainFragment.getViewModel());
        if (mLoveMatchActivityRetainFragment.getViewModel() != null) mViewModel = mLoveMatchActivityRetainFragment.getViewModel();


        if (savedInstanceState != null) {
            mBinding.setHasError(savedInstanceState.getBoolean(("haserror")));
            mBinding.setExists(savedInstanceState.getBoolean("exists"));
        }

        ArrayAdapter<String> cadapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, AppConfig.cities);
        mBinding.locationEdt.setAdapter(cadapter);

        ArrayAdapter<String> radapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, AppConfig.races);
        mBinding.raceEdt.setAdapter(radapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchData(page);
        Disposable disp = RxView.clicks(mBinding.locationLlt).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                expandSearchFilters();
            }
        });
        RxEventBus.add(this, disp);
        Disposable disp1 = RxView.clicks(mBinding.collapseImgv).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                hideSoftKeyboard();
                collapseSearchFilters();
            }
        });
        RxEventBus.add(this, disp1);
        Disposable disp2 = RxView.clicks(mBinding.locationEdt).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                expandSearchFilters();
            }
        });
        RxEventBus.add(this, disp2);
        Disposable disp3 = RxView.clicks(mBinding.retryBtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                fetchData(page);
            }
        });
        RxEventBus.add(this, disp3);
        Disposable disp4 = RxView.clicks(mBinding.searchImgv).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                hideSoftKeyboard();
                if (mBinding.locationEdt.getText().toString().isEmpty() && mBinding.raceEdt.getText().toString().isEmpty()) {
                    showSnackBar(getResources().getString(R.string.please_fill_out_search_filters), getResources().getString(R.string.close), Snackbar.LENGTH_SHORT);
                } else if (!mBinding.locationEdt.getText().toString().isEmpty() && mBinding.raceEdt.getText().toString().isEmpty()) {
                    if (!Arrays.asList(AppConfig.cities).contains(mBinding.locationEdt.getText().toString())) {
                        showSnackBar(getResources().getString(R.string.city_no_match), getResources().getString(R.string.close), Snackbar.LENGTH_SHORT);
                    } else {
                        city = mBinding.locationEdt.getText().toString();
                        race = "";
                        search();
                    }
                } else if (mBinding.locationEdt.getText().toString().isEmpty() && !mBinding.raceEdt.getText().toString().isEmpty()) {
                    if (!Arrays.asList(AppConfig.races).contains(mBinding.raceEdt.getText().toString())) {
                        showSnackBar(getResources().getString(R.string.race_not_match), getResources().getString(R.string.close), Snackbar.LENGTH_SHORT);
                    } else {
                        race = mBinding.raceEdt.getText().toString();
                        city = "";
                        search();
                    }
                } else {
                    if (!Arrays.asList(AppConfig.cities).contains(mBinding.locationEdt.getText().toString()) || !Arrays.asList(AppConfig.races).contains(mBinding.raceEdt.getText().toString())) {
                        showSnackBar(getResources().getString(R.string.city_race_not_match), getResources().getString(R.string.close), Snackbar.LENGTH_SHORT);
                    } else {
                        city = mBinding.locationEdt.getText().toString();
                        race = mBinding.raceEdt.getText().toString();
                        search();
                    }
                }
            }
        });
        RxEventBus.add(this, disp4);
        Disposable disp5 = RxView.clicks(mBinding.clearImgv).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                if (isNetworkAvailable()) {
                    page = 1;
                    hasfilters = false;
                    mBinding.setHasFilters(false);
                    collapseSearchFilters();
                    fetchData(page);
                    availableData = true;
                } else {
                    showSnackBar(getResources().getString(R.string.no_internet_connection), getResources().getString(R.string.retry), Snackbar.LENGTH_SHORT);
                }
            }
        });
        RxEventBus.add(this, disp5);
    }

    @Override
    protected void onPause() {
        super.onPause();
        RxEventBus.unregister(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("exists", mBinding.getExists());
        outState.putBoolean(("haserror"), mBinding.getHasError());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog();
        mLoveMatchActivityRetainFragment.retainViewModel(mViewModel);
//        mLoveMatchActivityRetainFragment = null;
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void onViewClick(View view) {
        position = (int)view.getTag();
        showPetProfile(position);
        mBinding.locationEdt.setText("");
        mBinding.raceEdt.setText("");
    }

    @Override
    public void onLoveImageViewClick(View view) {
        position = (int)view.getTag();
        int liked = data.get(position).isLiked() == 0 ? 1 : 0;

        likeDislikeRequest.setAuthtoken(mToken);
        likeDislikeRequest.setAction(getResources().getString(R.string.like_dislike_pet));
        likeDislikeRequest.setLiked(liked);
//        likeDislikeRequest.setSubaction(subaction);
        likeDislikeRequest.setP_id(data.get(position).getId());
//        isLoading = true;
        mViewModel.likeDislikePet(likeDislikeRequest);
    }

    @Override
    public void onMessageIconClick(View view) {
        PetObj obj = data.get((int)view.getTag());
        Bundle bundle = new Bundle();
        bundle.putInt(getResources().getString(R.string.role), 1);
        bundle.putInt(getResources().getString(R.string.user_role_id), obj.getUser_role_id());
        bundle.putInt(getResources().getString(R.string.pet_id), obj.getId());
        bundle.putString(getResources().getString(R.string.action), getResources().getString(R.string.get_chat_rooom_msgs_by_participants));
        bundle.putString(getResources().getString(R.string.receiver), obj.getOwnername());
        bundle.putString(getResources().getString(R.string.receiver_surname), obj.getSurname());
        bundle.putString(getResources().getString(R.string.pet_name), obj.getP_name());
        startActivity(new Intent(this, ChatRoomActivity.class).putExtras(bundle));
    }

    @Override
    public void onPetDataSuccess(GetPetsResponse response) {
        dismissDialog();
//        Log.e(debugTag, "HERE THATS "+response.getData() + " PAGE "+ page);
        mBinding.setHasError(false);
        isLoading = false;
        error = false;
        mBinding.setExists(true);
        if (response.getData() != null && response.getData().size() < 20) availableData = false;
        if (page == 1) {
            data = response.getData();
            if (data != null && data.isEmpty()) {
                mBinding.setHasError(true);
                mBinding.setErrorText(getResources().getString(R.string.no_items));
//                    mBinding.setNoitems(true);
            } else {
                initializeRcView(response.getData());
            }
        } else {
            for (PetObj item : response.getData()) {
                data.add(item);
                rcvAdapter.notifyItemInserted(data.size());
            }
        }
    }

    @Override
    public void onLikeDislikeSuccess(LikeDislikeResponse response) {
        int total_likes = data.get(position).getTotal_likes();
        if (response.getLiked() == 1) {
            total_likes++;
            data.get(position).setTotal_likes(total_likes);
            data.get(position).setLiked(1);
        } else {
            total_likes--;
            data.get(position).setTotal_likes(total_likes);
            data.get(position).setLiked(0);
        }
        rcvAdapter.notifyItemChanged(position);
        showSnackBar(getResources().getString(R.string.success_action), getResources().getString(R.string.close), Snackbar.LENGTH_SHORT);
    }

    @Override
    public void onSuccess(LoveMatchResponse response) {
        dismissDialog();
        mBinding.setHasError(false);
        isLoading = false;
        error = false;
        mBinding.setExists(true);
        if (!response.getAction().equals(getResources().getString(R.string.get_all_pets_by_filter))) {
            int total_likes = data.get(position).getTotal_likes();
            if (response.getSubaction().equals(getResources().getString(R.string.like_pet))) {
                total_likes++;
                data.get(position).setTotal_likes(total_likes);
                data.get(position).setLiked(1);
            } else {
                total_likes--;
                data.get(position).setTotal_likes(total_likes);
                data.get(position).setLiked(0);
            }
            rcvAdapter.notifyItemChanged(position);
            showSnackBar(getResources().getString(R.string.success_action), getResources().getString(R.string.close), Snackbar.LENGTH_SHORT);
        } else {
            if (response.getData() != null && response.getData().size() < 20) availableData = false;
            if (page == 1) {
                data = response.getData();
                if (data != null && data.isEmpty()) {
                    mBinding.setHasError(true);
                    mBinding.setErrorText(getResources().getString(R.string.no_items));
//                    mBinding.setNoitems(true);
                } else {
//                    initializeRcView(response.getData());
                }
            } else {
                for (PetObj item : response.getData()) {
                    data.add(item);
                    rcvAdapter.notifyItemInserted(data.size());
                }
            }
        }
    }

    @Override
    public void onError(int resource) {
        dismissDialog();
        uiUtls.showSnackBar(mBinding.cntFrml, getResources().getString(resource), getResources().getString(R.string.retry), Snackbar.LENGTH_INDEFINITE).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean aBoolean) throws Exception {
                mViewModel.retry();
            }
        });
    }

    @Override
    public void onGetPetsByFilterError(int resource) {
        isLoading = false;
        error = true;
        if (resource == R.string.no_owner_exists) {
            mBinding.setExists(false);
            mBinding.setHasError(true);
            mBinding.setErrorText(getResources().getString(resource));
        } else {
            if (page == 1) {
                mBinding.setHasError(true);
                mBinding.setErrorText(getResources().getString(R.string.error));
            } else {
                showSnackBar(getResources().getString(resource), getResources().getString(R.string.close), Snackbar.LENGTH_SHORT);
            }
        }
    }

    private void search() {
        page = 1;
        hasfilters = true;
        mBinding.setHasFilters(true);
        if (!data.isEmpty()) {
            data.clear();
            rcvAdapter.notifyDataSetChanged();
        }
        fetchData(page);
    }

    private void showPetProfile(int position) {
        String[] urls = {};
        if (data.get(position).getStrurls() != null) urls = data.get(position).getStrurls().replace("[", "").replace("]", "").split(",");
        Intent intent = new Intent(this, PetProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(getResources().getString(R.string.pet_obj), data.get(position));
        bundle.putStringArray(getResources().getString(R.string.urls), urls);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void collapseSearchFilters() {
        mBinding.setExpanded(false);
        mBinding.locationIconImgv.setImageResource(R.drawable.ic_search_white);
        mBinding.locationEdt.setHint(getResources().getString(R.string.love_match_location_plhldr));
        mBinding.locationEdt.setFocusable(false);
        if (!hasfilters) {
            if (!mBinding.locationEdt.getText().toString().isEmpty())mBinding.locationEdt.setText("");
            if (!mBinding.raceEdt.getText().toString().isEmpty())mBinding.raceEdt.setText("");
        }
    }

    private void initializeRcView(final ArrayList<PetObj> data) {
        linearLayoutManager = new LinearLayoutManager(this);
        if (rcvAdapter == null)mBinding.rcv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        rcvAdapter = new RecyclerViewAdapter(R.layout.love_match_rcv_row) {
            @Override
            protected Object getObjForPosition(int position, ViewDataBinding mBinding) {
                return data.get(position);
            }
            @Override
            protected int getLayoutIdForPosition(int position) {
                return R.layout.love_match_rcv_row;
            }
            @Override
            protected int getTotalItems() {
                return data.size();
            }

            @Override
            protected Object getClickListenerObject() {
                return mViewModel;
            }
        };
        mBinding.rcv.setLayoutManager(new LinearLayoutManager(this));
        mBinding.rcv.setNestedScrollingEnabled(false);
        mBinding.rcv.setAdapter(rcvAdapter);
        mBinding.rcv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisibleItem =   linearLayoutManager.findLastVisibleItemPosition();
                if (totalItemCount <= lastVisibleItem + AppConfig.VISIBLE_THRESHOLD) {
                    if (!isLoading && availableData && !error) {
                        page++;
                        fetchData(page);
                    }
                }
            }
        });
    }

    private void fetchData(int page) {
//        if (page ==1 )initializeProgressDialog(getResources().getString(R.string.please_wait));
        final GetPetsByFilterRequest request = new GetPetsByFilterRequest();
        request.setAction(getResources().getString(R.string.get_all_pets_by_filter));
        request.setAuthtoken(mToken);
        request.setPage(page);
        isLoading = true;
        if (hasfilters) {
            request.setHasfilters(true);
            request.setCity(city);
            request.setRace(race);
//                collapseSearchFilters();
        }
        mViewModel.getPetsByFilter(request);


//        if (isNetworkAvailable()) {
//
//
//        } else {
//            if (page > 1) {
//                error = true;
//                showSnackBar(getResources().getString(R.string.no_internet_connection), getResources().getString(R.string.retry), Snackbar.LENGTH_INDEFINITE);
//            } else {
//                mBinding.setHasError(true);
//                mBinding.setErrorText(getResources().getString(R.string.no_internet_connection));
//            }
//        }
    }

    private void expandSearchFilters() {
        mBinding.setExpanded(true);
        mBinding.locationIconImgv.setImageResource(R.drawable.ic_location_white);
        mBinding.locationEdt.setHint(getResources().getString(R.string.location_filter_plhldr));
        mBinding.locationEdt.setFocusableInTouchMode(true);
    }

    public void showSnackBar(final String msg, final String action, int length_code) {
        Snackbar snackbar = Snackbar
                .make(mBinding.cntFrml, msg, length_code)
                .setAction(action, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (action.equals(getResources().getString(R.string.retry)) && page > 1) fetchData(page);
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
