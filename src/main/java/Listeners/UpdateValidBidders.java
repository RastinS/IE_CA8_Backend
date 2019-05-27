package Listeners;

import DataManagers.ProjectData.ProjectDataHandler;
import Models.Project;
import Models.User;
import Repositories.UserRepository;
import Services.ProjectService;
import java.util.List;

public class UpdateValidBidders implements Runnable{

	@Override
	public void run() {
		List<Project> projects = ProjectDataHandler.getProjectsForUpdate();
		List<User> users = UserRepository.getUsers();
		for(Project project : projects) {
			ProjectService.setValidBidders(project, users);
			ProjectDataHandler.addValidBiddersToDB(project);
		}
	}
}
