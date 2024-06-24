package services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;
    private String userName;

    @BeforeEach
    public void setUp() {
        jwtService = new JwtService();
        userName = "TestUser";
    }

    @Test
    public void generateTokenTest() {
        String token = jwtService.generateToken(userName);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    public void verifyTokenTest() {
        String token = jwtService.generateToken(userName);
        assertTrue(jwtService.verifyToken(token));
    }

    @Test
    public void verifyWrongTokenTest() {
        String wrongToken = "wrongToken";
        assertFalse(jwtService.verifyToken(wrongToken));
    }
}