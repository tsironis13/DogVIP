package com.tsiro.dogvip.utilities;

import android.databinding.Observable;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * Created by giannis on 15/10/2017.
 */

public class TEst implements ObservableOnSubscribe<Object> {



    @Override
    public void subscribe(@NonNull ObservableEmitter<Object> e) throws Exception {
        e.onNext(new Object());
    }
}
