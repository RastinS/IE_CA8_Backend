package Models;

public class Endorsement {
    private String endorserID;
    private String endorsedID;
    private String skillName;

    public Endorsement(String endorsingUser, String endorsedUser, String endorsedSkill) {
        this.endorserID = endorsingUser;
        this.endorsedID = endorsedUser;
        this.skillName = endorsedSkill;
    }

    public String getEndorsedSkillName() {
        return skillName;
    }

    public String getEndorsedUserID() {
        return endorsedID;
    }

    public String getEndorsingUserID() {
        return endorserID;
    }

    public void setEndorsedSkill(String endorsedSkill) {
        this.skillName = endorsedSkill;
    }

    public void setEndorsedUser(String endorsedUser) {
        this.endorserID = endorsedUser;
    }

    public void setEndorsingUser(String endorsingUser) {
        this.endorsedID = endorsingUser;
    }
}
