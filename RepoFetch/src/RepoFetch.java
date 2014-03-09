import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import java.util.HashMap;

/**
 * Created by afoote97 on 3/8/14.
 */
public class RepoFetch {
    public static void main(String[] args) throws Exception {

        API api = new API();
        RequestCallback cb = new RequestCallback();
        api.get("search?created:2014-01-01..2014-02-01%20stars:10&per_page:100&page:1", cb);

    }

}
