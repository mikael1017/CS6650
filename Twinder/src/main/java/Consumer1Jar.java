import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer1Jar {

  public static String QUEUE_NAME_1 = "fanout-queue-1";

  public static void main(String[] args) throws IOException, TimeoutException {
    ConnectionFactory factory = new ConnectionFactory();
    System.out.println("Connecting...");
    factory.setHost("ec2-34-219-129-127.us-west-2.compute.amazonaws.com");
    factory.setUsername("jaewoo");
    factory.setVirtualHost("cherry_broker");
    factory.setPassword("wodn1017");
    Connection connection = factory.newConnection();
    System.out.println("Connected to the RMQ server");
    // Second Queue
    FanoutExchange ex = new FanoutExchange();
    ex.createExchangeAndQueue(connection);
    System.out.println("Consumer started");
    MapCounter likeCounter = new MapCounter();
    MapCounter dislikeCounter = new MapCounter();
    ListCounter potentialMatches = new ListCounter();
    Runnable consumer1 = new Consumer1(connection, likeCounter, dislikeCounter);
    Runnable consumer2 = new Consumer2(connection, potentialMatches);
    Thread recv1 = new Thread(consumer1);
//    Thread recv2 = new Thread(consumer2);
    recv1.start();
//    recv2.start();

  }

}