package com.ngsown.knowyoursky.utils.rxjava;

import io.reactivex.rxjava3.core.Scheduler;

public interface SchedulerProvider {
    Scheduler io();
    Scheduler mainThread();
    Scheduler computation();
}
