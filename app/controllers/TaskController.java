package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.TaskService;
import models.Task;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

public class TaskController extends Controller {

    private final TaskService taskService;

    @Inject
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    public Result createTask() {
        Task task = new Task();
        task.setName("Новая задача");
        task.setDescription("Описание новой задачи");
        task.setCreatedDate(new Date());

        taskService.createTask(task);

        return ok(Json.toJson(task));
    }

    public Result getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return ok(Json.toJson(tasks));
    }

    public Result getTaskById(String id) {
        Task task = taskService.getTaskById(id);
        if (task != null) {
            return ok(Json.toJson(task));
        } else {
            return notFound();
        }
    }

    public Result deleteTask(String id) {
        taskService.deleteTask(id);
        return ok("Task deleted successfully");
    }

    public Result updateTask(String id, Http.Request request) {
        JsonNode json = request.body().asJson();
        String newName = json.findPath("name").textValue();
        String newDescription = json.findPath("description").textValue();

        Task existingTask = taskService.getTaskById(id);
        if (existingTask == null) {
            return notFound("Task not found");
        }

        existingTask.setName(newName);
        existingTask.setDescription(newDescription);

        taskService.updateTask(existingTask);

        return ok(Json.toJson(existingTask));
    }
}





