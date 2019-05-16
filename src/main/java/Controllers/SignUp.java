package Controllers;

import ErrorClasses.DuplicateUsernameException;
import Services.UserService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin (origins = "*", allowedHeaders = "*")
@RestController
public class SignUp {

	@RequestMapping (value = "/signUp", method = RequestMethod.POST)
	public ResponseEntity signUp(HttpServletRequest req, @RequestBody String reqData) {
		try {
			JSONObject data = new JSONObject(reqData);
			UserService.signUp(data);
			return ResponseEntity.ok("user signed up");
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (DuplicateUsernameException e) {
			return new ResponseEntity<>("duplicate username", HttpStatus.NOT_ACCEPTABLE);
		}
		return new ResponseEntity<>("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
