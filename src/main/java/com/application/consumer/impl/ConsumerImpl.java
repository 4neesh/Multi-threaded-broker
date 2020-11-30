package com.application.consumer.impl;

import com.application.consumer.Consumer;
import com.application.core.MessageBroker;
import com.application.core.enums.MessageType;
import com.application.exception.InputValidator;
import com.application.producer.PoisonPill;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Consumer implementation of {@code Consumer}
 * @author aneesh
 */
public class ConsumerImpl implements Consumer {

    private MessageBroker broker;
    private volatile static AtomicInteger poisonPillCounter ;
    private static Map<MessageType, Integer> poisonMap;

    /**
     * Constructor instantiates the poison map and broker.
     * @param broker broker the consumer queries for messages.
     * @param numberOfProducers used to define the poison pills to be counted.
     */
    public ConsumerImpl(MessageBroker broker, int numberOfProducers){

        InputValidator.validateBroker(broker);
        populatePoisonMapWithValues(numberOfProducers);
        setPoisonPillCounter(new AtomicInteger(MessageType.values().length * numberOfProducers));
        this.broker = broker;
    }

    private void setPoisonPillCounter(AtomicInteger counter) {
        poisonPillCounter = counter;
    }

    public <T> T consume(MessageType type) {
        InputValidator.validateMessageType(type);
        return broker.consume(type);
    }

    /**
     * Start the runnable thread by consuming messages from the broker until all the poison pills
     * have been consumed across the consumer instances.
     */
    public void run() {
        MessageType[] values = MessageType.values();

        while(producersKeepSendingMessages()) {

            int random = findRandomQueueWithMessages(values);

            Object message = consume(values[random]);
            if (message instanceof PoisonPill) {
                poisonMap.put(values[random], poisonMap.get(values[random]) - 1);

                poisonPillCounter.getAndDecrement();
            }
        }
    }



    private void populatePoisonMapWithValues(int numberOfProducers) {
        poisonMap = new HashMap<>();
        for(MessageType m : MessageType.values()){
            poisonMap.put(m, numberOfProducers);
        }
    }

    /**
     * return the number of poison pills remaining in the application
     * @return remaining poison pills to be consumed.
     */
    public AtomicInteger getPoisonPillCounter() {
        return poisonPillCounter;
    }

    private int findRandomQueueWithMessages(MessageType[] values) {
        int random = ThreadLocalRandom.current().nextInt(values.length);
        while(queueDoesNotHaveProducers(values, random)){
            random = ThreadLocalRandom.current().nextInt(values.length);
        }
        return random;
    }

    private boolean producersKeepSendingMessages() {
        return poisonPillCounter.get() > 0;
    }

    private boolean queueDoesNotHaveProducers(MessageType[] values, int random) {
        return poisonMap.get(values[random]) < 1;
    }
}
