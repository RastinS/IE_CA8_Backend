package Controllers;

import Services.UserService;

import javax.servlet.http.HttpServletRequest;

class HelperMethods {
    static boolean isUserNotLoggedIn(HttpServletRequest req) {
        String selfID = req.getHeader("user-token");
        return (!UserService.authenticateUser(selfID));
    }
}
