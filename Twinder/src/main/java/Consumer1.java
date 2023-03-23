

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;

public class Consumer1 implements Runnable {
  private static final String QUEUE_NAME = "Twinder";
  private MongoClient mdbClient;

  private Connection connection;
  public Consumer1(Connection connection, MongoClient mdbClient) {
    this.connection = connection;
    this.mdbClient = mdbClient;
  }

  @Override
  public void run() {
    try {
      Channel channel = connection.createChannel();
      channel.basicQos(1);
      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");
//      Things to do when the message is consumed
        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        JsonObject jsonObject = new JsonParser().parse(message).getAsJsonObject();
        String swiperId = jsonObject.get("swiper").getAsString();
        String swipe = jsonObject.get("swipe").getAsString();
        if (swipe.equals("right")) {
//        like
//          System.out.println("added to like counter db");
        } else {
//          System.out.println("added to dislike counter db");
        }
      };

//      channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
//      });
      channel.basicConsume(FanoutExchange.QUEUE_NAME_1, false, deliverCallback, consumerTag -> {});

      // Wait for messages to be consumed

//      System.out.println("All messages received:");
//      System.out.println(this.likeCounter.toString());
//      System.out.println(this.dislikeCounter.toString());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }
}