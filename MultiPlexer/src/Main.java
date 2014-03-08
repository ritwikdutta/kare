import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

/**
 * @author arshsab
 * @since 03 2014
 */

public class Main {
    public static void main(String... args) throws IOException {
        Properties props = new Properties();

        props.load(new FileInputStream("slaves.properties"));

        ArrayList<String> slaveServers = new ArrayList<String>();

        for (int i = 0; ; i++) {
            String server = props.getProperty("server." + i);

            if (server != null)
                slaveServers.add(server);
        }


    }
}
