package com.tsiro.dogvip.login.signin;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
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
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.POJO.login.LoginResponse;
import com.tsiro.dogvip.POJO.login.SignInEmailRequest;
import com.tsiro.dogvip.POJO.login.SignInUpFbGoogleRequest;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.base.fragment.BaseFragment;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.SigninFrgmtBinding;
import com.tsiro.dogvip.login.LoginActivity;
import com.tsiro.dogvip.login.LoginContract;
import com.tsiro.dogvip.login.forgotpass.ForgotPaswrdFrgmt;
import com.tsiro.dogvip.login.signup.RegisterFrgmt;
import com.tsiro.dogvip.utilities.animation.AnimationListener;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by giannis on 17/5/2017.
 */

public class SignInFrgmt extends BaseFragment implements LoginContract.SignInView, GoogleApiClient.OnConnectionFailedListener {

    private static final String debugTag = SignInFrgmt.class.getSimpleName();
    private View mView;
    private SigninFrgmtBinding mBinding;
//    private AwesomeValidation mAwesomeValidation;
    private int fragmentCreatedCode; // login activity: check if fragments are created on button click
    private CallbackManager mCallbackMngr;
    private GoogleApiClient mGoogleApiClient;
    @Inject
    SignInViewModel mViewModel;
    @Inject
    SignInEmailRequest signInEmailRequest;
    @Inject
    SignInUpFbGoogleRequest signInUpFbGoogleRequest;
    @Inject
    SignInRetainFragment mSignInRetainFragment;
    @Inject
    AwesomeValidation mAwesomeValidation;

