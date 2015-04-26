package com.ziliang.RunTracker;

import android.util.Log;

import java.util.Date;

/**
 * Created by Kurt on 4/24/2015.
 */
public class Run {
    private Date startDate;
    private long mId;
    public Run(){
        mId=-1;
        startDate=new Date();
    }
    public Date getStartDate(){
        return startDate;
    }
    public void setStartDate(Date startDate){
        this.startDate=startDate;
    }
    public int getDurationSeconds(long endMillis){
        return (int)((endMillis-startDate.getTime())/1000);
    }
    public static String formatDuration(int durationSeconds){
        int seconds=durationSeconds%60;
        int minutes=((durationSeconds-seconds)/60)%60;
        int hours=(durationSeconds-(minutes*60)-seconds)/3600;
        return String.format("%02d:%02d:%02d",hours,minutes,seconds);
    }
    public long getId(){
        return mId;
    }
    public void setId(long id){
        mId=id;
    }
}
