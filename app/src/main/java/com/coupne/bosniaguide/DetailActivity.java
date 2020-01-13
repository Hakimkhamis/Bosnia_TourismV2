package com.coupne.bosniaguide;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private String AlbumTitle = "";
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        AlbumTitle = getIntent().getStringExtra("AlbumTitle");
        String str[] = AlbumTitle.split("/");   //(category/title) split -> title
        setTitle(str[1]);

        mRecyclerView = findViewById(R.id.recycler_detail);

        new FirebaseDatabaseHelper(AlbumTitle).readPlaceData(new FirebaseDatabaseHelper.DataStatus() {

            @Override
            public void DataIsLoaded(List<PlaceData> placeData, List<String> keys) {
                new Recycler_Config().setConfig(mRecyclerView, DetailActivity.this, placeData, keys, AlbumTitle);
            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataISUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }
        });

    } @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                DetailActivity.this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }




}
