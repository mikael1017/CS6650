import com.google.gson.JsonObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import org.bson.Document;

public class Producer {
  private final static String QUEUE_NAME = "Twinder";
  private static JsonObject payload = null;
  private Connection connection;
  public Producer(Connection connection, JsonObject payload) {
    this.payload = payload;
    this.connection = connection;
  }

  public void send() throws IOException, TimeoutException {
//    send it to tempStore in mongoDB
    MongoClient client = MongoClients.create("mongodb+srv://jaewoo:wodn1017@cs6650.o3m9wao.mongodb.net/?retryWrites=true&w=majority");
    MongoDatabase db = client.getDatabase("twinderDB");
    MongoCollection col = db.getCollection("twinderCollection");

    Document sampleDoc = new Document("_id", "2").append("name", "Jaewoo Smith");
    col.insertOne(sampleDoc);
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