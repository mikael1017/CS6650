import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.time.*;
import java.util.concurrent.CountDownLatch;

public class RequestCounter {

  private static final HttpClient httpClient = HttpClient.newBuilder()
      .version(Version.HTTP_2)
      .connectTimeout(Duration.ofSeconds(10))
      .build();

  private static final int NUMTHREADS = 100;
  private int count = 0;

  synchronized public void inc() {
    count++;
  }

  public int getVal() {
    return this.count;
  }

  public static void main(String[] args) throws IOException, InterruptedException {


    HttpRequest request = HttpRequest.newBuilder()
        .GET()
        .uri(URI.create("http://localhost:8080/HelloWorld/hello/get"))
        .setHeader("User", "HttpClient bot")
        .build();

    final RequestCounter counter = new RequestCounter();
    CountDownLatch completed = new CountDownLatch(NUMTHREADS);

    for (int i = 0; i < NUMTHREADS; i++) {
      ClientThread thread = new ClientThread(httpClient, request, System.currentTimeMillis());
      new Thread(thread).start();
    }
  }
}
