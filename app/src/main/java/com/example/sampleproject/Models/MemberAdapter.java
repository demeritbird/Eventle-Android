package com.example.sampleproject.Models;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sampleproject.Activities.ApplicationActivity;
import com.example.sampleproject.Activities.EntryActivity;
import com.example.sampleproject.R;

import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

  private final List<Member> list;

  public MemberAdapter(List<Member> list) {
    this.list = list;
  }

  @Override
  public MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_entry_button, parent, false);
    return new MemberViewHolder(view);
  }

  @Override
  public void onBindViewHolder(MemberViewHolder holder, int position) {
    holder.bind(list.get(position));
    System.out.println(list.get(position));

    holder.entryButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        Intent intentSub = new Intent(v.getContext(), ApplicationActivity.class);
        int pos = holder.getAdapterPosition();
        String id = list.get(pos).getId();
        System.out.println("//////////////");
        System.out.println(id);
        System.out.println(id == "1");
        System.out.println(id == "2");
        System.out.println(id.getClass());
        System.out.println("//////////////");
        intentSub.putExtra("id", id);


        if (id.equals("1")) {
          intentSub.putExtra("otherId", "2");
          intentSub.putExtra("username", holder.entryButton.getText());
        } else if (id.equals("2")) {
          intentSub.putExtra("otherId", "1");
          intentSub.putExtra("username", holder.entryButton.getText());
        } else {
          intentSub.putExtra("username", "sad face :C");
        }

        v.getContext().startActivity(intentSub);
      }
    });
  }

  @Override
  public int getItemCount() {
    return list.size();
  }

  public static class MemberViewHolder extends RecyclerView.ViewHolder {

    Button entryButton;


    public MemberViewHolder(@NonNull View itemView) {
      super(itemView);
      entryButton = itemView.findViewById(R.id.entry_button);
    }

    public void bind(Member member) {
      entryButton.setText(member.getName());
    }
  }

}
