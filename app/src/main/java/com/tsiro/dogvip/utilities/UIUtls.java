package com.tsiro.dogvip.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.tsiro.dogvip.R;
import com.tsiro.dogvip.di.qualifiers.ApplicationContext;
import com.tsiro.dogvip.di.scope.PerActivity;

import org.reactivestreams.Subscriber;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.MaybeSource;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by giannis on 11/10/2017.
 */

public class UIUtls {

    private Context mContext;
    private ProgressDialog mProgressDialog;

    @Inject
    public UIUtls(Context context) {
        this.mContext = context;
    }

    public Maybe<Boolean> showSnackBar(final View view, final String msg, final String action, final int length) {
        return Maybe.using(
                new Callable<Snackbar>() {
                    @Override
                    public Snackbar call() throws Exception {
                        return getStyledSnackBar(view, msg, length);
                    }
                },
                new Function<Snackbar, MaybeSource<? extends Boolean>>() {
                    @Override
                    public MaybeSource<? extends Boolean> apply(@NonNull final Snackbar snackbar) throws Exception {
                        return Maybe.create(new MaybeOnSubscribe<Boolean>() {
                            @Override
                            public void subscribe(@NonNull final MaybeEmitter<Boolean> e) throws Exception {
                                snackbar.setAction(action, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (!e.isDisposed()) e.onSuccess(true);
                                    }
                                });
                                snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                    @Override
                                    public void onDismissed(Snackbar transientBottomBar, int event) {
                                        super.onDismissed(transientBottomBar, event);
                                        if (!e.isDisposed()) e.onComplete();
                                    }
                                });
                                snackbar.show();
                            }
                        });
                    }
                },
                new Consumer<Snackbar>() {
                    @Override
                    public void accept(@NonNull Snackbar snackbar) throws Exception {
                        snackbar.addCallback(null);
                        snackbar = null;
                    }
                }
        );
    }

    private Snackbar getStyledSnackBar(View view, String msg, int length) {
        Snackbar snackbar = Snackbar.make(view, msg, length);
        snackbar.setActionTextColor(ContextCompat.getColor(mContext, android.R.color.black));
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        sbView.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.white));
        textView.setTextColor(ContextCompat.getColor(mContext, android.R.color.holo_red_light));
        return snackbar;
    }

    public ProgressDialog initializeProgressDialog(String msg) {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage(msg);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        return mProgressDialog;
    }

    public void dismissDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void hideSoftKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
