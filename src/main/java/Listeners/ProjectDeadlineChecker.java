package Listeners;

import Models.Project;
import Services.BidService;
import Services.ProjectService;

import java.util.List;

public class ProjectDeadlineChecker implements Runnable {

	@Override
	public void run () {
		List<Project> validProjects = ProjectService.getAuctionableProjects();
		if(validProjects != null && validProjects.size() != 0)
			for(Project project : validProjects)
				BidService.setWinner(project);
	}
}
