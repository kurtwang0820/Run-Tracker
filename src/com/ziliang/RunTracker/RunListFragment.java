package com.ziliang.RunTracker;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Kurt on 4/25/2015.
 */
public class RunListFragment extends ListFragment {
    private static final int REQUEST_NEW_RUN = 0;
    private RunDatabaseHelper.RunCursor cursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //query the list of runs
        cursor = RunManager.get(getActivity()).queryRuns();
        //create an adapter to point at this cursor
        RunCursorAdapter adapter = new RunCursorAdapter(getActivity(), cursor);
        setListAdapter(adapter);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        ListView tasksList = (ListView) v.findViewById(android.R.id.list); //get ListView
        registerForContextMenu(tasksList);
        return v;
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.run_list_item_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        RunCursorAdapter adapter = (RunCursorAdapter) getListAdapter();
        RunManager runManager = RunManager.get(getActivity());
        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                RunDatabaseHelper.RunCursor runCursor = (RunDatabaseHelper.RunCursor) adapter.getItem(position);
                long runId = runCursor.getRun().getId();
                if(runManager.isTrackingRun(runCursor.getRun())){
                    runManager.stopRun();
                }
                runManager.removeRun(runId);
                cursor.requery();
                adapter.notifyDataSetChanged();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.run_list_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_run:
                Intent i = new Intent(getActivity(), RunActivity.class);
                startActivityForResult(i, REQUEST_NEW_RUN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_NEW_RUN == requestCode) {
            cursor.requery();
            ((RunCursorAdapter) getListAdapter()).notifyDataSetChanged();
        }
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        Intent i = new Intent(getActivity(), RunActivity.class);
        i.putExtra(RunActivity.EXTRA_RUN_ID, id);
        startActivity(i);
    }

    @Override
    public void onResume() {
        ((RunCursorAdapter) getListAdapter()).notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        cursor.close();
        super.onDestroy();
    }

    private static class RunCursorAdapter extends CursorAdapter {
        private RunDatabaseHelper.RunCursor runCursor;

        public RunCursorAdapter(Context context, RunDatabaseHelper.RunCursor runCursor) {
            super(context, runCursor, 0);
            this.runCursor = runCursor;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            //get the run of current row
            Run run = runCursor.getRun();
            if (RunManager.get(context).isTrackingRun(run)) {
                view.setBackgroundColor(Color.GRAY);
            } else {
                view.setBackgroundResource(android.R.drawable.list_selector_background);
            }
            //set up the start date text view
            TextView startDateTextView = (TextView) view;
            String cellText = context.getString(R.string.cell_text, run.getStartDate());
            startDateTextView.setText(cellText);
        }
    }
}
