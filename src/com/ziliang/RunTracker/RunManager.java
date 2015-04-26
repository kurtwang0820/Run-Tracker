package com.ziliang.RunTracker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

/**
 * Created by Kurt on 4/24/2015.
 */
public class RunManager {
    public static final String ACTION_LOCATION = "com.ziliang.runtracker.ACTION_LOCATION";
    private static final String TAG="RunManager";
    private static final String PREF_CURRENT_RUN_ID="RunManager.currentRunId";
    private static final String PREFS_FILE="runs";
    private static RunManager runManager;
    private Context mAppContext;
    private LocationManager locationManager;
    private RunDatabaseHelper helper;
    private SharedPreferences preferences;
    private long currentRunId;
    private RunManager(Context appContext) {
        mAppContext = appContext;
        locationManager = (LocationManager) mAppContext.getSystemService(Context.LOCATION_SERVICE);
        helper=new RunDatabaseHelper(mAppContext);
        preferences=mAppContext.getSharedPreferences(PREFS_FILE,Context.MODE_PRIVATE);
        currentRunId=preferences.getLong(PREF_CURRENT_RUN_ID,-1);
    }
    //use singleton design pattern
    public static RunManager get(Context context) {
        if (runManager == null) {
            runManager = new RunManager(context.getApplicationContext());
        }
        return runManager;
    }
    public Run startNewRun(){
        //insert new run into db
        Run run=insertRun();
        //start tracking the run
        startTrackingRun(run);
        return run;
    }
    public void startTrackingRun(Run run){
        //keep id
        currentRunId=run.getId();
        //store it in shared preference
        preferences.edit().putLong(PREF_CURRENT_RUN_ID,currentRunId).commit();
        //start location update
        startLocationUpdates();
    }
    public void stopRun(){
        stopLocationUpdates();
        currentRunId=-1;
        preferences.edit().remove(PREF_CURRENT_RUN_ID).commit();
    }
    public void insertLocation(Location location){
        if(currentRunId!=-1){
            helper.insertLocation(currentRunId,location);
        }else{
            Log.e(TAG, "Location received with no tracking run; ignoring.");
        }
    }
    public void removeRun(long runId){
        helper.deleteRun(runId);
    }
    public void startLocationUpdates() {
        String provider = LocationManager.GPS_PROVIDER;
        Location lastKnown = locationManager.getLastKnownLocation(provider);
        if (lastKnown != null) {
            lastKnown.setTime(System.currentTimeMillis());
            broadcastLocation(lastKnown);
        }
        PendingIntent pi = getLocationPendingIntent(true);
        locationManager.requestLocationUpdates(provider, 0, 0, pi);
    }
    public void stopLocationUpdates() {
        PendingIntent pi = getLocationPendingIntent(false);
        if (pi != null) {
            locationManager.removeUpdates(pi);
            pi.cancel();
        }
    }
    //determine if we have registered the pending intent
    public boolean isTrackingRun() {
        return getLocationPendingIntent(false) != null;
    }
    public boolean isTrackingRun(Run run) {
        return run != null && run.getId() == currentRunId;
    }
    public RunDatabaseHelper.RunCursor queryRuns(){
        return helper.queryRun();
    }
    public Run getRun(long id){
        Run run=null;
        RunDatabaseHelper.RunCursor cursor=helper.queryRun(id);
        cursor.moveToFirst();
        if(!cursor.isAfterLast()){
            run=cursor.getRun();
        }
        cursor.close();
        return run;
    }
    public Location getLastLocationForRun(long runId){
        Location location=null;
        RunDatabaseHelper.LocationCursor cursor=helper.queryLastLocationForRun(runId);
        cursor.moveToFirst();
        if(!cursor.isAfterLast()){
            location=cursor.getLocation();
        }
        cursor.close();
        return location;
    }
    private void broadcastLocation(Location location) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        broadcast.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
        mAppContext.sendBroadcast(broadcast);
    }
    //create an intent which will be used to broadcast, shouldCreate indicates whether we should create a new pending intent
    private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
        return PendingIntent.getBroadcast(mAppContext, 0, broadcast, flags);
    }
    private Run insertRun(){
        Run run=new Run();
        run.setId(helper.insertRun(run));
        return run;
    }
}
