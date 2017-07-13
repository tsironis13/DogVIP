package com.tsiro.dogvip.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsiro.dogvip.BR;
import com.tsiro.dogvip.POJO.mypets.pet.PetObj;
import com.tsiro.dogvip.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giannis on 10/7/2017.
 */

public class PickPetSpnrAdapter extends ArrayAdapter<PetObj> {

    private Context mContext;
    private int resource;
    private LayoutInflater mInflater;

    public PickPetSpnrAdapter(@NonNull Context context, @LayoutRes int resource, ArrayList<PetObj> objs) {
        super(context, resource, objs);
        this.mContext = context;
        this.resource = resource;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView);
    }

    private View getCustomView(int position, View convertView) {
        if (mInflater == null) mInflater = LayoutInflater.from(mContext);
        ViewDataBinding mBinding = DataBindingUtil.getBinding(convertView);
        if (mBinding == null) mBinding = DataBindingUtil.inflate(mInflater, resource, null, false);

        mBinding.setVariable(BR.obj, getItem(position));
        mBinding.getRoot().setTag(position);

        return mBinding.getRoot();
    }
}
