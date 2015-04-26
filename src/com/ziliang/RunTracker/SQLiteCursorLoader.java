package com.ziliang.RunTracker;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by kurtw_000 on 2015/4/26.
 */
public abstract class SQLiteCursorLoader extends AsyncTaskLoader<Cursor> {
    private Cursor cursor;
    public SQLiteCursorLoader(Context context){
        super(context);
    }
    protected abstract Cursor loadCursor();
    @Override
    public Cursor loadInBackground(){
        Cursor cursor1=loadCursor();
        if(cursor1!=null){
            //ensure that the content window is filled
            cursor1.getCount();
        }
        return cursor1;
    }
    @Override
    public void deliverResult(Cursor data){
        Cursor oldCursor=cursor;
        cursor=data;
        if(isStarted()){
            super.deliverResult(data);
        }
        if(oldCursor!=null&&oldCursor!=data&&!oldCursor.isClosed()){
            oldCursor.close();
        }
    }
    @Override
    protected void onStartLoading(){
        if(cursor!=null){
            deliverResult(cursor);
        }
        if(takeContentChanged()||cursor==null){
            forceLoad();
        }
    }
    @Override
    protected void onStopLoading(){
        //attempt to cancel the current load task if possible
        cancelLoad();
    }
    @Override
    public void onCanceled(Cursor cursor){
        if(cursor!=null&&!cursor.isClosed()){
            cursor.close();
        }
    }
    @Override
    protected void onReset(){
        super.onReset();
        //ensure the loader is stopped
        onStopLoading();
        if(cursor!=null&&!cursor.isClosed()){
            cursor.close();
        }
        cursor=null;
    }
}
