package com.application.core.impl;

import com.application.core.MessageBroker;
import com.application.core.enums.MessageType;
import com.application.exception.InputValidator;
import com.application.producer.PoisonPill;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Message Broker Implementation of {@code MessageBoker}
 * @author aneesh
 */
public class MessageBrokerImpl implements MessageBroker {

    private volatile static int totalMessages;
    private volatile static int consumedMessages;
    private volatile static int unconsumedMessages;

    private static HashMap<String, LinkedBlockingQueue> messageTypeQueues;

    public MessageBrokerImpl(){
        totalMessages = 0;
        consumedMessages = 0;
        unconsumedMessages = 0;
        messageTypeQueues = new HashMap<>();
    }
    public <T> void publishMessage(MessageType messageType, T message) {
        InputValidator.validatePublishedMessage(messageType, message);
        LinkedBlockingQueue queue = obtainQueue(messageType);

        synchronized (this) {
            queue.offer(message);
            if (!(message instanceof PoisonPill)) {
                totalMessages++;
                unconsumedMessages++;
            }
        }
        //uncomment the below code to see the messages being published into the queue.
//        synchronized (this) {
//            queue.add(message);
//
//            if (message instanceof PoisonPill) {
//                System.out.println(Thread.currentThread().getName() + " published POISON_PILL to " + messageType.toString());
//            } else {
//                totalMessages++;
//                unconsumedMessages++;
//                System.out.println(Thread.currentThread().getName() + " published message: " + message + " to " + messageType.toString());
//            }
//        }

    }

    public <T> T consume(MessageType messageType) {
        InputValidator.validateMessageType(messageType);
        LinkedBlockingQueue queue = obtainQueue(messageType);
        Object message = null;

      synchronized (this) {

            message = queue.poll();

            if (message == null) {
             } else if (!(message instanceof PoisonPill)) {
                 consumedMessages++;
                 unconsumedMessages--;
             } else {
             }
         }

        //comment out the above synchronized block and uncomment the below block to see the messages being consumed from the queue.
//        synchronized (this) {
//
//            message = queue.poll();
//
//            if (message == null) {
//                 System.out.println(Thread.currentThread().getName() + " could not obtain a message.");
//             } else if (!(message instanceof PoisonPill)) {
//                 consumedMessages++;
//                 unconsumedMessages--;
//                 System.out.println(Thread.currentThread().getName() + " consumed message: " + (T) message.toString() + " from " + messageType.toString());
//             } else {
//                 System.out.println(Thread.currentThread().getName() + " consumed POISON PILL from " + messageType.toString());
//             }
//         }

        return (T)message;
    }


    public int publishedMessagesCount() {
        return totalMessages;
    }

    public int consumedMessagesCount() {
        return consumedMessages;
    }

    public int unConsumedMessages() {
        return unconsumedMessages;
    }

    private LinkedBlockingQueue obtainQueue(MessageType messageType) {

        String queueKey = messageType.toString();
        synchronized (this) {
            if (!messageTypeQueues.containsKey(queueKey)) {
                messageTypeQueues.put(queueKey, new LinkedBlockingQueue());
            }
        }
        return messageTypeQueues.get(queueKey);
    }


}
