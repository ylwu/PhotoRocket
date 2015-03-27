package com.yask.android.photorocket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ConnectionReceiver extends BroadcastReceiver {
    public ConnectionReceiver() {
    }
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(ConnectionReceiver.class.getSimpleName(), "action: "
                + intent.getAction());
        String action = intent.getAction();
        if (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
            return;
        boolean noConnectivity = intent.getBooleanExtra(
                ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        NetworkInfo aNetworkInfo = (NetworkInfo) intent
                .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
        if (!noConnectivity) {
            if ((aNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
                    || (aNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI)) {
                System.out.println("TRYING TO START SERVICE");
                Intent i = new Intent(context, UploadService.class);
                context.startService(i);
            }
        } else {
            if ((aNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
                    || (aNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI)) {
            }
        }
    }
}
