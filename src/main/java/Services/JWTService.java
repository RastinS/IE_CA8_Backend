package Services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class JWTService {
	private static Algorithm algorithm = Algorithm.HMAC256("joboonja");

	public static String createJWT (String username) {
		Date     now_date = new Date();
		Calendar c        = Calendar.getInstance();
		c.setTime(now_date);
		c.add(Calendar.DATE, 1);
		Date                    expire_date = c.getTime();
		HashMap<String, Object> jwtHeader   = new HashMap<>();
		jwtHeader.put("alg", "HS256");
		jwtHeader.put("typ", "JWT");
		return JWT.create()
				.withHeader(jwtHeader)
				.withIssuer("CA8")
				.withIssuedAt(now_date)
				.withExpiresAt(expire_date)
				.withClaim("username", username)
				.sign(algorithm);
	}

	public static Boolean checkJWT (String token) {
		JWTVerifier jwtVerifier = JWT.require(algorithm)
				.withIssuer("CA8")
				.build();
		DecodedJWT jwt = jwtVerifier.verify(token);
		return true;
	}
}
