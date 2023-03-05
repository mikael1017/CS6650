import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer2Jar {

  private static final int THREAD_NUM = 100;
  public static void main(String[] args) throws IOException, TimeoutException {
    ConnectionFactory factory = new ConnectionFactory();
    System.out.println("Connecting...");
    factory.setHost("ec2-52-12-168-19.us-west-2.compute.amazonaws.com");
    factory.setUsername("jaewoo");
    factory.setVirtualHost("cherry_broker");
    factory.setPassword("wodn1017");
    Connection connection = factory.newConnection();
    System.out.println("Connected to the RMQ server");
    // Second Queue
    FanoutExchange ex = new FanoutExchange();
    ex.createExchangeAndQueue(connection);
    System.out.println("Consumer started");
    System.out.println("Consumer 2");
    ListCounter potentialMatches = new ListCounter();
    Runnable consumer2 = new Consumer2(connection, potentialMatches);
    for (int i = 0; i < THREAD_NUM; i++) {
      new Thread(consumer2).start();
    }
  }

}
