import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class exampleMongo {

  public static void main(String[] args) {

    MongoClient client = MongoClients.create("mongodb+srv://jaewoo:wodn1017@cs6650.o3m9wao.mongodb.net/?retryWrites=true&w=majority");
    MongoDatabase db = client.getDatabase("twinderDB");
    MongoCollection col = db.getCollection("twinderCollection");

    Document sampleDoc = new Document("_id", "2").append("name", "Jaewoo Smith");
    col.insertOne(sampleDoc);
  }

}
