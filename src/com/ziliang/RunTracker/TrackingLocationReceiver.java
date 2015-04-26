package com.ziliang.RunTracker;

import android.content.Context;
import android.location.Location;

/**
 * Created by Kurt on 4/25/2015.
 */
public class TrackingLocationReceiver extends LocationReceiver {
    @Override
    protected void onLocationReceived(Context context, Location location){
        RunManager.get(context).insertLocation(location);
    }
}
