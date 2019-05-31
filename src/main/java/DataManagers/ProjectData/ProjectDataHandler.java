package DataManagers.ProjectData;

import DataManagers.DBConnectionPool.DataBaseConnector;
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
	private static final String     BID_COLUMNS          = "(userID, projectID, amount, value)";
	private static final String     VALID_BIDDER_COLUMNS = "(userID, projectID)";
	private static final String     BID_WINNER_COLUMNS   = "(userID, projectID, amount)";

	public static void init () {
		Connection con = DataBaseConnector.getConnection();
		try {
			DataManager.dropExistingTable("project");
			DataManager.dropExistingTable("projectSkill");
			DataManager.dropExistingTable("bid");
			DataManager.dropExistingTable("bidWinner");
			DataManager.dropExistingTable("validBidder");
			Statement st = con.createStatement();

			String sql = "CREATE TABLE " +
					"project " +
					"(id VARCHAR(100) PRIMARY KEY, " +
					"title VARCHAR(100), " +
					"budget INTEGER, " +
					"description VARCHAR(400), " +
					"imageUrl VARCHAR(200), " +
					"deadline VARCHAR(50)," +
					"creationDate VARCHAR(20))";
			st.executeUpdate(sql);

			sql = "CREATE TABLE " +
					"projectSkill " +
					"(projectID VARCHAR(100), " +
					"skillName VARCHAR(50), " +
					"point INTEGER, " +
					"FOREIGN KEY (projectID) REFERENCES project(id) ON DELETE CASCADE ," +
					"FOREIGN KEY (skillName) REFERENCES skill(name) ON DELETE CASCADE)";
			st.executeUpdate(sql);

			sql = "CREATE TABLE " +
					"bid " +
					"(userID VARCHAR(20), " +
					"projectID VARCHAR(100), " +
					"amount INTEGER, " +
					"value INTEGER, " +
					"FOREIGN KEY (userID) REFERENCES user(id) ON DELETE CASCADE," +
					"FOREIGN KEY (projectID) REFERENCES project(id) ON DELETE CASCADE)";
			st.executeUpdate(sql);

			sql = "CREATE TABLE " +
					"bidWinner " +
					"(userID VARCHAR(20), " +
					"projectID VARCHAR(100) PRIMARY KEY, " +
					"amount INTEGER, " +
					"FOREIGN KEY (userID) REFERENCES user(id) ON DELETE CASCADE," +
					"FOREIGN KEY (projectID) REFERENCES project(id) ON DELETE CASCADE)";
			st.executeUpdate(sql);

			sql = "CREATE TABLE " +
					"validBidder " +
					"(userID VARCHAR(20), " +
					"projectID VARCHAR(100), " +
					"PRIMARY KEY(userID, projectID), " +
					"FOREIGN KEY (userID) REFERENCES user(id) ON DELETE CASCADE, " +
					"FOREIGN KEY (projectID) REFERENCES project(id) ON DELETE CASCADE)";
			st.executeUpdate(sql);

			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		DataBaseConnector.releaseConnection(con);
	}

	public static void addProjects (List<Project> projects, List<User> users) {
		String projectSql     = "INSERT INTO project " + COLUMNS + " VALUES (?, ?, ?, ?, ?, ?, ?)";
		String skillSql       = "INSERT INTO projectSkill " + SKILL_COLUMNS + " VALUES (?, ?, ?)";
		String validBidderSql = "INSERT INTO validBidder " + VALID_BIDDER_COLUMNS + " VALUES (?, ?)";

		Connection con = DataBaseConnector.getConnection();
		try {
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
		} catch (SQLException e) {
			e.printStackTrace();
		}
		DataBaseConnector.releaseConnection(con);
	}

	public static List<Project> getProjects (String pageNum) {
		Statement     stmt;
		String        sql;
		List<Project> projects = new ArrayList<>();

		Connection con = DataBaseConnector.getConnection();
		try {
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
				project.setSkills(getProjectSkills(project.getId()));
				setProjectBids(project);
				setAuctionWinnerBid(project);
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}
		DataBaseConnector.releaseConnection(con);
		return projects;
	}

	public static List<Project> getProjectsForUpdate () {
		Statement     stmt;
		String        sql = "SELECT * FROM project";
		List<Project> projects = new ArrayList<>();

		Connection con = DataBaseConnector.getConnection();
		try {
			stmt = con.createStatement();

			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next())
				projects.add(ProjectDataMapper.projectDBtoDomain(rs));

			rs.close();
			stmt.close();

			for (Project project : projects)
				project.setSkills(getProjectSkills(project.getId()));

		} catch (SQLException se) {
			se.printStackTrace();
		}
		DataBaseConnector.releaseConnection(con);
		return projects;
	}

	public static Project getProject (String id) {
		String sql = "SELECT * FROM project WHERE id = ?";
		Project project = null;
		Connection con = DataBaseConnector.getConnection();
		try {
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				if (rs.getString(1).equals(id))
					project = ProjectDataMapper.projectDBtoDomain(rs);
			}
			if (project == null) {
				DataBaseConnector.releaseConnection(con);
				return null;
			}

			project.setSkills(getProjectSkills(project.getId()));
			setProjectBids(project);
			setAuctionWinnerBid(project);

			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		DataBaseConnector.releaseConnection(con);
		return project;
	}

	private static List<Skill> getProjectSkills (String projectID) {
		List<Skill> skills = new ArrayList<>();
		String      sql    = "SELECT skillName, point FROM projectSkill WHERE projectID = ?";

		Connection conn = DataBaseConnector.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, projectID);
			ResultSet rss = st.executeQuery();
			while (rss.next())
				skills.add(SkillDataMapper.skillDBtoDomain(rss));

			rss.close();
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		DataBaseConnector.releaseConnection(conn);
		return skills;
	}

	private static void setProjectBids (Project project) {
		String sql = "SELECT userID, amount, value FROM bid WHERE projectID = ?";

		Connection conn = DataBaseConnector.getConnection();
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, project.getId());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				project.addBid(new Bid(rs.getString(1), project.getId(), rs.getInt(2), rs.getInt(3)));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		DataBaseConnector.releaseConnection(conn);
	}

	public static void addBidToDB (Bid bid) {
		String sql = "INSERT INTO bid " + BID_COLUMNS + " VALUES (?, ?, ?, ?)";

		Connection con = DataBaseConnector.getConnection();
		try {
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, bid.getBiddingUserID());
			stmt.setString(2, bid.getProjectID());
			stmt.setInt(3, bid.getBidAmount());
			stmt.setInt(4, bid.getBidValue());
			stmt.executeUpdate();

			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		DataBaseConnector.releaseConnection(con);
	}


	public static List<Project> getValidProjects (String username, String pageNum) {
		String userID = UserDataHandler.getIDWithUsername(username);
		String sql;
		if (pageNum == null || pageNum.equals(""))
			sql = "SELECT p.* FROM project p, validBidder vb WHERE vb.userID = ? AND p.id = vb.projectID";
		else {
			sql = "SELECT p.* FROM project p, validBidder vb WHERE vb.userID = ? AND p.id = vb.projectID ORDER BY p.creationDate DESC LIMIT 15 OFFSET " + Integer.parseInt(pageNum) * 15;
		}
		List<Project> projects = new ArrayList<>();

		Connection con = DataBaseConnector.getConnection();
		try {
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, userID);
			ResultSet rs = stmt.executeQuery();
			while (rs.next())
				projects.add(ProjectDataMapper.projectDBtoDomain(rs));

			for (Project project : projects) {
				project.setSkills(getProjectSkills(project.getId()));
				setAuctionWinnerBid(project);
				setProjectBids(project);
			}

			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		DataBaseConnector.releaseConnection(con);
		return projects;
	}

	public static void addProjectToDB (Project project) {
		String projectSql     = "INSERT INTO project " + COLUMNS + " VALUES (?, ?, ?, ?, ?, ?, ?)";
		String skillSql       = "INSERT INTO projectSkill " + SKILL_COLUMNS + " VALUES (?, ?, ?)";

		Connection con = DataBaseConnector.getConnection();
		try {
			PreparedStatement sst = con.prepareStatement(skillSql);
			PreparedStatement pst = con.prepareStatement(projectSql);

			ProjectService.setValidBidders(project, UserDataHandler.getUsers());
			ProjectDataMapper.projectDomainToDB(project, pst);
			pst.executeUpdate();
			addValidBiddersToDB(project);
			for (Skill skill : project.getSkills()) {
				SkillDataMapper.skillDomainToDB(skill, project.getId(), sst);
				sst.executeUpdate();
			}
			pst.close();
			sst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		DataBaseConnector.releaseConnection(con);
	}

	public static void addValidBiddersToDB(Project project) {
		String validBidderSql = "INSERT INTO validBidder " + VALID_BIDDER_COLUMNS + " VALUES (?, ?)";
		Connection conn = DataBaseConnector.getConnection();
		try {
			PreparedStatement vst = conn.prepareStatement(validBidderSql);
			for (String userID : project.getValidBidders()) {
				ProjectDataMapper.validBidderDomainToDB(userID, project.getId(), vst);
				vst.executeUpdate();
			}
			vst.close();
		} catch (SQLException e) {
			//e.printStackTrace();
		}
		DataBaseConnector.releaseConnection(conn);

	}

	public static List<Project> getProjectWithTitle (String title, String username) {
		String            sql;
		PreparedStatement stmt;

		List<Project> projects = null;
		Connection con = DataBaseConnector.getConnection();
		try {
			if (username == null || username.equals("")) {
				sql = "SELECT * FROM project WHERE title LIKE ?";
				stmt = con.prepareStatement(sql);
				stmt.setString(1, '%' + title + '%');
			} else {
				sql = "SELECT p.* FROM project p, validBidder vb WHERE vb.userName = ? AND p.id = vb.projectID AND p.title LIKE ?";
				stmt = con.prepareStatement(sql);
				stmt.setString(1, username);
				stmt.setString(2, '%' + title + '%');
			}
			projects = getProjectsWithStatement(stmt);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		DataBaseConnector.releaseConnection(con);
		return projects;
	}

	public static List<Project> getProjectsWithDesc (String desc, String username) {
		String            sql;
		PreparedStatement stmt;
		List<Project> projects = null;
		Connection con = DataBaseConnector.getConnection();
		try {
			if (username == null || username.equals("")) {
				sql = "SELECT * FROM project WHERE description LIKE ?";
				stmt = con.prepareStatement(sql);
				stmt.setString(1, '%' + desc + '%');
			} else {
				sql = "SELECT p.* FROM project p, validBidder vb WHERE vb.userName = ? AND p.id = vb.projectID AND p.description LIKE ?";
				stmt = con.prepareStatement(sql);
				stmt.setString(1, username);
				stmt.setString(2, '%' + desc + '%');
			}
			projects = getProjectsWithStatement(stmt);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		DataBaseConnector.releaseConnection(con);
		return projects;
	}

	private static List<Project> getProjectsWithStatement (PreparedStatement stmt) {
		List<Project> projects = new ArrayList<>();
		try {
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Project project = ProjectDataMapper.projectDBtoDomain(rs);
				project.setSkills(getProjectSkills(project.getId()));
				setProjectBids(project);
				setAuctionWinnerBid(project);
				projects.add(project);
			}
			rs.close();
			stmt.close();
			return projects;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int getProjectsNum() {
		String sql = "SELECT COUNT(*) FROM project";
		Connection con = DataBaseConnector.getConnection();
		int num = 0;
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			num = rs.getInt(1);
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		DataBaseConnector.releaseConnection(con);
		return num;
	}

	public static int getProjectsNum(String username) {
		String userID = UserDataHandler.getIDWithUsername(username);
		String sql = "SELECT COUNT(*) FROM project p, validBidder vb WHERE vb.userID = ? AND p.id = vb.projectID";
		int num = 0;
		Connection con = DataBaseConnector.getConnection();
		try {
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, userID);
			ResultSet rs = stmt.executeQuery();
			num = rs.getInt(1);
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		DataBaseConnector.releaseConnection(con);
		return num;
	}

	public static List<Project> getAuctionableProjects() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String sql = "SELECT * FROM project p WHERE p.deadline < ? AND NOT EXISTS(SELECT * FROM bidWinner bw WHERE p.id = bw.projectID)";
		List<Project> projects = null;
		Connection con = DataBaseConnector.getConnection();
		try {
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setLong(1, timestamp.getTime());
			projects = getProjectsWithStatement(stmt);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		DataBaseConnector.releaseConnection(con);
		return projects;
	}

	public static void addBidWinner(String userID, String projectID, int bidAmount) {
		String bwSQL = "INSERT INTO bidWinner " + BID_WINNER_COLUMNS + " VALUES (?, ?, ?)";
		String vbSQL = "DELETE FROM validBidder WHERE projectID = ?";
		String bSQL = "DELETE FROM bid WHERE projectID = ?";

		Connection con = DataBaseConnector.getConnection();
		try {
			PreparedStatement stmt = con.prepareStatement(bwSQL);
			stmt.setString(1, userID);
			stmt.setString(2, projectID);
			stmt.setInt(3, bidAmount);
			stmt.executeUpdate();
			stmt.close();

			stmt = con.prepareStatement(vbSQL);
			stmt.setString(1, projectID);
			stmt.executeUpdate();
			stmt.close();

			stmt = con.prepareStatement(bSQL);
			stmt.setString(1, projectID);
			stmt.executeUpdate();
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		DataBaseConnector.releaseConnection(con);
	}

	private static void setAuctionWinnerBid(Project project) {
		String sql = "SELECT * FROM bidWinner WHERE projectID = ?";

		Connection conn = DataBaseConnector.getConnection();
		try{
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, project.getId());
			ResultSet rs = stmt.executeQuery();
			Bid winnerBid = null;
			while (rs.next()) {
				winnerBid = new Bid(rs.getString(1), project.getId(), rs.getInt(2), rs.getInt(3));
			}
			project.setWinnerBid(winnerBid);
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		DataBaseConnector.releaseConnection(conn);

	}

	public static void deleteProjectRecords(String projectID) {
		String sql = "DELETE FROM validBidder WHERE projectID = ?";

		Connection con = DataBaseConnector.getConnection();
		try {
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, projectID);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		DataBaseConnector.releaseConnection(con);
	}
}
