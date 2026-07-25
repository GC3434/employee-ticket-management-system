package com.ugc.EmpMngmntAndTktingSys.exception;

public class InvalidTicketStateException extends RuntimeException{
    public InvalidTicketStateException(String message){
        super(message);
    }
}
