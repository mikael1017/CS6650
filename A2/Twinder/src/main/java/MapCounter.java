import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapCounter {

  private Map<String, Integer> counter;


  public MapCounter() {
    this.counter = new ConcurrentHashMap<>();
  }
  synchronized public void add(String swiperId) {
    this.counter.put(swiperId, this.counter.getOrDefault(swiperId, 0) + 1);
  }

  public String toString() {
    return this.counter.toString();
  }
}
