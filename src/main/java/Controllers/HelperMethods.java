package Controllers;

import Services.UserService;
import javax.servlet.http.HttpServletRequest;

class HelperMethods {
    static boolean isUserNotLoggedIn(HttpServletRequest req) {
        String username = (String) req.getAttribute("username");
        return (!UserService.authenticateUser(username));
    }
}
