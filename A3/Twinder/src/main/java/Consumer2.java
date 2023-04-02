import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;

public class Consumer2 implements Runnable {
  private static final String QUEUE_NAME = "Twinder";
  private Connection connection;
  private ListCounter potentialMatches;

  public Consumer2(Connection connection, ListCounter potentialMatches) {
    this.connection = connection;
    this.potentialMatches = potentialMatches;
  }

  @Override
  public void run() {
    try {
      Channel channel = this.connection.createChannel();
      channel.basicQos(1);
      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");
        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        JsonObject jsonObject = new JsonParser().parse(message).getAsJsonObject();
        String swipe = jsonObject.get("swipe").getAsString();
        String swiperId = jsonObject.get("swiper").getAsString();
        String swipeeId = jsonObject.get("swipee").getAsString();
        if (swipe.equals("right")) {
          potentialMatches.add(swiperId, swipeeId);
        }
//        System.out.println("Received message: " + message);
      };

//      channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
//      });
//      channel.basicConsume(FanoutExchange.QUEUE_NAME_2, false, deliverCallback, consumerTag -> {});
      // Wait for messages to be consumed
//      System.out.println(potentialMatches.toString());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
