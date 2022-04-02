package com.sunnydsouza.gsheets.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Utility class to read properties from a file.
 *
 * @author sunnydsouza
 */
public class PropertyFileReader {

  static Properties prop = new Properties();
  static Map<String, String> propMap = new HashMap<>();

  /**
   * Reads the properties from the file/files. The property values can then be fetched using {@link
   * #getPropValues(String)}
   *
   * @param propertyFiles
   */
  public static void readPropertyFiles(String... propertyFiles) {
    try {
      for (String everyPropertyFile : propertyFiles) {
        FileInputStream inputStream = new FileInputStream(everyPropertyFile);
        if (inputStream != null) {
          Map<String, String> tempPropertyMap = new HashMap<>();
          prop.load(inputStream);
          for (Map.Entry allProp : prop.entrySet()) {
            tempPropertyMap.put((String) allProp.getKey(), (String) allProp.getValue());
          }
          propMap.putAll(tempPropertyMap);
        } else {
          throw new FileNotFoundException(
              "property file '"
                  + System.getProperty("user.dir")
                  + everyPropertyFile
                  + "' not found");
        }
      }
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Returns the property value for the given key.
   *
   * @param propertyKey
   * @return the value of the property
   */
  public static String getPropValues(String propertyKey) {

    try {
      return propMap.get(propertyKey);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void setPropValues(String propertyKey,String propertyValue) {
    propMap.put(propertyKey,propertyValue);
  }
}
