import org.apache.log4j.Logger;

import java.util.Properties;


public class Main {
    static Logger logger = Logger.getLogger (Main.class.getName ( ));
    private static  Integer SERVER_PORT ;

    public static void main(String[] args)  {
        logger.info ("== Program Started ==");
        DataManager dataManager = new DataManager ();
        SERVER_PORT = Integer.parseInt (dataManager.getProperty ("tcp.server.port"));
        if (new Request ( ).initiate ( )) {
            logger.info ("== Collected were finished ==");
        }
        TcpMultiServer tcpMultiServer = new TcpMultiServer ();
        tcpMultiServer.start (SERVER_PORT);

    }
}
