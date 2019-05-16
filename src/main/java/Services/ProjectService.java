package Services;

import DataManagers.DataManager;
import DataManagers.ProjectData.ProjectDataHandler;
import Models.Project;
import Models.User;
import Repositories.ProjectRepository;

import java.util.ArrayList;
import java.util.List;

public class ProjectService {
	public static Project getProject (String id) {
		return ProjectDataHandler.getProject(id);
	}

	public static List<Project> getProjects (String pageNum) {
		return ProjectRepository.getProjects(pageNum);
	}

	public static List<Project> getProjects (String ID, String pageNum) {
		return ProjectDataHandler.getValidProjects(ID, pageNum);
	}

	public static void setValidBidders(Project project, List<User> users) {
		for(User user : users) {
			if(BidService.isUserSkillValidForProject(user, project))
				project.addValidBidder(user.getId());
		}
	}

	public static List<Project> findProjectsWithTitle(String title, String userID) {
		return DataManager.getProjectsWithTitle(title, userID);
	}

	public static List<Project> findProjectsWithDesc(String desc, String userID) {
		return DataManager.getProjectsWithDesc(desc, userID);
	}

	public static int getProjectsNum() {
		return DataManager.getProjectsNum();
	}

	public static int getProjectsNum(String userID) {
		return DataManager.getProjectsNum(userID);
	}
}
