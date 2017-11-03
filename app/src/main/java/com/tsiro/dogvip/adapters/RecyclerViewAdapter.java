package com.tsiro.dogvip.adapters;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.AppConfig;

import java.util.Calendar;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by giannis on 24/6/2017.
 */

public abstract class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private static final String debugTag = RecyclerViewAdapter.class.getSimpleName();
    private int layoutid;
    private ViewDataBinding mBinding;

    public RecyclerViewAdapter(int layoutId) {
        this.layoutid = layoutId;
    }

    public void setData() {

    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == AppConfig.MY_CHAT_VIEW_ITEM) {
            layoutid = R.layout.chat_item_self_rcv_row;
        } else if (viewType == AppConfig.OTHER_CHAT_VIEW_ITEM) {
            layoutid = R.layout.chat_item_other_rcv_row;
        }
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        mBinding = DataBindingUtil.inflate(layoutInflater, layoutid, parent, false);

        return new RecyclerViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        Object item = getObjForPosition(position, mBinding);
        holder.bind(item, getClickListenerObject(), position);
    }

    @Override
    public int getItemViewType(int position) {
        return getLayoutIdForPosition(position);
    }

    @Override
    public int getItemCount() {
        return getTotalItems();
    }

    //circle images

    @BindingAdapter("bind:textPl")
    public static void setText(TextView textView, String text) {
        if (textView.getId() == R.id.ageTtv || textView.getId() == R.id.pageTtv || textView.getId() == R.id.customageTtv) {
            if (text != null) {
                String[] holder = text.split("/");
                int current_year = Calendar.getInstance().get(Calendar.YEAR);
                int current_month = Calendar.getInstance().get(Calendar.MONTH) + 1;
                int age = current_year - Integer.valueOf(holder[2]);
                String str;
                if (age > 0) {
                    str = String.valueOf(age);
                    if (textView.getId() == R.id.customageTtv || textView.getId() == R.id.pageTtv) {
                        if (age == 1) {
                            str += " έτους";
                        } else {
                            str += " ετών";
                        }
                    }
                } else {
                    int month = current_month - Integer.valueOf(holder[1]);
                    if (month == 0) {
                        month = 1;
                    } else {
                        month++;
                    }
                    str = String.valueOf(month);
                    if (textView.getId() == R.id.customageTtv || textView.getId() == R.id.pageTtv) str += " μηνών";
                }
                textView.setText(str);
            }
        }
    }

    @BindingAdapter(value={"bind:text1", "bind:text2"}, requireAll=false)
    public static void setTextSpanColor(TextView textView, String text1, String text2) {
        String bindtext = "";
        if (textView.getId() == R.id.dateTimeLostTtv) {
            bindtext = textView.getContext().getResources().getString(R.string.date_time_holder, text1, text2);
        } else if (textView.getId() == R.id.locationTtv) {
            bindtext = textView.getContext().getResources().getString(R.string.place_holder, text1);
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(bindtext, Html.FROM_HTML_MODE_LEGACY));
        } else {
            textView.setText(Html.fromHtml(bindtext));
        }
    }

//    protected abstract int getsItemCount();

    protected abstract Object getObjForPosition(int position, ViewDataBinding mBinding);

    protected abstract Object getClickListenerObject();

    protected abstract int getLayoutIdForPosition(int position);

    protected abstract int getTotalItems();

}
