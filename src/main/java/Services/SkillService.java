package Services;

import DataManagers.DataManager;
import Models.Skill;

public class SkillService {

    public static boolean isSkillValid(String skillName) {
        for(Skill skill : DataManager.getSkills()) {
            if(skill.getName().equals(skillName))
                return true;
        }
        return false;
    }
}
