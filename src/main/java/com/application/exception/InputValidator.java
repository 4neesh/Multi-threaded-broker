package com.application.exception;

import com.application.core.MessageBroker;
import com.application.core.enums.MessageType;

/**
 * Input validation is carried out on method arguments.
 * @author aneesh
 */
public class InputValidator {

    /**
     * Validate the broker argument passed in is not null.
     * @param broker The MessageBroker to be validated.
     */
    public static void validateBroker(MessageBroker broker) {
        if(broker == null){
            throw new BrokerException("Broker cannot be null");
        }
    }

    /**
     *Validate the published message to the queue.
     * @param messageType MessageType to be validated.
     * @param message the message content.
     * @param <T> The type of the Message Object.
     */
    public static <T> void validatePublishedMessage(MessageType messageType, T message) {
        if(messageType == null){
            throw new MessageTypeException("Published message MessageType cannot be null.");
        }
        if(message == null){
            throw new BrokerException("Published message cannot be null");
        }
    }

    /**
     * Validate the message type is not null for consumed MessageType.
     * @param messageType MessageType to be validated.
     */
    public static void validateMessageType(MessageType messageType) {
        if(messageType == null){
            throw new MessageTypeException("Consumed MessageType cannot be null.");
        }
    }
}
