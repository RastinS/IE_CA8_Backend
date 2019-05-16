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
public class AddSkill {

	@RequestMapping (value = "/addSkill", method = RequestMethod.POST)
	public ResponseEntity addSkill (HttpServletRequest req, @RequestBody String reqData) {
		try {
			JSONObject data = new JSONObject(reqData);
			String selfID = req.getHeader("user-token");
			String skillName = data.getString("skillName");
			UserService.addSkillToUser(selfID, skillName);
			return ResponseEntity.ok("Skill added successfully!");
		} catch (HadSkillException e) {
			e.printStackTrace();
			return new ResponseEntity<>("Skill already in skill set.", HttpStatus.BAD_REQUEST);
		} catch (SkillNotFoundException e) {
			e.printStackTrace();
			return new ResponseEntity<>("Skill not in database.", HttpStatus.NOT_FOUND);
		} catch (JSONException e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch(UserNotFoundException e) {
			e.printStackTrace();
			return new ResponseEntity<>("No user with such userID !", HttpStatus.NOT_FOUND);
		} catch(UserNotLoggedInException e) {
			e.printStackTrace();
			return new ResponseEntity<>("Login First !", HttpStatus.FORBIDDEN);
		}
	}
}

//{"skillName"="Node.js"}