
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class ClientSingleThread {
  static CyclicBarrier barrier;

  public static void main(String[] args) throws BrokenBarrierException, InterruptedException {

    CountDownLatch completed = new CountDownLatch(1);
    new ClientThread(completed).start();

    System.out.println("terminating ....");
  }

}
