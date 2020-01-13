package com.coupne.bosniaguide;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hedgehog.ratingbar.RatingBar;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class MoreDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String AlbumTitle = "", AlbumName = "";
    private RecyclerView mRecyclerView, rvCommnets;

    private ViewPager viewPager;
    private CircleIndicator circleIndicator;
    private MyViewPager myPager;
    private GoogleMap mMap;
    private TextView description;
    private Button btnComment;
    private EditText edtComment, etlongDecs, etAddress;

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth mAuth;

    private RecyclerView.LayoutManager layoutManager;
    private RecyclerAdapter adapter;
    private PlaceData placeData;

    private CommnetAdater commnetAdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        placeData = (PlaceData) getIntent().getSerializableExtra("data");
        AlbumTitle = getIntent().getStringExtra("AlbumTitle");
        AlbumName = getIntent().getStringExtra("title");
        setTitle(AlbumName);

        mRecyclerView = findViewById(R.id.recycler_more_detail);
        rvCommnets = findViewById(R.id.rvCommnets);
        rvCommnets.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        if (placeData.getCommit() != null){
            commnetAdater = new CommnetAdater(placeData.getCommit().values());
        }else{
            commnetAdater = new CommnetAdater(new ArrayList<Commit>());
        }

        rvCommnets.setAdapter(commnetAdater);

        btnComment = findViewById(R.id.commit_btn);
        etAddress = findViewById(R.id.address);
        etlongDecs = findViewById(R.id.long_description);

        edtComment = findViewById(R.id.comment_edt);

        if (!TextUtils.isEmpty(placeData.getLongDescription())) {

            etlongDecs.setText(placeData.getLongDescription());
        }

        if (!TextUtils.isEmpty(placeData.getAddress())) {

            etAddress.setText(placeData.getAddress());
        }

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child(AlbumTitle);
        mAuth = FirebaseAuth.getInstance();

        Button btnDelete = findViewById(R.id.btnDelete);
        Button btnUpdate = findViewById(R.id.btnUpdate);
        if (MainActivity.ADMIN_ACTION == MainActivity.DELETE) {
            btnDelete.setVisibility(View.VISIBLE);
            btnComment.setVisibility(View.GONE);
            btnUpdate.setVisibility(View.GONE);
            etAddress.setEnabled(false);
            etlongDecs.setEnabled(false);
        } else if (MainActivity.ADMIN_ACTION == MainActivity.UPDATE) {
            btnDelete.setVisibility(View.GONE);
            btnComment.setVisibility(View.GONE);
            btnUpdate.setVisibility(View.VISIBLE);
            etAddress.setEnabled(true);
            etlongDecs.setEnabled(true);
        } else {
            etAddress.setEnabled(false);
            etlongDecs.setEnabled(false);
            btnDelete.setVisibility(View.GONE);
            btnUpdate.setVisibility(View.GONE);
            btnComment.setVisibility(View.VISIBLE);
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etAddress.getText().toString().trim())) {
                    Toast.makeText(MoreDetailActivity.this, "Address must not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                placeData.setAddress(etAddress.getText().toString().trim());
                if (TextUtils.isEmpty(etlongDecs.getText().toString().trim())) {
                    Toast.makeText(MoreDetailActivity.this, "Description must not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                placeData.setLongDescription(etlongDecs.getText().toString().trim());

                myRef.child(placeData.getKey()).setValue(placeData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MoreDetailActivity.this, "Data updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MoreDetailActivity.this, "Something went wrong please try again later", Toast.LENGTH_SHORT).show();
                        }
                        MainActivity.ADMIN_ACTION = -1;
                        onBackPressed();
                    }
                });


            }
        });


        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef.child(placeData.getKey()).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        Toast.makeText(MoreDetailActivity.this, "Data removed successfully", Toast.LENGTH_SHORT).show();
                        MainActivity.ADMIN_ACTION = -1;
                        onBackPressed();
                    }

                });
            }
        });

        //https://tourguidebosnia.firebaseio.com/Activities/Football/-LmGtejGNmoeXEG_PNTE/commit
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!new Session(MoreDetailActivity.this).isLoggedIn()) {
                    Toast.makeText(MoreDetailActivity.this, "Please login before comment", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edtComment.getText().toString().trim())) {
                    return;
                }


                /*List<Commit> commits = new ArrayList<>();
                if (placeData.getCommit().values() != null){
                    commits.addAll(placeData.getCommit().values());
                }
                commits.add(new Commit("name", edtComment.getText().toString().trim()));*/
                final Commit commit = new Commit(new Session(MoreDetailActivity.this).getName(), edtComment.getText().toString().trim());
                myRef.child(placeData.getKey()).child("commit").push()
                        .setValue(commit)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    if (commnetAdater != null){
                                        commnetAdater.addCommit(commit);
                                    }
                                    edtComment.setText("");
                                    Toast.makeText(MoreDetailActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(MoreDetailActivity.this, "failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                /*myRef.child(placeData.getKey()).child("commit").setValue(commits).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MoreDetailActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MoreDetailActivity.this, "failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/

                /*if (mAuth != null) {
                    Toast.makeText(MoreDetailActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MoreDetailActivity.this, "Please Login", Toast.LENGTH_SHORT).show();
                }*/
            }
        });


        //===================== Set RatingBar ============================
        RatingBar mRatingBar = (RatingBar) findViewById(R.id.ratingbar);
        /*mRatingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
        mRatingBar.setStarEmptyDrawable(getResources().getDrawable(R.mipmap.star_empty));
        mRatingBar.setStarHalfDrawable(getResources().getDrawable(R.mipmap.star_half));
        mRatingBar.setStarFillDrawable(getResources().getDrawable(R.mipmap.star_full));
        mRatingBar.setStarCount(5);
        mRatingBar.setStar(2.5f);
        mRatingBar.halfStar(true);
        mRatingBar.setmClickable(true);
        mRatingBar.setStarImageWidth(120f);
        mRatingBar.setStarImageHeight(60f);
        mRatingBar.setImagePadding(35);
        mRatingBar.setOnRatingChangeListener(
                new RatingBar.OnRatingChangeListener() {
                    @Override
                    public void onRatingChange(float RatingCount) {

                        if (!new Session(MoreDetailActivity.this).isLoggedIn()) {
                            Toast.makeText(MoreDetailActivity.this, "Please login first before giving rating", Toast.LENGTH_SHORT).show();
                        }
//                        Toast.makeText(MoreDetailActivity.this, "the fill star is" + RatingCount, Toast.LENGTH_SHORT).show();
                    }
                }
        );

        //===================== Set ViewPager ===========================
        myPager = new MyViewPager(this);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(myPager);
        circleIndicator = findViewById(R.id.circle);
        circleIndicator.setViewPager(viewPager);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                MoreDetailActivity.this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //===================== Set Map ==================================
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 150);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }


}
