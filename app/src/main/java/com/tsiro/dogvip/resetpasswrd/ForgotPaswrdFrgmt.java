package com.tsiro.dogvip.resetpasswrd;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
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
import com.tsiro.dogvip.LoginActivity;
import com.tsiro.dogvip.POJO.forgotpasswrd.ForgotPaswrdObj;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.BaseFragment;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ForgotpaswrdFrgmtBinding;
import com.tsiro.dogvip.login.SignInFrgmt;
import com.tsiro.dogvip.requestmngrlayer.ForgotPaswrdRequestManager;
import com.tsiro.dogvip.utilities.animation.AnimationListener;
import com.tsiro.dogvip.utilities.common.CommonUtls;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by giannis on 17/5/2017.
 */

public class ForgotPaswrdFrgmt extends BaseFragment implements ForgotPaswrdContract.View{

    private static final String debugTag = ForgotPaswrdFrgmt.class.getSimpleName();
    private View mView;
    private ForgotpaswrdFrgmtBinding mBinding;
    private FragmentManager mFragmentManager;
    private int fragmentCreatedCode; // login activity: check if fragments are created on button click
    private ForgotPaswrdContract.ViewModel mForgotPaswrdViewModel;
    private AwesomeValidation mAwesomeValidation;
    private ForgotPaswrdObj forgotPaswrdObj;
    private ProgressDialog mProgressDialog;
    private boolean emailValidated;
    private Lifecycle.BaseView baseView;
    private int user_id;

    public static ForgotPaswrdFrgmt newInstance(int x) {
        Bundle bundle = new Bundle();
        bundle.putInt(AppConfig.FRAGMENT_CREATED, x);
        ForgotPaswrdFrgmt sign = new ForgotPaswrdFrgmt();
        sign.setArguments(bundle);
        return sign;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if ( context instanceof Activity) this.baseView = (Lifecycle.BaseView) context;
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
        mFragmentManager = getActivity().getSupportFragmentManager();

        mForgotPaswrdViewModel = new ForgotPaswrdViewModel(ForgotPaswrdRequestManager.getInstance());

        mAwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        mAwesomeValidation.addValidation(mBinding.emailEdt, Patterns.EMAIL_ADDRESS, getResources().getString(R.string.not_valid_email));

        forgotPaswrdObj = new ForgotPaswrdObj();
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return mForgotPaswrdViewModel;
    }

    @Override
    public void onResume() {
        super.onResume();
        Disposable disp2 = RxTextView.textChanges(mBinding.emailEdt)
                .skip(1)
                .filter(new Predicate<CharSequence>() {
                    @Override
                    public boolean test(@NonNull CharSequence charSequence) throws Exception {
                        return emailValidated;
                    }
                })
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(@NonNull CharSequence charSequence) throws Exception {
                        if (emailValidated) {
                            mBinding.passLlt.setVisibility(View.GONE);
                            mBinding.confpassLlt.setVisibility(View.GONE);
                            emailValidated = false;
                        }
                    }
                });
        RxEventBus.add(this, disp2);

        Disposable disp = RxView.clicks(mBinding.submitBtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                String subaction = getResources().getString(R.string.forgot_paswrd_email_subaction);
                if (emailValidated) {
                    mAwesomeValidation.addValidation(mBinding.passEdt, "^(?=.*\\D)[a-zA-Z\\d]{8}$", getResources().getString(R.string.not_valid_pass));
                    mAwesomeValidation.addValidation(mBinding.confpassEdt, mBinding.passEdt, getResources().getString(R.string.passwrds_not_match));
                    subaction = getResources().getString(R.string.forgot_paswrd_confpass_subaction);
                }
                if (mAwesomeValidation.validate()) {
                    baseView.hideSoftKeyboard();
                    submit(mBinding.emailEdt.getText().toString(), subaction);
                }
            }
        });
        RxEventBus.add(this, disp);

        Disposable disp1 = RxView.clicks(mBinding.signInBtn).subscribe(
                new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        baseView.hideSoftKeyboard();
                        mFragmentManager
                                .beginTransaction()
                                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                                .replace(R.id.loginContainer, SignInFrgmt.newInstance(200), getResources().getString(R.string.signin_fgmt))
                                .addToBackStack(getResources().getString(R.string.signin_fgmt))
                                .commit();
                    }
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
    public void onSuccess(final ForgotPaswrdObj response) {
        ((LoginActivity) getActivity()).dismissDialog();
        if (response.getSubaction().equals(getResources().getString(R.string.forgot_paswrd_email_subaction))) {
            mBinding.passLlt.setVisibility(View.VISIBLE);
            mBinding.confpassLlt.setVisibility(View.VISIBLE);
            emailValidated = true;
            user_id = response.getUserId();
        } else {
            new CommonUtls(getActivity()).buildNotification(getResources().getString(R.string.reset_passwrd_request), getResources().getString(R.string.reset_passwrd_success));
            mFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                    .replace(R.id.loginContainer, SignInFrgmt.newInstance(100), getResources().getString(R.string.signin_fgmt))
                    .addToBackStack(getResources().getString(R.string.signin_fgmt))
                    .commit();
        }

    }

    @Override
    public void onError(final int resource, final boolean msglength) {
        ((LoginActivity) getActivity()).dismissDialog();
        int style = R.style.SnackBarSingleLine;
        if (msglength) style = R.style.SnackBarMultiLine;
        ((LoginActivity)getActivity()).showSnackBar(style, getResources().getString(resource));
    }

    private void submit(String email, String subaction) {
        if (((LoginActivity) getActivity()).isNetworkAvailable()) {
            forgotPaswrdObj.setAction(getResources().getString(R.string.forgot_paswrd));
            forgotPaswrdObj.setSubaction(subaction);
            forgotPaswrdObj.setEmail(email);
            if (subaction.equals(getResources().getString(R.string.forgot_paswrd_confpass_subaction))) {
                forgotPaswrdObj.setNewpassword(mBinding.passEdt.getText().toString());
                forgotPaswrdObj.setConfNewpassword(mBinding.confpassEdt.getText().toString());
                forgotPaswrdObj.setUserId(user_id);
            }
            mForgotPaswrdViewModel.fogotpass(forgotPaswrdObj);
            mProgressDialog = ((LoginActivity) getActivity()).initializeProgressDialog(getResources().getString(R.string.please_wait));
        } else {
            ((LoginActivity)getActivity()).showSnackBar(R.style.SnackBarSingleLine, getResources().getString(R.string.no_internet_connection));
        }
    }
}
