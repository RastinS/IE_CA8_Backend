package Controllers;

import Models.Skill;
import Repositories.SkillRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@CrossOrigin (origins = "*", allowedHeaders = "*")
@RestController
public class GetSkills {
    @RequestMapping (value = "/skills", method = RequestMethod.GET)
    public ResponseEntity getSkills(HttpServletRequest req) {
        List<Skill> skills = SkillRepository.getSkills();
        if(skills != null)
            return ResponseEntity.ok(skills);
        else
            return new ResponseEntity<>("Couldn't fetch skills list from database!", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
