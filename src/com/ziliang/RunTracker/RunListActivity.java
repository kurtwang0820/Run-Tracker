package com.ziliang.RunTracker;

import android.app.Fragment;

/**
 * Created by Kurt on 4/25/2015.
 */
public class RunListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new RunListFragment();
    }
}
