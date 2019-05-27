package Controllers;

import Services.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

@CrossOrigin (origins = "*", allowedHeaders = "*")
@RestController
public class getProjectsNum {

	@RequestMapping (value = "/getProjectsNum", method = RequestMethod.GET)
	public ResponseEntity projectsNum(HttpServletRequest req) {
		String username = (String) req.getAttribute("username");
		if(username == null || username.equals(""))
			return ResponseEntity.ok(ProjectService.getProjectsNum());
		else
			return ResponseEntity.ok(ProjectService.getProjectsNum(username));
	}
}
