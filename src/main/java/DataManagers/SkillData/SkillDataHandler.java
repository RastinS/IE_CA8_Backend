package DataManagers.SkillData;

import DataManagers.DataBaseConnector;
import DataManagers.DataManager;
import DataManagers.UserData.UserDataHandler;
import Models.Skill;
import Models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SkillDataHandler {
	private static final String COLUMNS = "(name)";
	private static Connection con = null;

	public static void init() {
		try {
			DataManager.dropExistingTable("skill");
			con = DataBaseConnector.getConnection();
			Statement st = con.createStatement();


			String sql = "CREATE TABLE " +
					"skill " +
					"(name TEXT PRIMARY KEY)";
			st.executeUpdate(sql);

			st.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void addSkills(List<Skill> skills) {
		String sql = "INSERT INTO skill " + COLUMNS + " VALUES (?)";

		try {
			con = DataBaseConnector.getConnection();
			PreparedStatement st = con.prepareStatement(sql);
			for(Skill skill : skills) {
				st.setString(1, skill.getName());
				st.executeUpdate();
			}
			st.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static List<Skill> getSkills() {
		Statement stmt;
		List<Skill> skills = new ArrayList<>();
		try{
			con = DataBaseConnector.getConnection();
			stmt = con.createStatement();

			String sql = "SELECT * FROM skill";
			ResultSet rs = stmt.executeQuery(sql);

			while(rs.next())
				skills.add(new Skill(rs.getString("name")));

			rs.close();
			stmt.close();
			con.close();
		}catch(SQLException se){
			se.printStackTrace();
		}

		return skills;
	}
}
