

import static com.mongodb.client.model.Updates.inc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.bson.Document;

public class Consumer1 implements Runnable {
  private static final String QUEUE_NAME = "tempStore";
  private MongoDatabase db;

  private RMQChannelPool pool;
  private int BATCH_SIZE = 15;
  public Consumer1(RMQChannelPool pool, MongoDatabase db) {
    this.db = db;
    this.pool = pool;

  }

  @Override
  public void run() {
    try {
      List<Document> likeList = new ArrayList<>();
      List<Document> dislikeList = new ArrayList<>();
      AtomicInteger messageCount = new AtomicInteger();
      AtomicInteger batchCount = new AtomicInteger();

      Channel channel = this.pool.borrowObject();
      channel.basicQos(BATCH_SIZE);

      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");
//      Things to do when the message is consumed
//        System.out.println(message);
//        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
//        System.out.println(" [x] Received '" + message + "'");
        Document incomingMessage = Document.parse(message);
//        JsonObject jsonObject = new JsonParser().parse(message).getAsJsonObject();
        String swiperId = incomingMessage.getString("swiper");
        String swipeeId = incomingMessage.getString("swipee");
        String swipe = incomingMessage.getString("swipe");
//        System.out.println(swipe);
        String collectionName = "";

//        create a new payload
        Document payload = new Document("_id", swipeeId);

        switch (swipe) {
          case "right":
//            System.out.println("right");
            likeList.add(payload);
            break;

          case "left":
//            System.out.println("left");
            dislikeList.add(payload);
            break;
        }

        messageCount.getAndIncrement();
        if (messageCount.get() >= BATCH_SIZE) {
//          System.out.println("Batch " + batchCount + " is full, sending to db");
          MongoCollection likeColl = db.getCollection("likes");
          MongoCollection dislikeColl = db.getCollection("dislikes");
          System.out.println("sending the message to the collection: ");

//          likeColl.insertMany(likeList);
//          dislikeColl.insertMany(dislikeList);
          UpdateOptions options = new UpdateOptions().upsert(true);
          long startTime = System.currentTimeMillis();

//    new ClientThread(barrier).start();
//
//          for (Document doc : likeList) {
//            likeColl.updateOne(new Document("_id", doc.getString("_id")),
//                inc("numLikes", 1), options);
//          }
//
//          for (Document doc : dislikeList) {
//            dislikeColl.updateOne(new Document("_id", doc.getString("_id")),
//                inc("numDislikes", 1), options);
//          }
          long endTime = System.currentTimeMillis();
          long totalTime = endTime - startTime;
          System.out.println("Time taken to update the db: " + totalTime);
          channel.basicAck(delivery.getEnvelope().getDeliveryTag(), true);
          likeList.clear();
          dislikeList.clear();
          messageCount.set(0);
        }
//        System.out.println(likeList);p
//        System.out.println(dislikeList);
//        System.out.println(messageCount);
      };

      channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {});
      this.pool.returnObject(channel);
      // Wait for messages to be consumed

//      System.out.println("All messages received:");
//      System.out.println(this.likeCounter.toString());
//      System.out.println(this.dislikeCounter.toString());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }
}