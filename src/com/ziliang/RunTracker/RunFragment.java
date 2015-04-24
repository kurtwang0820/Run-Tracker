package com.ziliang.RunTracker;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by Kurt on 4/24/2015.
 */
public class RunFragment extends Fragment {
    private Button mStartButton, mStopButton;
    private TextView mStartedTextView, mLatitudeTextView, mLongitudeTextView, mAltitudeTextView, mDurationTextView;
    private RunManager runManager;
    private Run run;
    private Location lastLocation;
    private BroadcastReceiver locationReceiver=new LocationReceiver(){
        @Override
        protected void onLocationReceived(Context context, Location location) {
            lastLocation=location;
            if(isVisible()){
                updateUI();
            }
        }

        @Override
        protected void onProviderEnabledChanged(boolean enabled) {
            int toastText=enabled?R.string.gps_enabled:R.string.gps_disabled;
            Toast.makeText(getActivity(),toastText,Toast.LENGTH_LONG).show();
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        runManager=RunManager.get(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_run,container,false);
        mStartButton=(Button)v.findViewById(R.id.run_startButton);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runManager.startLocationUpdates();
                updateUI();
            }
        });
        mStopButton=(Button)v.findViewById(R.id.run_stopButton);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runManager.stopLocationUpdates();
                updateUI();
            }
        });
        mStartedTextView=(TextView)v.findViewById(R.id.run_startedTextView);
        mLatitudeTextView=(TextView)v.findViewById(R.id.run_latitudeTextView);
        mLongitudeTextView=(TextView)v.findViewById(R.id.run_longitudeTextView);
        mAltitudeTextView=(TextView)v.findViewById(R.id.run_altitudeTextView);
        mDurationTextView=(TextView)v.findViewById(R.id.run_durationTextView);
        updateUI();
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(locationReceiver, new IntentFilter(RunManager.ACTION_LOCATION));
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(locationReceiver);
        super.onStop();
    }

    private void updateUI(){
        boolean started=runManager.isTrackingRun();
        if(run!=null){
            mStartedTextView.setText(run.getStartDate().toString());
        }
        int durationSeconds=0;
        if(run!=null&&lastLocation!=null){
            durationSeconds=run.getDurationSeconds(lastLocation.getTime());
            mLatitudeTextView.setText(Double.toString(lastLocation.getLatitude()));
            mLongitudeTextView.setText(Double.toString(lastLocation.getLongitude()));
            mAltitudeTextView.setText(Double.toString(lastLocation.getAltitude()));
        }
        mDurationTextView.setText(Run.formatDuration(durationSeconds));
        mStartButton.setEnabled(!started);
        mStopButton.setEnabled(started);
    }
}
