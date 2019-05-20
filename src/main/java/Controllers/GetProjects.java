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
public class GetProjects {

	@RequestMapping (value = "/projects", method = RequestMethod.GET)
	public ResponseEntity getProjects (HttpServletRequest req) {

		String user_token = req.getHeader("user-token");
		String pageNum    = req.getParameter("page_number");

		List<Project> projects;
		if (user_token != null && !user_token.equals("") && JWTService.checkJWT(user_token)) {
			projects = ProjectService.getProjects(JWTService.decodeUsernameJWT(req.getHeader("user-token")), pageNum);
		} else {
			projects = ProjectService.getProjects(pageNum);
		}

		if (projects != null)
			return ResponseEntity.ok(projects);
		else
			return new ResponseEntity<>("Couldn't fetch projects list from database!", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@RequestMapping (value = "/projects/{id}", method = RequestMethod.GET)
	public ResponseEntity getProject (HttpServletRequest req, @PathVariable String id) {

		Project project = ProjectService.getProject(id);
		if (project != null)
			return ResponseEntity.ok(project);
		else
			return new ResponseEntity<>("project not found with this ID!", HttpStatus.NOT_FOUND);
	}
}
