package com.example.sampleproject.Fragments;

import android.graphics.Color;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sampleproject.Components.CalendarPickerDialog;
import com.example.sampleproject.Components.CustomCalendarView;
import com.example.sampleproject.Models.Event;
import com.example.sampleproject.Models.EventAdapter;
import com.example.sampleproject.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CalendarFragment extends Fragment {
    public RecyclerView recyclerView;
    public static EventAdapter recyclerAdapter; // Create Object of the Adapter class
    DatabaseReference mbase; // Create object of the Firebase Realtime Database
    Date selectedDate = CustomCalendarView.dateSelected;
    Boolean isPrivate = false;
    Boolean isComplete = false;
//    private CalendarFragment calendarFragment = new CalendarFragment();

    Button btnPrivate;
    Button btnPublic;

    TextView todayText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);
        recyclerView = root.findViewById(R.id.recycler2);
        todayText = root.findViewById(R.id.tv_selDate);
        Date today = new Date();
        String todayString = CalendarPickerDialog.makeDateString(today.getDay(), today.getMonth(), today.getYear());

        todayText.setText(todayString);


        initRecycler(root);


        //// Init Calendar View ////
        CustomCalendarView calendarView = ((CustomCalendarView) root.findViewById(R.id.calendar_view));
        calendarView.setEventHandler(new CustomCalendarView.EventHandler() {
            @Override
            public void onDayLongPress(Date date) {
                // show returned day
                DateFormat df = SimpleDateFormat.getDateInstance();
//                Toast.makeText(getContext(), df.format(date), Toast.LENGTH_SHORT).show();
                selectedDate = date;


                Calendar now = Calendar.getInstance();
                now.setTime(selectedDate);
                now.set(Calendar.HOUR, 0);
                now.set(Calendar.MINUTE, 0);
                now.set(Calendar.SECOND, 0);
                now.set(Calendar.HOUR_OF_DAY, 0);

                selectedDate = now.getTime();
                Toast.makeText(getContext(), selectedDate.toString(), Toast.LENGTH_SHORT).show();
                updateRecycler(root);
            }
        });


        //// Init GET firebase ////
        calendarView.invokeFirebaseEvent(calendarView);


        final String[] newTitle = {""};
        final String[] newDescription = {""};;
        final Boolean[] isPrivate = {false};
        Date newDate= new Date();


        //// Init Components ////
        FloatingActionButton dialogButton = root.findViewById(R.id.fab);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
                View bottomSheetView = inflater.inflate(R.layout.fragment_bottom_dialog, root.findViewById(R.id.bottomDialogContainer));
                EditText titleDialog = bottomSheetView.findViewById(R.id.title_dialog);
                EditText descriptionDialog = bottomSheetView.findViewById(R.id.description_dialog);


                titleDialog.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}
                    @Override
                    public void afterTextChanged(Editable editable) {
                        newTitle[0] = editable.toString();
                    }
                });
                descriptionDialog.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}
                    @Override
                    public void afterTextChanged(Editable editable) {
                        newDescription[0] = editable.toString();
                    }
                });

                Button btnDeadline = bottomSheetView.findViewById(R.id.btn_deadline);
                CalendarPickerDialog.initDatePicker(btnDeadline.getContext(), btnDeadline);
                btnDeadline.setText(CalendarPickerDialog.getTodayDate());

                btnPrivate = bottomSheetView.findViewById(R.id.btn_private_sel);
                btnPublic = bottomSheetView.findViewById(R.id.btn_public_sel);

                btnPublic.setBackgroundColor(Color.RED);
                btnPrivate.setBackgroundColor(Color.GRAY);
                btnPrivate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isPrivate[0] = true;
                        btnPrivate.setBackgroundColor(Color.YELLOW);
                        btnPublic.setBackgroundColor(Color.GRAY);
                    }
                });

                btnPublic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isPrivate[0] = false;
                        btnPublic.setBackgroundColor(Color.RED);
                        btnPrivate.setBackgroundColor(Color.GRAY);
                    }
                });


                bottomSheetView.findViewById(R.id.buttonDialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Date newDate = new Date(btnDeadline.getText().toString());
                        Date today = new Date();
                        long daysBetween = TimeUnit.DAYS.convert(newDate.getTime()-today.getTime(), TimeUnit.MILLISECONDS);
                        String newDaysLeft = String.valueOf(daysBetween);

                        ////  Check Validation ////
                        if (newTitle[0].equals("")) {
                            Toast.makeText(getContext(),R.string.error_emptyTitle, Toast.LENGTH_SHORT).show();
                        } else if (newDescription[0].equals("")) {
                            Toast.makeText(getContext(),R.string.error_emptyDescription, Toast.LENGTH_SHORT).show();
                        } else if (newDaysLeft.charAt(0) == '-') {
                            Toast.makeText(getContext(),R.string.error_wrongDate, Toast.LENGTH_SHORT).show();
                        } else {
                            DatabaseReference firebase = FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link)).getReference().child("events");
                            String uuid = UUID.randomUUID().toString();
                            Event event = new Event(newTitle[0], newDescription[0], newDate.toString(), newDaysLeft, uuid, isPrivate[0], false);

                            //// Post to Firebase TODO: put in another function ////
                            firebase.child(uuid).child("title").setValue(event.getTitle());
                            firebase.child(uuid).child("description").setValue(event.getDescription());
                            firebase.child(uuid).child("deadline").setValue(event.getDeadline());
                            firebase.child(uuid).child("daysleft").setValue(event.getDaysLeft());
                            firebase.child(uuid).child("uid").setValue(event.getUid());
                            firebase.child(uuid).child("isprivate").setValue(event.getIsPrivate());
                            firebase.child(uuid).child("iscomplete").setValue(event.getIsComplete());
                            firebase.child(uuid).child("priority").setValue(event.getDaysLeft());

                            Toast.makeText(getContext(), "added!", Toast.LENGTH_SHORT).show();
                            bottomSheetDialog.dismiss();

                            updateRecycler(root);

                            // Reload Page w new fragment.
                            Fragment f;
                            f = new CalendarFragment();
                            FragmentTransaction ft2 =   getFragmentManager().beginTransaction();
                            ft2.replace(R.id.framelayout_line,f);
                            ft2.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            ft2.commit();
                        }


                    }
                });

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });

        return root;
    }


    private void btnPrivacySettingOnClickListeners() {

    }

    private void postToFireBase(View bottomSheetView, String title, String description, String daysleft) {
//        bottomSheetView.findViewById(R.id.buttonDialog).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DatabaseReference firebase = FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link)).getReference().child("events");
//                String uuid = UUID.randomUUID().toString();
//                Event event = new Event(title, description, "deadline", daysleft, uuid, isPrivate, isComplete);
//                firebase.child(uuid).child("title").setValue(event.getTitle());
//                firebase.child(uuid).child("description").setValue(event.getDescription());
//                firebase.child(uuid).child("deadline").setValue(event.getDeadline());
//                firebase.child(uuid).child("daysleft").setValue(event.getDaysLeft());
//                firebase.child(uuid).child("uid").setValue(event.getUid());
//
//                Toast.makeText(getContext(), "added!", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void initRecycler(View root) {
        // Create a instance of the database and get its reference
        mbase = (DatabaseReference) FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link)).getReference().child("events");
        Query query = mbase.orderByChild("deadline").equalTo(selectedDate.toString());

        FirebaseRecyclerOptions<Event> options
                = new FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(query, Event.class)
                .build();

        // FIXME: must just be month, day year la, use the date formatter than we will roll from there

        recyclerView = root.findViewById(R.id.recycler2);
        recyclerView.setNestedScrollingEnabled(false);

        // To display the Recycler view linearly
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // It is a class provide by the FirebaseUI to make a query in the database to fetch appropriate data

        // Connecting object of required Adapter class to the Adapter class itself
        recyclerAdapter = new EventAdapter(options);
        // Connecting Adapter class with the Recycler view*/
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void updateRecycler(View root) {
        // Create a instance of the database and get its reference
        mbase = (DatabaseReference) FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link)).getReference().child("events");
        Query query = mbase.orderByChild("deadline").equalTo(selectedDate.toString());

        // FIXME: must just be month, day year la, use the date formatter than we will roll from there
        FirebaseRecyclerOptions<Event> options
                = new FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(query, Event.class)
                .build();

        recyclerAdapter.updateOptions(options);
        recyclerView.setAdapter(recyclerAdapter);

        String selDateString = CalendarPickerDialog.makeDateString(selectedDate.getDate(), selectedDate.getMonth() + 1, selectedDate.getYear() + 1900);
        todayText.setText(selDateString);
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