package com.example.sampleproject.Helper;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sampleproject.Models.Event;
import com.example.sampleproject.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

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


//    public static void submitFromDialog(DatabaseReference firebaseMembers, String[] newTitle, String[] newDescription, String id, String otherId, BottomSheetDialog bottomSheetDialog, View bottomSheetView, Button btnDeadline, Boolean isPrivate, ) {
//        Date newDate = new Date(btnDeadline.getText().toString());
//        Date today = new Date();
//
//        Calendar todayCal = TimeHelper.setDateTimeOneDown(today);
//        Date newToday = todayCal.getTime();
//
//        long newDaysLeft = TimeHelper.calcDaysBetween(newDate,newToday);
//
//        ////  Check Validation ////
//        if (newTitle[0].equals("")) {
//            Toast.makeText(bottomSheetView.getContext(),R.string.error_emptyTitle, Toast.LENGTH_SHORT).show();
//        } else if (newDescription[0].equals("")) {
//            Toast.makeText(bottomSheetView.getContext(),R.string.error_emptyDescription, Toast.LENGTH_SHORT).show();
//        } else {
//
//
////            DatabaseReference firebaseMembers = FirebaseDatabase.getInstance(bottomSheetView.getResources().getString(R.string.firebase_link)).getReference()
////                    .child("members");
//            String uuid = UUID.randomUUID().toString();
//            Event event = new Event(newTitle[0], newDescription[0], newDate.toString(), (int) newDaysLeft, uuid, isPrivate, false);
//
//            if (event.getIsPrivate()) {
//                FirebaseHelper.postToFirebase(event, firebaseMembers, id);
//            } else {
//                FirebaseHelper.postToFirebase(event, firebaseMembers, id);
//                FirebaseHelper.postToFirebase(event, firebaseMembers, otherId);
//            }
//
//            Toast.makeText(bottomSheetView.getContext(), R.string.success_eventAdded, Toast.LENGTH_SHORT).show();
//            bottomSheetDialog.dismiss();
//
//            updateRecycler(bottomSheetView,id,errorMsg);
//            refreshFragment();
//        }
//
//
//    }

}
