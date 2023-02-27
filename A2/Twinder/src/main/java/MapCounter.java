import java.util.HashMap;
import java.util.Map;

public class MapCounter {

  private Map<String, Integer> counter;


  public MapCounter() {
    counter = new HashMap<>();
  }
  synchronized public void add(String swiperId) {
    this.counter.put(swiperId, this.counter.getOrDefault(swiperId, 0) + 1);
  }

  public String toString() {
    return this.counter.toString();
  }
}
