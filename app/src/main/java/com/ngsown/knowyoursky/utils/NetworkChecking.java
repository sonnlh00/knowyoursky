package com.ngsown.knowyoursky.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;

import com.ngsown.knowyoursky.utils.rxjava.SchedulerProvider;

import javax.inject.Inject;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;


public class NetworkChecking{
    private boolean isNetworkAvailable = false;
    private final Context context;
    private final SchedulerProvider schedulerProvider = new SchedulerProvider();
    @Inject
    public NetworkChecking(Context context){
        this.context = context;
    }
    public void registerNetworkObserver(Observer<Boolean> observer){
        try {
            Observable<Boolean> observable = Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Throwable {
                    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkRequest.Builder builder = new NetworkRequest.Builder();
                    connectivityManager.registerNetworkCallback(builder.build(), new ConnectivityManager.NetworkCallback(){
                        @Override
                        public void onAvailable(@androidx.annotation.NonNull Network network) {
                            emitter.onNext(true);
                            isNetworkAvailable = true;
                            super.onAvailable(network);
                        }

                        @Override
                        public void onLost(@androidx.annotation.NonNull Network network) {
                            isNetworkAvailable = false;
                            emitter.onNext(false);
                            super.onLost(network);
                        }

                        @Override
                        public void onUnavailable() {
                            isNetworkAvailable = false;
                            emitter.onNext(false);
                            super.onUnavailable();
                        }
                    });
                }
            }).subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.mainThread());
            observable.subscribe(observer);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean isNetworkAvailable() {
        return isNetworkAvailable;
    }
}
