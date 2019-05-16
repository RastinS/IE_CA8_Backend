package DataManagers;

import org.apache.commons.dbcp.BasicDataSource;
import java.sql.*;

public class DataBaseConnector {
	private static BasicDataSource ds = new BasicDataSource();
	static final String DB_URL = "jdbc:sqlite:jaboonja.DB";

	static {
		ds.setDriverClassName("org.sqlite.JDBC");
		ds.setUrl(DB_URL);
		ds.setMinIdle(5);
		ds.setMaxIdle(10);
	}

	public static Connection getConnection() throws SQLException {
		return ds.getConnection();
	}

	private DataBaseConnector(){ }
}
