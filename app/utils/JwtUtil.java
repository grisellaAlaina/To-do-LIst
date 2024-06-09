package utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

import java.util.Date;

public class JwtUtil {
    private static final String SECRET_KEY = "G9HJ2ALjxWpt!yH$gy4G8LBNx!Fw!@Kw";

    public static String generateToken(String username) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        Date expirationDate = new Date(System.currentTimeMillis() + 3600000);
        return JWT.create()
                .withIssuer("your_issuer")
                .withSubject(username)
                .withExpiresAt(expirationDate)
                .sign(algorithm);
    }

    public static boolean verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            JWT.require(algorithm)
                    .withIssuer("your_issuer")
                    .build()
                    .verify(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public static String getUsernameFromToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getSubject();
        } catch (JWTDecodeException exception) {
            return null;
        }
    }

    public static String generateTokenResponse(String username) {
        String token = generateToken(username);
        ObjectNode jsonResponse = Json.newObject();
        jsonResponse.put("token", token);
        return jsonResponse.toString();
    }
}
