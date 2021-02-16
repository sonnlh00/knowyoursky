package com.ngsown.knowyoursky;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.databinding.ObservableBoolean;

import com.ngsown.knowyoursky.model.Variables;

import java.util.Observable;
import java.util.Observer;

public class NetworkChecking extends Observable {
    private boolean isNetworkAvailable = false;
    private Context context;
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
                    setChanged();
                    notifyObservers();
                    clearChanged();
                    Log.d("NETWORK_STATUS", "True");
                }
                @Override
                public void onLost(Network network) {
                    isNetworkAvailable = false;
                    setChanged();
                    notifyObservers();
                    clearChanged();
                    Log.d("NETWORK_STATUS", "Fail");
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
            isNetworkAvailable = false;
        }
    }

    public boolean isNetworkAvailable() {
        return isNetworkAvailable;
    }
}
