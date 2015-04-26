package com.ziliang.RunTracker;

import android.content.Context;

/**
 * Created by kurtw_000 on 2015/4/26.
 */
public class RunLoader extends DataLoader<Run> {
    private long runId;
    public RunLoader(Context context,long runId){
        super(context);
        this.runId=runId;
    }
    @Override
    public Run loadInBackground(){
        return RunManager.get(getContext()).getRun(runId);
    }
}
