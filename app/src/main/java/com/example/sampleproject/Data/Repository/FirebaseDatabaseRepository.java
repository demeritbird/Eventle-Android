package com.example.sampleproject.Data.Repository;


import com.example.sampleproject.Data.Mapper.FirebaseMapper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public abstract class FirebaseDatabaseRepository<Model> {

  protected DatabaseReference databaseReference;
  protected FirebaseDatabaseRepositoryCallback<Model> firebaseCallback;
  private BaseValueEventListener listener;
  private FirebaseMapper mapper;

  protected abstract String getRootNode();

  public FirebaseDatabaseRepository(FirebaseMapper mapper) {
    // databaseReference = FirebaseDatabase.getInstance().getReference(getRootNode());

    databaseReference = FirebaseDatabase.getInstance("https://scheduleapp-3ebb7-default-rtdb.asia-southeast1.firebasedatabase.app").getReference(getRootNode());
    this.mapper = mapper;
  }

  public void addListener(FirebaseDatabaseRepositoryCallback<Model> firebaseCallback) {
    this.firebaseCallback = firebaseCallback;
    listener = new BaseValueEventListener(mapper, firebaseCallback);
    databaseReference.addValueEventListener(listener);
  }

  public void removeListener() {
    databaseReference.removeEventListener(listener);
  }

  public interface FirebaseDatabaseRepositoryCallback<T> {
    void onSuccess(List<T> result);

    void onError(Exception e);
  }
}