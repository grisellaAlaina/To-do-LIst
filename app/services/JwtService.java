package services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.apache.log4j.Logger;

import javax.inject.Singleton;
import java.util.Date;

@Singleton
public class JwtService {

    private static final Logger log = Logger.getLogger(JwtService.class);

    private static final String SECRET_KEY = "G9HJ2ALjxWpt!yH$gy4G8LBNx!Fw!@Kw";

    public String generateToken(String username) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        Date expirationDate = new Date(System.currentTimeMillis() + 999900000);
        String token = JWT.create()
                .withSubject(username)
                .withExpiresAt(expirationDate)
                .sign(algorithm);
        log.info("JWT created for user: " + username);
        return token;
    }

    public boolean verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            JWT.require(algorithm)
                    .build()
                    .verify(token);
            log.info("JWT verified successfully");
            return true;
        } catch (Exception exception) {
            log.error("JWT verification failed: " + exception.getMessage());
            return false;
        }
    }
}