    public static SignInFrgmt newInstance(int x) {
        Bundle bundle = new Bundle();
        bundle.putInt(AppConfig.FRAGMENT_CREATED, x);
        SignInFrgmt signInFrgmt = new SignInFrgmt();
        signInFrgmt.setArguments(bundle);
        return signInFrgmt;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if ( context instanceof Activity) this.baseView = (Lifecycle.BaseView) context;
        mGoogleApiClient = ((LoginActivity)getActivity()).getmGoogleApiClient();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeViewModel();
        fragmentCreatedCode = getArguments().getInt(AppConfig.FRAGMENT_CREATED);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (fragmentCreatedCode != 100 && fragmentCreatedCode != 200) return null;

        if (mView == null) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.signin_frgmt, container, false);
            mView = mBinding.getRoot();
            fragmentCreatedCode = 300;
        }
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        Log.e(debugTag, getActivity() +  " onActivityCReated" + mViewModel);
        mAwesomeValidation.addValidation(mBinding.emailEdt, Patterns.EMAIL_ADDRESS, getResources().getString(R.string.not_valid_email));
        mAwesomeValidation.addValidation(mBinding.passEdt, "^(?=.*\\D)[a-zA-Z\\d]{8}$", getResources().getString(R.string.not_valid_pass));
        initializeFBSignIn();
    }

    @Override
    public void onResume() {
        super.onResume();
        Disposable disp = RxView.clicks(mBinding.facebookBtn).subscribe(o -> mBinding.hiddenFbBtn.performClick());
        RxEventBus.add(this, disp);

        Disposable disp1 = RxView.clicks(mBinding.googleBtn).subscribe(o -> {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, AppConfig.GOOGLE_REQ_CODE);
        });
        RxEventBus.add(this, disp1);

        Disposable disp2 = RxView.clicks(mBinding.notMemberBtn).subscribe(
                o -> {
                    ((LoginActivity)getActivity()).hideSoftKeyboard();
                    getFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                                .replace(R.id.loginContainer, RegisterFrgmt.newInstance(), getResources().getString(R.string.regstr_fgmt))
                                .addToBackStack(getResources().getString(R.string.regstr_fgmt))
                                .commit();
                }
        );
        RxEventBus.add(this, disp2);

        Disposable disp3 = RxView.clicks(mBinding.forgotpassBtn).subscribe(
                o -> {
                    ((LoginActivity)getActivity()).hideSoftKeyboard();
                    getFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                                .replace(R.id.loginContainer, ForgotPaswrdFrgmt.newInstance(100), getResources().getString(R.string.forgotpaswrd_fgmt))
                                .addToBackStack(getResources().getString(R.string.forgotpaswrd_fgmt))
                                .commit();
                }
        );
        RxEventBus.add(this, disp3);

        Disposable disp4 = RxView.clicks(mBinding.signInBtn).subscribe(o -> {
            if (mAwesomeValidation.validate()) {
                ((LoginActivity)getActivity()).hideSoftKeyboard();
                signInEmailUser();
            }
        });
        RxEventBus.add(this, disp4);
    }

    @Override
    public void onPause() {
        super.onPause();
        RxEventBus.unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSignInRetainFragment.retainViewModel(mViewModel);
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
    public Lifecycle.ViewModel getViewModel() {
        return mViewModel;
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
    public void onSuccessFbLogin(LoginResponse response) {
        mBinding.setProcessing(false);
        logoutFBUser();
        ((LoginActivity)getActivity()).addAccount(response);
    }

    @Override
    public void onSuccessGoogleLogin(LoginResponse response) {
        mBinding.setProcessing(false);
        logoutGoogleUser();
        ((LoginActivity)getActivity()).addAccount(response);
    }

    @Override
    public void onSuccessEmailSignIn(LoginResponse response) {
        mBinding.setProcessing(false);
        ((LoginActivity)getActivity()).addAccount(response);
    }

    @Override
    public void onErrorFbLogin(int resource) {
        mBinding.setProcessing(false);
        logoutFBUser();
        onError(resource);
    }

    @Override
    public void onErrorGoogleLogin(int resource) {
        logoutGoogleUser();
        onError(resource);
    }

    @Override
    public void onError(final int resource) {
        mBinding.setProcessing(false);
        if (getActivity()!= null)((LoginActivity)getActivity()).onError(resource);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConfig.GOOGLE_REQ_CODE) {
            signInUpFbGoogleRequest.setAction(getResources().getString(R.string.signin_user));
            signInUpFbGoogleRequest.setDeviceid(android.os.Build.SERIAL);
            signInUpFbGoogleRequest.setRegtype(2);
            mViewModel.handleGoogleSignInResult(Auth.GoogleSignInApi.getSignInResultFromIntent(data), signInUpFbGoogleRequest);
        } else {
            mCallbackMngr.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnectionFailed(@android.support.annotation.NonNull ConnectionResult connectionResult) {
        ((LoginActivity)getActivity()).onError(AppConfig.getCodes().get(AppConfig.STATUS_ERROR));
    }

    private void signInEmailUser() {
        signInEmailRequest.setAction(getResources().getString(R.string.signin_user));
        signInEmailRequest.setEmail(mBinding.emailEdt.getText().toString());
        signInEmailRequest.setPassword(mBinding.passEdt.getText().toString());
        signInEmailRequest.setRegtype(0); //0 -> email registr
        mViewModel.signInEmail(signInEmailRequest);
    }

    private void initializeFBSignIn() {
        mCallbackMngr = CallbackManager.Factory.create();

        mBinding.hiddenFbBtn.setReadPermissions(getResources().getString(R.string.email));
        mBinding.hiddenFbBtn.setFragment(this);
        mBinding.hiddenFbBtn.registerCallback(mCallbackMngr, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), (object, response) -> {
                    try {
                        signInUpFbGoogleRequest.setAction(getResources().getString(R.string.signin_user));
                        signInUpFbGoogleRequest.setDeviceid(android.os.Build.SERIAL);
                        signInUpFbGoogleRequest.setRegtype(1);
                        mViewModel.handleFbSignInResult(object.getString(getResources().getString(R.string.email)), signInUpFbGoogleRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                ((LoginActivity)getActivity()).onError(AppConfig.getCodes().get(AppConfig.ERROR_SIGNIN_CANCELED));
            }

            @Override
            public void onError(FacebookException error) {
                ((LoginActivity)getActivity()).onError(AppConfig.getCodes().get(AppConfig.STATUS_ERROR));
            }
        });
    }

    private void logoutFBUser() {
        LoginManager.getInstance().logOut();
    }

    private void logoutGoogleUser() {
        if (mGoogleApiClient.isConnected())
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(status -> {});
    }

    private void initializeViewModel() {
        if (getFragmentManager().findFragmentByTag(getResources().getString(R.string.retained_signin_fgmt)) == null) {
            getFragmentManager().beginTransaction().add(mSignInRetainFragment, getResources().getString(R.string.retained_signin_fgmt)).commit();
        } else {
            mSignInRetainFragment = (SignInRetainFragment) getFragmentManager().findFragmentByTag(getResources().getString(R.string.retained_signin_fgmt));
        }
        if (mSignInRetainFragment.getViewModel() != null) mViewModel = mSignInRetainFragment.getViewModel();
    }
}
