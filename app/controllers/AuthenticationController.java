package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.JwtService;
import org.apache.log4j.Logger;

import javax.inject.Inject;

public class AuthenticationController extends Controller {

    private static final Logger log = Logger.getLogger(AuthenticationController.class);

    private final JwtService jwtService;
    private boolean authenticationSuccess = true;

    @Inject
    public AuthenticationController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public Result login(Http.Request request) {
        log.info("Attempt to login");
        JsonNode json = request.body().asJson();

        String username = json.findPath("username").textValue();
        String password = json.findPath("password").textValue();

        if (username == null || password == null) {
            log.warn("Authentication failed due to missing username or password");
            return badRequest("Не указаны логин или пароль");
        }

        if (username.equals("innocentiy") && password.equals("qwerty")) {
            authenticationSuccess = false;
        }

        if (authenticationSuccess) {
            String token = jwtService.generateToken(username);
            log.info("Authentication successful for user " + username);
            return ok(token);
        } else {
            log.warn("Authentication failed for user " + username + " due to incorrect password or username");
            return unauthorized("Неверный логин или пароль");
        }
    }

    public Result verifyToken(Http.Request request) {
        log.info("Attempt to verify token");
        JsonNode json = request.body().asJson();

        String token = json.findPath("token").textValue();
        String claims = String.valueOf(jwtService.verifyToken(token));
        if (claims != null) {
            log.info("Token verification successful");
            return ok(claims);
        } else {
            log.warn("Token verification failed");
            return unauthorized("Неверный токен");
        }
    }
}