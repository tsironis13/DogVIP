package com.tsiro.dogvip.login.forgotpass;

import android.app.ProgressDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.tsiro.dogvip.POJO.BaseResponseObj;
import com.tsiro.dogvip.POJO.forgotpasswrd.ForgotPaswrdObj;
import com.tsiro.dogvip.POJO.login.forgotpass.ForgotPassRequest;
import com.tsiro.dogvip.POJO.login.forgotpass.ForgotPassResponse;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.base.fragment.BaseFragment;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ForgotpaswrdFrgmtBinding;
import com.tsiro.dogvip.login.LoginActivity;
import com.tsiro.dogvip.login.LoginContract;
import com.tsiro.dogvip.login.signin.SignInFrgmt;
import com.tsiro.dogvip.utilities.animation.AnimationListener;
import com.tsiro.dogvip.utilities.common.CommonUtls;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import javax.inject.Inject;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Created by giannis on 17/5/2017.
 */

public class ForgotPaswrdFrgmt extends BaseFragment implements LoginContract.ForgotPassView {

    private View mView;
    private ForgotpaswrdFrgmtBinding mBinding;
    private int fragmentCreatedCode; // login activity: check if fragments are created on button click
    private boolean emailValidated;
    @Inject
    ForgotPaswrdViewModel mViewModel;
    @Inject
    ForgotPaswrdRetainFragment mForgotPaswrdRetainFragment;
    @Inject
    AwesomeValidation mAwesomeValidation;
    @Inject
    ForgotPassRequest forgotPassRequest;

    public static ForgotPaswrdFrgmt newInstance(int x) {
        Bundle bundle = new Bundle();
        bundle.putInt(AppConfig.FRAGMENT_CREATED, x);
        ForgotPaswrdFrgmt sign = new ForgotPaswrdFrgmt();
        sign.setArguments(bundle);
        return sign;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentCreatedCode = getArguments().getInt(AppConfig.FRAGMENT_CREATED);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (fragmentCreatedCode != 100) return null;

        if (mView == null ) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.forgotpaswrd_frgmt, container, false);
            mView = mBinding.getRoot();
            fragmentCreatedCode = 300;
        }
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeViewModel();

        mAwesomeValidation.addValidation(mBinding.emailEdt, Patterns.EMAIL_ADDRESS, getResources().getString(R.string.not_valid_email));
        if (savedInstanceState != null) {
            mBinding.setEmailisvalid(savedInstanceState.getBoolean("kalase"));
            if (mBinding.getEmailisvalid()) {
                mAwesomeValidation.addValidation(mBinding.passEdt, "^(?=.*\\D)[a-zA-Z\\d]{8}$", getResources().getString(R.string.not_valid_pass));
                mAwesomeValidation.addValidation(mBinding.confpassEdt, mBinding.passEdt, getResources().getString(R.string.passwrds_not_match));
            }
        }
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void onResume() {
        super.onResume();
        Disposable disp2 = RxTextView.textChanges(mBinding.emailEdt)
                .skip(1)
                .filter(charSequence -> emailValidated)
                .subscribe(charSequence -> {
                    if (emailValidated) {
//                        mBinding.setViewstate(false);
                        emailValidated = false;
                    }
                });
        RxEventBus.add(this, disp2);

        Disposable disp = RxView.clicks(mBinding.submitBtn).subscribe(o -> {
            forgotPassRequest.setEmail(mBinding.emailEdt.getText().toString());
            forgotPassRequest.setConfNewpassword(mBinding.passEdt.getText().toString());
            forgotPassRequest.setNewpassword(mBinding.confpassEdt.getText().toString());

            if (mAwesomeValidation.validate()) {
                ((LoginActivity)getActivity()).hideSoftKeyboard();
                mViewModel.handleUserInputAction(forgotPassRequest);
//                submit(mBinding.emailEdt.getText().toString(), subaction);
            }
        });
        RxEventBus.add(this, disp);

        Disposable disp1 = RxView.clicks(mBinding.signInBtn).subscribe(
                o -> {
                    ((LoginActivity)getActivity()).hideSoftKeyboard();
                    getFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                                    .replace(R.id.loginContainer, SignInFrgmt.newInstance(200), getResources().getString(R.string.signin_fgmt))
                                    .addToBackStack(getResources().getString(R.string.signin_fgmt))
                                    .commit();
                }
        );
        RxEventBus.add(this, disp1);
    }

    @Override
    public void onPause() {
        super.onPause();
        RxEventBus.unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("kalase", mBinding.getEmailisvalid());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mForgotPaswrdRetainFragment.retainViewModel(mViewModel);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (nextAnim != 0) {
            Animation animation = AnimationUtils.loadAnimation(getActivity(), nextAnim);
            if (enter) return super.onCreateAnimation(transit, true, nextAnim);
            animation.setAnimationListener(new AnimationListener());

            AnimationSet animationSet = new AnimationSet(true);
            animationSet.addAnimation(animation);

            return animationSet;
        } else {
            return null;
        }
    }

    @Override
    public void onProcessing() {
        mBinding.setProcessing(true);
        ((LoginActivity)getActivity()).onProcessing(mBinding.loadingInd);
    }

    @Override
    public void onStopProcessing() {
        if (mBinding.getProcessing()) mBinding.setProcessing(false);
    }

    @Override
    public void onSuccessIsEmailValid(ForgotPassResponse response) {
        mAwesomeValidation.addValidation(mBinding.passEdt, "^(?=.*\\D)[a-zA-Z\\d]{8}$", getResources().getString(R.string.not_valid_pass));
        mAwesomeValidation.addValidation(mBinding.confpassEdt, mBinding.passEdt, getResources().getString(R.string.passwrds_not_match));
        mBinding.setProcessing(false);
        mBinding.setEmailisvalid(true);
        mViewModel.setEmailValid(response.getUserId());
    }

    @Override
    public void onSuccessNewPassChange(BaseResponseObj response) {
        mBinding.setProcessing(false);
        new CommonUtls(getActivity()).buildNotification(getResources().getString(R.string.reset_passwrd_request), getResources().getString(R.string.reset_passwrd_success));
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .replace(R.id.loginContainer, SignInFrgmt.newInstance(100), getResources().getString(R.string.signin_fgmt))
                .addToBackStack(getResources().getString(R.string.signin_fgmt))
                .commit();
    }

    @Override
    public void onError(final int resource) {
        mBinding.setProcessing(false);
        ((LoginActivity)getActivity()).onError(resource);
    }

    private void initializeViewModel() {
        if (getFragmentManager().findFragmentByTag(getResources().getString(R.string.retained_forgotpass_fgmt)) == null) {
            getFragmentManager().beginTransaction().add(mForgotPaswrdRetainFragment, getResources().getString(R.string.retained_forgotpass_fgmt)).commit();
        } else {
            mForgotPaswrdRetainFragment = (ForgotPaswrdRetainFragment) getFragmentManager().findFragmentByTag(getResources().getString(R.string.retained_forgotpass_fgmt));
        }
        if (mForgotPaswrdRetainFragment.getViewModel() != null) mViewModel = mForgotPaswrdRetainFragment.getViewModel();
    }

}
