import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer1Jar {

  public static String QUEUE_NAME = "tempStore";
  private static final int THREAD_NUM = 200;
  public static void main(String[] args) throws IOException, TimeoutException {
    ConnectionFactory factory = new ConnectionFactory();
    System.out.println("Connecting...");
    factory.setHost("ec2-54-218-193-93.us-west-2.compute.amazonaws.com");
    factory.setUsername("jaewoo");
    factory.setVirtualHost("cherry_broker");
    factory.setPassword("wodn1017");
    Connection connection = factory.newConnection();
    RMQChannelFactory RMQFactory = new RMQChannelFactory(connection);
    RMQChannelPool pool = new RMQChannelPool(THREAD_NUM, RMQFactory);
    System.out.println("Connected to the RMQ server");
    // Second Queue
//    FanoutExchange ex = new FanoutExchange();
//    ex.createExchangeAndQueue(connection);
    System.out.println("Consumer started");
    System.out.println("Consumer 1");
    MongoClient mdbClient = MongoClients.create("mongodb+srv://jaewoo:wodn1017@cs6650.o3m9wao.mongodb.net/?retryWrites=true&w=majority");
    MongoDatabase db = mdbClient.getDatabase("twinderDB");
    MapCounter likeCounter = new MapCounter();
    MapCounter dislikeCounter = new MapCounter();
    Runnable consumer1 = new Consumer1(pool, db);
    for (int i = 0; i < THREAD_NUM; i++) {
      new Thread(consumer1).start();
    }

//    after it collects all the data and sorted from the temp

  }

}
