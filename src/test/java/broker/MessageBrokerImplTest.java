package broker;

import com.application.consumer.Consumer;
import com.application.consumer.impl.ConsumerImpl;
import com.application.core.MessageBroker;
import com.application.core.enums.MessageType;
import com.application.core.impl.MessageBrokerImpl;
import com.application.exception.BrokerException;
import com.application.exception.MessageTypeException;
import com.application.producer.PoisonPill;
import com.application.producer.Producer;
import com.application.producer.impl.ProducerImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class MessageBrokerImplTest {

    private static MessageBroker broker;
    private static Producer producer;
    private static Consumer consumer;

    @Before
    public void setup(){
        broker = new MessageBrokerImpl();
        producer = new ProducerImpl(broker,10);
        consumer = new ConsumerImpl(broker, 1);
    }
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void publishNullMessageType(){
        expectedException.expect(MessageTypeException.class);
        broker.publishMessage(null, "TEST");

    }

    @Test
    public void publishNullMessage(){
        expectedException.expect(BrokerException.class);
        broker.publishMessage(MessageType.ORDER_CONFIRMED, null);
    }

    @Test
    public void consumeNullMessageType(){
        expectedException.expect(MessageTypeException.class);
        broker.consume(null);
    }

    @Test
    public void unconsumedMessagesForZeroPublishedOrConsumedMessages(){

        assertEquals("Default value of unconsumed messages is not zero for broker.",0, broker.unConsumedMessages());
    }

    @Test
    public void consumedMessagesForZeroConsumedMessages(){

        assertEquals("Default value of unconsumed messages is not zero for broker.",0, broker.consumedMessagesCount());
    }

    @Test
    public void publishedMessagesForZeroPublishedMessages(){

        assertEquals("Default value of unconsumed messages is not zero for broker.",0, broker.publishedMessagesCount());
    }

    @Test
    public void singleMessageIsConsumedInOrderConfirmedQueue(){
        producer.publish(MessageType.ORDER_CONFIRMED, "TEST");
        consumer.consume(MessageType.ORDER_CONFIRMED);

        assertEquals("Consumed message count not reflecting consumed message in ORDER_CONFIRMED queue.", 1,broker.consumedMessagesCount());
    }

    @Test
    public void singleMessageIsConsumedInBuyOrderQueue(){
        producer.publish(MessageType.BUY_ORDER, "TEST");
        consumer.consume(MessageType.BUY_ORDER);

        assertEquals("Consumed message count not reflecting consumed message in BUY_ORDER queue.", 1,broker.consumedMessagesCount());
    }

    @Test
    public void singleMessageIsConsumedInSellOrderQueue(){
        producer.publish(MessageType.SELL_ORDER, "TEST");
        consumer.consume(MessageType.SELL_ORDER);

        assertEquals("Consumed message count not reflecting consumed message in SELL_ORDER queue.", 1,broker.consumedMessagesCount());
    }


    @Test
    public void decrementUnconsumedMessages(){
        producer.publish(MessageType.SELL_ORDER, "TEST");
        producer.publish(MessageType.BUY_ORDER, "TEST");
        producer.publish(MessageType.ORDER_CONFIRMED, "TEST");
        consumer.consume(MessageType.SELL_ORDER);

        assertEquals("Unconsumed messages does not reflect published messages to all queues.",2,broker.unConsumedMessages());
    }

    @Test
    public void unconsumedMessageRemainsZeroForNullConsume(){
        consumer.consume(MessageType.SELL_ORDER);

        assertEquals("Consume returns null does influences unconsumed messages.", 0, broker.unConsumedMessages());
    }

    @Test
    public  void publishedMessageRemainsZeroForPoisonPillPublish(){
        producer.publish(MessageType.SELL_ORDER, new PoisonPill());

        assertEquals("Broker registers poison pill in message count.",0, broker.publishedMessagesCount());
    }

    @Test
    public  void unconsumedMessageRemainsZeroForPoisonPillPublish(){
        producer.publish(MessageType.SELL_ORDER, new PoisonPill());

        assertEquals("Broker registers poison pill in message count.",0, broker.unConsumedMessages());
    }

    @Test
    public  void consumedMessageRemainsZeroForPoisonPillPublish(){
        producer.publish(MessageType.SELL_ORDER, new PoisonPill());
        producer.publish(MessageType.SELL_ORDER, "TEST");
        consumer.consume(MessageType.SELL_ORDER);
        consumer.consume(MessageType.SELL_ORDER);

        assertEquals("Broker registers poison pill in message count.",1, broker.consumedMessagesCount());
    }

    @Test
    public void firstProducedMessageIsConsumedSellOrder(){
        producer.publish(MessageType.SELL_ORDER, "TESTA");
        producer.publish(MessageType.SELL_ORDER, "TESTB");
        producer.publish(MessageType.SELL_ORDER, "TESTC");
        Object message1 = consumer.consume(MessageType.SELL_ORDER);
        Object message2 = consumer.consume(MessageType.SELL_ORDER);
        Object message3 = consumer.consume(MessageType.SELL_ORDER);

        assertEquals("Queue of messages are not consumed in a FIFO order for sell order (message 1).", "TESTA", message1);
        assertEquals("Queue of messages are not consumed in a FIFO order for sell order (message 2).", "TESTB", message2);
        assertEquals("Queue of messages are not consumed in a FIFO order for sell order (message 3).", "TESTC", message3);
    }

    @Test
    public void firstProducedMessageIsConsumedBuyOrder(){
        producer.publish(MessageType.BUY_ORDER, "TESTA");
        producer.publish(MessageType.BUY_ORDER, "TESTB");
        producer.publish(MessageType.BUY_ORDER, "TESTC");
        Object message1 = consumer.consume(MessageType.BUY_ORDER);
        Object message2 = consumer.consume(MessageType.BUY_ORDER);
        Object message3 = consumer.consume(MessageType.BUY_ORDER);

        assertEquals("Queue of messages are not consumed in a FIFO order for buy order (message 1).", "TESTA", message1);
        assertEquals("Queue of messages are not consumed in a FIFO order for buy order (message 2).", "TESTB", message2);
        assertEquals("Queue of messages are not consumed in a FIFO order for buy order (message 3).", "TESTC", message3);
    }

    @Test
    public void firstProducedMessageIsConsumedOrderConfirmed(){
        producer.publish(MessageType.ORDER_CONFIRMED, "TESTA");
        producer.publish(MessageType.ORDER_CONFIRMED, "TESTB");
        producer.publish(MessageType.ORDER_CONFIRMED, "TESTC");
        Object message1 = consumer.consume(MessageType.ORDER_CONFIRMED);
        Object message2 = consumer.consume(MessageType.ORDER_CONFIRMED);
        Object message3 = consumer.consume(MessageType.ORDER_CONFIRMED);

        assertEquals("Queue of messages are not consumed in a FIFO order for order confirmed (message 1).", "TESTA", message1);
        assertEquals("Queue of messages are not consumed in a FIFO order for order confirmed (message 2).", "TESTB", message2);
        assertEquals("Queue of messages are not consumed in a FIFO order for order confirmed (message 3).", "TESTC", message3);
    }


}
