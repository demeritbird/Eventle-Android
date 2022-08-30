package com.example.sampleproject.Models;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    public eventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_event_card, parent, false);

        return new eventsViewHolder(view);
    }


    class eventsViewHolder extends RecyclerView.ViewHolder   implements View.OnClickListener{
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
            final String[] newTitle = {title.getText().toString()};
            final String[] newDescription = {description.getText().toString()};

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickEdit(editButton, newTitle, newDescription);
                }
            });
            delButton.setOnClickListener(this);
            completeButton.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.btn_isComplete) {
                onClickComplete(completeButton);
            }  else if(view.getId() == R.id.btn_delEvent) {
                onClickDelete(delButton);
            }
        }


        private void onClickComplete(Button completeButton) {
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

        private void onClickEdit(Button editButton, String[] newTitle, String[] newDescription) {
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(view.getContext(), R.style.BottomSheetDialogTheme);
                    View bottomSheetView = LayoutInflater.from(bottomSheetDialog.getContext()).inflate(R.layout.fragment_bottom_dialog, null);
                    newTitle[0] = title.getText().toString();
                    newDescription[0] = description.getText().toString();


                    // Text Components


                    // Edit Event //
                    TextView eventTitle = bottomSheetView.findViewById(R.id.event_title);
                    eventTitle.setText(R.string.edit_event);

                    // Title //
                    EditText titleDialog = bottomSheetView.findViewById(R.id.title_dialog);
                    titleDialog.setText(title.getText());


                    titleDialog.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {}
                        @Override
                        public void afterTextChanged(Editable editable) {
                            newTitle[0] = editable.toString();
                            System.out.println(newTitle[0]);
                        }
                    });

                    // Description
                    EditText descriptionDialog = bottomSheetView.findViewById(R.id.description_dialog);
                    descriptionDialog.setText(description.getText());


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


                    // Deadline Date Dialog //
                    Button btnDeadline = bottomSheetView.findViewById(R.id.btn_deadline);
                    btnDeadline.setText(deadline.getText().toString());
                    CalendarPickerDialog.initDatePicker(btnDeadline.getContext(), btnDeadline);


                    // privacy //
                    Boolean newPrivacy;
                    Button privateSel = bottomSheetView.findViewById(R.id.btn_private_sel);
                    Button publicSel = bottomSheetView.findViewById(R.id.btn_public_sel);
                    if (isPrivate) {
                        privateSel.setBackgroundColor(Color.YELLOW);
                        publicSel.setBackgroundColor(Color.GRAY);
                    } else {
                        publicSel.setBackgroundColor(Color.RED);
                        privateSel.setBackgroundColor(Color.GRAY);
                    }
                    btnPrivacySelect(privateSel, publicSel);


                    DatabaseReference firebase = FirebaseDatabase.getInstance(view.getContext().getResources().getString(R.string.firebase_link)).getReference().child("events").child(uid);


                    // Button //
                    bottomSheetView.findViewById(R.id.buttonDialog).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Date newDate = new Date(btnDeadline.getText().toString());
                            Date today = new Date();
                            long daysBetween = TimeUnit.DAYS.convert(newDate.getTime()-today.getTime(), TimeUnit.MILLISECONDS);
                            String newDaysLeft = String.valueOf(daysBetween);

                            ////  Check Validation ////
                            if (newTitle[0].equals("")) {
                                Toast.makeText(bottomSheetDialog.getContext(),R.string.error_emptyTitle, Toast.LENGTH_SHORT).show();
                            } else if (newDescription[0].equals("")) {
                                Toast.makeText(bottomSheetDialog.getContext(),R.string.error_emptyDescription, Toast.LENGTH_SHORT).show();
                            } else if (newDaysLeft.charAt(0) == '-') {
                                Toast.makeText(bottomSheetDialog.getContext(),R.string.error_wrongDate, Toast.LENGTH_SHORT).show();
                            } else {
                                Event event = new Event(newTitle[0], newDescription[0], newDate.toString(), newDaysLeft, uid, isPrivate, isComplete);
                                firebase.child("title").setValue(event.getTitle());
                                firebase.child("description").setValue(event.getDescription());
                                firebase.child("deadline").setValue(event.getDeadline());
                                firebase.child("daysleft").setValue(event.getDaysLeft());
                                firebase.child("uid").setValue(event.getUid());
                                firebase.child("isprivate").setValue(event.getIsPrivate());
                                firebase.child("priority").setValue(event.getDaysLeft());
                                firebase.child("iscomplete").setValue(event.getIsComplete());
                                bottomSheetDialog.dismiss();
                            }
                        }
                    });

                    //postToFireBase(bottomSheetView, event, firebase);
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
//        bottomSheetView.findViewById(R.id.buttonDialog).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                firebase.child("title").setValue(event.getTitle());
//                firebase.child("description").setValue(event.getDescription());
//                firebase.child("deadline").setValue(event.getDeadline());
//                firebase.child("daysleft").setValue(event.getDaysLeft());
//                firebase.child("uid").setValue(event.getUid());
//                firebase.child("isprivate").setValue(event.getIsPrivate());
//                firebase.child("iscomplete").setValue(event.getIsComplete());
//            }
//        });
    }
}
