package com.example.sampleproject.Helper;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sampleproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MiscHelper {

    // yea i know you can make both into one, but its easier to read for me
    public static void selectPublic(Button btnPublic, Button btnPrivate) {
        btnPrivate.setBackgroundResource(R.drawable.eventcard_viewbg_complete);
        btnPublic.setBackgroundResource(R.drawable.eventcard_viewbg);
    }

    public static void selectPrivate(Button btnPrivate, Button btnPublic) {
        btnPublic.setBackgroundResource(R.drawable.eventcard_viewbg_complete);
        btnPrivate.setBackgroundResource(R.drawable.eventcard_viewbg);
    }

    public static void increasePriority(DataSnapshot snapshot) {
        snapshot.child("priority").getRef().setValue(99999 + Integer.parseInt(snapshot.child("daysleft").getValue().toString()));
    }

    public static void remainPriority(DataSnapshot snapshot) {
        snapshot.child("priority").getRef().setValue(Integer.parseInt(snapshot.child("daysleft").getValue().toString()) );
    }


    public static void checkEmptyList(Query query, TextView errorMsg) {
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    errorMsg.setVisibility(View.VISIBLE);
                } else {
                    errorMsg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
