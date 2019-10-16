import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DataManager {
    static Logger logger = Logger.getLogger (DataManager.class.getName ( ));

    public DataManager() {
    }

    public String readStreamFromReader(Reader reader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder ( );
        int cp;
        while ((cp = reader.read ( )) != -1) {
            stringBuilder.append ((char) cp);
        }

        return stringBuilder.toString ( );
    }

    public String csvToJson(String csv) {
        try {
            CsvSchema csvSchema = CsvSchema.builder ( ).setUseHeader (true).build ( );
            CsvMapper csvMapper = new CsvMapper ( );
            List <Object> readAll = null;
            readAll = csvMapper.readerFor (Map.class).with (csvSchema).readValues (csv).readAll ( );
            ObjectMapper mapper = new ObjectMapper ( );
            return mapper.writerWithDefaultPrettyPrinter ( ).writeValueAsString (readAll);
        } catch (IOException e) {
            e.printStackTrace ( );
            return null;
        }
    }

    public String toJson(String type, String data) throws IOException {
        String json = "";
        ObjectMapper jsonMapper = new ObjectMapper ( );
        switch (type) {
            case "application/json": {
                return data;
            }
            case "application/xml": {
                XmlMapper xmlMapper = new XmlMapper ( );
                JsonNode node = xmlMapper.readTree (data.getBytes ( ));
                json = jsonMapper.writeValueAsString (node);
                break;
            }
            case "text/csv": {
                json = csvToJson (data);
                break;
            }
            case "application/x-yaml": {
                YAMLMapper yamlMapper = new YAMLMapper ( );
                JsonNode node = yamlMapper.readTree (data.getBytes ( ));
                json = jsonMapper.writeValueAsString (node);
                break;
            }
            default: {
                logger.error ("Unknown data type : " + type);
                break;
            }
        }

        return json;
    }

    public ArrayList <String> getValuesOfKey(String key, List <String> jsonList) {
        ArrayList <String> values = new ArrayList <> ( );
        for (String json : jsonList) {
            new HttpClient ( ).searchJsonKey (key, JsonParser.parseString (json), values);
        }
        return values;
    }

    public ArrayList <String> getJsonObjectByKeyAndValue(String key, String value, List <String> jsonList) {
        ArrayList <String> objects = new ArrayList <> ( );
        for (String obj : jsonList) {
            JsonElement jsonElement = JsonParser.parseString (obj);
            if (jsonElement.isJsonArray ( )) {
                for (JsonElement jsonElement1 : jsonElement.getAsJsonArray ( )) {
                    if (jsonElement1.isJsonObject ( )) {
                        {
                            if (jsonElement1.getAsJsonObject ( ).get (key) != null) {
                                String val = jsonElement1.getAsJsonObject ( ).get (key).getAsString ( );
                                if (val.equals (value)) {
                                    objects.add (jsonElement1.toString ( ));
                                }
                            }
                        }
                    }
                }
            } else {
                if (jsonElement.isJsonObject ( )) {
                    if (jsonElement.getAsJsonObject ( ).get (key) != null) {
                        String val = jsonElement.getAsJsonObject ( ).get (key).getAsString ( );
                        if (val.equals (value)) {
                            objects.add (jsonElement.toString ( ));
                        }
                    }
                }
            }
        }
        return objects;
    }

    public String getProperty(String property){
        String value = "";
        try {
            InputStream inputStream  = DataManager.class.getResourceAsStream ("/application.properties");
            Properties properties = new Properties ( );
            properties.load (inputStream);
            value = properties.getProperty (property);
        } catch (FileNotFoundException e) {
            e.printStackTrace ( );
        } catch (IOException e) {
            e.printStackTrace ( );
        }
        return value;
    }


}
