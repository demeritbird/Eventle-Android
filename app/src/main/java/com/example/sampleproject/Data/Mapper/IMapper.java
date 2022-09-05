package com.example.sampleproject.Data.Mapper;


public interface IMapper<From, To> {
  To map(From from);
}