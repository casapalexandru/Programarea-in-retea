import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class HttpClient {
    static Logger logger = Logger.getLogger (HttpClient.class.getName ( ));
    protected String accessToken;
    private String baseUri;
    private DataManager dataManager;


    HttpClient() {
            this.dataManager = new DataManager ( );
            String host = dataManager.getProperty ("http.host");
            String port = dataManager.getProperty ("http.port");
            this.baseUri = "http://" + host + ":" + port;
    }

    public String get(String uri) throws IOException {
        org.apache.http.client.HttpClient client = HttpClientBuilder.create ( ).build ( );
        String text = null;
        HttpGet request = new HttpGet (this.baseUri + uri);
        request.setHeader ("X-Access-Token", accessToken);
        HttpResponse response = client.execute (request);
        if (response.getStatusLine ( ).getStatusCode ( ) == 200) {
            BufferedReader reader = new BufferedReader (new InputStreamReader (response.getEntity ( ).getContent ( )));
            text = dataManager.readStreamFromReader (reader);
            if (uri.equals ("/register")) {
                searchJsonKey ("access_token", JsonParser.parseString (text), new ArrayList <> ( ));
            }
        } else {
            logger.error ("Http Request Failed with status code " + uri + ": " + response.getStatusLine ( ).getStatusCode ( ));
        }
        return text;
    }

    public String searchKeyValue(String key, String text) {
        String value = "";
        List <String> list = new ArrayList <> ( );
        searchJsonKey (key, JsonParser.parseString (text), list);
        if (list.isEmpty ( )) {
            return null;
        } else {
            value = list.get (0);
        }
        return value;
    }

    public List <String> searchJsonKey(String key, JsonElement jsonElement, List <String> list) {
        if (jsonElement.isJsonArray ( )) {
            for (JsonElement jsonElement1 : jsonElement.getAsJsonArray ( )) {
                searchJsonKey (key, jsonElement1, list);
            }
        } else {
            if (jsonElement.isJsonObject ( )) {
                Set <Map.Entry <String, JsonElement>> entrySet = jsonElement
                        .getAsJsonObject ( ).entrySet ( );
                for (Map.Entry <String, JsonElement> entry : entrySet) {
                    String key1 = entry.getKey ( );
                    if (key1.equals (key)) {
                        String value = entry.getValue ( ).toString ( );
                        try {
                            JSONObject jsonKeyObj = new JSONObject (value);
                            Iterator <String> keySet = jsonKeyObj.keys ( );
                            while (keySet.hasNext ( )) {
                                list.add (jsonKeyObj.get (keySet.next ( )).toString ( ));
                            }
                        } catch (JSONException e) {
                            logger.warn (e.getMessage ( ));
                            if (key.equals ("access_token")) {
                                accessToken = entry.getValue ( ).getAsString ( );
                                logger.info ("Access Token Received : " + accessToken);
                            } else {
                                list.add (entry.getValue ( ).getAsString ( ));
                            }

                        }

                    }
                    searchJsonKey (key, entry.getValue ( ), list);
                }
            } else {
                if (jsonElement.toString ( ).equals (key)) {
                    list.add (jsonElement.toString ( ));
                }
            }
        }

        return list;
    }


}
