package Services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class JWTService {
	public static String createJWT () {
		Date     now_date = new Date();
		Calendar c        = Calendar.getInstance();
		c.setTime(now_date);
		c.add(Calendar.DATE, 1);
		Date expire_date = c.getTime();

		HashMap<String, Object> jwtHeader = new HashMap<>();
		jwtHeader.put("alg", "HS256");
		jwtHeader.put("typ", "JWT");
		return JWT.create()
				.withHeader(jwtHeader)
				.withIssuer("CA8")
				.withIssuedAt(now_date)
				.withExpiresAt(expire_date)
				.sign(Algorithm.HMAC256("secret"));
	}

	public static Boolean verifyToken (String token) {
		return true;
	}
}
