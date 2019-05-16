package Controllers;

import Models.User;
import Repositories.UserRepository;
import Services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@CrossOrigin (origins = "*", allowedHeaders = "*")
@RestController
public class GetUsers {

	@RequestMapping (value = "/users", method = RequestMethod.GET)
	public ResponseEntity getUsers (HttpServletRequest req) {

		List<User> users;
		String selfID = req.getHeader("user-token");
		if(selfID == null || selfID.equals(""))
			users = UserRepository.getUsers();
		else
			users = UserRepository.getUsers(selfID);

		if (users.size() != 0)
			return ResponseEntity.ok(users);
		else
			return new ResponseEntity<>("Couldn't fetch user list from database!", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@RequestMapping (value = "/users/{id}", method = RequestMethod.GET)
	public ResponseEntity getUser (HttpServletRequest req, @PathVariable String id) {

		User user = UserService.findUserWithID(id);
		if (user != null)
			return ResponseEntity.ok(user);
		else
			return new ResponseEntity<>("User not found with this ID!", HttpStatus.NOT_FOUND);
	}

}
