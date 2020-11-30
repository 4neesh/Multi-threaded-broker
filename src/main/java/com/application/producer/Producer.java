package com.application.producer;

import com.application.core.MessageBroker;
import com.application.core.enums.MessageType;

/**
 * Producer interface extends {@code Runnable} to launch producer thread.
 */
public interface Producer extends Runnable {

    /**
     * Publish message into the queue.
     * @param type the queue type to send a message into.
     * @param message the message to be sent.
     * @param <T> the class of message sent to the queue.
     */
    <T> void publish(MessageType type, T message);

    /**
     * Obtain the message broker for the producer
     * @return producer message broker
     */
    MessageBroker getMessageBroker();

    /**
     * Obtain the minimum number of messages a producer must send.
     * @return minimum number of messages produced.
     */
    int getMinimumMessagesPerProducer();
}
