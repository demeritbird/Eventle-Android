package com.example.sampleproject.ViewModels;

//import android.arch.lifecycle.LiveData;
//import android.arch.lifecycle.MutableLiveData;
//import android.arch.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


//import androidx.annotation.NonNull;
//import androidx.lifecycle.LiveData;
//

import com.example.sampleproject.Data.Repository.FirebaseDatabaseRepository;
import com.example.sampleproject.Data.Repository.MemberRepository;
import com.example.sampleproject.Models.Member;

//import com.example.sampleproject.domain.Member;

import java.util.List;

public class MemberViewModel extends ViewModel {

  private MutableLiveData<List<Member>> members;
  private MemberRepository repository = new MemberRepository();

  public LiveData<List<Member>> getMembers() {
    if (members == null) {
      members = new MutableLiveData<>();
      loadMembers();
    }
    return members;
  }

  @Override
  protected void onCleared() {
    repository.removeListener();
  }

  private void loadMembers() {
    repository.addListener(new FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Member>() {
      @Override
      public void onSuccess(List<Member> result) {
        members.setValue(result);
      }

      @Override
      public void onError(Exception e) {
        members.setValue(null);
      }
    });
  }
}

//import androidx.annotation.NonNull;
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.ViewModel;
//
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//public class MemberViewModel extends ViewModel {
//  private static final DatabaseReference memberNameRef =
//          FirebaseDatabase.getInstance(String.valueOf(R.string.firebase_link)).getReference().child("members");
//
//  private final FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(memberNameRef);
//
//  @NonNull
//  public LiveData<DataSnapshot> getDataSnapshotLiveData() {
//    return liveData;
//  }
//}