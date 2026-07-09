
package com.alertengine.config;

import java.io.InputStream;
import java.util.Properties;

public class AppConfig {

    private static Properties props = new Properties();

    static{

        try{

            InputStream in = AppConfig.class
                .getClassLoader()
                .getResourceAsStream("config.properties");

            props.load(in);

        }catch(Exception e){

            throw new RuntimeException(e);
        }
    }

    public static String get(String key){

        return props.getProperty(key);
    }
}
