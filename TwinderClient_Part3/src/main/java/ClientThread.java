

import com.opencsv.CSVWriter;
import io.swagger.client.*;
import io.swagger.client.model.*;
import io.swagger.client.api.SwipeApi;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;

public class ClientThread extends Thread {
  private static final int RANDOMSTRING_LENGTH = 256;
  private static final int MAX_RETRIES = 5;

  private final static int NUM_REQUESTS = 10000;
  private final CountDownLatch latch;
  private final RequestCounter counter;
  private final RequestCounter failCounter;
  private final LatencyCounter latencyCounter;


  public ClientThread() throws IOException {
    this.latch = null;
    this.counter = null;
    this.failCounter = null;
    this.latencyCounter = null;
  }
  public ClientThread(CountDownLatch latch, RequestCounter counter, RequestCounter failCounter, LatencyCounter latencyCounter) {
    this.latch = latch;
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


      int swiper = random.nextInt(1, 5000);
      //    swiper = 5001;
      int swipee = random.nextInt(1, 1000000);
      boolean isLeft = random.nextBoolean();
      boolean needToRetry = true;
      int numRetry = 0;
      String value = (isLeft) ? "left" : "right";
      String randomString = generateString(RANDOMSTRING_LENGTH, random);

      SwipeApi apiInstance = new SwipeApi();
      SwipeDetails body = new SwipeDetails(); // SwipeDetails | response details

      body.setSwipee(Integer.toString(swipee));
      body.setSwiper(Integer.toString(swiper));
      body.setComment(randomString);

      //    Using while loop to add a retry feature of api call

      while (needToRetry && numRetry < MAX_RETRIES) {
        try {
          long before = System.currentTimeMillis();
          String result = apiInstance.swipe(body, value);
          long after = System.currentTimeMillis();

          System.out.println(result);
          long delay = after - before;
          System.out.println("Latency : " + delay + " ms");
          latencyCounter.add(delay);
          needToRetry = (result.equals("201")) ? false : true;

          String dirName = "/Users/jaewoocho/Desktop/School_Work/CS6650/TwinderClient_Part3/src/main/java/result.csv";
          File file = new File(dirName);
          try {
            FileWriter outputFile = new FileWriter(file, true);
            CSVWriter writer = new CSVWriter(outputFile);
            List<String[]> data = new ArrayList<String[]>();
            DateFormat simple = new SimpleDateFormat(
                "dd MMM yyyy HH:mm:ss:SSS Z");

            // Creating date from milliseconds
            // using Date() constructor
            Date currentDate = new Date(before);
            String startTime = simple.format(currentDate);
            String latency = "" + delay;
            String[] curData = {startTime, "POST", latency, result};
            writer.writeNext(curData);
            writer.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        } catch (ApiException e) {
          needToRetry = true;
          System.err.println("Exception when calling SwipeApi#swipe");
          System.out.println("try number : " + numRetry);
          System.out.println(e.getCode());
          numRetry++;
          this.failCounter.inc();
        }
        this.counter.inc();
      }
    }
    latch.countDown();
  }

  /**
   * generate a random string of length that is equal to the input
   *
   * @param length
   * @return
   */
  private static String generateString(int length, ThreadLocalRandom random) {
    String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    StringBuilder builder = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      builder.append(alphabet.charAt(random.nextInt(alphabet.length())));
    }
    return builder.toString();
  }

}
