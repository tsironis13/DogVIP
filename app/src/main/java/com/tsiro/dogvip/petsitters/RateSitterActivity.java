package com.tsiro.dogvip.petsitters;

import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.POJO.petsitter.BookingObj;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivityRateSitterBinding;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by thomatou on 9/20/17.
 */

public class RateSitterActivity extends BaseActivity {

    private static final String debugTag = RateSitterActivity.class.getSimpleName();
    private ActivityRateSitterBinding mBinding;
    private BookingObj bookingObj;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_rate_sitter);
        setSupportActionBar(mBinding.incltoolbar.toolbar);

        if (savedInstanceState != null) {
            bookingObj = savedInstanceState.getParcelable(getResources().getString(R.string.parcelable_obj));
        } else {
            if (getIntent().getExtras() != null) {
                bookingObj = getIntent().getExtras().getParcelable(getResources().getString(R.string.parcelable_obj));
            }
        }
        mBinding.setObj(bookingObj);
        if (bookingObj.getServices() != null) {
            if (bookingObj.getServices().isEmpty()) {
                TextView textView = new TextView(this);
                textView.setText(getResources().getString(R.string.no_searched_services));
//                    textView.setTextColor(ContextCompat.getColor(this, R.color.color_white));
                textView.setTypeface(Typeface.DEFAULT_BOLD);
                mBinding.servicesLlt.addView(textView);
            } else {
                String[] services = getResources().getStringArray(R.array.services);
                for (Integer service : bookingObj.getServices()) {
                    TextView textView = new TextView(this);
                    textView.setText(services[service-1]);
                    textView.setTypeface(Typeface.DEFAULT_BOLD);
//                        textView.setTextColor(ContextCompat.getColor(this, R.color.color_white));
                    mBinding.servicesLlt.addView(textView);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        RxView.clicks(mBinding.addCommentEdt).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBinding.scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                }, 200);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getResources().getString(R.string.parcelable_obj), bookingObj);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.submit_comment_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return null;
    }
}
