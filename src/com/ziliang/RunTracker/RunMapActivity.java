package com.ziliang.RunTracker;

import android.app.Fragment;

/**
 * Created by kurtw_000 on 4/26/2015.
 */
public class RunMapActivity extends SingleFragmentActivity {
    //a key for passing a run id as a long
    public static final String EXTRA_RUN_ID="com.ziliang.runtracker.run_id";
    @Override
    protected Fragment createFragment(){
        long runId=getIntent().getLongExtra(EXTRA_RUN_ID,-1);
        if(runId!=-1){
            return RunMapFragment.newInstance(runId);
        }else{
            return new RunMapFragment();
        }
    }
}
