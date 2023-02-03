import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ClientThread implements Runnable {

  private HttpClient client;
  private HttpRequest request;
  private long startTime;

  public ClientThread(HttpClient client, HttpRequest request, long startTime) {
    this.client = client;
    this.request = request;
    this.startTime = startTime;
  }

  @Override
  public void run() {
    System.out.println("Run called : ");

    HttpResponse<String> response = null;
    try {
      response = client.send(request, HttpResponse.BodyHandlers.ofString());
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    HttpHeaders headers = response.headers();
    long timeTaken = System.currentTimeMillis() - this.startTime;
    System.out.println("Time took :" + timeTaken + "ms");
//    headers.map().forEach((k, v) -> System.out.println(k + ":" + v));

//    System.out.println(headers);
//    // print status code
//    System.out.println(response.statusCode());
//
//    // print response body
//    System.out.println(response.body());
//
//    System.out.println(response.request().headers());

  }
}
