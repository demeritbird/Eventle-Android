package com.example.sampleproject.data.entity;

public class MemberEntity {

  public String name;
  public String id;

  public MemberEntity() { }

  public MemberEntity(String name, String id) {
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

