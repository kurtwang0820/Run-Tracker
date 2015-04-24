package com.ziliang.RunTracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

/**
 * Created by Kurt on 4/24/2015.
 */
public class LocationReceiver extends BroadcastReceiver {
    private static final String TAG="LocationReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Location location=(Location)intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
        if(location!=null){
            onLocationReceived(context,location);
            return;
        }
        if(intent.hasExtra(LocationManager.KEY_PROVIDER_ENABLED)){
            boolean enabled=intent.getBooleanExtra(LocationManager.KEY_PROVIDER_ENABLED,false);
            onProviderEnabledChanged(enabled);
        }
    }
    protected void onLocationReceived(Context context,Location location){

    }
    protected void onProviderEnabledChanged(boolean enabled){

    }
}
