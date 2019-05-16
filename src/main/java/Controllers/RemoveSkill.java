package Controllers;

import Services.UserService;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin (origins = "*", allowedHeaders = "*")
@RestController
public class RemoveSkill {

	@RequestMapping (value = "/removeSkill", method = RequestMethod.POST)
	public ResponseEntity removeSkill (HttpServletRequest req, @RequestBody String reqData) {
		if(HelperMethods.isUserNotLoggedIn(req))
			return new ResponseEntity<>("Login First !", HttpStatus.FORBIDDEN);

		try {
			JSONObject data   = new JSONObject(reqData);
			String selfID = req.getHeader("user-token");

			UserService.deleteSkill(data.getString("skillName"), UserService.findUserWithID(selfID));
			return ResponseEntity.ok("deleted successfully!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

}

//{"skillName"="HTML"}
