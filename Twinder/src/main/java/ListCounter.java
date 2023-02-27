import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class ListCounter {
  private Map<String, ArrayList<String>> counter;
  public ListCounter() {
    counter = new HashMap<>();
  }
  synchronized public void add(String swiperId, String swipeeId) {
    if (!this.counter.containsKey(swiperId)) {
      this.counter.put(swiperId, new ArrayList<>());
    }
    this.counter.get(swiperId).add(swipeeId);
  }

  public String toString() {
    return this.counter.toString();
  }


}
