package com.example.sampleproject.Data.Mapper;


import com.example.sampleproject.Models.Member;
import com.example.sampleproject.data.entity.MemberEntity;

public class MemberMapper extends FirebaseMapper<MemberEntity, Member> {

  public Member map(MemberEntity memberEntity) {
    Member member = new Member();
    member.setName(memberEntity.getName());
    member.setId(memberEntity.getId());

    return member;
  }
}
