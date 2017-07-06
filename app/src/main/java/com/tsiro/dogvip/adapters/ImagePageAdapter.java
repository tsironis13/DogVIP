package com.tsiro.dogvip.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tsiro.dogvip.R;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by giannis on 5/7/2017.
 */

public class ImagePageAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private String[] mResources;

    public ImagePageAdapter(Context context, String[] resources) {
        mContext = context;
        mResources = resources;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.image_pager_adapter_item, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageItemImgv);
        Glide.with(mContext)
                .load(mResources[position].replace("\"", "").trim())
                .transition(withCrossFade())
                .apply(new RequestOptions().error(R.drawable.ic_pets).placeholder(R.drawable.ic_pets))
                .into(imageView);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public int getCount() {
        return mResources.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
