package DataManagers.UserData;

import DataManagers.DataBaseConnector;
import DataManagers.DataManager;
import DataManagers.SkillData.SkillDataMapper;
import Models.Skill;
import Models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDataHandler {
	private static final String USER_COLUMNS = "(id, firstName, lastName, jobTitle, profilePictureUrl, bio, isLoggedIn)";
	private static final String SKILL_COLUMNS = "(userID, skillName, point)";
	private static Connection con = null;

	public static void init() {
		try {
			DataManager.dropExistingTable("user");
			DataManager.dropExistingTable("userSkill");
			DataManager.dropExistingTable("endorsement");
			con = DataBaseConnector.getConnection();
			Statement st = con.createStatement();

			String sql = "CREATE TABLE " +
					"user " +
					"(id TEXT PRIMARY KEY, " +
					"firstName TEXT, " +
					"lastName TEXT, " +
					"jobTitle TEXT, " +
					"profilePictureUrl TEXT, " +
					"bio TEXT, " +
					"isLoggedIn INTEGER)";
			st.executeUpdate(sql);

			sql = "CREATE TABLE " +
					"userSkill " +
					"(userID TEXT, " +
					"skillName TEXT, " +
					"point INTEGER, " +
					"PRIMARY KEY (userID, skillName), " +
					"FOREIGN KEY (userID) REFERENCES user(id), " +
					"FOREIGN KEY (skillName) REFERENCES skill(name))";
			st.executeUpdate(sql);

			sql = "CREATE TABLE " +
					"endorsement " +
					"(endorserID TEXT, " +
					"endorsedID TEXT, " +
					"skillName TEXT, " +
					"FOREIGN KEY (endorserID) REFERENCES user(id)," +
					"FOREIGN KEY (endorsedID, skillName) REFERENCES userSkill(userID, skillName))";
			st.executeUpdate(sql);

			st.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void addUsers(List<User> users) {
		String userSql = "INSERT INTO user " + USER_COLUMNS + " VALUES (?, ?, ?, ?, ?, ?, ?)";
		String skillSql = "INSERT INTO userSkill " + SKILL_COLUMNS + " VALUES (?, ?, ?)";

		try {
			con = DataBaseConnector.getConnection();
			PreparedStatement ust = con.prepareStatement(userSql);
			PreparedStatement sst = con.prepareStatement(skillSql);

			for(User user : users) {
				UserDataMapper.userDomainToDB(user, ust);
				ust.executeUpdate();
				for(Skill skill : user.getSkills()) {
					SkillDataMapper.skillDomainToDB(skill, user.getId(), sst);
					sst.executeUpdate();
				}
			}

			ust.close();
			sst.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static List<User> getUsers() {
		Statement stmt;
		List<User> users = new ArrayList<>();
		try{
			con = DataBaseConnector.getConnection();
			stmt = con.createStatement();

			String sql = "SELECT * FROM user";
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next())
				users.add(UserDataMapper.userDBtoDomain(rs));
			rs.close();
			stmt.close();

			for(User user : users) {
				user.setSkills(getUserSkills(user.getId(), con));
				setUserEndorsements(user, con);
			}
			con.close();
		} catch (SQLException se){
			se.printStackTrace();
		}
		return users;
	}

	private static void getEndorsements(String userID, Skill skill, Connection con) {
		String sql = "SELECT endorserID FROM endorsement WHERE endorsedID = ? AND skillName = ?";
		try {
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, userID);
			stmt.setString(2, skill.getName());
			ResultSet rs = stmt.executeQuery();
			while (rs.next())
				skill.addEndorser(rs.getString(1));
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static User getUser(String ID) {
		String sql = "SELECT * FROM user WHERE id = " + ID;
		try {
			User user = null;
			con = DataBaseConnector.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				if(rs.getString(1).equals(ID))
					user = UserDataMapper.userDBtoDomain(rs);
			}
			stmt.close();
			rs.close();
			if(user == null)
				return null;

			user.setSkills(getUserSkills(user.getId(), con));
			setUserEndorsements(user, con);
			con.close();
			return user;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static List<Skill> getUserSkills(String userID, Connection con) {
		List<Skill> skills = new ArrayList<>();
		String sql = "SELECT skillName, point FROM userSkill WHERE userID = ?";

		try {
			PreparedStatement st = con.prepareStatement(sql);
			st.setString(1, userID);
			ResultSet rss = st.executeQuery();
			while (rss.next()) {
				Skill skill = SkillDataMapper.skillDBtoDomain(rss);
				UserDataHandler.getEndorsements(userID, skill, con);
				skills.add(skill);
			}
			rss.close();
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return skills;
	}

	public static void addUserSkillToDB(String userID, String skillName) {
		String sql = "INSERT INTO userSkill " + SKILL_COLUMNS + "VALUES (?, ?, ?)";
		try {
			con = DataBaseConnector.getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, userID);
			stmt.setString(2, skillName);
			stmt.setInt(3, 0);
			stmt.executeUpdate();
			stmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void addEndorsement(String endorserID, String endorsedID, Skill skill) {
		String skillSql = "UPDATE userSkill SET point = ? WHERE userID = ? AND skillName = ?";
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

			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void setUserEndorsements(User user, Connection con) {
		String sql = "SELECT endorsedID, skillname FROM endorsement WHERE endorserID = ?";
		try {
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, user.getId());
			ResultSet rs = stmt.executeQuery();
			while(rs.next())
				user.addEndorsement(rs.getString(1), rs.getString(2));
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void removeUserSkill(String skillName, String userID) {
		String sql = "DELETE FROM userSkill WHERE userID = ? AND skillName = ?";
		try {
			con = DataBaseConnector.getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, userID);
			stmt.setString(2, skillName);
			stmt.executeUpdate();
			stmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static List<User> getUserWithName(String name) {
		String sql = "SELECT * FROM user WHERE firstName LIKE ? OR lastName LIKE ?";
		List<User> users = new ArrayList<>();
		try {
			con = DataBaseConnector.getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, '%' + name + '%');
			stmt.setString(2, '%' + name + '%');
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				User user = UserDataMapper.userDBtoDomain(rs);
				user.setSkills(getUserSkills(user.getId(), con));
				setUserEndorsements(user, con);
				users.add(user);
			}
			rs.close();
			stmt.close();
			con.close();
			return users;
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
