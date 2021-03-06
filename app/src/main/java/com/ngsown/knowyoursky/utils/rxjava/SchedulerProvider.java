package com.ngsown.knowyoursky.utils.rxjava;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SchedulerProvider {

    public static Scheduler io() {
        return Schedulers.io();
    }

    public static Scheduler mainThread() {
        return AndroidSchedulers.mainThread();
    }

    public static Scheduler computation() {
        return Schedulers.computation();
    }

    public static Scheduler trampoline() { return Schedulers.trampoline(); }

    public static Scheduler newThread() { return Schedulers.newThread(); }
}
