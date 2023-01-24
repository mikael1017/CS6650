public class Philosopher implements Runnable {

  private final Object leftChopStick;
  private final Object rightChopStick;

  Philosopher(Object leftChopStick, Object rightChopStick) {
    this.leftChopStick = leftChopStick;
    this.rightChopStick = rightChopStick;
  }

  private void LogEvent(String event) throws InterruptedException {
    System.out.println(Thread.currentThread().getName() + "" + event);
    Thread.sleep(1000);
  }

  @Override
  public void run() {
    try {
      while (true) {
        LogEvent(": Thinking deeply");
        synchronized (leftChopStick) {
          LogEvent(": Picked up left chop stick");
          synchronized (rightChopStick) {
            LogEvent(": Picked up right chop stick - eating");
            LogEvent(": Put down right chopstick");
          }
          LogEvent(": Put down left chopstick. Returning to deep thinking");
        }
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

}
