package Repositories;

import DataManagers.DataManager;
import Models.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private static User loggedInUser = null;

    public static User getLoggedInUser () {
        return loggedInUser;
    }

    public static void setLoggedInUser (User loggedInUser) {
        UserRepository.loggedInUser = loggedInUser;
        loggedInUser.setLoggedIn(true);
    }

    public static User setUser (String data) throws JSONException {
        User       user       = new User();
        JSONObject jsonObject = new JSONObject(data);
        user.setId(jsonObject.getString("id"));
        user.setFirstName(jsonObject.getString("firstName"));
        user.setLastName(jsonObject.getString("lastName"));
        user.setJobTitle(jsonObject.getString("jobTitle"));
        user.setBio(jsonObject.getString("bio"));
        user.setSkills(jsonObject.getString("skills"));
        return user;
    }

    public static List<User> setUsers(String data) throws JSONException{
        List<User> users = new ArrayList<User>();
        JSONArray usersArray = new JSONArray(data);
        for (int i = 0; i < usersArray.length(); i++) {
            users.add(setUser(usersArray.getString(i)));
        }
        return users;
    }



    public static List<User> getUsers() {
        return DataManager.getUsers();
    }

    public static List<User> getUsers(String selfID) {
        ArrayList<User> users =  new ArrayList<User>(DataManager.getUsers());
        for(User user : users) {
            if(user.getId().equals(selfID)) {
                users.remove(user);
                return users;
            }
        }
        return users;
    }
}
