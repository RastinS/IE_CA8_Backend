package DataManagers.UserData;

import DataManagers.DBConnectionPool.DataBaseConnector;
import DataManagers.DataManager;
import DataManagers.SkillData.SkillDataMapper;
import Models.Skill;
import Models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDataHandler {
	private static final String     USER_COLUMNS  = "(id, firstName, lastName, jobTitle, profilePictureUrl, bio, userName, password, isLoggedIn, token)";
	private static final String     SKILL_COLUMNS = "(userID, skillName, point)";
	private static       Connection con           = null;

	public static void init () {
		try {
			DataManager.dropExistingTable("user");
			DataManager.dropExistingTable("userSkill");
			DataManager.dropExistingTable("endorsement");
			con = DataBaseConnector.getConnection();
			Statement st = con.createStatement();

			String sql = "CREATE TABLE " +
					"user " +
					"(id VARCHAR(20) PRIMARY KEY, " +
					"firstName VARCHAR(50), " +
					"lastName VARCHAR(50), " +
					"jobTitle VARCHAR(100), " +
					"profilePictureUrl VARCHAR(50), " +
					"bio VARCHAR(200), " +
					"userName VARCHAR(20), " +
					"password VARCHAR(20), " +
					"isLoggedIn INTEGER," +
					"token VARCHAR(100))";
			st.executeUpdate(sql);

			sql = "CREATE TABLE " +
					"userSkill " +
					"(userID VARCHAR(20), " +
					"skillName VARCHAR(50), " +
					"point INTEGER, " +
					"PRIMARY KEY (userID, skillName), " +
					"FOREIGN KEY (userID) REFERENCES user(id) ON DELETE CASCADE, " +
					"FOREIGN KEY (skillName) REFERENCES skill(name) ON DELETE CASCADE)";
			st.executeUpdate(sql);

			sql = "CREATE TABLE " +
					"endorsement " +
					"(endorserID VARCHAR(20), " +
					"endorsedID VARCHAR(20), " +
					"skillName VARCHAR(50), " +
					"FOREIGN KEY (endorserID) REFERENCES user(id) ON DELETE CASCADE," +
					"FOREIGN KEY (endorsedID, skillName) REFERENCES userSkill(userID, skillName) ON DELETE CASCADE)";
			st.executeUpdate(sql);

			st.close();
			DataBaseConnector.releaseConnection(con);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void addUsers (List<User> users) {
		String userSql  = "INSERT INTO user " + USER_COLUMNS + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String skillSql = "INSERT INTO userSkill " + SKILL_COLUMNS + " VALUES (?, ?, ?)";

		try {
			con = DataBaseConnector.getConnection();
			PreparedStatement ust = con.prepareStatement(userSql);
			PreparedStatement sst = con.prepareStatement(skillSql);

			for (User user : users) {
				UserDataMapper.userDomainToDB(user, ust);
				ust.executeUpdate();
				for (Skill skill : user.getSkills()) {
					SkillDataMapper.skillDomainToDB(skill, user.getId(), sst);
					sst.executeUpdate();
				}
			}

			ust.close();
			sst.close();
			DataBaseConnector.releaseConnection(con);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static List<User> getUsers () {
		Statement  stmt;
		List<User> users = new ArrayList<>();
		try {
			con = DataBaseConnector.getConnection();
			stmt = con.createStatement();

			String    sql = "SELECT * FROM user";
			ResultSet rs  = stmt.executeQuery(sql);
			while (rs.next())
				users.add(UserDataMapper.userDBtoDomain(rs));
			rs.close();
			stmt.close();

			for (User user : users) {
				user.setSkills(getUserSkills(user.getId()));
				setUserEndorsements(user);
			}
			DataBaseConnector.releaseConnection(con);
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return users;
	}

	private static void getEndorsements (String userID, Skill skill) {
		String sql = "SELECT endorserID FROM endorsement WHERE endorsedID = ? AND skillName = ?";
		try {
			Connection conn = DataBaseConnector.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userID);
			stmt.setString(2, skill.getName());
			ResultSet rs = stmt.executeQuery();
			while (rs.next())
				skill.addEndorser(rs.getString(1));
			rs.close();
			stmt.close();
			DataBaseConnector.releaseConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static User getUser (String ID) {
		String sql = "SELECT * FROM user WHERE id = " + ID;
		try {
			User user = null;
			con = DataBaseConnector.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs   = stmt.executeQuery(sql);
			while (rs.next()) {
				if (rs.getString(1).equals(ID))
					user = UserDataMapper.userDBtoDomain(rs);
			}
			stmt.close();
			rs.close();
			if (user == null)
				return null;

			user.setSkills(getUserSkills(user.getId()));
			setUserEndorsements(user);
			DataBaseConnector.releaseConnection(con);
			return user;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static List<Skill> getUserSkills (String userID) {
		List<Skill> skills = new ArrayList<>();
		String      sql    = "SELECT skillName, point FROM userSkill WHERE userID = ?";

		try {
			Connection conn = DataBaseConnector.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, userID);
			ResultSet rss = st.executeQuery();
			while (rss.next()) {
				Skill skill = SkillDataMapper.skillDBtoDomain(rss);
				UserDataHandler.getEndorsements(userID, skill);
				skills.add(skill);
			}
			rss.close();
			st.close();
			DataBaseConnector.releaseConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return skills;
	}

	private static void setSkillStatement(PreparedStatement stmt, String userID, String skillName) throws SQLException {
		stmt.setString(1, userID);
		stmt.setString(2, skillName);
	}

	public static void addUserSkillToDB (String userID, String skillName) {
		String sql = "INSERT INTO userSkill " + SKILL_COLUMNS + "VALUES (?, ?, ?)";
		try {
			con = DataBaseConnector.getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			setSkillStatement(stmt, userID,  skillName);
			stmt.setInt(3, 0);
			stmt.executeUpdate();
			stmt.close();
			DataBaseConnector.releaseConnection(con);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void addEndorsement (String endorserID, String endorsedID, Skill skill) {
		String skillSql   = "UPDATE userSkill SET point = ? WHERE userID = ? AND skillName = ?";
		String endorseSql = "INSERT INTO endorsement VALUES (?, ?, ?)";

		try {
			con = DataBaseConnector.getConnection();
			PreparedStatement stmt = con.prepareStatement(endorseSql);
			stmt.setString(1, endorserID);
			stmt.setString(2, endorsedID);
			stmt.setString(3, skill.getName());
			stmt.executeUpdate();
			stmt.close();

			stmt = con.prepareStatement(skillSql);
			stmt.setInt(1, skill.getPoint());
			stmt.setString(2, endorsedID);
			stmt.setString(3, skill.getName());
			stmt.executeUpdate();
			stmt.close();

			DataBaseConnector.releaseConnection(con);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void setUserEndorsements (User user) {
		String sql = "SELECT endorsedID, skillname FROM endorsement WHERE endorserID = ?";
		try {
			Connection conn = DataBaseConnector.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, user.getId());
			ResultSet rs = stmt.executeQuery();
			while (rs.next())
				user.addEndorsement(rs.getString(1), rs.getString(2));
			rs.close();
			stmt.close();
			DataBaseConnector.releaseConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void removeUserSkill (String skillName, String userID) {
		String sql = "DELETE FROM userSkill WHERE userID = ? AND skillName = ?";
		try {
			con = DataBaseConnector.getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, userID);
			stmt.setString(2, skillName);
			stmt.executeUpdate();
			stmt.close();
			DataBaseConnector.releaseConnection(con);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static List<User> getUserWithName (String name) {
		String     sql   = "SELECT * FROM user WHERE firstName = ? OR lastName = ?";
		List<User> users = new ArrayList<>();
		try {
			con = DataBaseConnector.getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, name);
			stmt.setString(2, name);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				User user = UserDataMapper.userDBtoDomain(rs);
				user.setSkills(getUserSkills(user.getId()));
				setUserEndorsements(user);
				users.add(user);
			}
			rs.close();
			stmt.close();
			DataBaseConnector.releaseConnection(con);
			return users;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static User findUserWithUsername (String userName) {
		String sql = "SELECT * FROM user WHERE userName = ?";

		try {
			con = DataBaseConnector.getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, userName);
			ResultSet rs   = stmt.executeQuery();
			User      user = null;
			while (rs.next())
				user = UserDataMapper.userDBtoDomain(rs);
			rs.close();
			stmt.close();
			DataBaseConnector.releaseConnection(con);
			return user;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void addUserToDB (User user) {
		String sql = "INSERT INTO user " + USER_COLUMNS + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try {
			con = DataBaseConnector.getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			UserDataMapper.userDomainToDB(user, stmt);
			stmt.executeUpdate();
			stmt.close();
			DataBaseConnector.releaseConnection(con);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static String getNextValidUserID () {
		String sql = "SELECT * FROM user ORDER BY id DESC LIMIT 1";

		try {
			con = DataBaseConnector.getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet         rs   = stmt.executeQuery();
			String            out  = Integer.toString(Integer.parseInt(rs.getString("id")) + 1);
			if (out == null)
				out = "1";
			rs.close();
			stmt.close();
			DataBaseConnector.releaseConnection(con);
			return out;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean checkPasswordCorrectness (String userName, String password) {
		String sql = "SELECT u.password FROM user u WHERE u.userName = ?";
		try {
			con = DataBaseConnector.getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, userName);
			ResultSet rs = stmt.executeQuery();
			if (rs.getString(1).equals(password)) {
				rs.close();
				DataBaseConnector.releaseConnection(con);
				return true;
			} else {
				rs.close();
				DataBaseConnector.releaseConnection(con);
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void userLogin (String userName) {
		String sql = "UPDATE user SET isLoggedIn = 1 WHERE userName = ?";
		try {
			con = DataBaseConnector.getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, userName);
			stmt.executeUpdate();
			stmt.close();
			DataBaseConnector.releaseConnection(con);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static String getIDWithUsername(String username) {
		String sql = "SELECT id FROM user WHERE userName = ?";
		try {
			con = DataBaseConnector.getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			String id = rs.getString(1);
			rs.close();
			stmt.close();
			DataBaseConnector.releaseConnection(con);
			return id;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
