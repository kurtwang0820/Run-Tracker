package com.ziliang.RunTracker;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.Loader;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by Kurt on 4/24/2015.
 */
public class RunFragment extends Fragment{
    private static final String ARG_RUN_ID = "RUN_ID";
    private static final int LOAD_LOCATION = 1;
    private static final int LOAD_RUN = 0;
    private Button mStartButton, mStopButton;
    private TextView mStartedTextView, mLatitudeTextView, mLongitudeTextView, mAltitudeTextView, mDurationTextView;
    private RunManager runManager;
    private Run run;
    private Location lastLocation;
    private BroadcastReceiver locationReceiver = new LocationReceiver() {
        @Override
        protected void onLocationReceived(Context context, Location location) {
            if (!runManager.isTrackingRun(run)) {
                return;
            }
            lastLocation = location;
            if (isVisible()) {
                updateUI();
            }
        }

        @Override
        protected void onProviderEnabledChanged(boolean enabled) {
            int toastText = enabled ? R.string.gps_enabled : R.string.gps_disabled;
            Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();
        }
    };

    private class LocationLoaderCallbacks implements LoaderManager.LoaderCallbacks<Location> {

        @Override
        public Loader<Location> onCreateLoader(int id, Bundle args) {
            return new LastLocationLoader(getActivity(), args.getLong(ARG_RUN_ID));
        }

        @Override
        public void onLoadFinished(Loader<Location> loader, Location data) {
            lastLocation = data;
            updateUI();
        }

        @Override
        public void onLoaderReset(Loader<Location> loader) {
            //do nothing
        }
    }

    public static RunFragment newInstance(long runId) {
        Bundle args = new Bundle();
        args.putLong(ARG_RUN_ID, runId);
        RunFragment rf = new RunFragment();
        rf.setArguments(args);
        return rf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        runManager = RunManager.get(getActivity());
        Bundle args = getArguments();
        if (args != null) {
            long runId = args.getLong(ARG_RUN_ID, -1);
            if (runId != -1) {
                LoaderManager loaderManager = getLoaderManager();
                loaderManager.initLoader(LOAD_RUN, args, new RunLoaderCallbacks());
                loaderManager.initLoader(LOAD_LOCATION, args, new LocationLoaderCallbacks());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_run, container, false);
        mStartButton = (Button) v.findViewById(R.id.run_startButton);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (run == null) {
                    run = runManager.startNewRun();
                } else {
                    runManager.startTrackingRun(run);
                }
                updateUI();
            }
        });
        mStopButton = (Button) v.findViewById(R.id.run_stopButton);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runManager.stopRun();
                updateUI();
            }
        });
        mStartedTextView = (TextView) v.findViewById(R.id.run_startedTextView);
        mLatitudeTextView = (TextView) v.findViewById(R.id.run_latitudeTextView);
        mLongitudeTextView = (TextView) v.findViewById(R.id.run_longitudeTextView);
        mAltitudeTextView = (TextView) v.findViewById(R.id.run_altitudeTextView);
        mDurationTextView = (TextView) v.findViewById(R.id.run_durationTextView);
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

    private void updateUI() {
        boolean started = runManager.isTrackingRun();
        boolean trackingThisRun = runManager.isTrackingRun(run);
        if (run != null) {
            mStartedTextView.setText(run.getStartDate().toString());
        }
        int durationSeconds = 0;
        if (run != null && lastLocation != null) {
            durationSeconds = run.getDurationSeconds(lastLocation.getTime());
            mLatitudeTextView.setText(Double.toString(lastLocation.getLatitude()));
            mLongitudeTextView.setText(Double.toString(lastLocation.getLongitude()));
            mAltitudeTextView.setText(Double.toString(lastLocation.getAltitude()));
        }
        mDurationTextView.setText(Run.formatDuration(durationSeconds));
        mStartButton.setEnabled(!started);
        mStopButton.setEnabled(started && trackingThisRun);
    }

    private class RunLoaderCallbacks implements LoaderManager.LoaderCallbacks<Run> {
        @Override
        public Loader<Run> onCreateLoader(int id, Bundle args) {
            return new RunLoader(getActivity(), args.getLong(ARG_RUN_ID));
        }

        @Override
        public void onLoadFinished(Loader<Run> loader, Run data) {
            run = data;
            updateUI();
        }

        @Override
        public void onLoaderReset(Loader<Run> loader) {
            //do nothing
        }
    }
}
