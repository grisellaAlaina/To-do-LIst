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
    public void generateTokenTest_tokenShouldCreateToken() {
        String token = jwtService.generateToken(userName);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    public void verifyTokenTest_shouldReturnTrue() {
        String token = jwtService.generateToken(userName);
        assertTrue(jwtService.verifyToken(token));
    }

    @Test
    public void verifyWrongTokenTest_shouldReturnFalse() {
        String wrongToken = "wrongToken";
        assertFalse(jwtService.verifyToken(wrongToken));
    }
}