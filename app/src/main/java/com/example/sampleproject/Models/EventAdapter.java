package com.example.sampleproject.Models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sampleproject.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class EventAdapter extends FirebaseRecyclerAdapter<
        Event, EventAdapter.eventsViewHolder> {

    public EventAdapter(@NonNull FirebaseRecyclerOptions<Event> options) {
        super(options);
    }

    @Override
    protected void
    onBindViewHolder(@NonNull eventsViewHolder holder, int position, @NonNull Event model) {
        holder.title.setText(model.getTitle());
        holder.description.setText(model.getDescription());
        holder.deadline.setText(model.getDeadline());
        holder.daysleft.setText(model.getDaysLeft());
    }


    @NonNull
    @Override
    public eventsViewHolder
    onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_card, parent, false);
        return new eventsViewHolder(view);
    }


    class eventsViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, deadline, daysleft;

        public eventsViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            deadline = itemView.findViewById(R.id.deadline);
            daysleft = itemView.findViewById(R.id.days_left);

            Button check = itemView.findViewById(R.id.check_button);
            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println(title.getText());
                    System.out.println(description.getText());
                    System.out.println(deadline.getText());
                    System.out.println(daysleft.getText());
                }
            });
        }
    }
}
