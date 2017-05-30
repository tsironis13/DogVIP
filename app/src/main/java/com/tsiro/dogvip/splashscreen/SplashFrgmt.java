package com.tsiro.dogvip.splashscreen;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;

import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.databinding.SplashFrgmtBinding;
import com.tsiro.dogvip.login.SignInFrgmt;
import com.tsiro.dogvip.register.RegisterFrgmt;
import com.tsiro.dogvip.utilities.animation.AnimationListener;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by giannis on 15/5/2017.
 */

public class SplashFrgmt extends Fragment {

    private View view;
    private SplashFrgmtBinding mBinding;
    private FragmentManager mFragmentManager;

    public static SplashFrgmt newInstance() {
        return new SplashFrgmt();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.splash_frgmt, container, false);
            view = mBinding.getRoot();
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManager = getActivity().getSupportFragmentManager();
    }

    @Override
    public void onResume() {
        super.onResume();
        Disposable disp1 = RxView.clicks(mBinding.registerBtn).subscribe(
                new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        mFragmentManager
                                .beginTransaction()
                                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                                .replace(R.id.loginContainer, RegisterFrgmt.newInstance(), getResources().getString(R.string.regstr_fgmt))
                                .addToBackStack(getResources().getString(R.string.regstr_fgmt))
                                .commit();
                    }
                }
        );
        RxEventBus.add(this, disp1);

        Disposable disp2 = RxView.clicks(mBinding.signInBtn).subscribe(
                new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
//                        GraphRequest delPermRequest = new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest.Callback() {
//                            @Override
//                            public void onCompleted(GraphResponse graphResponse) {
//                                if(graphResponse!=null){
//                                    FacebookRequestError error =graphResponse.getError();
//                                    if(error!=null){
//                                        Log.e("TAG", error.toString());
//                                    }else {
////                                        finish();
//                                    }
//                                }
//                            }
//                        });
//                        Log.d("TAG","Executing revoke permissions with graph path" + delPermRequest.getGraphPath());
//                        delPermRequest.executeAsync();
//                        LoginManager.getInstance().logOut();
                        mFragmentManager
                                .beginTransaction()
                                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                                .replace(R.id.loginContainer, SignInFrgmt.newInstance(100), getResources().getString(R.string.signin_fgmt))
                                .addToBackStack(getResources().getString(R.string.signin_fgmt))
                                .commit();
                    }
                });
        RxEventBus.add(this, disp2);
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

    private static void addToDisposables(CompositeDisposable compDisp, Disposable disp) {
        if (compDisp == null) compDisp = new CompositeDisposable();
        compDisp.add(disp);
    }

}
