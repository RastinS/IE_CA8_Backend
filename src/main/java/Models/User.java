package Models;

import Controllers.Endorse;
import Repositories.SkillRepository;
import ErrorClasses.SkillNotFoundException;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class User {
	private String            id;
	private String            firstName;
	private String            lastName;
	private String            jobTitle;
	private String            profilePictureURL = "";
	private List<Skill>       skills = new ArrayList<>();
	private String            bio;
	private List<Endorsement> endorsements       = new ArrayList<Endorsement>();
	private boolean isLoggedIn = false;

	public String getId () {
		return id;
	}

	public void setId (String id) {
		this.id = id;
	}

	public String getFirstName () {
		return firstName;
	}

	public void setFirstName (String firstName) {
		this.firstName = firstName;
	}

	public String getLastName () {
		return lastName;
	}

	public void setLastName (String lastName) {
		this.lastName = lastName;
	}

	public String getJobTitle () {
		return jobTitle;
	}

	public void setJobTitle (String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getProfilePictureURL () {
		return profilePictureURL;
	}

	public void setProfilePictureURL (String profilePictureURL) {
		this.profilePictureURL = profilePictureURL;
	}

	public List<Skill> getSkills () {
		return skills;
	}

	public void setSkills (String skills) throws JSONException {
		this.skills = SkillRepository.setSkills(skills, "");
	}

	public void setSkills (List<Skill> skills) { this.skills = skills;}

	public int getSkillPoint (Skill skill) {
		for (Skill currSkill : skills) {
			if (currSkill.getName().equals(skill.getName()))
				return currSkill.getPoint();
		}
		return 0;
	}

	public String getBio () {
		return bio;
	}

	public void setBio (String bio) {
		this.bio = bio;
	}

	public void addSkill (Skill skill) {
		skills.add(skill);
	}

	public boolean hasSkill (String skillName) {
		for (Skill skill : skills) {
			if (skill.getName().equals(skillName))
				return true;
		}
		return false;
	}

	public boolean deleteSkill (String skillName) {
		for (Skill skill : skills) {
			if (skill.getName().equals(skillName)) {
				skills.remove(skill);
				return true;
			}
		}
		return false;
	}

	public Skill getSkill (String skillName) throws SkillNotFoundException {
		for (Skill skill : skills) {
			if (skill.getName().equals(skillName))
				return skill;
		}
		throw new SkillNotFoundException();
	}

	public void addEndorsement (Endorsement endorsement) {
		endorsements.add(endorsement);
	}

	public void addEndorsement(String userID, String skillName) {
		endorsements.add(new Endorsement(this.getId(), userID, skillName));
	}

	public boolean isLoggedIn() {
		return isLoggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		isLoggedIn = loggedIn;
	}

	public void setEndorsements(List<Endorsement> endorsements) {
		this.endorsements = endorsements;
	}
}
