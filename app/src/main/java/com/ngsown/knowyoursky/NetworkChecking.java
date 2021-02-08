package com.ngsown.knowyoursky;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.util.Log;

import com.ngsown.knowyoursky.model.Variables;

public class NetworkChecking {
    private boolean isNetworkAvailable;
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
                    Variables.isNetworkAvailable = true;
                    //isNetworkAvailable = true;
                    Log.d("NETWORK_STATUS", "True");
                }
                @Override
                public void onLost(Network network) {
                    Variables.isNetworkAvailable = false;
                    Log.d("NETWORK_STATUS", "Fail");
                }
            });
            Variables.isNetworkAvailable = false;
        }
        catch (Exception e){
            e.printStackTrace();
            isNetworkAvailable = false;
        }
    }
    public boolean isNetworkAvailable() {
        return isNetworkAvailable;
    }

    private void setNetworkAvailable(boolean networkAvailable) {
        isNetworkAvailable = networkAvailable;
    }
}
