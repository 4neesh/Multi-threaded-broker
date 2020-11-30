package com.application.consumer;

import com.application.core.enums.MessageType;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Consumer interface extends {@code Runnable} to launch consumer thread.
 * @author aneesh
 */
public interface Consumer extends Runnable {

    /**
     * consume a message of type {@code type}
     * @param type message type defines the queue to be consumed.
     * @param <T> Object to be returned from the queue.
     * @return message from the queue of type {@code <T>}
     */
    <T> T consume(MessageType type);

    /**
     * Obtain the number of poison pills within the Consumer that are to be obtained.
     * @return count of the poison pills
     */
    AtomicInteger getPoisonPillCounter();
}
