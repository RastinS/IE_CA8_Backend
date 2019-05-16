package Controllers;

import Models.User;
import Repositories.UserRepository;
import Services.JWTService;
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
	public ResponseEntity SignUpUser (@RequestBody String requestBody) throws JSONException, UnsupportedEncodingException {
		JSONObject data  = new JSONObject(requestBody);
		User       user  = UserRepository.setUserForAuth(data);
		String     token = JWTService.createJWT(user);
		return ResponseEntity.status(HttpStatus.OK).body(token);
	}

	@PostMapping (value = "/sign-in")
	public ResponseEntity SignInUser (HttpServletRequest req) {
		return (ResponseEntity) ResponseEntity.ok();
	}
}
