package com.tsiro.dogvip.adapters;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
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
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.interfaces.RecyclerCallback;

import java.util.Calendar;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by giannis on 24/6/2017.
 */

public abstract class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private int layoutid;
    private ViewDataBinding mBinding;


    public RecyclerViewAdapter(int layoutId) {
        this.layoutid = layoutId;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
    @BindingAdapter("bind:imageUrl")
    public static void loadImage(ImageView imageView, String url) {
        //.skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
        int default_img = 0;
        if (imageView.getId() == R.id.petImgv) {
            default_img = R.drawable.ic_pets;
        } else if (imageView.getId() == R.id.profileImgv) {
            default_img = R.drawable.default_person;
        }
        Glide.with(imageView.getContext())
                .load(url)
                .transition(withCrossFade())
                .apply(new RequestOptions().circleCrop().error(default_img).placeholder(default_img))
                .into(imageView);
    }

    @BindingAdapter("bind:textPl")
    public static void setText(TextView textView, String text) {
        if (textView.getId() == R.id.ageTtv || textView.getId() == R.id.pageTtv || textView.getId() == R.id.customageTtv) {
            String[] holder = text.split("/");
            int current_year = Calendar.getInstance().get(Calendar.YEAR);
            int age = current_year - Integer.valueOf(holder[2]);
            String fage = String.valueOf(age);
            if (textView.getId() == R.id.customageTtv) fage = fage + " ετών";
            textView.setText(fage);
        }
//        Log.e("asa", text.split("/").toString());
    }

//    protected abstract int getsItemCount();

    protected abstract Object getObjForPosition(int position, ViewDataBinding mBinding);

    protected abstract Object getClickListenerObject();

    protected abstract int getLayoutIdForPosition(int position);

    protected abstract int getTotalItems();

}
