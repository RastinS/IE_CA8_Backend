package Services;

import DataManagers.DataManager;
import DataManagers.UserData.UserDataHandler;
import ErrorClasses.*;
import Models.Endorsement;
import Models.Project;
import Models.Skill;
import Models.User;
import Repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class UserService {

	public static boolean canBid (Project project, User user) {
		for (Skill skill : project.getSkills()) {
			if (user.getSkillPoint(skill) < skill.getPoint())
				return false;
		}
		return true;
	}

	public static User findUserWithID (String selectID) {
		return DataManager.findUserWithID(selectID);
	}

	public static List<User> getUsers () {
		List<User> users        = new ArrayList<>(DataManager.getUsers());
		User       loggedInUser = UserRepository.getLoggedInUser();

		for (int i = 0; i < users.size(); i++) {
			if (users.get(i).getId().equals(loggedInUser.getId())) {
				users.remove(i);
				return users;
			}
		}
		return users;
	}

	public static void addSkillToUser (String userID, String skillName) throws UserNotLoggedInException, HadSkillException, SkillNotFoundException, UserNotFoundException {
		User user = findUserWithID(userID);
		if(user == null)
			throw new UserNotFoundException();

		if (!SkillService.isSkillValid(skillName)) {
			skillName = Character.toUpperCase(skillName.charAt(0)) + skillName.substring(1);
			if (!SkillService.isSkillValid(skillName))
				throw new SkillNotFoundException();
		}

		if(user.isLoggedIn()) {
			if (user.hasSkill(skillName))
				throw new HadSkillException();
			user.addSkill(new Skill(skillName));
			UserDataHandler.addUserSkillToDB(userID, skillName);
		} else {
			throw new UserNotLoggedInException();
		}
	}

	public static void endorseSkill (String selfID, String userID, String skillName) throws NullPointerException, SkillNotFoundException, HadEndorsedException, UserNotFoundException, UserNotLoggedInException {
		User self = findUserWithID(selfID);
		if(self == null) {
			throw new UserNotFoundException();
		}
		if(!self.isLoggedIn()) {
			throw new UserNotLoggedInException();
		}

		User  user = findUserWithID(userID);
		if(user == null) {
			throw new UserNotFoundException();
		}

		Skill skill = user.getSkill(skillName);
		if (skill.wasEndorsedBy(self.getId()))
			throw new HadEndorsedException();

		skill.addPoint();
		UserDataHandler.addEndorsement(selfID, userID, skill);
		self.addEndorsement(new Endorsement(self.getId(), user.getId(), skill.getName()));
	}

	public static void deleteSkill (String skillName, User user) throws DontHaveSkillException{
		if(!user.hasSkill(skillName))
			throw new DontHaveSkillException();
		DataManager.removeUserSkill(skillName, user.getId());
	}

	public static boolean authenticateUser(String selfID) {
		User user = UserService.findUserWithID(selfID);
		if(user == null)
			return false;
		if(!user.isLoggedIn())
			return false;
		return true;
	}

	public static List<User> findUserWithName(String name) {
		return DataManager.getUserWithName(name);
	}
}
