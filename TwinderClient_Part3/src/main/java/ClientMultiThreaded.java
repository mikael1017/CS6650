
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class ClientMultiThreaded {

  public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
    final int NUMTHREADS = 50;

    final RequestCounter counter = new RequestCounter();
    CountDownLatch completed = new CountDownLatch(NUMTHREADS);
    final RequestCounter failCounter = new RequestCounter();
    final LatencyCounter latencyCounter = new LatencyCounter();
    long startTime = System.currentTimeMillis();
    for (int i = 0; i < NUMTHREADS; i++) {
      new ClientThread(completed, counter, failCounter, latencyCounter).start();
    }
//    new ClientThread(barrier).start();
    completed.await();
    long endTime = System.currentTimeMillis();
    long totalTime = endTime - startTime;
    int totRequest = counter.getVal() + failCounter.getVal();
    double throughput =  (totRequest / ((double)totalTime/1000.0));
    double avgLatency = latencyCounter.getVal() / (counter.getVal() + failCounter.getVal());
    System.out.println("terminating ....");
    System.out.println("Results: ");
    System.out.println("Total requests ran : " + counter.getVal());
    System.out.println("Total failed requests : " + failCounter.getVal());
    System.out.println("AVG latency : " + (latencyCounter.getVal() / counter.getVal()) + " ms");
    System.out.println("Total time it took : " + totalTime + " ms");
    System.out.println("Total throughput : " + throughput + " requests/sec");
    double expectedThroughput =  (NUMTHREADS / (avgLatency/1000.0));
    System.out.println("Expected throughput : " + expectedThroughput + " requests/sec");
  }

}
