package com.application;

import com.application.consumer.Consumer;
import com.application.consumer.impl.ConsumerImpl;
import com.application.core.MessageBroker;
import com.application.core.impl.MessageBrokerImpl;
import com.application.producer.Producer;
import com.application.producer.impl.ProducerImpl;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Main application class to define and start producer and executor threads with message broker.
 * @author aneesh
 */
public class Main {

    private static int minMessagesPerProducer;
    private static int numberOfProducers;
    private static int numberOfConsumers;
    private static int numberOfThreads;
    private static MessageBroker broker;
    private static Producer producer;
    private static Consumer consumer;
    private static ExecutorService threadPool;

    public static void main(String[] args) {

        takeApplicationParameters();

        setBroker();
        setProducer();
        setConsumer();
        setExecutorService();

        executeProducerThreads();
        executeConsumerThreads();

        summariseBrokerActivity();

    }

    private static void takeApplicationParameters() {
        Scanner in = new Scanner(System.in);
        numberOfThreads =0;
        numberOfConsumers = 0;
        numberOfProducers = 0;
        minMessagesPerProducer = 0;
        while(allValuesAreZero()) {
            try {
                System.out.println("Enter the number of producers (int), number of consumers (int), minimum number of producer messages (int) and number of threads (int) comma separated:");
                System.out.println("e.g: 3,3,1000,6");
                String propertyList = in.next();
                String[] properties = propertyList.split(",");

                numberOfProducers = Integer.parseInt(properties[0]);
                numberOfConsumers = Integer.parseInt(properties[1]);
                minMessagesPerProducer = Integer.parseInt(properties[2]);
                numberOfThreads = Integer.parseInt(properties[3]);

            } catch (Exception e) {
                System.out.println("Enter the number of producers (int), number of consumers (int), minimum number of producer messages (int) and number of threads (int) comma separated:");
                System.out.println("e.g: 3,3,1000,6");
                in.next();
            }
        }
    }

    private static boolean allValuesAreZero() {
        return (numberOfThreads<= 0 || numberOfProducers <= 0 || numberOfConsumers <= 0 || minMessagesPerProducer <= 0);
    }

    private static void setBroker() {
        broker = new MessageBrokerImpl();

    }

    private static void setProducer() {

        producer = new ProducerImpl(broker, minMessagesPerProducer);
    }

    private static void setConsumer() {
        consumer = new ConsumerImpl(broker, numberOfProducers);
    }

    private static void setExecutorService() {

        threadPool = Executors.newFixedThreadPool(numberOfThreads);
    }

    private static void executeConsumerThreads() {

        for(int i = 0; i<numberOfConsumers; i++){
            threadPool.submit(consumer);
        }
    }

    private static void executeProducerThreads() {
        for(int i = 0; i<numberOfProducers; i++){
            threadPool.submit(producer);
        }
    }

    private static void summariseBrokerActivity() {
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(60, TimeUnit.SECONDS);
            printSummary(broker);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void printSummary(MessageBroker broker) {

        System.out.println("-------------------------------");
        System.out.println("Number of Producers: " + numberOfProducers);
        System.out.println("Number of Consumers: " + numberOfConsumers);
        System.out.println("Minimum messages per Producer: " + minMessagesPerProducer);
        System.out.println("Number of Threads: " + numberOfThreads);
        System.out.println("-------------------------------");
        System.out.println("Unconsumed messages = " + broker.unConsumedMessages());
        System.out.println("Published messages = " + broker.publishedMessagesCount());
        System.out.println("Consumed messages = " + broker.consumedMessagesCount());

    }

}
