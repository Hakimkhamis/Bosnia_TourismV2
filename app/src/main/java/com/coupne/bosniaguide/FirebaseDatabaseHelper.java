package com.coupne.bosniaguide;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FirebaseDatabaseHelper {
    private static final String TAG = "FirebaseDatabaseHelper";
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceData;
    private List<PlaceData> placedata = new ArrayList<>();

    public interface DataStatus {
        void DataIsLoaded(List<PlaceData> placeData, List<String> keys);
        void DataIsInserted();
        void DataISUpdated();
        void DataIsDeleted();
    }
    public FirebaseDatabaseHelper(String str) {
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceData = mDatabase.getReference(str);//reference database
    }

    //https://tourguidebosnia.firebaseio.com//LandMarks/Mountains/
    public void readPlaceData(final DataStatus dataStatus) {
        mReferenceData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                placedata.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()) {
                    keys.add(keyNode.getKey());
                    PlaceData placeData = keyNode.getValue(PlaceData.class);
                    placeData.setKey(keyNode.getKey());


                    placedata.add(placeData);
                }
                dataStatus.DataIsLoaded(placedata, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
