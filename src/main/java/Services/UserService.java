package Services;

import DataManagers.DataManager;
import DataManagers.UserData.UserDataHandler;
import ErrorClasses.*;
import Models.Endorsement;
import Models.Skill;
import Models.User;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

public class UserService {

	public static User findUserWithID (String selectID) {
		return DataManager.findUserWithID(selectID);
	}

	public static void addSkillToUser (String userID, String skillName) throws UserNotLoggedInException, HadSkillException, SkillNotFoundException, UserNotFoundException {
		User user = findUserWithID(userID);
		if (user == null)
			throw new UserNotFoundException();

		if (!SkillService.isSkillValid(skillName)) {
			skillName = Character.toUpperCase(skillName.charAt(0)) + skillName.substring(1);
			if (!SkillService.isSkillValid(skillName))
				throw new SkillNotFoundException();
		}

		if (user.isLoggedIn()) {
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
		if (self == null) {
			throw new UserNotFoundException();
		}
		if (!self.isLoggedIn()) {
			throw new UserNotLoggedInException();
		}

		User user = findUserWithID(userID);
		if (user == null) {
			throw new UserNotFoundException();
		}

		Skill skill = user.getSkill(skillName);
		if (skill.wasEndorsedBy(self.getId()))
			throw new HadEndorsedException();

		skill.addPoint();
		UserDataHandler.addEndorsement(selfID, userID, skill);
		self.addEndorsement(new Endorsement(self.getId(), user.getId(), skill.getName()));
	}

	public static void deleteSkill (String skillName, User user) throws DontHaveSkillException {
		if (!user.hasSkill(skillName))
			throw new DontHaveSkillException();
		DataManager.removeUserSkill(skillName, user.getId());
	}

	public static boolean authenticateUser (String selfID) {
		User user = UserService.findUserWithID(selfID);
		if (user == null)
			return false;
		return user.isLoggedIn();
	}

	public static List<User> findUserWithName (String name) {
		return DataManager.getUserWithName(name);
	}

	public static void signUp (JSONObject data, String token) throws DuplicateUsernameException {
		try {
			String userName = data.getString("userName");

			if (isUsernameValid(userName)) {
				String firstName         = data.getString("firstName");
				String lastName          = data.getString("lastName");
				String jobTitle          = data.getString("jobTitle");
				String profilePictureURL = data.getString("profilePictureURL");
				String bio               = data.getString("bio");
				String password          = data.getString("password");

				User newUser = new User();
				newUser.setFirstName(firstName);
				newUser.setLastName(lastName);
				newUser.setJobTitle(jobTitle);
				newUser.setProfilePictureURL(profilePictureURL);
				newUser.setBio(bio);
				newUser.setUserName(userName);
				newUser.setPassword(password);
				newUser.setToken(token);

				newUser.setId(DataManager.getNextValidUserID());

				DataManager.addUserToDB(newUser);
			} else {
				throw new DuplicateUsernameException();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private static boolean isUsernameValid (String userName) {
		User dupUser = DataManager.findUserWithUsername(userName);
		return dupUser == null;
	}

	public static void signIn (JSONObject data) throws NoSuchUsernameException, WrongPasswordException, JSONException {
		String userName = data.getString("userName");
		if (isUsernameValid(userName))
			throw new NoSuchUsernameException();
		String password = data.getString("password");
		if (isPasswordCorrect(userName, password)) {
			userLogin(userName);
		} else
			throw new WrongPasswordException();
	}

	private static boolean isPasswordCorrect (String userName, String password) {
		return DataManager.checkPasswordCorrectness(userName, password);
	}

	private static void userLogin (String userName) {
		DataManager.userLogin(userName);
	}

	public static User findUserWithUserName(String userName) {
		return DataManager.findUserWithUsername(userName);
	}
}