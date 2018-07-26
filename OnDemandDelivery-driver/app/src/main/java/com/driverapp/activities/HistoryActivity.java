package com.driverapp.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.driverapp.R;
import com.driverapp.adapters.HistoryAdapter;
import com.driverapp.models.TripModel;

import java.util.List;



public class HistoryActivity extends BaseActivity {

    public static final String TAG = "HistoryActivity";
    private static final String TITLE = "History";
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initialseToolbar();
        initialiseViews();
    }

    private void initialseToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setTitle(TITLE);
        toolbar.setTitle(TITLE);
    }

    @Override
    public void initialiseViews() {
        super.initialiseViews();
        mRecyclerView = findViewById(R.id.history_recyclerview);
        mAdapter = new HistoryAdapter();
        mLayoutManager = new LinearLayoutManager(HistoryActivity.this, LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    public class TripHistory extends AsyncTask<String,String,List<TripModel>>{

        @Override
        protected List<TripModel> doInBackground(String... strings) {

            return null;
        }

        @Override
        protected void onPostExecute(List<TripModel> tripModels) {
            super.onPostExecute(tripModels);

        }
    }
}
