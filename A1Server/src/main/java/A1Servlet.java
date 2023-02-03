import com.google.gson.Gson;
import java.io.PrintWriter;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;


@WebServlet("/swipe")
public class A1Servlet extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String urlPath = request.getPathInfo();
//    System.out.println(urlPath);
    PrintWriter out = response.getWriter();
    response.setCharacterEncoding("UTF-8");
    response.setContentType("application/json");
    Message statusMessage = new Message("");
    Gson gson = new Gson();

    if (urlPath == null || urlPath.isEmpty()) {
      statusMessage.setMessage("Invalid input");
      String messageJson = gson.toJson(statusMessage);
      out.print(messageJson);
      System.out.println(statusMessage.getMessage());
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

//      handle error messages

    }
    String[] urlParts = urlPath.split("/");
    if (!isUrlValid(urlParts)) {
      statusMessage.setMessage("Invalid input");

      String messageJson = gson.toJson(statusMessage);
      out.print(messageJson);
      System.out.println(statusMessage.getMessage());
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    } else {
      statusMessage.setMessage("Created!");
      String messageJson = gson.toJson(statusMessage);
      out.print(messageJson);
      System.out.println(statusMessage.getMessage());
      System.out.println(request.getReader().readLine());
      response.setStatus(HttpServletResponse.SC_CREATED);
//      return dummy data
    }
//     Send the response
  }

  private boolean isUrlValid(String[] urlParts) {
    System.out.println("length" + urlParts.length);
    if (urlParts.length != 2) {
      return false;
    }
    String leftOrRight = urlParts[1];
    if (!leftOrRight.equals("left") && !leftOrRight.equals("right")) {
      return false;
    }

    for (int i = 0; i < urlParts.length; i++) {
      System.out.println(i);
      System.out.println(urlParts[i]);
    }

    return true;
  }
}
