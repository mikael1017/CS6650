public class LatencyCounter {
  private int totalLatency = 0;


  public LatencyCounter() {

  }
  synchronized public void add(long latency) {
    this.totalLatency += latency;
  }

  public int getVal() {
    return this.totalLatency;
  }

}
