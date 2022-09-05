package com.example.sampleproject.Data.Repository;

import com.example.sampleproject.Models.Member;
import com.example.sampleproject.Data.Mapper.MemberMapper;


public class MemberRepository extends FirebaseDatabaseRepository<Member> {

  public MemberRepository()  {
    super(new MemberMapper());
  }

  @Override
  protected String getRootNode() {
    return "members";
  }
}
