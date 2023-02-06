
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import org.HdrHistogram.Histogram;

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
//    process stored data into


    long endTime = System.currentTimeMillis();
    long totalTime = endTime - startTime;
    int totRequest = counter.getVal() + failCounter.getVal();
    double throughput =  (totRequest / ((double)totalTime/1000.0));
    double avgLatency = latencyCounter.getVal() / (counter.getVal() + failCounter.getVal());
    Histogram histogram = new Histogram(10000L, 4);
    try {
      String dirName = "/Users/jaewoocho/Desktop/School_Work/CS6650/TwinderClient_Part3/src/main/java/result.csv";
      CSVReader reader = new CSVReader(new FileReader(dirName));
      String[] nextLine;
      while ((nextLine = reader.readNext()) != null) {
        int col = 1;
        for (String token : nextLine) {
          if (col == 3) {
            histogram.recordValue(Long.valueOf(token));
          }
          col += 1;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (CsvValidationException e) {
      throw new RuntimeException(e);
    }

    System.out.println("terminating ....");
    System.out.println("Results: ");
    System.out.println("Total requests ran : " + counter.getVal());
    System.out.println("Total failed requests : " + failCounter.getVal());
    System.out.println("Total time it took : " + totalTime + " ms");
    System.out.println("Mean response time : " + (latencyCounter.getVal() / counter.getVal()) + " ms");
    System.out.println("Median response time : " + histogram.getValueAtPercentile(50) + " ms");
    System.out.println("99th Percentile : " + histogram.getValueAtPercentile(99) + " ms");
    System.out.println("Min response time : " + histogram.getValueAtPercentile(0) + " ms");
    System.out.println("Max response time : " + histogram.getValueAtPercentile(100) + " ms");

    double expectedThroughput =  (NUMTHREADS / (avgLatency/1000.0));
    System.out.println("Total throughput : " + throughput + " requests/sec");
    System.out.println("Expected throughput using mean response time : " + expectedThroughput + " requests/sec");
  }

}
