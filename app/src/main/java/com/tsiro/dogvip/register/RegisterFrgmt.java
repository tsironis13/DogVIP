package com.tsiro.dogvip.register;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
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
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.LoginActivity;
import com.tsiro.dogvip.POJO.registration.RegistrationRequest;
import com.tsiro.dogvip.POJO.registration.AuthenticationResponse;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.accountmngr.MyAccountManager;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.BaseFragment;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.RegisterFrgmtBinding;
import com.tsiro.dogvip.login.SignInFrgmt;
import com.tsiro.dogvip.requestmngrlayer.AuthenticationRequestManager;
import com.tsiro.dogvip.utilities.animation.AnimationListener;
import com.tsiro.dogvip.utilities.common.CommonUtls;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by giannis on 15/5/2017.
 */

public class RegisterFrgmt extends BaseFragment implements RegistrationContract.View {

    private static final String debugTag = RegisterFrgmt.class.getSimpleName();
    private View mView;
    private RegisterFrgmtBinding mBinding;
    private RegistrationContract.ViewModel mRegstrViewModel;
    private RegistrationRequest request;
    private ProgressDialog mProgressDialog;
    private CallbackManager mCallbackMngr;
    private AwesomeValidation mAwesomeValidation;
    private boolean mFBUserLoggedIn, mGoogleUserLoggedIn;
    private FragmentManager mFragmentManager;
    private Lifecycle.BaseView baseView;
    private GoogleApiClient mGoogleApiClient;

    public static RegisterFrgmt newInstance() {
        return new RegisterFrgmt();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if ( context instanceof Activity) this.baseView = (Lifecycle.BaseView) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null ) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.register_frgmt, container, false);
            mView = mBinding.getRoot();
        }
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mGoogleApiClient = ((LoginActivity)getActivity()).getmGoogleApiClient();

        mFragmentManager = getActivity().getSupportFragmentManager();
        mRegstrViewModel = new RegistrationViewModel(AuthenticationRequestManager.getInstance());

        mAwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        mAwesomeValidation.addValidation(mBinding.emailEdt, Patterns.EMAIL_ADDRESS, getResources().getString(R.string.not_valid_email));
        mAwesomeValidation.addValidation(mBinding.passEdt, "^(?=.*\\D)[a-zA-Z\\d]{8}$", getResources().getString(R.string.not_valid_pass));
        mAwesomeValidation.addValidation(mBinding.confpassEdt, mBinding.passEdt, getResources().getString(R.string.passwrds_not_match));

        request = new RegistrationRequest();

        initializeFBSignUp();
    }

    @Override
    public void onResume() {
        super.onResume();
        Disposable disp = RxView.clicks(mBinding.registerBtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                if (mAwesomeValidation.validate()) {
                    baseView.hideSoftKeyboard();
                    registerUser(mBinding.emailEdt.getText().toString(), 0);
                }
            }
        });
        RxEventBus.add(this, disp);
        Disposable disp1 = RxView.clicks(mBinding.facebookBtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                mBinding.hiddenFbBtn.performClick();
            }
        });
        RxEventBus.add(this, disp1);
        Disposable disp2 = RxView.clicks(mBinding.googleBtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, AppConfig.GOOGLE_REQ_CODE);
            }
        });
        RxEventBus.add(this, disp2);
        Disposable disp3 = RxView.clicks(mBinding.termsBtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                Uri uri = Uri.parse("http://dogvip.votingsystem.gr/terms.html");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        RxEventBus.add(this, disp3);
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
    public void onSuccess(final AuthenticationResponse response) {
        ((LoginActivity) getActivity()).dismissDialog();
        if (response.getRegtype() == 0) {
            new CommonUtls(getActivity()).buildNotification(getResources().getString(R.string.welcome), getResources().getString(R.string.confirm_account));
            mFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                    .replace(R.id.loginContainer, SignInFrgmt.newInstance(100), getResources().getString(R.string.signin_fgmt))
                    .addToBackStack(getResources().getString(R.string.signin_fgmt))
                    .commit();
        } else {
            logoutFBUser();
            logoutGoogleUser();

            MyAccountManager mAccountManager = ((LoginActivity)getActivity()).getMyAccountManager();
            //check if account exists, otherwise display the error
            if (mAccountManager.addAccount(response.getEmail(), response.getAuthtoken())) {
                ((LoginActivity)getActivity()).logUserIn(true);
            } else {
                ((LoginActivity)getActivity()).showSnackBar(R.style.SnackBarSingleLine, getResources().getString(R.string.error));
            }
        }
    }

    @Override
    public void onError(final int resource, final boolean msglength) {
        logoutFBUser();
        logoutGoogleUser();

        ((LoginActivity) getActivity()).dismissDialog();
        int style = R.style.SnackBarSingleLine;
        if (msglength) style = R.style.SnackBarMultiLine;
        ((LoginActivity)getActivity()).showSnackBar(style, getResources().getString(resource));
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return mRegstrViewModel;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConfig.GOOGLE_REQ_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignUpResult(result);
        } else {
            mCallbackMngr.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void registerUser(String email, int regtype) {
        if (((LoginActivity) getActivity()).isNetworkAvailable()) {
            request.setAction(getResources().getString(R.string.register_user));
            if (regtype == 0) { //email registration
                request.setEmail(email);
                request.setPassword(mBinding.passEdt.getText().toString());
                request.setConfpassword(mBinding.confpassEdt.getText().toString());
                request.setRegstrType(regtype); //0 -> email registr
            } else {
                request.setEmail(email);
                request.setDeviceid(android.os.Build.SERIAL);
                request.setRegstrType(regtype); // 1 -> fb registr, 2 -> google regstr
            }
            mRegstrViewModel.register(request);
            mProgressDialog = ((LoginActivity) getActivity()).initializeProgressDialog(getResources().getString(R.string.please_wait));
        } else {
            ((LoginActivity)getActivity()).showSnackBar(R.style.SnackBarSingleLine, getResources().getString(R.string.no_internet_connection));
        }
    }

    private void initializeFBSignUp() {
        mCallbackMngr = CallbackManager.Factory.create();

        mBinding.hiddenFbBtn.setReadPermissions(getResources().getString(R.string.email));
        mBinding.hiddenFbBtn.setFragment(this);
        mBinding.hiddenFbBtn.registerCallback(mCallbackMngr, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            mFBUserLoggedIn = true;
                            registerUser(object.getString(getResources().getString(R.string.email)), 1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                ((LoginActivity)getActivity()).showSnackBar(R.style.SnackBarSingleLine, getResources().getString(R.string.sign_up_cancel));
            }

            @Override
            public void onError(FacebookException error) {
                ((LoginActivity)getActivity()).showSnackBar(R.style.SnackBarSingleLine, getResources().getString(R.string.error));
            }
        });
    }

    private void handleGoogleSignUpResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            mGoogleUserLoggedIn = true;

            GoogleSignInAccount account = result.getSignInAccount();
            if (account != null)registerUser(account.getEmail(), 2);
        }
    }

    private void logoutFBUser() {
        if (mFBUserLoggedIn) {
            LoginManager.getInstance().logOut();
            mFBUserLoggedIn = false;
        }
    }

    private void logoutGoogleUser() {
        if (mGoogleUserLoggedIn && mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {@Override public void onResult(@android.support.annotation.NonNull Status status) {}});
            mGoogleUserLoggedIn = false;
        }
    }
}
