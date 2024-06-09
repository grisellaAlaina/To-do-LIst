package services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import javax.inject.Singleton;
import java.util.Date;

@Singleton
public class JwtService {

    private static final String SECRET_KEY = "G9HJ2ALjxWpt!yH$gy4G8LBNx!Fw!@Kw";
    public String generateToken(String username) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        Date expirationDate = new Date(System.currentTimeMillis() + 3600000);
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(expirationDate)
                .sign(algorithm);
    }

    public boolean verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            JWT.require(algorithm)
                    .build()
                    .verify(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}