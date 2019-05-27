package DataManagers.DBConnectionPool;

import java.sql.Connection;

public interface ConnectionPool {
	Connection getPoolConnection();
	boolean releasePoolConnection(Connection connection);
	String getUrl();
	String getUser();
	String getPassword();
}