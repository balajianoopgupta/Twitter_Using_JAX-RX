package com.twitterservices;

import java.util.*;
import java.sql.*;

class ConnectionPooling
{

	String databaseUrl = "jdbc:mysql://localhost:3306/twitter";
	String userName = "root";
	String password = "balaji";

	Vector connectionPool = new Vector();

	public ConnectionPooling()
	{
		initialize();
	}

	public ConnectionPooling(
		//String databaseName,
		String databaseUrl,
		String userName,
		String password
		)
	{
		this.databaseUrl = databaseUrl;
		this.userName = userName;
		this.password = password;
		initialize();
	}

	private void initialize()
	{
		initializeConnectionPool();
	}

	private void initializeConnectionPool()
	{
		while(!checkIfConnectionPoolIsFull())
		{
			System.out.println("Connection Pool is NOT full. Adding new connections");
			//Adding new connection instance until the pool is full
			connectionPool.addElement(createNewConnectionForPool());
		}
		System.out.println("Connection Pool is full.");
	}

	private synchronized boolean checkIfConnectionPoolIsFull()
	{
		final int MAX_POOL_SIZE = 5000;
		if(connectionPool.size() < 5000)
		{
			return false;
		}

		return true;
	}

	//Creating a connection
	private Connection createNewConnectionForPool()
	{
		Connection connection = null;

		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(databaseUrl, userName, password);
			System.out.println("Connection: "+connection);
		}
		catch(SQLException sqle)
		{
			System.err.println("SQLException: "+sqle);
			return null;
		}
		catch(ClassNotFoundException cnfe)
		{
			System.err.println("ClassNotFoundException: "+cnfe);
			return null;
		}

		return connection;
	}

	public synchronized Connection getConnectionFromPool()
	{
		Connection connection = null;
		if(connectionPool.size() > 0)
		{
			connection = (Connection) connectionPool.firstElement();
			connectionPool.removeElementAt(0);
		}
		return connection;
	}

	public synchronized void returnConnectionToPool(Connection connection)
	{
		connectionPool.addElement(connection);
	}

	public static void main(String args[])
	{
		ConnectionPooling ConnectionPooling = new ConnectionPooling();
	}

}