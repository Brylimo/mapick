package com.enjoytrip.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {
	private static final String URL = "jdbc:mysql://localhost:3306/ssafytrip";    
    private static final String USER = "ssafy";
    private static final String PASSWORD = "ssafy";
    
    public static Connection getConnection() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("user", USER);
        properties.setProperty("password", PASSWORD);
        properties.setProperty("profileSQL", "true");
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Use com.mysql.jdbc.Driver if you're not on MySQL 8+ yet.
            System.out.println("Driver loaded!");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find the driver in the classpath!", e);
        }
        
        return DriverManager.getConnection(URL, properties);
    }
    
    public static void close(AutoCloseable... closeables) {
    	for (AutoCloseable res : closeables) {
			try {
				res.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
}
