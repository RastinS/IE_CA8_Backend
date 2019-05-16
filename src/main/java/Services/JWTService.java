package Services;

import Models.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

public class JWTService {
	public static String createJWT (User user) throws UnsupportedEncodingException {
		Date     now_date = new Date();
		Calendar c        = Calendar.getInstance();
		c.setTime(now_date);
		c.add(Calendar.DATE, 1);
		Date      expire_date = c.getTime();
		Algorithm algorithm   = Algorithm.HMAC256("joboonja");
		return JWT.create()
				.withIssuer("farid_rastin")
				.withIssuedAt(now_date)
				.withExpiresAt(expire_date)
				.withClaim("userName", user.getUserName())
				.sign(algorithm);
	}

	public static Boolean verifyToken (String token) {
		return true;
	}
}
