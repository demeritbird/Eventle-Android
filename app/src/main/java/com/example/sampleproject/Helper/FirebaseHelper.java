package com.example.sampleproject.Helper;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.sampleproject.Models.Event;
import com.example.sampleproject.Models.Member;
import com.example.sampleproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;

public class FirebaseHelper {

    public static void calcInsights(View root, DatabaseReference firebaseMemberId, int eventsInWeek, int eventsInMonth) {
        final int[] eventsInWeeks = {eventsInWeek};
        final int[] eventsInMonths = {eventsInMonth};

        DatabaseReference firebaseEvents = firebaseMemberId.child("events");
        firebaseEvents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventsInWeeks[0] = 0;
                eventsInMonths[0] = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Date today = new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(today);

                    Date childDate = new Date(String.valueOf(snapshot.child("deadline").getValue()));
                    Calendar cal2 = Calendar.getInstance();
                    cal2.setTime(childDate);

                    // check same week //
                    boolean isSameWeek = cal.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR) &&
                            cal.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
                    if ( isSameWeek ) { eventsInWeeks[0] += 1; }

                    // check same month //
                    boolean isSameMonth = cal.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)&&
                            cal.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
                    if (isSameMonth) { eventsInMonths[0] += 1; }
                }

                // Set Week //
                String stringEventsInWeek = String.valueOf( eventsInWeeks[0]);
                TextView eventsInWeek = root.findViewById(R.id.tv_eventsPerWeek);
                eventsInWeek.setText(stringEventsInWeek);

                // Set Month //
                String stringEventsInMoth = String.valueOf(eventsInMonths[0]);
                TextView eventsInMoth = root.findViewById(R.id.tv_eventsPerMonth);
                eventsInMoth.setText(stringEventsInMoth);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public static void changeImageFromFirebase(ImageView userImage, String id, Uri imageUri) {
        final Uri[] URI = {imageUri};

        DatabaseReference firebaseMemberId = FirebaseDatabase.getInstance("https://scheduleapp-3ebb7-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("members").child("member" + id);
        DatabaseReference firebaseImage = firebaseMemberId.child("image").child("imageUri");
        firebaseImage.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String imageString = dataSnapshot.getValue().toString();
                if (!imageString.equals("")) {
                    URI[0] = Uri.parse(imageString);
                    Picasso.get().load(URI[0]).placeholder(R.drawable.dembirdimage).into(userImage);
                } else {
                    Log.w(String.valueOf(R.string.error_readFirebase), String.valueOf(R.string.TAG_failImage));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }


    // Requires firebase -> till members

    public static void postToFirebase(Event event, DatabaseReference firebase, String selId) {
        DatabaseReference selFirebase = firebase.child("member" + selId).child("events").child(event.getUid());

        selFirebase.child("title").setValue(event.getTitle());
        selFirebase.child("description").setValue(event.getDescription());
        selFirebase.child("deadline").setValue(event.getDeadline());
        selFirebase.child("daysleft").setValue(event.getDaysLeft());
        selFirebase.child("uid").setValue(event.getUid());
        selFirebase.child("isprivate").setValue(event.getIsPrivate());

        if (event.getIsComplete()) {
            selFirebase.child("priority").setValue(event.getDaysLeft() + 99999);
        } else {
            selFirebase.child("priority").setValue(event.getDaysLeft());
        }


        selFirebase.child("iscomplete").setValue(event.getIsComplete());
    }

    public static void delFromFirebase(String uid, DatabaseReference firebase, String selId) {
        firebase.child("member" + selId).child("events").child(uid).setValue(null);
    }

    public static void uploadImageToFirebase(Uri uri, DatabaseReference dbRef, String id) {
        StorageReference storageReference = FirebaseStorage.getInstance("gs://scheduleapp-3ebb7.appspot.com").getReference();
        StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + ".jpg");

        fileRef.putFile(uri).addOnSuccessListener((new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Member member = new Member(uri.toString());
                        dbRef.child("member" + id).child("image").setValue(member);
                    }
                });
            }
        })).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(String.valueOf(R.string.TAG_failValue), String.valueOf(R.string.error_readFirebase));
            }
        });
    }

    public static void submitEventOnClick(Event event, DatabaseReference firebaseMembers, String id, String otherId, Boolean isAdding) {

        if (event.getIsPrivate()) {
            FirebaseHelper.postToFirebase(event, firebaseMembers, id);
            if(!isAdding) {FirebaseHelper.delFromFirebase(event.getUid(), firebaseMembers, otherId);}
        } else {
            FirebaseHelper.postToFirebase(event, firebaseMembers, id);
            FirebaseHelper.postToFirebase(event, firebaseMembers, otherId);
        }


    }

}
