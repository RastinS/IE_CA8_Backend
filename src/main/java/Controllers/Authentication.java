package Controllers;

import ErrorClasses.DuplicateUsernameException;
import Services.JWTService;
import Services.UserService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@CrossOrigin (origins = "*", allowedHeaders = "*")
@RestController

public class Authentication {
	@PostMapping (value = "/sign-up")
	public ResponseEntity SignUpUser (@RequestBody String requestBody) throws UnsupportedEncodingException {
		try {
			JSONObject data = new JSONObject(requestBody);
			UserService.signUp(data);
			String token = JWTService.createJWT();
			return ResponseEntity.status(HttpStatus.OK).header("user-token", token).body("user signed up");
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (DuplicateUsernameException e) {
			return new ResponseEntity<>("duplicate username", HttpStatus.NOT_ACCEPTABLE);
		}
		return new ResponseEntity<>("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@PostMapping (value = "/sign-in")
	public ResponseEntity SignInUser (HttpServletRequest req) {
		return (ResponseEntity) ResponseEntity.ok();
	}
}
