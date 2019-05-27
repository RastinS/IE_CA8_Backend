package Services;

import DataManagers.DataManager;
import DataManagers.ProjectData.ProjectDataHandler;
import Models.Project;
import Models.User;
import Repositories.ProjectRepository;
import java.util.List;

public class ProjectService {
	public static Project getProject (String id) {
		return ProjectDataHandler.getProject(id);
	}

	public static List<Project> getProjects (String pageNum) {
		return ProjectRepository.getProjects(pageNum);
	}

	public static List<Project> getProjects (String username, String pageNum) {
		return ProjectDataHandler.getValidProjects(username, pageNum);
	}

	public static void setValidBidders(Project project, List<User> users) {
		for(User user : users) {
			if(BidService.isUserSkillValidForProject(user, project))
				project.addValidBidder(user.getId());
		}
	}

	public static List<Project> findProjectsWithTitle(String title, String username) {
		return DataManager.getProjectsWithTitle(title, username);
	}

	public static List<Project> findProjectsWithDesc(String desc, String username) {
		return DataManager.getProjectsWithDesc(desc, username);
	}

	public static int getProjectsNum() {
		return DataManager.getProjectsNum();
	}

	public static int getProjectsNum(String username) {
		return DataManager.getProjectsNum(username);
	}

	public static List<Project> getAuctionableProjects() {return DataManager.getAuctionableProjects(); }
}
