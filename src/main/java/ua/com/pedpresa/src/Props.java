package ua.com.pedpresa.src;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Props {
    public static String DB_URL = null;
    public static String DB_USER = null;
    public static String DB_PASS = null;
    public static String DB_DRIVER = null;
    public static String SQL1 = null;
    public static String SQL2 = null;
    public static String SQL3 = null;
    public static String SQL4 = null;
    public static String SQL51 = null;
    public static String SQL52 = null;
    public static String SQL53 = null;
    public static String SQL54 = null;
    public static String SQL55 = null;

    //        ------------------------------ read .properties -----------------------

    public static void getProperties() throws IOException {
        File file = new File("src/main/resources/application.properties");
        java.util.Properties properties = new java.util.Properties();
        properties.load(new FileReader(file));

        DB_URL = properties.getProperty("spring.datasource.url");
        DB_USER = properties.getProperty("spring.datasource.username");
        DB_PASS = properties.getProperty("spring.datasource.password");
        DB_DRIVER = properties.getProperty("db.driver");
        SQL1 = properties.getProperty("sql1");
        SQL2= properties.getProperty("sql2");
        SQL3 = properties.getProperty("sql3");
        SQL4 = properties.getProperty("sql4");
        SQL51 = properties.getProperty("sql51");
        SQL52 = properties.getProperty("sql52");
        SQL53 = properties.getProperty("sql53");
        SQL54 = properties.getProperty("sql54");
        SQL55 = properties.getProperty("sql55");

    }
}
