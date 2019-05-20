package Controllers;

import Models.Project;
import Services.JWTService;
import Services.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@CrossOrigin (origins = "*", allowedHeaders = "*")
@RestController
public class getProjectsNum {

	@RequestMapping (value = "/getProjectsNum", method = RequestMethod.GET)
	public ResponseEntity projectsNum(HttpServletRequest req) {
		String username = JWTService.decodeUsernameJWT(req.getHeader("user-token"));
		if(username == null || username.equals(""))
			return ResponseEntity.ok(ProjectService.getProjectsNum());
		else
			return ResponseEntity.ok(ProjectService.getProjectsNum(username));
	}
}
