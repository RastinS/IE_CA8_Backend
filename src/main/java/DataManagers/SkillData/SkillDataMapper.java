package DataManagers.SkillData;

import Models.Skill;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SkillDataMapper {
	public static void skillDomainToDB(Skill skill, String ID, PreparedStatement st) {
		try {
			st.setString(1, ID);
			st.setString(2, skill.getName());
			st.setInt(3, skill.getPoint());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Skill skillDBtoDomain(ResultSet rs) {
		Skill skill = new Skill();
		try {
			skill.setName(rs.getString(1));
			skill.setPoint(rs.getInt(2));
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return skill;
	}
}
