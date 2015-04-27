package com.ziliang.RunTracker;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by kurtw_000 on 4/26/2015.
 */
public class LocationListCursorLoader extends SQLiteCursorLoader {
    private long runId;
    public LocationListCursorLoader(Context context,long runId){
        super(context);
        this.runId=runId;
    }
    @Override
    protected Cursor loadCursor() {
        return RunManager.get(getContext()).queryLocationsForRun(runId);
    }
}
