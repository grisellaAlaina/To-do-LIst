package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.JwtService;
import javax.inject.Inject;

public class AuthenticationController extends Controller {

    private final JwtService jwtService;
    private boolean authenticationSuccess = true;

    @Inject
    public AuthenticationController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public Result login(Http.Request request) {
        JsonNode json = request.body().asJson();

        String username = json.findPath("username").textValue();
        String password = json.findPath("password").textValue();

        if (username == null || password == null) {
            return badRequest("Не указаны логин или пароль");
        }

        if (username.equals("innocentiy") && password.equals("qwerty")) {
            authenticationSuccess = false;
        }

        if (authenticationSuccess) {
            String token = jwtService.generateToken(username);
            return ok(token);
        } else {
            return unauthorized("Неверный логин или пароль");
        }
    }

    public Result verifyToken(Http.Request request) {
        JsonNode json = request.body().asJson();

        String token = json.findPath("token").textValue();
        String claims = String.valueOf(jwtService.verifyToken(token));
        if (claims != null) {
            return ok(claims);
        } else {
            return unauthorized("Неверный токен");
        }
    }
}