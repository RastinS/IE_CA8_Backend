package Controllers;

import ErrorClasses.DuplicateUsernameException;
import ErrorClasses.NoSuchUsernameException;
import ErrorClasses.WrongPasswordException;
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

@CrossOrigin (origins = "*", allowedHeaders = "*")
@RestController

public class Authentication {
	@PostMapping (value = "/sign-up")
	public ResponseEntity SignUpUser (@RequestBody String requestBody) {
		try {
			JSONObject data  = new JSONObject(requestBody);
			String     token = JWTService.createJWT(data.getString("userName"));
			UserService.signUp(data, token);
			return ResponseEntity.status(HttpStatus.OK).header("user-token", token).body("user signed up");
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (DuplicateUsernameException e) {
			return new ResponseEntity<>("duplicate username", HttpStatus.NOT_ACCEPTABLE);
		}
		return new ResponseEntity<>("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@PostMapping (value = "/sign-in")
	public ResponseEntity SignInUser (@RequestBody String requestBody) {
		try {
			JSONObject data = new JSONObject(requestBody);
			UserService.signIn(data);
			String token = JWTService.createJWT(data.getString("userName"));
			return ResponseEntity.status(HttpStatus.OK).header("user-token", token).body("logged in");
		} catch (NoSuchUsernameException | WrongPasswordException | JSONException e) {
			e.printStackTrace();
		}
		return (ResponseEntity) ResponseEntity.ok();
	}
}

//{"firstName" : "rastin", "lastName" : "soraki", "jobTitle" : "front-end developer", "profilePictureURL" : "", "bio" : "NUMB", "userName" : "rastin", "password" : "rssorsso"}
