package com.example.sampleproject.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.sampleproject.Components.CustomCalendarView;
import com.example.sampleproject.Models.Event;
import com.example.sampleproject.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

public class CalendarFragment extends Fragment {
    private final String TAG = "test tag message here";
    public HashSet<Date> events = new HashSet<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        //// Init Calendar View ////
        CustomCalendarView calendarView = ((CustomCalendarView) root.findViewById(R.id.calendar_view));
        calendarView.setEventHandler(new CustomCalendarView.EventHandler() {
            @Override
            public void onDayLongPress(Date date) {
                // show returned day
                DateFormat df = SimpleDateFormat.getDateInstance();
                Toast.makeText(getContext(), df.format(date), Toast.LENGTH_SHORT).show();
            }
        });

        //// Init GET firebase ////
        FirebaseDatabase database = FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link));
        DatabaseReference myRef = database.getReference().child("events");


        // Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Event user = snapshot.getValue(Event.class);
                    System.out.println(user.getDeadline());
                    Date addDate = new Date(user.getDeadline());

                    events.add(addDate);
                    System.out.println(events.size());
                }

                calendarView.updateCalendar(events);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


        //// Init Components ////
        Button dialogButton = root.findViewById(R.id.buttontodialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
                View bottomSheetView = inflater.inflate(R.layout.layout_bottom_dialog, root.findViewById(R.id.bottomDialogContainer));
                bottomSheetView.findViewById(R.id.buttonDialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(), "nicela", Toast.LENGTH_SHORT).show();
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // inflater.inflate(R.menu.calendar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }
}