package producer;

import com.application.core.MessageBroker;
import com.application.core.enums.MessageType;
import com.application.core.impl.MessageBrokerImpl;
import com.application.exception.BrokerException;
import com.application.producer.Producer;
import com.application.producer.impl.ProducerImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class ProducerImplTest {

    private static MessageBroker broker;
    private static Producer producer;

    @Before
    public void setup(){
        broker = new MessageBrokerImpl();
        producer = new ProducerImpl(broker, 10);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void brokerCannotBeNull(){
        expectedException.expect(BrokerException.class);
        Producer producer2 = new ProducerImpl(null, 10);

    }

    @Test
    public void brokerIsRegisteredAsExpected(){

        assertEquals("Producer does not assign correct broker to instance.", broker, producer.getMessageBroker());
    }

    @Test
    public void singleMessageIsRegisteredToSellOrder(){

        producer.publish(MessageType.SELL_ORDER, "TEST");
        assertEquals("Producer is not able to register message to SELL_ORDER queue.", 1, broker.publishedMessagesCount());
    }

    @Test
    public void singleMessageIsRegisteredToBuyOrder(){

        producer.publish(MessageType.BUY_ORDER, "TEST");
        assertEquals("Producer is not able to register message to BUY_ORDER queue.", 1, broker.publishedMessagesCount());
    }

    @Test
    public void singleMessageIsRegisteredToOrderConfirmedOrder(){

        producer.publish(MessageType.ORDER_CONFIRMED, "TEST");
        assertEquals("Producer is not able to register message to ORDER_CONFIRMED queue.", 1, broker.publishedMessagesCount());
    }

    @Test
    public void twoMessagesRegisteredToSellOrder(){

        producer.publish(MessageType.SELL_ORDER, "TEST");
        producer.publish(MessageType.SELL_ORDER, "TEST");
        assertEquals("Producer is not able to register multiple messages to SELL_ORDER queue.", 2, broker.publishedMessagesCount());
    }

    @Test
    public void twoMessagesRegisteredToBuyOrder(){

        producer.publish(MessageType.BUY_ORDER, "TEST");
        producer.publish(MessageType.BUY_ORDER, "TEST");
        assertEquals("Producer is not able to register multiple messages to BUY_ORDER queue.", 2, broker.publishedMessagesCount());
    }

    @Test
    public void twoMessagesRegisteredToOrderConfirmedOrder(){

        producer.publish(MessageType.ORDER_CONFIRMED, "TEST");
        producer.publish(MessageType.ORDER_CONFIRMED, "TEST");
        assertEquals("Producer is not able to register multiple messages to ORDER_CONFIRMED queue.", 2, broker.publishedMessagesCount());
    }

    @Test
    public void messagesOfAllTypesRegisteredToBroker(){
        producer.publish(MessageType.ORDER_CONFIRMED, "TEST");
        producer.publish(MessageType.BUY_ORDER, "TEST");
        producer.publish(MessageType.SELL_ORDER, "TEST");

        assertEquals("Producer is not able to register messages of all message types to individual queues.", 3, broker.publishedMessagesCount());
    }


    @Test
    public void minimumNumberOfMessagesAreRegistered(){
        assertEquals("Minimum number of messages per producer is not correctly assigned.",10, producer.getMinimumMessagesPerProducer());
    }

    @Test
    public void minimumMessagesPublished(){
        producer.run();
        assertEquals("Producer does not produce minimum number of messages when run.",10, broker.publishedMessagesCount());
    }

    @Test
    public void unconsumedMessagesForAllQueues(){
        producer.publish(MessageType.SELL_ORDER, "TEST");
        producer.publish(MessageType.BUY_ORDER, "TEST");
        producer.publish(MessageType.ORDER_CONFIRMED, "TEST");

        assertEquals("Unconsumed messages does not reflect published messages to all queues before being consumed.",3,broker.unConsumedMessages());
    }

}
