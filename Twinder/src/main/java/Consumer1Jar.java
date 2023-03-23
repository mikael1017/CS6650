import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer1Jar {

  public static String QUEUE_NAME_1 = "fanout-queue-1";
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
    System.out.println("Consumer 1");
//    Runnable consumer1 = new Consumer1(connection, mdbClient);
    for (int i = 0; i < THREAD_NUM; i++) {
      new Thread(consumer1).start();
    }

//    after it collects all the data and sorted from the temp

  }

}
