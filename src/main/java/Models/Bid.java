package Models;


public class Bid {
	private String  userID;
	private String  projectID;
	private Integer bidAmount;

	public Bid (String biddingUser, String project, Integer bidAmount) {
		this.userID = biddingUser;
		this.projectID = project;
		this.bidAmount = bidAmount;
	}

	public String getBiddingUserID () {
		return userID;
	}

	public void setBiddingUserID (String id) {
		this.userID = id;
	}

	public String getProjectID () {
		return projectID;
	}

	public void setProjectID (String projectID) {
		this.projectID = projectID;
	}

	public Integer getBidAmount () {
		return bidAmount;
	}

	public void setBidAmount (Integer bidAmount) {
		this.bidAmount = bidAmount;
	}
}