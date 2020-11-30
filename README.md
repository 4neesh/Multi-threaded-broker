# Message Broker
* The Message broker will create queues for different messages and post them into different queues depending on their specification.
* External dependencies: J-unit 4 and Tempus fugit. J-unit has been used for unit testing and Tempus fugit is used for 
concurrent unit testing.
* All functionality from the specification are as expected.

<<<<<<< HEAD
##Running the application
* Run from <code>Main</code> class or from the command line.
=======
## Running the application
* Run from <code>Main</code> class. 
>>>>>>> 770ec019c3a84539607605528c17b5c9655c4b8a
* Run from command line by navigating to the jar file in the target directory and running "java -jar MessageBroker.jar" 
* Pass in 4 values comma separated to represent the number of producers, number of consumers, 
  minimum number of messages each producer to publish, and the number of threads to execute.
* The console displays a summary total/consumed/unconsumed messages. You can find commented code in the 
<code>MessageBrokerImpl</code> to reveal further insight to the messages published and consumed.

<<<<<<< HEAD
##Thread safety
Thread safety applies to the following aspects:<br>
* totalMessages, consumedMessages, and unConsumedMessages are concurrently updated across all threads as they are published and consumed.
* Queues maintain their order of messages to ensure the oldest message is consumed in FIFO order.
* The producers can publish new messages to the queues as the messages are simultaneously consumed.
=======
## Thread safety
Thread safety is important to consider for the following aspects:<br>
* TotalPublishedMessages, ConsumedMessages, and UnconsumedMessages are concurrently updated across all threads as they are published and consumed.
* Messages can be published and consumed from the individual queues without causing a double-publish or double-read problem.
* Queues maintain their order of messages to ensure the oldest message is consumed from the queue in FIFO order.
* The producers can publish new messages to the queues as the messages are consumed.
>>>>>>> 770ec019c3a84539607605528c17b5c9655c4b8a

## Design
* LinkedBlockingQueue is used to store the messages. Each instance is unbounded and provides a thread-safe FIFO queue.
* The <code>MessageBrokerImpl</code> class uses a HashMap to obtain the respective LinkedBlockingQueue for each MessageType.
* The message counters are updated in a thread-safe manner as each message is published and consumed (see assumptions).
* The Producers will publish messages to random queues using a random number generator.
* The Consumer will query queues at random. As each queue stops receiving new messages, the Consumer will stop querying it. 
* The poison pill design pattern is used to instruct the consumers on when to stop querying specific (and all) queues.
* Custom exceptions are used to validate input parameters for the Consumer, Producer and MessageBroker.
Further detail on design decisions:

<strong><u>LinkedBlockingQueue</u></strong><br>
* <code>poll()</code> and <code>offer()</code> are used to publish and consume from the queue. Rather than blocking the 
queue for a defined time and risking thread starvation, they return null to the Producer/Consumer and relinquish the lock on the broker.
* LinkedBlockingQueue is boundless to support growth of the queue for when there are insufficient threads to both producers and consumers.
* LinkedBlockingQueue is chosen over ArrayBlockingQueue as LinkedBlockingQueue enables unbounded queues and contains separate locks for publishing and consuming messages.

<strong><u>Synchronized</u></strong><br>
* When the HashMap of the queues are first populated in the MessageBrokerImpl, the if statement to check if the queue exists is synchronized.
* The <code>offer()</code> method in the MessageBrokerImpl is synchronized to ensure the <code>totalMessages</code> and 
<code>unconsumedMessages</code> counters are incremented without interception.
* The <code>poll()</code> method in the MessageBrokerImpl is synchronized to ensure the <code>consumedMessages</code> and 
<code>unconsumedMessages</code> counters are incremented and decremented respectively without interception. 

<strong><u>Poison pill</u></strong><br>
* <code>PoisonPill</code> is used to alert the consumers when producers are no longer publishing messages and when queues are no longer appropriate to query.
* Each Producer publishes one PoisonPill to each queue when it no longer has regular messages to publish.
* Each Consumer has a count of all the poison pills encountered on each queue. When number of producers is equal to number of poison pills in a queue, 
the queue is no longer active. 
* The method <code>findRandomQueueWithMessages</code> will direct the consumer to only active queues.
* The poison pills are not accounted for by the message broker counters when published and consumed.

<strong><u>MessageType</u></strong><br>
* The <code>MessageBrokerImpl</code> uses a HashMap to obtain the individual queues for each MessageType value.
* The Producer and Consumer implementations obtain random MessageTypes to publish and consume from.
* Additional message queues are accommodated for by the ProducerImpl, ConsumerImpl and MessageBrokerImpl as new MessageTypes are defined.

<strong><u>Testing</u></strong><br>
* The Tempus fugit dependency is used to run the MainTest.class tests in 10 different thread pools. 
* The allCountersProducedAsExpected test will run 3 producers and consumers 
across 6 threads with 1000 messages published by each producer. 
Each test will assert upon the expected values for each of the three counters (totalMessages, consumedMessages, unconsumedMessages).
* The messagesAreProducedAndConsumedInOrder test will validate published messages are consumed in FIFO order by submitting 10 thread pools 
of 3 producers and 3 consumers where 1000 messages are published by each producer.

## Assumptions
* Assumption 1: The values of unconsumedMessages, publishedMessages and consumedMessages always reflect the exact point during runtime.
* Assumption 2: unconsumedMessages, publishedMessages and consumedMessages all have their own individual counters and are not derived from each other.

## Enhancements
* Assumption 1 requires each counter to be correct at all times. I could reduce latency of the broker by removing the two synchronized blocks 
(around publishing and consuming messages) and replace the counters with AtomicInteger values. The update of the counters will be thread-safe  
and will be correct at the end of the application. They may not always be correct during runtime, however there will be reduced latency without  
locking the broker to update the counters on each publish/consume operation.
* Assumption 2 requires counters to be kept for all values. I can remove the unconsumedMessages counter and derive it from the publishedMessages 
and consumedMessages counters. I could also derive the unconsumedMessages counter by querying the sum of all the queues <code>size()</code>.
