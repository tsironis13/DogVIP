package com.tsiro.dogvip.lostfound;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.LostFoundActivity;
import com.tsiro.dogvip.POJO.lostfound.LostFoundObj;
import com.tsiro.dogvip.POJO.lostfound.LostFoundRequest;
import com.tsiro.dogvip.POJO.lostfound.LostFoundResponse;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.adapters.LostFoundViewPager;
import com.tsiro.dogvip.base.activity.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivityLostBinding;
import com.tsiro.dogvip.requestmngrlayer.LostFoundRequestManager;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by giannis on 10/7/2017.
 */

public class LostActivity extends BaseActivity implements LostFoundContract.View {

    private ActivityLostBinding mBinding;
    private String mToken, subaction;
    private LostFoundContract.ViewModel mViewModel;
    private int type;//0 lost, 1 found
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_lost);
        mToken = getMyAccountManager().getAccountDetails().getToken();
        mViewModel = new LostFoundViewModel(LostFoundRequestManager.getInstance());
        setSupportActionBar(mBinding.incltoolbar.toolbar);
        callbackManager = CallbackManager.Factory.create();
        ShareDialog shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                showSnackBar(getResources().getString(R.string.success_action), getResources().getString(R.string.close), Snackbar.LENGTH_SHORT);
            }

            @Override
            public void onError(FacebookException error) {
                showSnackBar(getResources().getString(R.string.error), getResources().getString(R.string.close), Snackbar.LENGTH_SHORT);
            }

            @Override
            public void onCancel() {
            }
        });

        if (savedInstanceState != null) {
            type = savedInstanceState.getInt(getResources().getString(R.string.type));
        } else {
            if (getIntent() != null) {
                type = getIntent().getExtras().getInt(getResources().getString(R.string.type));
            }
        }
        if (getSupportActionBar()!= null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (type == 0) {
                setTitle(getResources().getString(R.string.lost_title));
                subaction = getResources().getString(R.string.lost_subaction);
            } else {
                setTitle(getResources().getString(R.string.found_title));
                subaction = getResources().getString(R.string.found_subaction);
            }
        }
        fetchData(mToken);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Disposable disp = RxView.clicks(mBinding.retryBtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                fetchData(mToken);
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
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(getResources().getString(R.string.type), type);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, LostFoundActivity.class));
        super.onBackPressed();
    }

    @Override
    public void onSuccess(LostFoundResponse response) {
        dismissDialog();
        mBinding.setHaserror(false);
        LostFoundViewPager viewPager = new LostFoundViewPager(getSupportFragmentManager(), getResources().getStringArray(R.array.lost_found_tabs), type, response);
        mBinding.viewPgr.enablePaging(false);
        mBinding.viewPgr.setAdapter(viewPager);
        mBinding.tabs.setupWithViewPager(mBinding.viewPgr);
    }

    @Override
    public void onError(int resource) {
        dismissDialog();
        mBinding.setHaserror(true);
        mBinding.setErrortext(getResources().getString(resource));
    }

    @Override
    public void onBaseViewClick(LostFoundObj lostFoundObj, int type) {
        if (lostFoundObj != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(getResources().getString(R.string.parcelable_obj), lostFoundObj);
            bundle.putInt(getResources().getString(R.string.type), type);
            startActivity(new Intent(this, LostFoundDetailsActivity.class).putExtras(bundle));
        }
    }

    @Override
    public void onShareIconClick(LostFoundObj lostFoundObj) {
        String image = lostFoundObj.getThumb_image();
        String quoteText;
        if (image == null) image = "http://dogvip.votingsystem.gr/dev/api/images/logo.png";
        if (type == 0) {
            quoteText = getResources().getString(R.string.lost_pet_lbl);
            if (!lostFoundObj.getRace().equals("")) quoteText += " " + lostFoundObj.getRace() + " ";
        } else {
            quoteText = getResources().getString(R.string.found_pet_lbl);
            if (!lostFoundObj.getInfo().equals("")) quoteText += " " + lostFoundObj.getInfo() + " ";
        }
        quoteText += " στην περιοχή " + lostFoundObj.getLocation() + " στις: " + lostFoundObj.getDisplaydate() + " και ώρα: " + lostFoundObj.getTime_lost();

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(image))
                    .setQuote(quoteText)
                    .build();
            ShareDialog shareDialog = new ShareDialog(this);
//            ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
//                    .putString("og:type", "article")
//                    .putString("og:title", "A Game of Thrones")
////                    .putString("og:description", "In the frozen wastes to the north of Winterfell, sinister and supernatural forces are mustering.")
////                    .putString("og:image", image)
////                    .putString("article:message", "ssss")
//                    .build();
//            ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
//                    .setActionType("news.publishes")
//                    .putObject("book", object)
//                    .build();

//            ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
//                    .setPreviewPropertyName("book")
//                    .setAction(action)
//                    .build();
//            shareDialog.show(content);

            shareDialog.show(linkContent);
        }
    }

    public String getmToken() {
        return mToken;
    }

    private void fetchData(String token) {
        if (isNetworkAvailable()) {
            initializeProgressDialog(getResources().getString(R.string.please_wait));
            LostFoundRequest request = new LostFoundRequest();
            request.setAction(getResources().getString(R.string.get_lost_found_entries));
            request.setSubaction(subaction);
            request.setAuthtoken(token);
            mViewModel.getLostPets(request);
        } else {
            mBinding.setHaserror(true);
            mBinding.setErrortext(getResources().getString(R.string.no_internet_connection));
        }
    }

    public void showSnackBar(final String msg, final String action, int length_code) {
        Snackbar snackbar = Snackbar
                .make(mBinding.cntFrml, msg, length_code)
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
