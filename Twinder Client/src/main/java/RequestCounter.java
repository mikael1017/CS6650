
public class RequestCounter {
  private int count = 0;


  public RequestCounter() {

  }
  synchronized public void inc() {
    count++;
  }

  public int getVal() {
    return this.count;
  }


}
