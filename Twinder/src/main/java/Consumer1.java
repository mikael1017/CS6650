

import static com.mongodb.client.model.Updates.inc;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateManyModel;
import com.mongodb.client.model.UpdateOptions;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.bson.Document;
import org.bson.conversions.Bson;

public class Consumer1 implements Runnable {
  private static final String QUEUE_NAME = "tempStore";
  private MongoDatabase db;

  private RMQChannelPool pool;
  private int BATCH_SIZE = 500;
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
//        String collectionName = "";

//        create a new payload
//        Document payload = new Document("_id", swipeeId);

//        payload.append("swipee", swipeeId);
//        bulkwrite()

        switch (swipe) {
          case "right":
//            System.out.println("right");
            likeList.add(new Document("_id", swipeeId));
            break;

          case "left":
//            System.out.println("left");
            dislikeList.add(new Document("_id", swipeeId));
            break;
        }

        messageCount.getAndIncrement();
        if (messageCount.get() >= BATCH_SIZE) {
//          System.out.println("Batch " + batchCount + " is full, sending to db");
          MongoCollection likeColl = db.getCollection("likes");
          MongoCollection dislikeColl = db.getCollection("dislikes");
          List<UpdateManyModel<Document>> udpateModels = new ArrayList<>();
          for (Document doc : likeList) {
            udpateModels.add(new UpdateManyModel<>(Filters.eq("_id", doc.get("_id")), inc("numLikes", 1), new UpdateOptions().upsert(true)));
          }
          for (Document doc : dislikeList) {
            udpateModels.add(new UpdateManyModel<>(Filters.eq("_id", doc.get("_id")), inc("numDisLikes", 1), new UpdateOptions().upsert(true)));
          }

          BulkWriteOptions options = new BulkWriteOptions().ordered(false);

//          MongoCollection likeCountColl = db.getCollection("likeCount");
//          MongoCollection dislikeCountColl = db.getCollection("dislikeCount");
          System.out.println("sending the message to the collection: ");

//          System.out.println("likeList size: " + likeList.size());
//          System.out.println("dislikeList size: " + dislikeList.size());
//          likeCountColl.insertMany(likeList);
//          dislikeCountColl.insertMany(dislikeList);
          long startTime = System.currentTimeMillis();

          likeColl.bulkWrite(udpateModels, options);
//          Bson regexFilter = Filters.regex("id", "^(" + String.join("|", likeList) + ")$");

//          for (String userId: likeList) {
//            likeColl.updateOne(new Document("_id", userId), new Document("$inc", new Document("numLikes", 1)), new UpdateOptions().upsert(true));
//          }
//
//          for (String userId : dislikeList) {
//            dislikeColl.updateOne(new Document("_id", userId), new Document("$inc", new Document("numDisLikes", 1)), new UpdateOptions().upsert(true));
//          }
//
//          UpdateOptions options = new UpdateOptions().upsert(true);
//          Bson likeFilter = Filters.in("_id", likeList);
//          Bson likeUpdate = new Document("$inc", new Document("numLikes", 1));
//          Bson dislikeFilter = Filters.in("_id", dislikeList);
//          Bson dislikeUpdate = new Document("$inc", new Document("numDisLikes", 1));
//          likeColl.updateMany(likeFilter, likeUpdate, options);
//          dislikeColl.updateMany(dislikeFilter, dislikeUpdate, options);

//
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