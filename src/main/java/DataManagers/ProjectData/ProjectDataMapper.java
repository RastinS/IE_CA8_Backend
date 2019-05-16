package DataManagers.ProjectData;

import Models.Project;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class ProjectDataMapper {
	private static final String COLUMNS = " id, title, budget, description, imageUrl, deadline, creationDate ";

	static void projectDomainToDB (Project project, PreparedStatement st) {
		try {
			st.setString(1, project.getId());
			st.setString(2, project.getTitle());
			st.setInt(3, project.getBudget());
			st.setString(4, project.getDescription());
			st.setString(5, project.getImageUrl());
			st.setLong(6, project.getDeadline());
			st.setLong(7, project.getCreationDate());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	static Project projectDBtoDomain (ResultSet rs) {
		Project project = new Project();
		try {
			project.setId(rs.getString(1));
			project.setTitle(rs.getString(2));
			project.setBudget(rs.getInt(3));
			project.setDescription(rs.getString(4));
			project.setImageUrl(rs.getString(5));
			project.setDeadline(rs.getLong(6));
			project.setCreationDate(rs.getLong(7));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return project;
	}

	static void validBidderDomainToDB (String userID, String projectID, PreparedStatement vst) {
		try {
			vst.setString(1, userID);
			vst.setString(2, projectID);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
