package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import play.mvc.Http;
import play.mvc.Result;
import services.JwtService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthenticationControllerTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLogin() {
        Http.Request request = mock(Http.Request.class);
        when(request.body()).thenReturn(new Http.RequestBody(asJson("username", "password")));

        String dummyToken = "generatedDummyToken";
        when(jwtService.generateToken(anyString())).thenReturn(dummyToken);

        Result result = authenticationController.login(request);

        assertEquals(200, result.status());
    }

    @Test
    public void testVerifyToken() {
        Http.Request request = mock(Http.Request.class);
        when(request.body()).thenReturn(new Http.RequestBody(asJson("token")));

        when(jwtService.verifyToken("token")).thenReturn(true);

        Result result = authenticationController.verifyToken(request);

        assertEquals(200, result.status());
    }

    private JsonNode asJson(String token) {
        ObjectNode jsonNodes = JsonNodeFactory.instance.objectNode();
        jsonNodes.put("token", token);
        return jsonNodes;
    }

    private JsonNode asJson(String username, String password) {
        ObjectNode jsonNodes = JsonNodeFactory.instance.objectNode();
        jsonNodes.put("username", username);
        jsonNodes.put("password", password);
        return jsonNodes;
    }
}