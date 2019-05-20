package Controllers;

import Services.JWTService;
import Services.UserService;

import javax.servlet.http.HttpServletRequest;

class HelperMethods {
    static boolean isUserNotLoggedIn(HttpServletRequest req) {
        String username = JWTService.decodeUsernameJWT(req.getHeader("user-token"));
        return (!UserService.authenticateUser(username));
    }
}
