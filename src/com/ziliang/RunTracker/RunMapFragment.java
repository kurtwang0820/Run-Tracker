package com.ziliang.RunTracker;

import android.app.LoaderManager;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Date;

/**
 * Created by kurtw_000 on 4/26/2015.
 */
public class RunMapFragment extends MapFragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String ARG_RUN_ID = "RUN_ID";
    private static final int LOAD_LOCATIONS=0;
    private GoogleMap googleMap;
    private RunDatabaseHelper.LocationCursor locationCursor;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Bundle args=getArguments();
        if(args!=null){
            long runId=args.getLong(ARG_RUN_ID,-1);
            if(runId!=-1){
                LoaderManager loaderManager=getLoaderManager();
                loaderManager.initLoader(LOAD_LOCATIONS,args,this);
            }
        }
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        long runId=args.getLong(ARG_RUN_ID, -1);
        return new LocationListCursorLoader(getActivity(),runId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        locationCursor=(RunDatabaseHelper.LocationCursor)data;
        updateUI();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        locationCursor.close();
        locationCursor=null;
    }

    public static RunMapFragment newInstance(long runId) {
        Bundle args = new Bundle();
        args.putLong(ARG_RUN_ID, runId);
        RunMapFragment runMapFragment = new RunMapFragment();
        runMapFragment.setArguments(args);
        return runMapFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInsttancetate) {
        View v = super.onCreateView(inflater, parent, savedInsttancetate);
        //stash a reference to the google map
        googleMap = getMap();
        //show the user's location
        googleMap.setMyLocationEnabled(true);
        return v;
    }

    private void updateUI(){
        if(googleMap==null||locationCursor==null){
            return;
        }
        PolylineOptions line=new PolylineOptions();
        LatLngBounds.Builder latLngBuilder=new LatLngBounds.Builder();
        locationCursor.moveToFirst();
        while(!locationCursor.isAfterLast()){
            Location location=locationCursor.getLocation();
            LatLng latlng=new LatLng(location.getLatitude(),location.getLongitude());
            Resources r=getResources();
            if(locationCursor.isFirst()){
                String startDate=new Date(location.getTime()).toString();
                MarkerOptions startMarkerOptions=new MarkerOptions().position(latlng).title(r.getString(R.string.run_start)).snippet(r.getString(R.string.run_started_at_format,startDate));
                googleMap.addMarker(startMarkerOptions);
            }else if(locationCursor.isLast()){
                String endDate=new Date(location.getTime()).toString();
                MarkerOptions finishMarkerOptions=new MarkerOptions().position(latlng).title(r.getString(R.string.run_finish)).snippet(r.getString(R.string.run_finished_at_format,endDate));
                googleMap.addMarker(finishMarkerOptions);
            }
            line.add(latlng);
            latLngBuilder.include(latlng);
            locationCursor.moveToNext();
        }
        googleMap.addPolyline(line);
        Display display=getActivity().getWindowManager().getDefaultDisplay();
        LatLngBounds latLngBounds=latLngBuilder.build();
        CameraUpdate movement= CameraUpdateFactory.newLatLngBounds(latLngBounds,display.getWidth(),display.getHeight(),15);
        googleMap.moveCamera(movement);
    }
}
