package controllers;

import domain.dto.TaskDTO;
import domain.dto.TaskRequestDTO;
import domain.mappers.TaskMapper;
import org.apache.log4j.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.TaskService;
import services.JwtService;
import domain.models.Task;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class TaskController extends Controller {

    private static final Logger log = Logger.getLogger(TaskController.class);

    private final TaskService taskService;
    private final TaskMapper taskMapper;
    private final JwtService jwtService;

    @Inject
    public TaskController(TaskService taskService, TaskMapper taskMapper, JwtService jwtService) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
        this.jwtService = jwtService;
    }

    public Result createTask(Http.Request request) {
        log.info("Attempt to create a new task");
        try {
            if (checkToken(request)) {
                return unauthorized("Invalid or missing JWT token");
            }
            TaskRequestDTO taskRequestDTO = taskMapper.toTaskRequestDTO(request, false);
            log.info("Converted request to TaskRequestDTO");
            Task task = taskService.createTaskWithPDF(
                    taskRequestDTO.getName(),
                    taskRequestDTO.getDescription(),
                    taskRequestDTO.getPdfFile());
            return ok(Json.toJson(taskMapper.convertToTaskDTO(task)));
        } catch (Exception e) {
            log.error("Failed to create task: " + e.getMessage());
            return internalServerError("Failed to create task: " + e.getMessage());
        }
    }

    public Result updateTask(Http.Request request) {
        log.info("Attempt to update task");
        try {
            if (checkToken(request)) {
                return unauthorized("Invalid or missing JWT token");
            }
            TaskRequestDTO taskUpdateDTO = taskMapper.toTaskRequestDTO(request, true);
            log.info("Converted request to TaskRequestDTO");
            Task existingTask = taskService.getTaskById(taskUpdateDTO.getId());
            if (existingTask == null) {
                return notFound("Task not found");
            }

            existingTask.setName(taskUpdateDTO.getName());
            existingTask.setDescription(taskUpdateDTO.getDescription());

            taskService.updateTask(existingTask);

            return ok(Json.toJson(taskMapper.convertToTaskDTO(existingTask)));

        } catch (Exception e) {
            log.error("Failed to update task: " + e.getMessage());
            return internalServerError("Failed to update task: " + e.getMessage());
        }
    }

    public Result getAllTasks(Http.Request request) {
        log.info("Fetching all tasks");
        try {
            if (checkToken(request)) {
                return unauthorized("Invalid or missing JWT token");
            }

            List<Task> tasks = taskService.getAllTasks();
            List<TaskDTO> taskDTOs = new ArrayList<>();
            for (Task task : tasks) {
                taskDTOs.add(taskMapper.convertToTaskDTO(task));
            }
            return ok(Json.toJson(taskDTOs));
        } catch (Exception e) {
            log.error("Failed to fetch tasks: " + e.getMessage());
            return internalServerError("Failed to fetch tasks: " + e.getMessage());
        }
    }

    public Result getTaskById(String id, Http.Request request) {
        log.info("Fetching task with id: " + id);
        try {
            if (checkToken(request)) {
                return unauthorized("Invalid or missing JWT token");
            }

            Task task = taskService.getTaskById(id);
            if (task != null) {
                return ok(Json.toJson(taskMapper.convertToTaskDTO(task)));
            } else {
                return notFound();
            }
        } catch (Exception e) {
            log.error("Failed to fetch task: " + e.getMessage());
            return internalServerError("Failed to fetch task: " + e.getMessage());
        }
    }

    public Result deleteTask(String id, Http.Request request) {
        log.info("Deleting task with id: " + id);
        try {
            if (checkToken(request)) {
                return unauthorized("Invalid or missing JWT token");
            }

            taskService.deleteTask(id);
            return ok("Task deleted successfully");
        } catch (Exception e) {
            log.error("Failed to delete task: " + e.getMessage());
            return internalServerError("Failed to delete task: " + e.getMessage());
        }
    }

    public Result exportTaskAsZip(String id, Http.Request request) {
        log.info("Exporting task with id: " + id + " as zip");
        try {
            if (checkToken(request)) {
                return unauthorized("Invalid or missing JWT token");
            }

            File zipFile = taskService.exportTaskAsZipFile(id);
            if (zipFile == null) {
                return notFound("Task not found with ID: " + id);
            }

            return ok().sendFile(zipFile, false, String.valueOf(true))
                    .withHeader("Content-Disposition", "attachment; filename=" + zipFile.getName());
        } catch (Exception e) {
            log.error("Failed to export task: " + e.getMessage());
            return internalServerError("Failed to export task: " + e.getMessage());
        }
    }

    private boolean checkToken(Http.Request request) {
        String token = request.headers().get("Authorization").orElse(null);
        return (token == null || !jwtService.verifyToken(token));
    }
}