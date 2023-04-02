
import com.google.gson.JsonObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Producer {
  private final static String QUEUE_NAME = "tempStore";
  private static JsonObject payload = null;
  private RMQChannelPool pool;
  public Producer(RMQChannelPool pool, JsonObject payload) {
    this.payload = payload;
    this.pool = pool;
  }

  public void send() {
//    System.out.println("Send method started");
//    System.out.println(this.payload.toString());
//    System.out.println("Create a new channel...");
    try {
      Channel channel = this.pool.borrowObject();
//    System.out.println("Created a new channel");
//    System.out.println("Producer sending....");
//      System.out.println("Payload is : " + this.payload.toString());
      channel.queueDeclare(QUEUE_NAME, true, false, false, null);

// Publish a message to the queue with the deliveryMode property set to 2
      channel.basicPublish("", "tempStore", MessageProperties.PERSISTENT_TEXT_PLAIN,
          this.payload.toString().getBytes(StandardCharsets.UTF_8));
//      channel.basicPublish(FanoutExchange.EXCHANGE_NAME, FanoutExchange.ROUTING_KEY,
//          MessageProperties.PERSISTENT_TEXT_PLAIN,
//          this.payload.toString().getBytes(StandardCharsets.UTF_8));
//    System.out.println("Producer sent successfully");
//    System.out.println(" [x] Sent '" + this.payload.toString());
      System.out.println("done");
      this.pool.returnObject(channel);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

}