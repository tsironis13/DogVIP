package com.tsiro.dogvip.adapters;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.tsiro.dogvip.BR;
import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.ImagePathIndex;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.uploadimagecontrol.ImageUploadControlActivity;
import com.tsiro.dogvip.uploadimagecontrol.ImageUploadControlPresenter;
import com.tsiro.dogvip.uploadimagecontrol.ImageUploadControlViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by giannis on 26/6/2017.
 */

public class ImageGridAdapter extends ArrayAdapter {

    private static final String debugTag = ImageGridAdapter.class.getSimpleName();
    private Context mContext;
    private List<Image> items;
    private int resource;
    private ImageUploadControlPresenter imageUploadControlPresenter;

    public ImageGridAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects, ImageUploadControlPresenter imageUploadControlPresenter) {
        super(context, resource, objects);
        this.mContext = context;
        this.items = objects;
        this.resource = resource;
        this.imageUploadControlPresenter = imageUploadControlPresenter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewDataBinding mBinding = DataBindingUtil.inflate(inflater, resource, null, false);
        mBinding.setVariable(BR.image, getItem(position));
        mBinding.setVariable(BR.presenter, imageUploadControlPresenter);
        mBinding.getRoot().setTag(position);
        return mBinding.getRoot();
    }

    public void updateData(Image image, boolean add, ArrayList<ImagePathIndex> checkedUrls) {
        if (add) {
            items.add(image);
        } else {
            for (int i = 0; i < checkedUrls.size(); i++) {
                int remove =  checkedUrls.get(i).getIndex();
                Log.e(debugTag, remove+"");
//                items.remove(remove);
            }
        }
        notifyDataSetChanged();
    }

    @BindingAdapter({"android:src"})
    public static void setImageViewResource(ImageView view, String url) {
        Glide.with(view.getContext()).load(url).apply(new RequestOptions().error(R.drawable.ic_pets).placeholder(R.drawable.ic_pets).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)).transition(withCrossFade()).into(view);
    }

//    @BindingAdapter("app:test")
//    public boolean checkIsMain(View view) {
//        Log.e(debugTag, "asdasa");
//        //get main image index
//        int index = (int)view.getTag();
//        return items.get(index).isMain();
//    }
}
