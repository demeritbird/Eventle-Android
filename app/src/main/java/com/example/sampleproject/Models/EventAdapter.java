package com.example.sampleproject.Models;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sampleproject.Components.CalendarPickerDialog;
import com.example.sampleproject.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class EventAdapter extends FirebaseRecyclerAdapter<Event, EventAdapter.eventsViewHolder> {


    public EventAdapter(@NonNull FirebaseRecyclerOptions<Event> options) {
        super(options);
    }

    @Override
    protected void
    onBindViewHolder(@NonNull eventsViewHolder holder, int position, @NonNull Event model) {
        holder.title.setText(model.getTitle());
        holder.description.setText(model.getDescription());

        String deadlineString = model.getDeadline();
        Date deadlineDate = new Date(deadlineString);
        int day = deadlineDate.getDate();
        int month = deadlineDate.getMonth();
        int year = deadlineDate.getYear();
        holder.deadline.setText(CalendarPickerDialog.makeDateString(day, month + 1, year + 1900));

        Date today = new Date();
        long daysBetween = TimeUnit.DAYS.convert(Math.abs(deadlineDate.getTime() - today.getTime()), TimeUnit.MILLISECONDS);
        holder.daysleft.setText(String.valueOf(daysBetween));

        Boolean isCompleted;
        holder.uid = model.getUid();
        holder.isPrivate = model.getIsPrivate();
        holder.isComplete = model.getIsComplete();
    }

    @NonNull
    @Override
    public eventsViewHolder
    onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_event_card, parent, false);

        return new eventsViewHolder(view);
    }


    class eventsViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, deadline, daysleft;
        String uid;
        Boolean isPrivate, isComplete;

        public eventsViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            deadline = itemView.findViewById(R.id.deadline);
            daysleft = itemView.findViewById(R.id.days_left);

            Button editButton = itemView.findViewById(R.id.check_button);
            CheckBox checkBox = (CheckBox) itemView.findViewById(R.id.cb_complete);

            if (isComplete == null || isComplete) {
                checkBox.setChecked(true);
            }

            onClickEdit(editButton);
        }

        private void onClickEdit(Button editButton) {
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(view.getContext(), R.style.BottomSheetDialogTheme);
                    View bottomSheetView = LayoutInflater.from(bottomSheetDialog.getContext()).inflate(R.layout.fragment_bottom_dialog, null);

                    // Text Components
                    TextView eventTitle = bottomSheetView.findViewById(R.id.event_title);
                    eventTitle.setText("Edit Event");
                    EditText titleDialog = bottomSheetView.findViewById(R.id.title_dialog);
                    titleDialog.setText(title.getText());
                    EditText descriptionDialog = bottomSheetView.findViewById(R.id.description_dialog);
                    descriptionDialog.setText(description.getText());
                    String left = "5";

                    // Deadline Date Dialog
                    Button btnDeadline = bottomSheetView.findViewById(R.id.btn_deadline);
                    CalendarPickerDialog.initDatePicker(btnDeadline.getContext(), btnDeadline);
                    btnDeadline.setText(CalendarPickerDialog.getTodayDate());
                    Date newDate = new Date(btnDeadline.getText().toString());

                    Button privateSel = bottomSheetView.findViewById(R.id.btn_private_sel);
                    Button publicSel = bottomSheetView.findViewById(R.id.btn_public_sel);

                    Event event = new Event(titleDialog.getText().toString(), descriptionDialog.getText().toString(), newDate.toString(), left, uid, isPrivate, isComplete);
                    if (isPrivate) {
                        privateSel.setBackgroundColor(Color.YELLOW);
                        publicSel.setBackgroundColor(Color.GRAY);
                    } else {
                        publicSel.setBackgroundColor(Color.RED);
                        privateSel.setBackgroundColor(Color.GRAY);
                    }

                    DatabaseReference firebase = FirebaseDatabase.getInstance(view.getContext().getResources().getString(R.string.firebase_link)).getReference().child("events").child(uid);

                    // Refactor Code here
                    btnPrivacySelect(privateSel, publicSel);

                    // Button //
                    postToFireBase(bottomSheetView, event, firebase);
                    bottomSheetDialog.setContentView(bottomSheetView);
                    bottomSheetDialog.show();
                }
            });
        }

        private void btnPrivacySelect(Button privateSel, Button publicSel) {
            privateSel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isPrivate = true;
                    privateSel.setBackgroundColor(Color.YELLOW);
                    publicSel.setBackgroundColor(Color.GRAY);

                }
            });

            publicSel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isPrivate = false;
                    publicSel.setBackgroundColor(Color.RED);
                    privateSel.setBackgroundColor(Color.GRAY);
                }
            });
        }
    }

    private void postToFireBase(View bottomSheetView, Event event, DatabaseReference firebase) {
        bottomSheetView.findViewById(R.id.buttonDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebase.child("title").setValue(event.getTitle());
                firebase.child("description").setValue(event.getDescription());
                firebase.child("deadline").setValue(event.getDeadline());
                firebase.child("daysleft").setValue(event.getDaysLeft());
                firebase.child("uid").setValue(event.getUid());
                firebase.child("isprivate").setValue(event.getIsPrivate());
                firebase.child("iscomplete").setValue(event.getIsComplete());
            }
        });
    }
}
