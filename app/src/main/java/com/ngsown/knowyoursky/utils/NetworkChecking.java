package com.ngsown.knowyoursky.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.util.Log;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;


public class NetworkChecking{
    private boolean isNetworkAvailable = false;
    private Context context;
    private BehaviorSubject<Boolean> observable = BehaviorSubject.create();
    @Inject
    public NetworkChecking(Context context){
        this.context = context;
    }
    public void registerNetworkCallback(){
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            connectivityManager.registerNetworkCallback(builder.build(), new ConnectivityManager.NetworkCallback(){
                @Override
                public void onAvailable(Network network) {
                    isNetworkAvailable = true;
                    observable.onNext(true);
//                    setChanged();
//                    notifyObservers();
//                    clearChanged();
                    Log.d("NETWORK_STATUS", "True");
                }
                @Override
                public void onLost(Network network) {
                    isNetworkAvailable = false;
                    observable.onNext(false);
//                    setChanged();
//                    notifyObservers();
//                    clearChanged();
                    Log.d("NETWORK_STATUS", "Fail");
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
            isNetworkAvailable = false;
        }
    }
    public Observable<Boolean> getNetworkObservable(){
        return observable;
    }
    public boolean isNetworkAvailable() {
        return isNetworkAvailable;
    }
}
