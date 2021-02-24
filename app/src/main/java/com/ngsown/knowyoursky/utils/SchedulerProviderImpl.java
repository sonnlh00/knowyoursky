package com.ngsown.knowyoursky.utils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SchedulerProviderImpl implements SchedulerProvider {
    @Override
    public Scheduler io() {
        return Schedulers.io();
    }

    @Override
    public Scheduler mainThread() {
        return AndroidSchedulers.mainThread();
    }
    @Override
    public Scheduler computation() {
        return Schedulers.computation();
    }
}
