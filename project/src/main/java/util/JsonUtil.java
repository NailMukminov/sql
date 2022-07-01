package util;

import aquality.selenium.browser.AqualityServices;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class JsonUtil {
    public static Object smartReadJson(String filename, String key) {
        try (FileReader reader = new FileReader(filename)){
            JSONParser jsonParser = new JSONParser();
            JSONObject rootJsonObject = (JSONObject) jsonParser.parse(reader);
            return rootJsonObject.get(key);
        } catch (IOException | ParseException | NullPointerException e) {
            AqualityServices.getLogger().warn(e.getMessage());
        }
        return null;
    }
}
