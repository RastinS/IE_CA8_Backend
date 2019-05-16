package DataManagers.ProjectData;

import DataManagers.DataBaseConnector;
import DataManagers.DataManager;
import DataManagers.SkillData.SkillDataMapper;
import DataManagers.UserData.UserDataHandler;
import Models.Bid;
import Models.Project;
import Models.Skill;
import Models.User;
import Services.ProjectService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectDataHandler {
	private static final String     COLUMNS              = "(id, title, budget, description, imageUrl, deadline, creationDate)";
	private static final String     SKILL_COLUMNS        = "(projectID, skillName, point)";
	private static final String     BID_COLUMNS          = "(userID, projectID, amount)";
	private static final String     VALID_BIDDER_COLUMNS = "(userID, projectID)";
	private static       Connection con                  = null;

	public static void init () {
		try {
			DataManager.dropExistingTable("project");
			DataManager.dropExistingTable("projectSkill");
			DataManager.dropExistingTable("bid");
			DataManager.dropExistingTable("bidWinner");
			DataManager.dropExistingTable("validBidder");
			con = DataBaseConnector.getConnection();
			Statement st = con.createStatement();

			String sql = "CREATE TABLE " +
					"project " +
					"(id TEXT PRIMARY KEY, " +
					"title TEXT, " +
					"budget INTEGER, " +
					"description TEXT, " +
					"imageUrl TEXT, " +
					"deadline INTEGER," +
					"creationDate TEXT)";
			st.executeUpdate(sql);

			sql = "CREATE TABLE " +
					"projectSkill " +
					"(projectID TEXT, " +
					"skillName TEXT, " +
					"point INTEGER, " +
					"FOREIGN KEY (projectID) REFERENCES project(id)," +
					"FOREIGN KEY (skillName) REFERENCES skill(name))";
			st.executeUpdate(sql);

			sql = "CREATE TABLE " +
					"bid " +
					"(userID TEXT, " +
					"projectID TEXT, " +
					"amount INTEGER, " +
					"FOREIGN KEY (userID) REFERENCES user(id)," +
					"FOREIGN KEY (projectID) REFERENCES project(id))";
			st.executeUpdate(sql);

			sql = "CREATE TABLE " +
					"bidWinner " +
					"(userID TEXT, " +
					"projectID TEXT PRIMARY KEY, " +
					"amount INTEGER, " +
					"FOREIGN KEY (userID) REFERENCES user(id)," +
					"FOREIGN KEY (projectID) REFERENCES project(id))";
			st.executeUpdate(sql);

			sql = "CREATE TABLE " +
					"validBidder " +
					"(userID TEXT, " +
					"projectID TEXT, " +
					"PRIMARY KEY(userID, projectID) " +
					"FOREIGN KEY (userID) REFERENCES user(id)," +
					"FOREIGN KEY (projectID) REFERENCES project(id))";
			st.executeUpdate(sql);

			st.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void addProjects (List<Project> projects, List<User> users) {
		String projectSql     = "INSERT INTO project " + COLUMNS + " VALUES (?, ?, ?, ?, ?, ?, ?)";
		String skillSql       = "INSERT INTO projectSkill " + SKILL_COLUMNS + " VALUES (?, ?, ?)";
		String validBidderSql = "INSERT INTO validBidder " + VALID_BIDDER_COLUMNS + " VALUES (?, ?)";

		try {
			con = DataBaseConnector.getConnection();
			PreparedStatement pst = con.prepareStatement(projectSql);
			PreparedStatement sst = con.prepareStatement(skillSql);
			PreparedStatement vst = con.prepareStatement(validBidderSql);

			for (Project project : projects) {
				ProjectService.setValidBidders(project, users);
				ProjectDataMapper.projectDomainToDB(project, pst);
				pst.executeUpdate();
				for (Skill skill : project.getSkills()) {
					SkillDataMapper.skillDomainToDB(skill, project.getId(), sst);
					sst.executeUpdate();
				}
				for (String userID : project.getValidBidders()) {
					ProjectDataMapper.validBidderDomainToDB(userID, project.getId(), vst);
					vst.executeUpdate();
				}
			}
			pst.close();
			sst.close();
			vst.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static List<Project> getProjects (String pageNum) {
		Statement     stmt;
		String        sql;
		List<Project> projects = new ArrayList<>();
		try {
			con = DataBaseConnector.getConnection();
			stmt = con.createStatement();

			if (pageNum == null || pageNum.equals(""))
				sql = "SELECT * FROM project";
			else
				sql = "SELECT * FROM project ORDER BY creationDate DESC LIMIT 15 OFFSET " + Integer.parseInt(pageNum) * 15;
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				projects.add(ProjectDataMapper.projectDBtoDomain(rs));
			}
			rs.close();
			stmt.close();

			for (Project project : projects) {
				project.setSkills(getProjectSkills(project.getId(), con));
				setProjectBids(project, con);
			}

			con.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}

		return projects;
	}

	public static Project getProject (String id) {
		String sql = "SELECT * FROM project WHERE id = ?";
		try {
			Project project = null;
			con = DataBaseConnector.getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				if (rs.getString(1).equals(id))
					project = ProjectDataMapper.projectDBtoDomain(rs);
			}
			if (project == null)
				return null;

			project.setSkills(getProjectSkills(project.getId(), con));
			setProjectBids(project, con);

			stmt.close();
			rs.close();
			con.close();
			return project;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static List<Skill> getProjectSkills (String projectID, Connection con) {
		List<Skill> skills = new ArrayList<>();
		String      sql    = "SELECT skillName, point FROM projectSkill WHERE projectID = ?";

		try {
			PreparedStatement st = con.prepareStatement(sql);
			st.setString(1, projectID);
			ResultSet rss = st.executeQuery();
			while (rss.next())
				skills.add(SkillDataMapper.skillDBtoDomain(rss));

			rss.close();
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return skills;
	}

	private static void setProjectBids (Project project, Connection con) {
		String sql = "SELECT userID, amount FROM bid WHERE projectID = ?";
		try {
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, project.getId());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				project.addBid(new Bid(rs.getString(1), project.getId(), rs.getInt(2)));
			}
			rs.close();
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void addBidToDB (Bid bid) {
		String sql = "INSERT INTO bid " + BID_COLUMNS + " VALUES (?, ?, ?)";

		try {
			con = DataBaseConnector.getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, bid.getBiddingUserID());
			stmt.setString(2, bid.getProjectID());
			stmt.setInt(3, bid.getBidAmount());
			stmt.executeUpdate();

			stmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static List<Project> getValidProjects (String userID, String pageNum) {
		String sql;
		if (pageNum == null || pageNum.equals(""))
			sql = "SELECT p.* FROM project p, validBidder vb WHERE vb.userID = ? AND p.id = vb.projectID";
		else {
			sql = "SELECT p.* FROM project p, validBidder vb WHERE vb.userID = ? AND p.id = vb.projectID ORDER BY p.creationDate DESC LIMIT 15 OFFSET " + Integer.parseInt(pageNum) * 15;
		}
		List<Project> projects = new ArrayList<>();

		try {
			con = DataBaseConnector.getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, userID);
			ResultSet rs = stmt.executeQuery();
			while (rs.next())
				projects.add(ProjectDataMapper.projectDBtoDomain(rs));

			for (Project project : projects) {
				project.setSkills(getProjectSkills(project.getId(), con));
				setProjectBids(project, con);
			}

			stmt.close();
			rs.close();
			con.close();
			return projects;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void addProjectToDB (Project project) {
		String projectSql     = "INSERT INTO project " + COLUMNS + " VALUES (?, ?, ?, ?, ?, ?, ?)";
		String skillSql       = "INSERT INTO projectSkill " + SKILL_COLUMNS + " VALUES (?, ?, ?)";
		String validBidderSql = "INSERT INTO validBidder " + VALID_BIDDER_COLUMNS + " VALUES (?, ?)";

		try {
			con = DataBaseConnector.getConnection();
			PreparedStatement sst = con.prepareStatement(skillSql);
			PreparedStatement pst = con.prepareStatement(projectSql);
			PreparedStatement vst = con.prepareStatement(validBidderSql);

			ProjectService.setValidBidders(project, UserDataHandler.getUsers());
			ProjectDataMapper.projectDomainToDB(project, pst);
			pst.executeUpdate();
			for (String userID : project.getValidBidders()) {
				ProjectDataMapper.validBidderDomainToDB(userID, project.getId(), vst);
				vst.executeUpdate();
			}
			for (Skill skill : project.getSkills()) {
				SkillDataMapper.skillDomainToDB(skill, project.getId(), sst);
				sst.executeUpdate();
			}
			pst.close();
			sst.close();
			vst.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static List<Project> getProjectWithTitle (String title, String userID) {
		String            sql;
		PreparedStatement stmt;
		try {
			con = DataBaseConnector.getConnection();
			if (userID == null || userID.equals("")) {
				sql = "SELECT * FROM project WHERE title LIKE ?";
				stmt = con.prepareStatement(sql);
				stmt.setString(1, '%' + title + '%');
			} else {
				sql = "SELECT p.* FROM project p, validBidder vb WHERE vb.userID = ? AND p.id = vb.projectID AND p.title LIKE ?";
				stmt = con.prepareStatement(sql);
				stmt.setString(1, userID);
				stmt.setString(2, '%' + title + '%');
			}
			List<Project> projects = getProjectsWithStatement(stmt, con);
			stmt.close();
			con.close();
			return projects;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<Project> getProjectsWithDesc (String desc, String userID) {
		String            sql;
		PreparedStatement stmt;
		try {
			con = DataBaseConnector.getConnection();
			if (userID == null || userID.equals("")) {
				sql = "SELECT * FROM project WHERE description LIKE ?";
				stmt = con.prepareStatement(sql);
				stmt.setString(1, '%' + desc + '%');
			} else {
				sql = "SELECT p.* FROM project p, validBidder vb WHERE vb.userID = ? AND p.id = vb.projectID AND p.description LIKE ?";
				stmt = con.prepareStatement(sql);
				stmt.setString(1, userID);
				stmt.setString(2, '%' + desc + '%');
			}
			List<Project> projects = getProjectsWithStatement(stmt, con);
			stmt.close();
			con.close();
			return projects;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static List<Project> getProjectsWithStatement (PreparedStatement stmt, Connection con) {
		List<Project> projects = new ArrayList<>();
		try {
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Project project = ProjectDataMapper.projectDBtoDomain(rs);
				project.setSkills(getProjectSkills(project.getId(), con));
				setProjectBids(project, con);
				projects.add(project);
			}
			rs.close();
			return projects;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int getProjectsNum() {
		String sql = "SELECT COUNT(*) FROM project";
		try {
			con = DataBaseConnector.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			int num = rs.getInt(1);
			rs.close();
			stmt.close();
			con.close();
			return num;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static int getProjectsNum(String userID) {
		String sql = "SELECT COUNT(*) FROM project p, validBidder vb WHERE vb.userID = ? AND p.id = vb.projectID";
		try {
			con = DataBaseConnector.getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, userID);
			ResultSet rs = stmt.executeQuery();
			int num = rs.getInt(1);
			rs.close();
			stmt.close();
			con.close();
			return num;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
