import com.application.consumer.Consumer;
import com.application.consumer.impl.ConsumerImpl;
import com.application.core.MessageBroker;
import com.application.core.enums.MessageType;
import com.application.core.impl.MessageBrokerImpl;
import com.application.producer.Producer;
import com.application.producer.impl.ProducerImpl;
import com.google.code.tempusfugit.concurrency.RepeatingRule;
import com.google.code.tempusfugit.concurrency.annotations.Repeating;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class MainTest {

    @Rule
    public RepeatingRule rule = new RepeatingRule();

    private static MessageBroker broker;
    private static Producer producer;
    private static Consumer consumer;
    private static ExecutorService threadPool;
    @Before
    public void setup(){
        broker = new MessageBrokerImpl();

        threadPool = Executors.newFixedThreadPool(10);
        producer = new ProducerImpl(broker, 1000);
        consumer = new ConsumerImpl(broker, 3);


    }

    @Test
    @Repeating(repetition = 10)
    public void allCountersProducedAsExpected(){
        for(int i = 0; i< 3; i++) {
            threadPool.submit(producer);
            threadPool.submit(consumer);
        }
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals("Broker is not reporting expected number of consumed messages from all consumers.", 3000, broker.consumedMessagesCount());
        assertEquals("Broker is not reporting expected number of unconsumed messages.", 0, broker.unConsumedMessages());
        assertEquals("Broker is not receiving expected number of messages from all producers.", 3000, broker.consumedMessagesCount());
    }

    @Test
    @Repeating(repetition = 10)
    public void messagesAreProducedAndConsumedInOrder(){
        List<Object> publishedMessages = new ArrayList<>();
        List<Object> consumedMessages = new ArrayList<>();
        int numberOfThreads  = 3;
        int numberOfMessages = 1000;
        for(int i =0; i<numberOfThreads; i++){
            threadPool.submit(()->{
                for(int j = 0; j<numberOfMessages; j++) {
                    int message = ThreadLocalRandom.current().nextInt();

                    synchronized (publishedMessages) {
                        broker.publishMessage(MessageType.SELL_ORDER, message);
                        publishedMessages.add(message);
                    }

                }
            });
            threadPool.submit(()->{
                for(int j = 0; j<numberOfMessages; j++) {
                    Object consumedMessage = null;

                        synchronized (consumedMessages) {
                            while (consumedMessage == null) {
                                consumedMessage = broker.consume(MessageType.SELL_ORDER);
                            }
                            consumedMessages.add(consumedMessage);
                        }
                }
            });
        }
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals("Published messages are not consumed in order.",publishedMessages, consumedMessages);



    }

}
