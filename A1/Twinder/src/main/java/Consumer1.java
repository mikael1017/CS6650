import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import consumer.MapCounter;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer1 {
  private static final String QUEUE_NAME = "Twinder";
  private final MapCounter likeCounter;
  private final MapCounter dislikeCounter;
  private Connection connection;
  public Consumer1(Connection connection, MapCounter likeCounter, MapCounter dislikeCounter) {
    this.connection = connection;
    this.likeCounter = likeCounter;
    this.dislikeCounter = dislikeCounter;
  }

  public void consume() throws IOException, TimeoutException, InterruptedException {
    Channel channel = connection.createChannel();
    channel.queueDeclare(QUEUE_NAME, false, false, false, null);

    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
      String message = new String(delivery.getBody(), "UTF-8");
//      Things to do when the message is consumed
      JsonObject jsonObject = new JsonParser().parse(message).getAsJsonObject();
      String swiperId = jsonObject.get("swiper").getAsString();
      String swipe = jsonObject.get("swipe").getAsString();
      if (swipe.equals("right")) {
//        like
        this.likeCounter.add(swiperId);
      } else {
        this.dislikeCounter.add(swiperId);
      }
      System.out.println("Received message: " + message);
    };

    channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
    });

    // Wait for messages to be consumed
    Thread.sleep(5000);

    System.out.println("All messages received:");
    System.out.println(likeCounter.toString());
    System.out.println(dislikeCounter.toString());
    channel.close();
  }
}
