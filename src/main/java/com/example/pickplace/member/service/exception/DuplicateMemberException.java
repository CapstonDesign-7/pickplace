package com.example.pickplace.member.service.exception;

public class DuplicateMemberException  extends RuntimeException{
    public DuplicateMemberException(String message) { super(message); }
}
