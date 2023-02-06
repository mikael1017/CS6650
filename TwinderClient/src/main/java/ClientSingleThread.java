
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class ClientSingleThread {
  static CyclicBarrier barrier;

  public static void main(String[] args) throws BrokenBarrierException, InterruptedException {

    new ClientThread().start();

    System.out.println("terminating ....");
  }

}
