import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import consumer.ListCounter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Consumer2 {
  private static final String QUEUE_NAME = "Twinder";
  private Connection connection;
  private ListCounter potentialMatches;

  public Consumer2(Connection connection, ListCounter potentialMatches) {
    this.connection = connection;
    this.potentialMatches = potentialMatches;
  }

  public void consume() throws IOException, InterruptedException, TimeoutException {
    Channel channel = this.connection.createChannel();
    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
      String message = new String(delivery.getBody(), "UTF-8");
      JsonObject jsonObject = new JsonParser().parse(message).getAsJsonObject();
      String swipe = jsonObject.get("swipe").getAsString();
      String swiperId = jsonObject.get("swiper").getAsString();
      String swipeeId = jsonObject.get("swipee").getAsString();
      if (swipe.equals("right")) {
        potentialMatches.add(swiperId, swipeeId);
      }
      System.out.println("Received message: " + message);
    };

    channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
    });

    // Wait for messages to be consumed
    Thread.sleep(5000);

    System.out.println(potentialMatches.toString());
    channel.close();
  }

}
