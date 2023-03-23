import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
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

@WebServlet(urlPatterns = {"/swipe", "/matches", "/stats"})
public class TwinderServlet extends HttpServlet {

  final int RANDOMSTRING_LENGTH = 256;
  final int MAX_SWIPER_NUM = 5000;
  final int MAX_SWIPEE_NUM = 1000000;
  final int MIN_NUM = 1;
  private ConnectionFactory factory;
  private Connection connection;

  private MongoClient mdbClient;

  @Override
  public void init() throws ServletException {
    this.mdbClient = MongoClients.create("mongodb+srv://jaewoo:wodn1017@cs6650.o3m9wao.mongodb.net/?retryWrites=true&w=majority");
  }
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    String urlPath = request.getPathInfo();
    String serviceId = urlPath.substring(0, 1);
    String userID = urlPath.substring(1);

    switch (serviceId) {
      case "matches": break;



      case "stats": break;


      default: break;
    }


  }

  private String getMatchData(String userId) {
//  {
//    "matchList": [
//      "string"
//  ]
//}
    return "";
  }
  private String getStatsData(String userId) {
//
//   {
//    "numLlikes": 72,
//    "numDislikes": 489
//   }
    return "";

  }




    @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
//    when it receives the post request, it sends the payload to the persistent queue
//      then when consumer receives the message, writes to swipeData into DB

    String urlPath = request.getPathInfo();
    PrintWriter out = response.getWriter();
    response.setCharacterEncoding("UTF-8");
    response.setContentType("application/json");
    Message statusMessage = new Message("");
    Gson gson = new Gson();
    String[] urlParts = urlPath.split("/");
    JsonParser parser = new JsonParser();
    BufferedReader reader = request.getReader();
    Document payload = Document.parse(ReadBigStringIn(reader));
    System.out.println("received the request");
    if (isUrlValid(urlParts) && isDataValid(payload)) {
      response.setStatus(HttpServletResponse.SC_CREATED);
      statusMessage.setMessage("Created!");
      gson.toJson(statusMessage);
      String leftOrRight = urlParts[1];
      payload.append("swipe", leftOrRight);
      System.out.println("Successful");
    } else {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      statusMessage.setMessage("Invalid input");
      gson.toJson(statusMessage);
    }
    out.write("" + response.getStatus());
  }

  private void send(Document payload) {
    MongoDatabase db = this.mdbClient.getDatabase("twinderDB");
    MongoCollection col = db.getCollection("tempStore");
    col.insertOne(payload);
  }

  private boolean isUrlValid(String[] urlParts) {
    if (urlParts.length != 2) {
      return false;
    }
    String leftOrRight = urlParts[1];
    if (!leftOrRight.equals("left") && !leftOrRight.equals("right")) {
      return false;
    }

    return true;
  }

  private boolean isDataValid(Document data) {
//    validate whether the input json payload contains correct parameters
    if (!data.containsKey("swiper") || !data.containsKey("swipee") || !data.containsKey("comment")) {
//      System.out.println("no parameter");
      return false;
    }
    int swiperNum = data.getInteger("swiper");
    int swipeeNum = data.getInteger("swipee");

    if (data.getString("comment").length() != RANDOMSTRING_LENGTH) {
      System.out.println("wrong length");
      return false;
    }
    if (swiperNum > MAX_SWIPER_NUM || swiperNum < MIN_NUM || swipeeNum > MAX_SWIPEE_NUM || swipeeNum < MIN_NUM) {
      System.out.println("num not in range");
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
