public class NamingThread implements Runnable {
  private String name;

  public NamingThread(String threadName) {
    System.out.println("Constructor called: " + threadName);
    name = threadName;
  }

  public void setName (String threadName) {
    name = threadName;
  }
  @Override
  public void run() {
//    Display info about this thread
    System.out.println("Run called:" + name);
    System.out.println(name + ":" + Thread.currentThread());
//    and terminate silently
//    jaewo
  }
}
