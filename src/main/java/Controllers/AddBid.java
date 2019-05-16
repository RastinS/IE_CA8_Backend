package Controllers;

import ErrorClasses.*;
import Services.BidService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin (origins = "*", allowedHeaders = "*")
@RestController
public class AddBid {

	@RequestMapping (value = "/addBid", method = RequestMethod.POST)
	public ResponseEntity addBid (HttpServletRequest req, @RequestBody String reqData) {
		try {
			JSONObject data      = new JSONObject(reqData);
			int        bidAmount = Integer.parseInt(data.getString("bidAmount"));
			String     projectId = data.getString("projectID");
			String     selfID    = req.getHeader("user-token");

			BidService.addBid(selfID, projectId, bidAmount);
			return ResponseEntity.ok("Your bid was successfully added :)");

		} catch (JSONException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("500");
		} catch (UserSkillsNotMatchWithProjectSkillException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("your skills do not match to this project skills !");
		} catch (BidGraterThanBudgetException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("your bid amount grater than this project budget !");
		} catch (UserNotFoundException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such user with userID !");
		} catch (ProjectNotFoundException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no such project with projectID !");
		} catch (DuplicateBidException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("you can not bid again !");
		} catch (UserNotLoggedInException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Login First !");
		}
	}
}

//{"bidAmount"="100000", "projectID"="2af588a2-942c-4201-b3bc-7d0376f0b470"}