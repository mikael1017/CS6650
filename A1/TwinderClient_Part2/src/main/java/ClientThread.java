

import io.swagger.client.*;
import io.swagger.client.model.*;
import io.swagger.client.api.SwipeApi;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;

public class ClientThread extends Thread {
  private static final int RANDOMSTRING_LENGTH = 256;
  private static final int MAX_RETRIES = 1;

  private final static int NUM_REQUESTS = 5000;
  private final CountDownLatch latch;
  private final RequestCounter counter;
  private final RequestCounter failCounter;
  private final LatencyCounter latencyCounter;


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
