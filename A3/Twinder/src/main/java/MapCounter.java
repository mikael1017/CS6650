import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapCounter {

  private Map<String, Integer> counter;


  public MapCounter() {
    this.counter = new ConcurrentHashMap<>();
  }
  synchronized public void add(String userId) {
    this.counter.put(userId, this.counter.getOrDefault(userId, 0) + 1);
  }

  public String toString() {
    return this.counter.toString();
  }
}
