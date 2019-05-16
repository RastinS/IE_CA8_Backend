package Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin (origins = "*", allowedHeaders = "*")
@RestController

public class Authentication {
	@PostMapping (value = "/sign-in")
	public ResponseEntity SignInUser (HttpServletRequest req) {
		return (ResponseEntity) ResponseEntity.ok();
	}

	@PostMapping (value = "/sign-up")
	public ResponseEntity SignUpUser (HttpServletRequest req) {
		return (ResponseEntity) ResponseEntity.ok();
	}
}
