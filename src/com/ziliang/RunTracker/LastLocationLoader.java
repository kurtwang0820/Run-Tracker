package com.ziliang.RunTracker;

import android.content.Context;
import android.location.Location;

/**
 * Created by kurtw_000 on 2015/4/26.
 */
public class LastLocationLoader extends DataLoader<Location> {
    private long runId;
    public LastLocationLoader(Context context,long runId){
        super(context);
        this.runId=runId;
    }
    @Override
    public Location loadInBackground(){
        return RunManager.get(getContext()).getLastLocationForRun(runId);
    }
}
