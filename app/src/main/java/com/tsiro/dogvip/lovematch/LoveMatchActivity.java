package com.tsiro.dogvip.lovematch;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.os.Handler;
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
import com.tsiro.dogvip.POJO.lovematch.LoveMatchRequest;
import com.tsiro.dogvip.POJO.lovematch.LoveMatchResponse;
import com.tsiro.dogvip.POJO.mypets.pet.PetObj;
import com.tsiro.dogvip.chatroom.ChatRoomActivity;
import com.tsiro.dogvip.lovematch.viewmodel.GetPetsViewModel;
import com.tsiro.dogvip.lovematch.viewmodel.LikeDislikeViewModel;
import com.tsiro.dogvip.lovematch.viewmodel.LoveMatchViewModel;
import com.tsiro.dogvip.networklayer.LoveMatchAPIService;
import com.tsiro.dogvip.petprofile.PetProfileActivity;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.adapters.RecyclerViewAdapter;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivityLoveMatchBinding;
import com.tsiro.dogvip.requestmngrlayer.LoveMatchRequestManager;
import com.tsiro.dogvip.retrofit.RetrofitFactory;
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
//    private GetPetsViewModel getPetsViewModel = new GetPetsViewModel(mLoveMatchRequestManager);;
//    @Inject
//    LoveMatchRequestManager loveMatchRequestManager;
    //    @Inject
//    ServiceAPI serviceAPI;
    @Inject
    LoveMatchViewModel mViewModel;
//    @Inject
//    GetPetsViewModel mViewModel;
//    @Inject
//    LikeDislikeViewModel mLikeDislikeViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

//        mViewModel1 = new LoveMatchViewModel(new LoveMatchRequestManager(new LoveMatchAPIService(RetrofitFactory.getInstance().getServiceAPI())));
//        mViewModel = new GetPetsViewModel(new LoveMatchRequestManager(new LoveMatchAPIService(RetrofitFactory.getInstance().getServiceAPI())));
//mLikeDislikeViewModel = new LikeDislikeViewModel(new LoveMatchRequestManager(new LoveMatchAPIService(RetrofitFactory.getInstance().getServiceAPI())));
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_love_match);
        mToken = getMyAccountManager().getAccountDetails().getToken();

        ArrayAdapter<String> cadapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, AppConfig.cities);
        mBinding.locationEdt.setAdapter(cadapter);

        ArrayAdapter<String> radapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, AppConfig.races);
        mBinding.raceEdt.setAdapter(radapter);

        fetchData(page);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
//        getViewModel().onViewAttached(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        RxEventBus.unregister(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog();
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
        if (isNetworkAvailable()) {
            if (!isLoading) {
                String subaction = data.get(position).isLiked() == 0 ? getResources().getString(R.string.like_pet) : getResources().getString(R.string.dislike_pet);
                LoveMatchRequest request = new LoveMatchRequest();
                request.setAction(getResources().getString(R.string.like_dislike_pet));
                request.setSubaction(subaction);
                request.setP_id(data.get(position).getId());
                request.setAuthtoken(mToken);
                isLoading = true;
                mViewModel.getPetsByFilter(request);
            }
        } else {
            showSnackBar(getResources().getString(R.string.no_internet_connection), getResources().getString(R.string.retry), Snackbar.LENGTH_SHORT);
        }
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
                    initializeRcView(response.getData());
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
//        RecyclerTouchListener listener = new RecyclerTouchListener(this, mBinding.rcv);
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
        mBinding.rcv.setLayoutManager(linearLayoutManager);
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
        if (isNetworkAvailable()) {
            if (page ==1 )initializeProgressDialog(getResources().getString(R.string.please_wait));
            final LoveMatchRequest request = new LoveMatchRequest();
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

        } else {
            if (page > 1) {
                error = true;
                showSnackBar(getResources().getString(R.string.no_internet_connection), getResources().getString(R.string.retry), Snackbar.LENGTH_INDEFINITE);
            } else {
                mBinding.setHasError(true);
                mBinding.setErrorText(getResources().getString(R.string.no_internet_connection));
            }
        }
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
