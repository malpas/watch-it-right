package app.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Holds the application's configuration variables. This includes admin credentials and API keys.
 * The application.properties file should have the following values:
 *      watchitright.jwt_key
 *      watchitright.tmdb_api_key
 *      watchitright.admin_username
 *      watchitright.admin_password
 */
public class ApplicationConfig {
    private static final String LOCATION = "application.properties";

    public static String getJWTKey() {
        return getProp("watchitright.jwt_key");
    }

    public static String getAdminPassword() {
        return getProp("watchitright.admin_password");
    }

    public static String getAdminUsername() {
        return getProp("watchitright.admin_username");
    }

    public static String getTmdbApiKey() {
        return getProp("watchitright.tmdb_api_key");
    }

    private static String getProp(String property) {
        Properties props = new Properties();

        try {
            FileInputStream in = new FileInputStream(LOCATION);
            props.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props.getProperty(property);

    }
}
