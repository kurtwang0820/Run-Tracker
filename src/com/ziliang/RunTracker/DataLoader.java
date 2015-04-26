package com.ziliang.RunTracker;

import android.content.AsyncTaskLoader;
import android.content.Context;

/**
 * Created by kurtw_000 on 2015/4/26.
 */
public abstract class DataLoader<D> extends AsyncTaskLoader<D> {
    private D data;
    public DataLoader(Context context){
        super(context);
    }
    @Override
    protected void onStartLoading(){
        if(data!=null){
            deliverResult(data);
        }else{
            forceLoad();
        }
    }
    @Override
    public void deliverResult(D data){
        this.data=data;
        if(isStarted()){
            super.deliverResult(this.data);
        }
    }
}
