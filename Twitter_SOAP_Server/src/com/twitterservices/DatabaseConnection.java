package com.twitterservices;
import java.sql.*;

public class DatabaseConnection {
	public static Connection config(){
		String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
		String databaseURL = "jdbc:mysql://localhost/twitter";
		String username = "root";
		String password = "balaji";
		Connection conn = null;
		try{
			//Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");
			//Create a new connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(databaseURL,username,password);
			return conn;
		}catch(Exception e){
			e.printStackTrace();
		}
		//If the password doesn't match, then set the connection to NULL
		Connection obj = null;
		return obj;
	}
}
