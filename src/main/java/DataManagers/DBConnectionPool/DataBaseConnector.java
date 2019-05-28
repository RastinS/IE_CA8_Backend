package DataManagers.DBConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBaseConnector implements ConnectionPool{
	private String url;
	private String user;
	private String password;
	private List<Connection> connectionPool;
	private List<Connection> usedConnections = new ArrayList<>();
	private static DataBaseConnector DBConPool;
	private static final int INITIAL_POOL_SIZE = 20;

	public static void init() throws SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		createDatabaseIfNotExists();

		List<Connection> pool = new ArrayList<>(INITIAL_POOL_SIZE);
		for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
			pool.add(createConnection());
		}

		DBConPool = new DataBaseConnector("jdbc:mysql://localhost:3306/jaboonjaDB", "root", "root", pool);
	}

	private DataBaseConnector() {}

	private DataBaseConnector(String url, String user, String password, List<Connection> pool) {
		this.url = url;
		this.user = user;
		this.password = password;
		connectionPool = pool;
	}

	private static void createDatabaseIfNotExists() {
		Connection conn;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "root");
			Statement s = conn.createStatement();
			int Result = s.executeUpdate("CREATE DATABASE IF NOT EXISTS jaboonjaDB;");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Connection getConnection() {
		return DBConPool.getPoolConnection();
	}

	@Override
	public Connection getPoolConnection() {
		Connection connection = connectionPool.remove(connectionPool.size() - 1);
		usedConnections.add(connection);
		return connection;
	}

	public static boolean releaseConnection(Connection con) {
		return DBConPool.releasePoolConnection(con);
	}

	@Override
	public boolean releasePoolConnection(Connection connection) { connectionPool.add(connection);
		return usedConnections.remove(connection);
	}

	private static Connection createConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:mysql://localhost:3306/jaboonjaDB", "root", "root");
	}

	public int getSize() {
		return connectionPool.size() + usedConnections.size();
	}

	public String getUrl() {
		return url;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

}
