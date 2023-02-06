import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.PrintWriter;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/swipe")
public class TwinderServlet extends HttpServlet {

  final int RANDOMSTRING_LENGTH = 256;

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

    if (isUrlValid(urlParts) && isDataValid(json)) {
      response.setStatus(HttpServletResponse.SC_CREATED);
      statusMessage.setMessage("Created!");
      String messageJson = gson.toJson(statusMessage);


    } else {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      statusMessage.setMessage("Invalid input");
      String messageJson = gson.toJson(statusMessage);
    }
    out.write("" + response.getStatus());
  }

  private boolean isUrlValid(String[] urlParts) {
//    System.out.println("coming into url valid function :");
//    System.out.println("length" + urlParts.length);
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
//    System.out.println("coming into data valid function :");
    int swiperNum = json.get("swiper").getAsInt();
    int swipeeNum = json.get("swipee").getAsInt();

    if (json.get("comment").getAsString().length() != RANDOMSTRING_LENGTH) {
      return false;
    }
    if (swiperNum > 5000 || swiperNum < 1 || swipeeNum > 1000000 || swipeeNum < 1) {
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
