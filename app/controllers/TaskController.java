package controllers;

import play.libs.Json;
import play.mvc.Controller;
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








}
