package com.example.sampleproject.Models;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class EventAdapter extends FirebaseRecyclerAdapter<Event, EventAdapter.eventsViewHolder> {


    public EventAdapter(@NonNull FirebaseRecyclerOptions<Event> options) {
        super(options);
    }

    @Override
    protected void
    onBindViewHolder(@NonNull eventsViewHolder holder, int position, @NonNull Event model) {
        holder.title.setText(model.getTitle());
        holder.description.setText(model.getDescription());

        Date deadlineDate = new Date(model.getDeadline());
        int day = deadlineDate.getDate();
        int month = deadlineDate.getMonth();
        int year = deadlineDate.getYear();
        holder.deadline.setText(CalendarPickerDialog.makeDateString(day, month + 1, year + 1900));

        holder.daysleft.setText(model.getDaysLeft());
        Boolean isCompleted;
        holder.uid = model.getUid();
        holder.isPrivate = model.getIsPrivate();
        holder.isComplete = model.getIsComplete();

        if (model.getIsComplete()) {
            holder.completeButton.setBackgroundColor(Color.RED);
            holder.yes.setBackgroundColor(Color.GRAY);
        } else {
            holder.completeButton.setBackgroundColor(Color.GRAY);
        }


    }

    @NonNull
    @Override
    public eventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_event_card, parent, false);

        return new eventsViewHolder(view);
    }


    class eventsViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, deadline, daysleft;
        String uid;
        Boolean isPrivate, isComplete;
        View yes =itemView.findViewById(R.id.event_background);

        Button completeButton = itemView.findViewById(R.id.btn_isComplete);
        Button editButton = itemView.findViewById(R.id.btn_editEvent);
        Button delButton = itemView.findViewById(R.id.btn_delEvent);

        public eventsViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            deadline = itemView.findViewById(R.id.deadline);
            daysleft = itemView.findViewById(R.id.days_left);



            onClickEdit(editButton);
            onClickDelete(delButton);

            completeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference firebase = FirebaseDatabase.getInstance(view.getContext().getResources().getString(R.string.firebase_link)).getReference().child("events").child(uid);

                    if (isComplete) {
                        System.out.println("event set to false");
                        firebase.child("iscomplete").setValue(false);
                        completeButton.setBackgroundColor(Color.GRAY);
                    } else {
                        System.out.println("event set to true");
                        firebase.child("iscomplete").setValue(true);
                        completeButton.setBackgroundColor(Color.RED);
                    }



                }
            });

        }

        private void onClickDelete(Button delButton) {
            delButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference firebase = FirebaseDatabase.getInstance(view.getContext().getResources().getString(R.string.firebase_link)).getReference().child("events").child(uid);
                    firebase.removeValue();
                }
            });
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
