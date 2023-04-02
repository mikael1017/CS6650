
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientMultiThreaded {

  public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
    final int NUMTHREADS = 100;

    RequestCounter postCounter = new RequestCounter();
    CountDownLatch completed = new CountDownLatch(NUMTHREADS);
    RequestCounter postFailCounter = new RequestCounter();
    LatencyCounter postLatencyCounter = new LatencyCounter();
    RequestCounter getCounter = new RequestCounter();
    RequestCounter getFailCounter = new RequestCounter();
    LatencyCounter getLatencyCounter = new LatencyCounter();

    long startTime = System.currentTimeMillis();
    Runnable postThread = new ClientThread(completed, postCounter, postFailCounter, postLatencyCounter);
    for (int i = 0; i < NUMTHREADS; i++) {
      new Thread(postThread).start();
    }
//    wait for post to start before get thread
    Thread.sleep(1000);

    Runnable getThread = new GetThread( getCounter, getFailCounter, getLatencyCounter);
    ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
    exec.scheduleAtFixedRate(getThread
    , 0, 1, TimeUnit.SECONDS);
    completed.await();
//    get.join();
    exec.shutdown();
    long endTime = System.currentTimeMillis();



    long totalTime = endTime - startTime;
//    int totRequest = counter.getVal() + failCounter.getVal();
//    double throughput =  (totRequest / ((double)totalTime/1000.0));
//    double avgLatency = latencyCounter.getVal() / (counter.getVal() + failCounter.getVal());
    System.out.println("terminating ....");
    printStat(postCounter, postFailCounter, postLatencyCounter, totalTime);
    printStat(getCounter, getFailCounter, getLatencyCounter, totalTime);

  }

  private static void printStat(RequestCounter counter, RequestCounter failCounter, LatencyCounter latencyCounter, long totalTime) {

    int totRequest = counter.getVal() + failCounter.getVal();
    double throughput =  (totRequest / ((double)totalTime/1000.0));
    System.out.println("Results: ");
    System.out.println("Total requests ran : " + counter.getVal());
    System.out.println("Total failed requests : " + failCounter.getVal());
    System.out.println("AVG latency : " + (latencyCounter.getVal() / counter.getVal()) + " ms");
    System.out.println("Total time it took : " + totalTime + " ms");
    System.out.println("Total throughput : " + throughput + " requests/sec");
//    double expectedThroughput =  (NUMTHREADS / (avgLatency/1000.0));
//    System.out.println("Expected throughput : " + expectedThroughput + " requests/sec");
  }

}
