package Repositories;

import DataManagers.DataManager;
import Models.Skill;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class SkillRepository {
    public static List<Skill> setSkills (String data, String type) throws JSONException {
        List<Skill> skills      = new ArrayList<Skill>();
        JSONArray   skillsArray = new JSONArray(data);
        for (int i = 0; i < skillsArray.length(); i++) {
            if (type.equals("FROM_IO")) {
                skills.add(new Skill(skillsArray.getJSONObject(i).getString("name")));
            } else {
                skills.add(new Skill(skillsArray.getJSONObject(i).getString("name"), skillsArray.getJSONObject(i).getInt("point")));
            }
        }
        return skills;
    }

    public static List<Skill> getSkills() {
        return DataManager.getSkills();
    }
}
