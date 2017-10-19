package com.tsiro.dogvip.profs.profprofile;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxCompoundButton;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.tsiro.dogvip.POJO.DialogActions;
import com.tsiro.dogvip.POJO.profs.DeleteProfRequest;
import com.tsiro.dogvip.POJO.profs.GetProfDetailsRequest;
import com.tsiro.dogvip.POJO.profs.ProfDetailsResponse;
import com.tsiro.dogvip.POJO.profs.ProfObj;
import com.tsiro.dogvip.POJO.profs.SearchProfFormValidation;
import com.tsiro.dogvip.POJO.profs.SearchProfsRequest;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivityProfProfileBinding;
import com.tsiro.dogvip.profs.SearchedProfsActivity;
import com.tsiro.dogvip.profs.prof.ProfActivity;
import com.tsiro.dogvip.requestmngrlayer.ProfRequestManager;
import com.tsiro.dogvip.utilities.NetworkUtls;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by giannis on 30/9/2017.
 */

public class ProfProfileActivity extends BaseActivity implements ProfProfileContract.View {

    private static final String debugTag = ProfProfileActivity.class.getSimpleName();
    private ActivityProfProfileBinding mBinding;
    private ProfProfileContract.ViewModel mViewModel;
    private String mToken;
    private MenuItem searchItem, deleteItem;
    private ProfObj profObj;
    private List<Integer> categoriesList = new ArrayList<>();
    private Observable<Integer> indexes = Observable.fromIterable(Arrays.asList(1,2,3,4,5,6));
    private DisposableSubscriber<SearchProfFormValidation> searchProfFormValidationDisp;
    private SearchProfFormValidation searchProfFormValidation = new SearchProfFormValidation();
    private NetworkUtls networkUtls = new NetworkUtls(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_prof_profile);
        setSupportActionBar(mBinding.incltoolbar.toolbar);
        mViewModel = new ProfProfileViewModel(ProfRequestManager.getInstance());
        mToken = getMyAccountManager().getAccountDetails().getToken();

