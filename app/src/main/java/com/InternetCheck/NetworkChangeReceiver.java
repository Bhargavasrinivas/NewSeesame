package com.InternetCheck;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private String LOG_TAG = "NetworkChangeReceiver";
    private boolean isConnected;
    private Activity ParentActivity;

    public Activity getParentActivity() {
        return ParentActivity;
    }

    public void setParentActivity(Activity parentActivity) {
        ParentActivity = parentActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.v(LOG_TAG, "Receieved notification about network status");
        if (isNetworkAvailable(context, intent)) {
            if (ParentActivity != null) {
                ParentActivity.finish();
            }
        } else {
            //context.startActivity(new Intent(context, Connection.class));
        }

    }


    private boolean isNetworkAvailable(Context context, Intent intent) {
        ConnectivityManager connectivity = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
            }
        }
        Log.v(LOG_TAG, "You are not connected to Internet!");
        return false;
    }

}
