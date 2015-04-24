package com.ziliang.RunTracker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

/**
 * Created by Kurt on 4/24/2015.
 */
public class RunManager {
    private static final String TAG="RunManager";
    public static final String ACTION_LOCATION="com.ziliang.runtracker.ACTION_LOCATION";
    private static RunManager runManager;
    private Context mAppContext;
    private LocationManager locationManager;
    private RunManager(Context appContext){
        mAppContext=appContext;
        locationManager=(LocationManager)mAppContext.getSystemService(Context.LOCATION_SERVICE);
    }
    public static RunManager get(Context context){
        if(runManager==null){
            runManager=new RunManager(context.getApplicationContext());
        }
        return runManager;
    }
    private PendingIntent getLocationPendingIntent(boolean shouldCreate){
        Intent broadcast=new Intent(ACTION_LOCATION);
        int flags=shouldCreate?0:PendingIntent.FLAG_NO_CREATE;
        return PendingIntent.getBroadcast(mAppContext,0,broadcast,flags);
    }
    public void startLocationUpdates(){
        String provider=LocationManager.GPS_PROVIDER;

        Location lastKnown=locationManager.getLastKnownLocation(provider);
        if(lastKnown!=null){
            lastKnown.setTime(System.currentTimeMillis());
            broadcastLocation(lastKnown);
        }
        PendingIntent pi=getLocationPendingIntent(true);
        locationManager.requestLocationUpdates(provider,0,0,pi);
    }
    public void stopLocationUpdates(){
        PendingIntent pi=getLocationPendingIntent(false);
        if(pi!=null){
            locationManager.removeUpdates(pi);
            pi.cancel();
        }
    }
    public boolean isTrackingRun(){
        return getLocationPendingIntent(false)!=null;
    }
    private void broadcastLocation(Location location){
        Intent broadcast=new Intent(ACTION_LOCATION);
        broadcast.putExtra(LocationManager.KEY_LOCATION_CHANGED,location);
        mAppContext.sendBroadcast(broadcast);
    }
}
