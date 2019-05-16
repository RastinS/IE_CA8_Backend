package Repositories;

import DataManagers.DataManager;
import Models.Bid;
import Models.Project;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProjectRepository {
	private static Project setProject (String data) throws JSONException {
		Project    project       = new Project();
		JSONObject projectObject = new JSONObject(data);
		project.setId(projectObject.getString("id"));
		project.setTitle(projectObject.getString("title"));
		project.setDescription(projectObject.getString("description"));
		project.setImageUrl(projectObject.getString("imageUrl"));
		project.setBudget(projectObject.getInt("budget"));
		project.setDeadline(projectObject.getLong("deadline"));
		project.setSkills(projectObject.getString("skills"));
		project.setCreationDate(projectObject.getLong("creationDate"));
		project.setBids(new ArrayList<Bid>());
		return project;
	}

	public static List<Project> setProjects (String data) throws JSONException {
		List<Project> projects      = new ArrayList<Project>();
		JSONArray     projectsArray = new JSONArray(data);
		for (int i = 0; i < projectsArray.length(); i++) {
			projects.add(setProject(projectsArray.getString(i)));
		}
		return projects;
	}

	public static List<Project> getProjects (String pageNum) {
		return DataManager.getProjects(pageNum);
	}
}
