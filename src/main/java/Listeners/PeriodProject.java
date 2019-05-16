package Listeners;

import DataManagers.DataManager;
import Extras.IOReader;
import Models.Project;
import Repositories.ProjectRepository;
import Static.Configs;

import java.util.List;

public class PeriodProject implements Runnable {

	@Override
	public void run () {
		try {
			List<Project> projects          = DataManager.getProjects("0");
			List<Project> projects_from_api = ProjectRepository.setProjects(IOReader.getHTML(Configs.SERVICE_URL + "/project"));

			for (Project project : projects_from_api) {
				if (project.getCreationDate() > projects.get(0).getCreationDate()) {
					DataManager.addProjectToDB(project);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
