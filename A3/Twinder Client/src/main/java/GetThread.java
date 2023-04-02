

import io.swagger.client.*;
import io.swagger.client.api.*;
import io.swagger.client.model.*;
import io.swagger.client.api.SwipeApi;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class GetThread implements Runnable {
  private static final int RANDOMSTRING_LENGTH = 256;
  private static final int MAX_RETRIES = 1;

  private final static int NUM_REQUESTS = 5;
  private final RequestCounter counter;
  private final RequestCounter failCounter;
  private final LatencyCounter latencyCounter;


  public GetThread(RequestCounter counter, RequestCounter failCounter, LatencyCounter latencyCounter) {
    this.counter = counter;
    this.failCounter = failCounter;
    this.latencyCounter = latencyCounter;
  }

  public  void run() {
//      TODO: need to send 500K post requests to the server
//        for each request,
//        swipe - either "left" or "right"
//        swiper - between 1 - 5000
//        swipee -between 1 - 1,000,000
//        comment - random string of 256 characters

    ThreadLocalRandom random = ThreadLocalRandom.current();
//  for each thread do this step
//    ------------------------------
      for (int i = 0; i < NUM_REQUESTS; i++) {
        boolean needToRetry = true;
        int numRetry = 0;
        int swiper = random.nextInt(1, 5000);
        String userId = String.valueOf(swiper);
        StatsApi statsInstance = new StatsApi();
        MatchesApi matchInstance = new MatchesApi();
        //    Using while loop to add a retry feature of api call
        while (needToRetry && numRetry < MAX_RETRIES) {
          try {
            System.out.println("sending the request to the server");
            long before = System.currentTimeMillis();
//            change this part to manual http request
            manualHttpRequest(userId);

//            statsInstance.matchStats(userId);
//                      matchInstance.matches(swiperId);
            long after = System.currentTimeMillis();
            System.out.println("got result");

            long delay = after - before;
            System.out.println("Get Latency : " + delay + " ms");
            this.latencyCounter.add(delay);
            needToRetry = false;


          } catch (IOException e) {
            throw new RuntimeException(e);
          }
          this.counter.inc();
        }
      }


  }

  private void manualHttpRequest(String userId) throws IOException {
    // TODO Auto-generated method stub
    URL url = new URL("http://ec2-35-162-108-4.us-west-2.compute.amazonaws.com:8080/Twinder_war/api/stats/" + userId);
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("GET");

    int status = con.getResponseCode();
    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
    String inputLine;
    StringBuffer content = new StringBuffer();
    while ((inputLine = in.readLine()) != null) {
      content.append(inputLine);
    }
    in.close();

//    System.out.println("Response code: " + status);
//    System.out.println("Response body: " + content.toString());
  }

  /**
   * generate a random string of length that is equal to the input
   *
   * @param length
   * @return
   */

}
