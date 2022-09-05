package com.example.sampleproject.Models;



import androidx.lifecycle.ViewModel;

public class Member {
  public String name;
  public String id;

  public Member() { }

  public Member(String name, String id) {
    this.name = name;
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

}
