package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.TaskService;
import services.JwtService;
import models.Task;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

public class TaskController extends Controller {

    private final TaskService taskService;
    private final JwtService jwtService;

    @Inject
    public TaskController(TaskService taskService, JwtService jwtService) {
        this.taskService = taskService;
        this.jwtService = jwtService;
    }

    public Result createTask(Http.Request request) {
        try {
            String token = request.headers().get("Authorization").orElse(null);
            if (token == null || !jwtService.verifyToken(token)) {
                return unauthorized("Invalid or missing JWT token");
            }

            JsonNode json = request.body().asJson();
            if (json == null) {
                return badRequest("Expecting JSON data");
            }

            String name = json.findPath("name").textValue();
            String description = json.findPath("description").textValue();

            if (name == null || name.isEmpty()) {
                return badRequest("Missing or empty 'name' field");
            }

            if (description == null || description.isEmpty()) {
                return badRequest("Missing or empty 'description' field");
            }

            Task task = new Task();
            task.setName(name);
            task.setDescription(description);
            task.setCreatedDate(new Date());

            taskService.createTask(task);

            return ok(Json.toJson(task));
        } catch (Exception e) {
            return internalServerError("Failed to create task: " + e.getMessage());
        }
    }

    public Result getAllTasks(Http.Request request) {
        try {
            String token = request.headers().get("Authorization").orElse(null);
            if (token == null || !jwtService.verifyToken(token)) {
                return unauthorized("Invalid or missing JWT token");
            }

            List<Task> tasks = taskService.getAllTasks();
            return ok(Json.toJson(tasks));
        } catch (Exception e) {
            return internalServerError("Failed to fetch tasks: " + e.getMessage());
        }
    }

    public Result getTaskById(String id, Http.Request request) {
        try {
            String token = request.headers().get("Authorization").orElse(null);
            if (token == null || !jwtService.verifyToken(token)) {
                return unauthorized("Invalid or missing JWT token");
            }

            Task task = taskService.getTaskById(id);
            if (task != null) {
                return ok(Json.toJson(task));
            } else {
                return notFound();
            }
        } catch (Exception e) {
            return internalServerError("Failed to fetch task: " + e.getMessage());
        }
    }

    public Result deleteTask(String id, Http.Request request) {
        try {
            String token = request.headers().get("Authorization").orElse(null);
            if (token == null || !jwtService.verifyToken(token)) {
                return unauthorized("Invalid or missing JWT token");
            }

            taskService.deleteTask(id);
            return ok("Task deleted successfully");
        } catch (Exception e) {
            return internalServerError("Failed to delete task: " + e.getMessage());
        }
    }

    public Result updateTask(String id, Http.Request request) {
        try {
            String token = request.headers().get("Authorization").orElse(null);
            if (token == null || !jwtService.verifyToken(token)) {
                return unauthorized("Invalid or missing JWT token");
            }

            JsonNode json = request.body().asJson();
            if (json == null) {
                return badRequest("Expecting JSON data");
            }

            String newName = json.findPath("name").textValue();
            String newDescription = json.findPath("description").textValue();

            if (newName == null || newName.isEmpty()) {
                return badRequest("Missing or empty 'name' field");
            }

            if (newDescription == null || newDescription.isEmpty()) {
                return badRequest("Missing or empty 'description' field");
            }

            Task existingTask = taskService.getTaskById(id);
            if (existingTask == null) {
                return notFound("Task not found");
            }

            existingTask.setName(newName);
            existingTask.setDescription(newDescription);

            taskService.updateTask(existingTask);

            return ok(Json.toJson(existingTask));
        } catch (Exception e) {
            return internalServerError("Failed to update task: " + e.getMessage());
        }
    }
}