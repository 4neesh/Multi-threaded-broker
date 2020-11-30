package consumer;

import com.application.consumer.Consumer;
import com.application.consumer.impl.ConsumerImpl;
import com.application.core.MessageBroker;
import com.application.core.enums.MessageType;
import com.application.core.impl.MessageBrokerImpl;
import com.application.exception.BrokerException;
import com.application.exception.MessageTypeException;
import com.application.producer.Producer;
import com.application.producer.impl.ProducerImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


import static org.junit.Assert.assertEquals;


public class ConsumerImplTest {

    private static MessageBroker broker;
    private static Consumer consumer;
    private static Producer producer;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @Before
    public void setup(){
        broker = new MessageBrokerImpl();
        consumer = new ConsumerImpl(broker, 10);
        producer = new ProducerImpl(broker, 10);
    }

    @Test
    public void brokerCannotBeNull(){
        expectedException.expect(BrokerException.class);
        Consumer consumer1 = new ConsumerImpl(null,10);

    }

    @Test
    public void pillCountIsEqualToProducerThreadsMessageTypes(){
        MessageBroker broker1 = new MessageBrokerImpl();
        Consumer consumer1 = new ConsumerImpl(broker1, 11);

        assertEquals("Consumer does not register correct number of poison pills.",33, consumer1.getPoisonPillCounter().get());
    }

    @Test
    public void consumerCannotConsumeNullArgument(){
        expectedException.expect(MessageTypeException.class);
        Object message = consumer.consume(null);
    }

    @Test
    public void singleMessageIsConsumedInOrderConfirmedQueue(){
        producer.publish(MessageType.ORDER_CONFIRMED, "ORDER_CONFIRMED TEST");
        Object message = consumer.consume(MessageType.ORDER_CONFIRMED);

        assertEquals("Consumed message count not reflecting consumed message in ORDER_CONFIRMED queue.", "ORDER_CONFIRMED TEST",message);
    }

    @Test
    public void singleMessageIsConsumedInBuyOrderQueue(){
        producer.publish(MessageType.BUY_ORDER, "BUY_ORDER TEST");
        Object message = consumer.consume(MessageType.BUY_ORDER);

        assertEquals("Consumed message count not reflecting consumed message in BUY_ORDER queue.", "BUY_ORDER TEST",message);
    }

    @Test
    public void singleMessageIsConsumedInSellOrderQueue(){
        producer.publish(MessageType.SELL_ORDER, "SELL_ORDER TEST");
        Object message = consumer.consume(MessageType.SELL_ORDER);

        assertEquals("Consumed message count not reflecting consumed message in SELL_ORDER queue.", "SELL_ORDER TEST",message);
    }

    @Test
    public void consumerReturnsNullForEmptyQueue(){
        producer.publish(MessageType.ORDER_CONFIRMED, "TEST");
        Object message = consumer.consume(MessageType.SELL_ORDER);

        assertEquals("Empty queue does not return null to consumer", null, message);
    }


}
