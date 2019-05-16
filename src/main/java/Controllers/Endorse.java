package Controllers;

import ErrorClasses.*;
import Services.UserService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin (origins = "*", allowedHeaders = "*")
@RestController
public class Endorse {

	@RequestMapping (value = "/endorse", method = RequestMethod.POST)
	public ResponseEntity endorse (HttpServletRequest req, @RequestBody String reqData) {
		try {
			JSONObject data = new JSONObject(reqData);
			String selfID = req.getHeader("user-token");
			String userID = data.getString("userID");
			String skillName = data.getString("skillName");

			UserService.endorseSkill(selfID, userID, skillName);
			return ResponseEntity.ok("Endorsed successfully!");
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (SkillNotFoundException e) {
			e.printStackTrace();
			return new ResponseEntity<>("skill not found", HttpStatus.NOT_FOUND);
		} catch (HadEndorsedException e) {
			e.printStackTrace();
			return new ResponseEntity<>("Skill already endorsed", HttpStatus.FORBIDDEN);
		} catch (UserNotFoundException e) {
			e.printStackTrace();
			return new ResponseEntity<>("no user with such userID !", HttpStatus.NOT_FOUND);
		} catch (UserNotLoggedInException e) {
			e.printStackTrace();
			return new ResponseEntity<>("Login First !", HttpStatus.FORBIDDEN);
		}
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
}

//{"userID"="2", "skillName"="HTML"}
