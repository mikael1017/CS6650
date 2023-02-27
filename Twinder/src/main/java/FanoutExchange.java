import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class FanoutExchange {

  public static String EXCHANGE_NAME = "fanout-exchange";
  public static String QUEUE_NAME_1 = "fanout-queue-1";
  public static String QUEUE_NAME_2 = "fanout-queue-2";

  public static String ROUTING_KEY = "";

  public void createExchangeAndQueue(Connection conn){
    try{
      if(conn != null){
        Channel channel = conn.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout", true);
        // First Queue
        channel.queueDeclare(QUEUE_NAME_1, true, false, false, null);
        channel.queueBind(QUEUE_NAME_1, EXCHANGE_NAME, ROUTING_KEY);

        // Second Queue
        channel.queueDeclare(QUEUE_NAME_2, true, false, false, null);
        channel.queueBind(QUEUE_NAME_2, EXCHANGE_NAME, ROUTING_KEY);

        channel.close();
      }
    }catch(Exception e){
      e.printStackTrace();
    }
  }
}