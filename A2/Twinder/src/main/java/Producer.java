import com.google.gson.JsonObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Producer {
  private final static String QUEUE_NAME = "Twinder";
  private static JsonObject payload = null;
  private Connection connection;
  public Producer(Connection connection, JsonObject payload) {
    this.payload = payload;
    this.connection = connection;
  }

  public void send() throws IOException, TimeoutException {
    System.out.println("Send method started");
    System.out.println(this.payload.toString());
    System.out.println("Create a new channel...");
    Channel channel = this.connection.createChannel();
    System.out.println("Created a new channel");
    System.out.println("Producer sending....");
    channel.basicPublish(FanoutExchange.EXCHANGE_NAME, FanoutExchange.ROUTING_KEY, null, this.payload.toString().getBytes(StandardCharsets.UTF_8));
    System.out.println("Producer sent successfully");
    System.out.println(" [x] Sent '" + "hello" + "'");
    System.out.println("done");
    channel.close();
  }

}