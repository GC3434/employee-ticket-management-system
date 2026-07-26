package com.ugc.EmpMngmntAndTktingSys.exception;

public class TicketAlreadyAssignedException extends RuntimeException{
    public TicketAlreadyAssignedException(String message){
        super(message);
    }
}
