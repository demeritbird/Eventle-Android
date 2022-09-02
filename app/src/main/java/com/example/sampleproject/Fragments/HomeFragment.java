package com.example.sampleproject.Fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sampleproject.Helper.FirebaseHelper;
import com.example.sampleproject.Helper.MiscHelper;
import com.example.sampleproject.Helper.TimeHelper;
import com.example.sampleproject.R;
import com.example.sampleproject.Models.Event;
import com.example.sampleproject.Models.EventAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    EventAdapter recyclerAdapter; // Create Object of the Adapter class
    DatabaseReference mbase; // Create object of the Firebase Realtime Database
    private Uri imageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //// Receive Intents ////
        Bundle resultIntent = getActivity().getIntent().getExtras();
        String id = resultIntent.getString("id", "1");
        String newUserName = resultIntent.getString("username", "no");

        /// Init Image & Username ////
        ImageView userImage = root.findViewById(R.id.iv_userImage);
        FirebaseHelper.changeImageFromFirebase(userImage, id, imageUri);

        /// Components ///
        TextView errorMsg = root.findViewById(R.id.tv_errormsg);
        TextView username = root.findViewById(R.id.tv_username);
        username.setText(newUserName);


        //// Recycler View ////
        recyclerView = root.findViewById(R.id.recycler1);
        initRecycler(root, id, errorMsg);
        return root;
    }

    public void initRecycler(View root, String selId, TextView errorMsg) {
        // Create a instance of the database and get its reference
        mbase = FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link)).getReference()
                .child("members").child("member" + selId).child("events");

        mbase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DataSnapshot deadlineString = snapshot.child("deadline");

                    Date deadlineDate = new Date(deadlineString.getValue().toString());

                    Date today = new Date();
                    Calendar todayCal = TimeHelper.setDateTimeOneDown(today);
                    Date newToday = todayCal.getTime();

                    long daysBetween = TimeUnit.DAYS.convert(deadlineDate.getTime() - newToday.getTime(), TimeUnit.MILLISECONDS);

                    if (snapshot.child("iscomplete").getValue().toString() == "true") {
                        MiscHelper.increasePriority(snapshot);

                        if (newToday.getDay() >= deadlineDate.getDay()) {
                            snapshot.child("daysleft").getRef().setValue((int) daysBetween - 1);
                        } else {
                            snapshot.child("daysleft").getRef().setValue((int) daysBetween);
                        }

                    } else {

                        if (newToday.getDay() >= deadlineDate.getDay()) {
                            snapshot.child("daysleft").getRef().setValue((int) daysBetween - 1);
                            snapshot.child("priority").getRef().setValue((int) daysBetween - 1);
                        } else {
                            snapshot.child("daysleft").getRef().setValue((int) daysBetween);
                            snapshot.child("priority").getRef().setValue((int) daysBetween);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(String.valueOf(R.string.error_readFirebase), String.valueOf(R.string.TAG_failValue), error.toException());
            }
        });
        Query queryBy = mbase.orderByChild("priority").startAt(0).limitToFirst(3);

        recyclerView.setNestedScrollingEnabled(false);
        // To display the Recycler view linearly
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // It is a class provide by the FirebaseUI to make a query in the database to fetch appropriate data
        FirebaseRecyclerOptions<Event> options = new FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(queryBy, Event.class)
                .build();
        // Connecting object of required Adapter class to the Adapter class itself
        recyclerAdapter = new EventAdapter(options);
        // Connecting Adapter class with the Recycler view*/
        //checkEmptyList(queryBy, errorMsg);
        MiscHelper.checkEmptyList(queryBy, errorMsg);
        recyclerView.setAdapter(recyclerAdapter);
    }


    // Function to tell the app to start getting from database on starting of the activity
    @Override
    public void onStart() {
        super.onStart();
        recyclerAdapter.startListening();
    }

    // Function to tell the app to stop getting data from database on stopping of the activity
    @Override
    public void onStop() {
        super.onStop();
        recyclerAdapter.stopListening();
    }
}