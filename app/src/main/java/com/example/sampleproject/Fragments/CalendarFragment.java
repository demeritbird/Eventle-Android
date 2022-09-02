package com.example.sampleproject.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sampleproject.Components.CalendarPickerDialog;
import com.example.sampleproject.Components.CustomCalendarView;
import com.example.sampleproject.Helper.FirebaseHelper;
import com.example.sampleproject.Helper.MiscHelper;
import com.example.sampleproject.Helper.TimeHelper;
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

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class CalendarFragment extends Fragment {
    public RecyclerView recyclerView;
    public static EventAdapter recyclerAdapter; // Create Object of the Adapter class
    DatabaseReference mbase; // Create object of the Firebase Realtime Database
    Date selectedDate = CustomCalendarView.dateSelected;

    Button btnPrivate;
    Button btnPublic;
    Button btnDeadline;

    TextView todayText;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);
        recyclerView = root.findViewById(R.id.recycler_calendar);
        todayText = root.findViewById(R.id.tv_selDate);
        TextView errorMsg = root.findViewById(R.id.tv_error_msg_calendarr);


        //// Receive Intents ////
        Bundle resultIntent = getActivity().getIntent().getExtras();
        String id = resultIntent.getString("id", "1");
        String otherId = resultIntent.getString("otherId", "2");

        Date today = new Date();
        String todayString = CalendarPickerDialog.makeDateString(today.getDate(), today.getMonth()+1, today.getYear()+1900);
        todayText.setText(todayString);

        initRecycler(root, id, errorMsg);

        //// Init Calendar View ////
        CustomCalendarView calendarView = ((CustomCalendarView) root.findViewById(R.id.calendar_view));
        calendarView.setEventHandler(new CustomCalendarView.EventHandler() {
            @Override
            public void onDayLongPress(Date date) {
                selectedDate = date;

                Calendar nowCal = TimeHelper.setDateTimeToZero(selectedDate);
                selectedDate = nowCal.getTime();

                updateRecycler(root, id, errorMsg);
            }
        });

        //// Init GET firebase ////
        calendarView.invokeFirebaseEvent(calendarView);

        final String[] newTitle = {""};
        final String[] newDescription = {""};;
        final Boolean[] isPrivate = {false};

        //// Init Components ////
        FloatingActionButton dialogButton = root.findViewById(R.id.fab);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
                View bottomSheetView = inflater.inflate(R.layout.fragment_bottom_dialog, root.findViewById(R.id.bottomDialogContainer));
                EditText titleDialog = bottomSheetView.findViewById(R.id.et_title_dialog);
                EditText descriptionDialog = bottomSheetView.findViewById(R.id.et_description_dialog);
                btnPrivate = bottomSheetView.findViewById(R.id.btn_private_sel);
                btnPublic = bottomSheetView.findViewById(R.id.btn_public_sel);
                btnDeadline = bottomSheetView.findViewById(R.id.btn_deadline);

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

                CalendarPickerDialog.initDatePicker(btnDeadline.getContext(), btnDeadline);
                btnDeadline.setText(CalendarPickerDialog.getTodayDate());

                MiscHelper.selectPrivate(btnPrivate, btnPublic);
                btnPrivate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isPrivate[0] = true;
                        MiscHelper.selectPrivate(btnPrivate, btnPublic);
                    }
                });

                btnPublic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isPrivate[0] = false;
                        MiscHelper.selectPublic(btnPublic, btnPrivate);
                    }
                });

                // FIXME: Refactor here //
                bottomSheetView.findViewById(R.id.btn_submit_event).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Date newDate = new Date(btnDeadline.getText().toString());
                        Date today = new Date();

                        Calendar todayCal = TimeHelper.setDateTimeOneDown(today);
                        Date newToday = todayCal.getTime();

                        long newDaysLeft = TimeHelper.calcDaysBetween(newDate,newToday);

                        ////  Check Validation ////
                        if (newTitle[0].equals("")) {
                            Toast.makeText(getContext(),R.string.error_emptyTitle, Toast.LENGTH_SHORT).show();
                        } else if (newDescription[0].equals("")) {
                            Toast.makeText(getContext(),R.string.error_emptyDescription, Toast.LENGTH_SHORT).show();
                        } else {
                            DatabaseReference firebaseMembers = FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link)).getReference()
                                                                                   .child("members");
                            String uuid = UUID.randomUUID().toString();
//                            Event event = new Event(newTitle[0], newDescription[0], newDate.toString(), (int) newDaysLeft, uuid, isPrivate[0], false);
//
//                            if (event.getIsPrivate()) {
//                                FirebaseHelper.postToFirebase(event, firebaseMembers, id);
//                            } else {
//                                FirebaseHelper.postToFirebase(event, firebaseMembers, id);
//                                FirebaseHelper.postToFirebase(event, firebaseMembers, otherId);
//                            }
                            FirebaseHelper.submitEventOnClick(new Event(newTitle[0], newDescription[0], newDate.toString(), (int) newDaysLeft, uuid, isPrivate[0] , false), firebaseMembers, id, otherId, true );

                            Toast.makeText(getContext(), R.string.success_eventAdded, Toast.LENGTH_SHORT).show();
                            bottomSheetDialog.dismiss();

                            updateRecycler(root,id,errorMsg);
                            refreshFragment();
                        }
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }

        });

        return root;
    }

    private void refreshFragment() {
        Fragment fragment;
        fragment = new CalendarFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.framelayout_area,fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    private void initRecycler(View root, String selId, TextView errorMsg) {
        // Create a instance of the database and get its reference

        Calendar nowCal = TimeHelper.setDateTimeToZero(selectedDate);

        Date changed = nowCal.getTime();

        mbase = (DatabaseReference) FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link)).getReference()
                                                    .child("members").child("member"+selId).child("events");
        Query query = mbase.orderByChild("deadline").equalTo(changed.toString());


        FirebaseRecyclerOptions<Event> options
                = new FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(query, Event.class)
                .build();

        recyclerView = root.findViewById(R.id.recycler_calendar);
        recyclerView.setNestedScrollingEnabled(false);

        // To display the Recycler view linearly
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // It is a class provide by the FirebaseUI to make a query in the database to fetch appropriate data

        // Connecting object of required Adapter class to the Adapter class itself
        recyclerAdapter = new EventAdapter(options);
        //checkEmptyList(query, errorMsg);
        MiscHelper.checkEmptyList(query, errorMsg);
        // Connecting Adapter class with the Recycler view*/
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void updateRecycler(View root, String selId, TextView errorMsg) {
        mbase = (DatabaseReference) FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link)).getReference()
                .child("members").child("member"+selId).child("events");
        Query query = mbase.orderByChild("deadline").equalTo(selectedDate.toString());

        FirebaseRecyclerOptions<Event> options
                = new FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(query, Event.class)
                .build();

        recyclerAdapter.updateOptions(options);
        MiscHelper.checkEmptyList(query, errorMsg);
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