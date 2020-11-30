package com.application.exception;

/**
 * Broker exception extends RuntimeException for exceptions when using the MessageBrokerImpl.
 * @author aneesh
 */
public class BrokerException extends RuntimeException{
    public BrokerException(String message) {
        super(message);
    }
}
