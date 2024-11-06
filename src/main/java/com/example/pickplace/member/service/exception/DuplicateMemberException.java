package com.example.pickplace.member.service;

public class DuplicateMemberException  extends RuntimeException{
    public DuplicateMemberException(String message) { super(message); }
}
