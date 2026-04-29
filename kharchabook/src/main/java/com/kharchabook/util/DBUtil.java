package com.kharchabook.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DBUtil {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new IllegalStateException("db.properties not found on classpath");
            }
            PROPS.load(in);
            Class.forName(PROPS.getProperty("db.driver"));
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Unable to initialize database configuration", e);
        }
    }

    private DBUtil() {
    }

    public static Connection getConnection() throws SQLException {
        String url = PROPS.getProperty("db.url");
        String username = PROPS.getProperty("db.username");
        String password = PROPS.getProperty("db.password");
        if (password == null || password.trim().isEmpty()) {
            Properties info = new Properties();
            info.setProperty("user", username);
            return DriverManager.getConnection(url, info);
        }
        return DriverManager.getConnection(url, username, password);
    }
}
