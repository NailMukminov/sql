package util;

import com.mysql.cj.jdbc.Driver;

import java.sql.Connection;
import java.sql.SQLException;

public final class ConnectionManager {
    static {
        loadDriver();
    }

    private ConnectionManager() {
    }

    public static Connection open() {
        try {
            return (new Driver().connect(PropertiesUtil.getProperties().getProperty("url"), PropertiesUtil.getProperties()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
