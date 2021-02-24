package com.ngsown.knowyoursky.utils.rxjava;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SchedulerProviderImpl implements SchedulerProvider {
    @Inject
    public SchedulerProviderImpl() {
    }

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
