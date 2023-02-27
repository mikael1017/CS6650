import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.concurrent.TimeoutException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/swipe")
public class TwinderServlet extends HttpServlet {

  final int RANDOMSTRING_LENGTH = 256;
  final int MAX_SWIPER_NUM = 5000;
  final int MAX_SWIPEE_NUM = 1000000;
  final int MIN_NUM = 1;
  private ConnectionFactory factory;
  private Connection connection;

  @Override
  public void init() throws ServletException {
    factory = new ConnectionFactory();
    factory.setHost("ec2-54-185-201-229.us-west-2.compute.amazonaws.com");
    factory.setUsername("jaewoo");
    factory.setPassword("wodn1017");
    factory.setVirtualHost("cherry_broker");
    try {
      connection = factory.newConnection();
    } catch (IOException e) {
      throw new RuntimeException("Error initializing RMQ connection", e);
    } catch (TimeoutException e) {
      throw new RuntimeException(e);
    }
    System.out.println(this.connection.toString());
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
//    Channel channel = this.connection.createChannel();
    System.out.println(this.connection.toString());
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

    System.out.println("received the request");
    if (isUrlValid(urlParts) && isDataValid(json)) {
      response.setStatus(HttpServletResponse.SC_CREATED);
      String leftOrRight = urlParts[1];
      json.addProperty("swipe", leftOrRight);
      statusMessage.setMessage("Created!");
      gson.toJson(statusMessage);
      System.out.println("Start sending a payload to rmq");
      produceMessage(json, this.connection);
      System.out.println("Successful");
    } else {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      statusMessage.setMessage("Invalid input");
      gson.toJson(statusMessage);
    }
    out.write("" + response.getStatus());
  }

  private void produceMessage(JsonObject payload, Connection conn) {
    FanoutExchange ex = new FanoutExchange();
    ex.createExchangeAndQueue(conn);
    Producer queueProducer = new Producer(this.connection, payload);
    try {
      queueProducer.send();
    } catch (TimeoutException e) {
      System.out.println("Timeout error");
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
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

  private boolean isDataValid(JsonObject json) {
//    validate whether the input json payload contains correct parameters
    if (!json.has("swiper") || !json.has("swipee") || !json.has("comment")) {
//      System.out.println("no parameter");
      return false;
    }
    int swiperNum = json.get("swiper").getAsInt();
    int swipeeNum = json.get("swipee").getAsInt();

    if (json.get("comment").getAsString().length() != RANDOMSTRING_LENGTH) {
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
