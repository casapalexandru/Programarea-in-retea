import com.google.gson.JsonParser;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Request {
    public static List <String> dataList = new ArrayList <> ( );
    static Logger logger = Logger.getLogger (Request.class.getName ( ));
    private static HttpClient httpClient;
    private static int index = 0;
    private static List <String> linkList = new ArrayList <> ( );
    private static ExecutorService executor;
    private static DataManager dataManager;
    private String uri = "/register";

    Request() {
        this.httpClient = new HttpClient ( );
        this.executor = Executors.newCachedThreadPool ( );
        this.dataManager = new DataManager ( );
    }


    public boolean initiate() {
        executor.submit (new Thread (uri));
        long startTime = System.currentTimeMillis ( );
        while (true) {
            long endTime = System.currentTimeMillis ( );
            long totalTime = endTime - startTime;
            if (totalTime >= 20000) {
                executor.shutdown ( );
                return true;
            }

        }
    }

    static class Thread implements Runnable {
        static Logger logger = Logger.getLogger (Thread.class.getName ( ));
        private String uri;


        Thread(String uri) {
            this.uri = uri;
            logger.info (uri);

        }

        @Override
        public void run() {
            try {
                String result = httpClient.get (uri);
                httpClient.searchJsonKey ("link", JsonParser.parseString (result), linkList);
                String data = httpClient.searchKeyValue ("data", result);
                if (data != null) {
                    String type = httpClient.searchKeyValue ("mime_type", result);
                    if (type == null) {
                        type = "application/json";
                    }
                    logger.info ("Result from " + uri + " " + type + " \n" + dataManager.toJson (type, data));
                    dataList.add (dataManager.toJson (type, data));
                }
            } catch (IOException e) {
                e.printStackTrace ( );
            }

            for (int i = index; i < linkList.size ( ); i++) {
                executor.submit (new Thread (linkList.get (i)));
                index++;

            }
        }
    }
}
