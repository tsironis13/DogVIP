package com.tsiro.dogvip.utilities.eventbus;

import android.util.Log;
import android.util.SparseArray;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;

/**
 * Created by giannis on 20/5/2017.
 */

public class RxEventBus<T> {

    private static final String debugTag = RxEventBus.class.getSimpleName();
    private Subject<T> subject;
    private static SparseArray<RxEventBus> mInstanceMap = new SparseArray<>();
    private static SparseArray<Subject> sSubjectMap = new SparseArray<>();
    private static Map<Object, CompositeDisposable> sSubscriptionsMap = new HashMap<>();

    private RxEventBus(int subjectCode, int subjectType) {
//        Log.e(debugTag, "constructor subject is : "+subject);
        if (subject == null) {
            switch (subjectType) {
                case 0:
                    subject = PublishSubject.create();
                    break;
                case 1:
                    subject = ReplaySubject.createWithSize(900);
                    break;
                case 2:
                    subject = BehaviorSubject.create();
                    break;
            }
            sSubjectMap.put(subjectCode, subject);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> RxEventBus<T> createSubject(int subjectCode, int subjectType) {
//        Log.e(debugTag, sSubjectMap.get(subjectCode)+" here");
        if (sSubjectMap.get(subjectCode) == null) {
            RxEventBus mInstance = new RxEventBus<T>(subjectCode, subjectType);
            mInstanceMap.put(subjectCode, mInstance);
            return mInstance;
        } else {
            return mInstanceMap.get(subjectCode);
        }
    }

    public static void add(Object obj, Disposable disp) {
        getCompositeSubscription(obj).add(disp);
    }

    public <E extends T> void post(E event) {
        subject.onNext(event);
    }

    public Observable<T> observe() {
        return subject;
    }

    //pass only events of specified type, filter all other
    public <E extends T> Observable<E> observeEvents(Class<E> eventClass) {
        return subject.ofType(eventClass);
    }

    private static CompositeDisposable getCompositeSubscription(Object object) {
        CompositeDisposable compositeSubscription = sSubscriptionsMap.get(object);
        if (compositeSubscription == null) {
            compositeSubscription = new CompositeDisposable();
            sSubscriptionsMap.put(object, compositeSubscription);
        }
        return compositeSubscription;
    }

    public static void unregister(Object lifecycle) {
//        //We have to remove the composition from the map, because once you unsubscribe it can't be used anymore
        CompositeDisposable compositeSubscription = sSubscriptionsMap.remove(lifecycle);
        if (compositeSubscription != null) if (!compositeSubscription.isDisposed()) compositeSubscription.dispose();
    }
}
