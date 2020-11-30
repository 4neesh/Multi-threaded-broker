package com.application.exception;

/**
 * MessageType exceptions extend RuntimeException for incorrect MessageType arguments.
 * @author aneesh
 */
public class MessageTypeException extends RuntimeException{
    public MessageTypeException(String message) {
        super(message);
    }
}
