package com.semanticweb.group2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config{

private static final String PROPERTIES_FILE = "config.properties";
private static Properties properties = new Properties();

private Config(){
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE);
    if (inputStream != null) {
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

private static Config single_instance = null;

public static Config getInstance()
{
    if (single_instance == null)
        single_instance = new Config();

    return single_instance;
}

public String getProperty(String key){
	return properties.getProperty(key);
}
}