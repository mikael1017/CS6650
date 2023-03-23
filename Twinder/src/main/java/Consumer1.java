import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoClient;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;

public class Consumer1 implements Runnable {
  private MongoClient mdbClient;

  public Consumer1(MongoClient mdbClient) {
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
//          System.out.println("added to like counter");
        } else {
//          System.out.println("added to dislike counter");
        }
      };

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }
}
