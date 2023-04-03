
import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.concurrent.TimeoutException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

@WebServlet("/twinder")
public class TwinderServlet extends HttpServlet {

  final String QUEUE_NAME = "tempStore";
  final int RANDOMSTRING_LENGTH = 256;
  final int MAX_SWIPER_NUM = 5000;
  final int MAX_SWIPEE_NUM = 1000000;
  final int MIN_NUM = 1;
  private ConnectionFactory factory;
  private Connection connection;
  final int MAX_CHANNELS = 10;
  private RMQChannelFactory RMQFactory;
  private RMQChannelPool getPool;
  private RMQChannelPool postPool;
  private RMQChannelPool pool;
  private MongoClient mdbClient;
  private MongoDatabase db;

  @Override
  public void init() throws ServletException {
    factory = new ConnectionFactory();
    factory.setHost("ec2-52-26-208-234.us-west-2.compute.amazonaws.com");
    factory.setUsername("jaewoo");
    factory.setPassword("wodn1017");
    factory.setVirtualHost("cherry_broker");
    MongoClient mdbClient = MongoClients.create("mongodb+srv://jaewoo:wodn1017@cs6650.o3m9wao.mongodb.net/?retryWrites=true&w=majority");
    this.db = mdbClient.getDatabase("twinderDB");

    System.out.println("Initialized");
    try {
      connection = factory.newConnection();
      RMQFactory = new RMQChannelFactory(connection);
      pool = new RMQChannelPool(MAX_CHANNELS, RMQFactory);

    } catch (IOException e) {
      throw new RuntimeException("Error initializing RMQ connection", e);
    } catch (TimeoutException e) {
      throw new RuntimeException(e);
    }
//    System.out.println(this.connection.toString());
  }
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    System.out.println("received a get request");
    String urlPath = request.getPathInfo();
    String[] urlParts = urlPath.split("/");
    PrintWriter out = response.getWriter();
    if (!isInputValid(urlParts)) {
//      return 400 Invalid inputs
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      out.write("400 error invalid input" + response.getStatus());
      out.flush();
    } else {
//     when it's valid
//      System.out.println("it's a valid request");
//      System.out.println("url path: " + urlPath);
      String serviceId = urlParts[1];
      String userID = urlParts[2];
//      String userID = ;
      JsonObject result = new JsonObject();
      System.out.println("service id: " + serviceId);
      System.out.println("user id: " + userID);
      switch (serviceId) {
        case "matches":
          result = getMatchData(userID);
          break;
        case "stats":
          result = getStatsData(userID);
          break;
        default:
          break;
      }
      System.out.println("hello");
      if (result == null) {
//        404 user not found
        System.out.println("404 user not found");
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        out.write("404 user not found" + response.getStatus());
      }
//
      System.out.println("result is " + result.toString());
      response.setContentType("application/json");
      response.setStatus(HttpServletResponse.SC_OK);
      out.write(new Gson().toJson(result));
      out.flush();
    }
  }

  private JsonObject getMatchData(String userId) {
    Document filter1 = new Document("swiper", userId);
//    filter2 is a filter to get user as a swipee
    Document filter2 = new Document("swipee", userId);
    MongoCollection likeCollection = this.db.getCollection("likes");
    ArrayList<String> likedUsers = getListOfUser(likeCollection, filter1, true);
    ArrayList<String> listOfUserLikedMe = getListOfUser(likeCollection, filter2, false);
//    check if the query data is not empty
    if (likedUsers.isEmpty()) {
//      return 404 user not found
      return null;
    }
    HashSet<String> possibleMatch = new HashSet<>();
    ArrayList<String> match = new ArrayList<>();
    for (String user : likedUsers) {
      possibleMatch.add(user);
    }
    for (String user : listOfUserLikedMe) {
      if (possibleMatch.contains(user)) {
        match.add(user);
      }
    }
    JsonObject result = new JsonObject();
    result.add("matchList", new Gson().toJsonTree(match));
    return result;
  }

  private ArrayList<String> getListOfUser(MongoCollection coll, Document filter, boolean isSwiper) {
    ArrayList<String> result = new ArrayList<>();
    MongoCursor<Document> cursor = coll.find(filter).iterator();
    while (cursor.hasNext()) {
      Document document = cursor.next();
      if (isSwiper) {
        result.add(document.getString("swipee"));
      } else {
        result.add(document.getString("swiper"));
      }
    }
    return result;
  }
  private JsonObject getStatsData(String userId) {
    System.out.println("get stats data");
    MongoCollection likeCollection = this.db.getCollection("likes");
    MongoCursor<Document> likeCounter = likeCollection.find(new Document("_id", userId)).cursor();
//    MongoCursor<Document> dislikeCounter = dislikeCountColl.find(new Document("_id", userId)).cursor();

//    FindIterable<Document> likeDoc = likeCollection.find(new Document("_id", userId));
//    FindIterable<Document> dislikeDoc = dislikeCollection.find(new Document("_id", userId));
//    if (likeDoc == null || dislikeDoc == null) {
//      return null;
//    }
    JsonObject resultObject = new JsonObject();
   if (likeCounter.hasNext()) {
     Document curr = likeCounter.next();
     int numLikes = curr.getInteger("numLikes");
     int numDislikes = curr.getInteger("numDislikes");
     resultObject.addProperty("numLikes", numLikes);
     resultObject.addProperty("numDislikes", numDislikes);
     return resultObject;
   }
   return resultObject;
//   int numLikes = 0;
//   int numDislikes = 0;
//    while (likeCounter.hasNext()) {
//      numLikes += 1;
//    }
//
//    while (dislikeCounter.hasNext()) {
//      numDislikes += 1;
//    }


  }

  private Integer getCounter(MongoCollection coll, String swiperId) {
    Document filter = new Document("swiper", swiperId);
    MongoCursor<Document> cursor = coll.find(filter).iterator();
//    System.out.println(swiperId);
    int counter = 0;
    while (cursor.hasNext()) {
      Document document = cursor.next();
      counter += 1;
    }
    return counter;
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String urlPath = request.getPathInfo();
    PrintWriter out = response.getWriter();
    response.setCharacterEncoding("UTF-8");
    response.setContentType("application/json");
    Message statusMessage = new Message("");
    Gson gson = new Gson();
    String[] urlParts = urlPath.split("/");
    JsonParser parser = new JsonParser();
    BufferedReader reader = request.getReader();
    JsonObject json = (JsonObject) parser.parse(ReadBigStringIn(reader));

//    System.out.println("received the request");
//    System.out.println("json object : " + json);
    if (isUrlValid(urlParts) && isDataValid(json)) {
      String leftOrRight = urlParts[2];
      json.addProperty("swipe", leftOrRight);
      statusMessage.setMessage("Created!");
      gson.toJson(statusMessage);
//      System.out.println("Start sending a payload to rmq");
      produceMessage(json);
      response.setStatus(HttpServletResponse.SC_CREATED);
//      System.out.println("Successful");
    } else {
      statusMessage.setMessage("Invalid input");
//      System.out.println(urlPath);
//      System.out.println(json.toString());
//      System.out.println(isUrlValid(urlParts));
//      System.out.println("failed");
      gson.toJson(statusMessage);
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
//    out.write("" + response.getStatus());
  }

  private void produceMessage(JsonObject payload) {
    try {
      Channel channel = this.pool.borrowObject();
      channel.queueDeclare(QUEUE_NAME, true, false, false, null);

// Publish a message to the queue with the deliveryMode property set to 2
      channel.basicPublish("", "tempStore", MessageProperties.PERSISTENT_TEXT_PLAIN,
          payload.toString().getBytes(StandardCharsets.UTF_8));
//      System.out.println("done");
      this.pool.returnObject(channel);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
//    Producer producer = new Producer(this.pool, payload);
//    System.out.println("payload at produceMessage function : " + payload);
//    producer.send();

  }
  private boolean isUrlValid(String[] urlParts) {
//    System.out.println(urlParts.length);

    if (urlParts.length != 3) {
      return false;
    }
    String leftOrRight = urlParts[2];
    if (!leftOrRight.equals("left") && !leftOrRight.equals("right")) {
      return false;
    }

    return true;
  }

  private boolean isInputValid(String[] urlParts) {
    if (urlParts.length != 3) {
      return false;
    }
    try {
      int userId = Integer.parseInt(urlParts[2]);
      if (userId > MAX_SWIPER_NUM || userId < MIN_NUM) {
        return false;
      }
    } catch (NumberFormatException e ) {
      return false;
    }
    return true;
  }


  private boolean isDataValid(JsonObject json) {
//    validate whether the input json payload contains correct parameters
    if (!json.has("swiper") || !json.has("swipee") || !json.has("comment")) {
//      System.out.println("no parameter");
      return false;
    }
    int swiperNum = json.get("swiper").getAsInt();
    int swipeeNum = json.get("swipee").getAsInt();

    if (json.get("comment").getAsString().length() != RANDOMSTRING_LENGTH) {
//      System.out.println("wrong length");
      return false;
    }
    if (swiperNum > MAX_SWIPER_NUM || swiperNum < MIN_NUM || swipeeNum > MAX_SWIPEE_NUM || swipeeNum < MIN_NUM) {
//      System.out.println("num not in range");
      return false;
    }
    return true;
  }
  private String ReadBigStringIn(BufferedReader buffIn) throws IOException {
    StringBuilder everything = new StringBuilder();
    String line;
    while( (line = buffIn.readLine()) != null) {
      everything.append(line);
    }
    return everything.toString();
  }
}