package Controllers;

import Models.User;
import Services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin (origins = "*", allowedHeaders = "*")
@RestController
public class GetProfile {
    @RequestMapping (value = "/profile", method = RequestMethod.GET)
    public ResponseEntity getProfile(HttpServletRequest req) {
        if(HelperMethods.isUserNotLoggedIn(req))
            return new ResponseEntity<>("Login First !", HttpStatus.FORBIDDEN);

        String userID = req.getHeader("user-token");

        User user = UserService.findUserWithID(userID);
        if (user != null)
            return ResponseEntity.ok(user);
        else
            return new ResponseEntity<>("No user found with this ID!", HttpStatus.NOT_FOUND);
    }
}