        ArrayAdapter<String> cadapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, AppConfig.cities);
        mBinding.locationEdt.setAdapter(cadapter);

        if (getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(getResources().getString(R.string.prof_profile_title));
        }
        if (savedInstanceState != null) {
            profObj = savedInstanceState.getParcelable(getResources().getString(R.string.parcelable_obj));
        }

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

        initializeView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Disposable disp = RxView.clicks(mBinding.retryBtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                initializeView();
            }
        });
        RxEventBus.add(this, disp);
        Disposable disp1 = RxView.clicks(mBinding.editProfFab).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                Bundle bundle = new Bundle();
                bundle.putParcelable(getResources().getString(R.string.parcelable_obj), profObj);
                startActivity(new Intent(ProfProfileActivity.this, ProfActivity.class).putExtras(bundle));
            }
        });
        RxEventBus.add(this, disp1);
        Disposable disp2 = RxView.clicks(mBinding.collapseImgv).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                hideSoftKeyboard();
                mBinding.locationEdt.setText("");
                mBinding.locationEdt.setFocusableInTouchMode(false);
                mBinding.setSearch(false);
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
        outState.putParcelable(getResources().getString(R.string.parcelable_obj), profObj);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(searchProfFormValidationDisp != null)searchProfFormValidationDisp.dispose();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profs_menu, menu);
        searchItem = menu.findItem(R.id.search_item);
        deleteItem = menu.findItem(R.id.delete_item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_item:
                if (mBinding.getSearch()) {
                    search();
//                    mBinding.setSearch(false);
                } else {
                    mBinding.locationEdt.setFocusableInTouchMode(true);
                    mBinding.setSearch(true);
                }
                return true;
            case R.id.delete_item:
                deleteProf();
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
        if (response.getAction() != null && response.getAction().equals(getResources().getString(R.string.delete_prof))) {
            deleteItem.setVisible(false);
            mBinding.setHaserror(true);
            mBinding.setErrortext(getResources().getString(R.string.no_prof_exists));
        } else if (response.getAction() != null && response.getAction().equals(getResources().getString(R.string.search_prof))) {
            if (!response.getProfs().isEmpty()) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(getResources().getString(R.string.data), response.getProfs());
                startActivity(new Intent(this, SearchedProfsActivity.class).putExtras(bundle));
            } else {
                showSnackBar(getResources().getString(R.string.no_data), getResources().getString(R.string.close), Snackbar.LENGTH_SHORT);
            }
        } else {
            profObj = response.getProf();
            if (response.getCode() == AppConfig.STATUS_OK) {
                searchItem.setVisible(true);
                deleteItem.setVisible(true);
                showProfDetails(profObj);
            } else if (response.getCode() == AppConfig.ERROR_NO_OWNER_EXISTS) {
                deleteItem.setVisible(true);
                showProfDetails(profObj);
            } else if (response.getCode() == AppConfig.ERROR_NO_PROF_EXISTS) {
                searchItem.setVisible(true);
                mBinding.setHaserror(true);
                mBinding.setErrortext(getResources().getString(R.string.no_prof_exists));
            } else if (response.getCode() == AppConfig.ERROR_NO_OWNER_AND_PROF_EXISTS) {
                mBinding.setHaserror(true);
                mBinding.setErrortext(getResources().getString(R.string.no_prof_exists));
            }
        }
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

    private void initializeView() {
        if (mBinding.getShowretry()) mBinding.setShowretry(false);
        if (isNetworkAvailable()) {
            GetProfDetailsRequest request = new GetProfDetailsRequest();
            request.setAction(getResources().getString(R.string.get_user_prof));
            request.setAuthtoken(mToken);
            initializeProgressDialog(getResources().getString(R.string.please_wait));
            mViewModel.getProfDetails(request);
        } else {
            mBinding.setHaserror(true);
            mBinding.setShowretry(true);
            mBinding.setErrortext(getResources().getString(R.string.no_internet_connection));
        }
    }

    private void showProfDetails(ProfObj profObj) {
        mBinding.setProf(profObj);
        for (Integer category: profObj.getCategories()) {
            TextView textView = new TextView(this);
            textView.setText(AppConfig.getProfCategoryHashMap().get(category));
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            mBinding.categoriesLlt.addView(textView);
        }
    }

    private DisposableSubscriber<SearchProfFormValidation> isSearchFormValid() {
        return new DisposableSubscriber<SearchProfFormValidation>() {
            @Override
            public void onNext(SearchProfFormValidation searchProfFormValidation) {
                if (searchProfFormValidation.getCode() == AppConfig.STATUS_ERROR) {
                    showSnackBar(searchProfFormValidation.getMsg(), getResources().getString(R.string.close), Snackbar.LENGTH_SHORT);
                } else {
                    SearchProfsRequest request = new SearchProfsRequest();
                    request.setAction(getResources().getString(R.string.search_prof));
                    request.setAuthtoken(mToken);
                    request.setCity(mBinding.locationEdt.getText().toString());
                    request.setCategories(categoriesList);
                    initializeProgressDialog(getResources().getString(R.string.please_wait));
                    mViewModel.searchProf(request);
                }
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onComplete() {
            }
        };
    }

    private Flowable<SearchProfFormValidation> subscribe1() {
        return Flowable
                .zip(
                        Observable.just(categoriesList).toFlowable(BackpressureStrategy.LATEST),
                        Observable.just(mBinding.locationEdt.getText().toString()).toFlowable(BackpressureStrategy.LATEST),
                        networkUtls.isNetworkAvailable1(),
                        new Function3<List<Integer>, String, Boolean, SearchProfFormValidation>() {
                            @Override
                            public SearchProfFormValidation apply(@NonNull List<Integer> categories, @NonNull String city, @NonNull Boolean networkOn) throws Exception {
                                if (city.isEmpty() && categories.isEmpty()) {
                                    searchProfFormValidation.setCode(AppConfig.STATUS_ERROR);
                                    searchProfFormValidation.setMsg(getResources().getString(R.string.please_fill_out_search_filters));
                                    return searchProfFormValidation;
                                }
                                if (!city.isEmpty() && !Arrays.asList(AppConfig.cities).contains(mBinding.locationEdt.getText().toString())) {
                                    searchProfFormValidation.setCode(AppConfig.STATUS_ERROR);
                                    searchProfFormValidation.setMsg(getResources().getString(R.string.city_no_match));
                                    return searchProfFormValidation;
                                }
                                if (!networkOn) {
                                    searchProfFormValidation.setCode(AppConfig.STATUS_ERROR);
                                    searchProfFormValidation.setMsg(getResources().getString(R.string.no_internet_connection));
                                    return searchProfFormValidation;
                                }
                                searchProfFormValidation.setCode(AppConfig.STATUS_OK);
                                return searchProfFormValidation;
                            }
                        }

                );
    }

    private void search() {
        hideSoftKeyboard();
        searchProfFormValidationDisp = isSearchFormValid();
        subscribe1().subscribe(searchProfFormValidationDisp);
    }

    private void deleteProf() {
        initializeDialog(getResources().getString(R.string.delete_owner), getResources().getString(R.string.delete_prof_desc), getResources().getString(R.string.delete_prof_title), getResources().getString(R.string.cancel), getResources().getString(R.string.confirm));
    }

    private void initializeDialog(String action, final String desc, String title, String positiveBtnTxt, String negativeBtnTxt) {
        Disposable dialogDisp = initializeGenericDialog(action, desc, title, positiveBtnTxt, negativeBtnTxt).subscribe(new Consumer<DialogActions>() {
            @Override
            public void accept(@NonNull DialogActions obj) throws Exception {
                if (obj.getAction().equals(getResources().getString(R.string.delete_owner))) {
                    if (obj.getSelected_action() == 1) {//positive action
                        DeleteProfRequest deleteProfRequest = new DeleteProfRequest();
                        deleteProfRequest.setAction(getResources().getString(R.string.delete_prof));
                        deleteProfRequest.setAuthtoken(mToken);
                        deleteProfRequest.setId(profObj.getId());
                        initializeProgressDialog(getResources().getString(R.string.please_wait));
                        mViewModel.deleteProf(deleteProfRequest);
                    }
                }
            }
        });
        RxEventBus.add(this, dialogDisp);
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
