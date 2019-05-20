package Services;

import DataManagers.DataManager;
import DataManagers.ProjectData.ProjectDataHandler;
import ErrorClasses.*;
import Models.Bid;
import Models.Project;
import Models.Skill;
import Models.User;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BidService {

	private static boolean isUserSuggested (Project project, User user) {
		for (Bid bid : project.getBids()) {
			if (bid.getBiddingUserID().equals(user.getId())) {
				return true;
			}
		}
		return false;
	}

	private static boolean isBidGraterThanBudget (Project project, int bidAmount) {
		return bidAmount <= project.getBudget();
	}

	public static boolean isUserSkillValidForProject (User user, Project project) {
		for (Skill skill : project.getSkills()) {
			if (user.getSkillPoint(skill) < skill.getPoint()) {
				return false;
			}
		}
		return true;
	}

	public static void addBid (String username, String projectID, int bidAmount) throws UserNotLoggedInException, DuplicateBidException, UserNotFoundException, ProjectNotFoundException, UserSkillsNotMatchWithProjectSkillException, BidGraterThanBudgetException {
		User user = UserService.findUserWithUserName(username);
		if (user == null) {
			throw new UserNotFoundException();
		}

		Project project = ProjectService.getProject(projectID);
		if (project == null) {
			throw new ProjectNotFoundException();
		}

		if (user.isLoggedIn()) {
			if (!BidService.isUserSuggested(project, user)) {
				if (BidService.isBidGraterThanBudget(project, bidAmount)) {
					if (BidService.isUserSkillValidForProject(user, project)) {
						Bid bid = new Bid(user.getId(), projectID, bidAmount);
						bid.setBidValue(computeBidValue(project, bid, user));
						ProjectDataHandler.addBidToDB(bid);
						project.addBid(bid);
					} else {
						throw new UserSkillsNotMatchWithProjectSkillException();
					}
				} else {
					throw new BidGraterThanBudgetException();
				}
			} else {
				throw new DuplicateBidException();
			}
		} else {
			throw new UserNotLoggedInException();
		}
	}

	public static void setWinner(Project project) {
		if(project.getBids() == null || project.getBids().size() == 0)
			DataManager.deleteProjectRecords(project.getId());
		else {
			Bid bestBid = findBestBid(project.getBids());
			DataManager.addBidWinner(bestBid.getBiddingUserID(), bestBid.getProjectID(), bestBid.getBidAmount());
		}
	}

	private static int computeBidValue(Project project, Bid bid, User user) {
		int value = 0;
		int skillPoint;
		for(Skill skill : project.getSkills()) {
			skillPoint = user.getSkillPoint(skill.getName());
			if(skillPoint != 0)
				value += 10000 * Math.pow((skillPoint - skill.getPoint()), 2);
		}
		value += project.getBudget() - bid.getBidAmount();
		return value;
	}

	private static Bid findBestBid(List<Bid> bids) {
		return Collections.max(bids, Comparator.comparing(Bid::getBidValue));
	}
}
