package com.application.producer.impl;

import com.application.core.MessageBroker;
import com.application.core.enums.MessageType;
import com.application.exception.InputValidator;
import com.application.producer.PoisonPill;
import com.application.producer.Producer;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Producer implementation of { @Code Producer}
 * @author aneesh
 */
public class ProducerImpl implements Producer {

    private static int minMessagesPerProducer;
    private static final PoisonPill POISON_PILL = new PoisonPill();
    private MessageBroker broker;

    public ProducerImpl(MessageBroker broker, int minMessagesArg){
        InputValidator.validateBroker(broker);
        this.broker = broker;
        minMessagesPerProducer = minMessagesArg;
    }

    public <T> void publish(MessageType type, T message){
        broker.publishMessage(type, message);

    }

    @Override
    public MessageBroker getMessageBroker() {
        return this.broker;
    }

    @Override
    public int getMinimumMessagesPerProducer() {
        return minMessagesPerProducer;
    }

    /**
     * Start the runnable thread by publishing messages to the broker until the minimum
     * number of messages has been met. Publish the poison pills to each queue once complete.
     */
    public void run() {
        MessageType[] values = MessageType.values();

        for(int i = 0; i< minMessagesPerProducer; i++) {
            int random = ThreadLocalRandom.current().nextInt(values.length);
            publish(values[random], ThreadLocalRandom.current().nextInt());
        }
        for(int i = 0; i<values.length; i++){
            publish(values[i], POISON_PILL);
        }
    }


}
