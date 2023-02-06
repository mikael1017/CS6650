
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class ClientMultiThreaded {

  public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
    final int NUMTHREADS = 200;

    final RequestCounter counter = new RequestCounter();
    CountDownLatch completed = new CountDownLatch(NUMTHREADS);
    long startTime = System.currentTimeMillis();
    for (int i = 0; i < NUMTHREADS; i++) {
      counter.inc();
      new ClientThread(completed).start();
    }
//    new ClientThread(barrier).start();
    completed.await();
    long endTime = System.currentTimeMillis();
    long totalTime = endTime - startTime;
    System.out.println("Total time it took : " + totalTime + "ms");

    System.out.println("Total threads ran : " + counter.getVal());
    System.out.println("terminating ....");
  }

}
