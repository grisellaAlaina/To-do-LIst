package controllers;

import dto.TaskDTO;
import org.apache.log4j.Logger;
import play.libs.Files;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.TaskService;
import services.JwtService;
import models.Task;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class TaskController extends Controller {

    private static final Logger log = Logger.getLogger(TaskController.class);

    private final TaskService taskService;
    private final JwtService jwtService;

    @Inject
    public TaskController(TaskService taskService, JwtService jwtService) {
        this.taskService = taskService;
        this.jwtService = jwtService;
    }

    public Result createTask(Http.Request request) {
        log.info("Attempt to create a new task");
        try {
            if (checkToken(request)) {
                return unauthorized("Invalid or missing JWT token");
            }

            Http.MultipartFormData<Files.TemporaryFile> body = request.body().asMultipartFormData();
            Map<String, String[]> formData = body.asFormUrlEncoded();
            Http.MultipartFormData.FilePart<Files.TemporaryFile> pdfFilePart = body.getFile("pdfFile");
            File pdfFile = pdfFilePart != null ? pdfFilePart.getRef().path().toFile() : null;
            String name = taskService.getValueFromFormData(formData, "name");
            String description = taskService.getValueFromFormData(formData, "description");

            if (name == null || name.isEmpty()) {
                return badRequest("Missing or empty 'name' field");
            }
            if (description == null || description.isEmpty()) {
                return badRequest("Missing or empty 'description' field");
            }

            Task task = taskService.createTaskWithPDF(name, description, pdfFile);

            return ok(Json.toJson(convertToTaskDTO(task)));
        } catch (Exception e) {
            log.error("Failed to create task: " + e.getMessage());
            return internalServerError("Failed to create task: " + e.getMessage());
        }
    }

    public Result updateTask(Http.Request request) {
        log.info("Attempt to update a task");
        try {
            if (checkToken(request)) {
                return unauthorized("Invalid or missing JWT token");
            }

            Http.MultipartFormData<Files.TemporaryFile> body = request.body().asMultipartFormData();
            Map<String, String[]> formData = body.asFormUrlEncoded();
            Http.MultipartFormData.FilePart<Files.TemporaryFile> pdfFilePart = body.getFile("pdfFile");
            File pdfFile = pdfFilePart != null ? pdfFilePart.getRef().path().toFile() : null;
            String id = taskService.getValueFromFormData(formData, "id");
            String newName = taskService.getValueFromFormData(formData, "name");
            String newDescription = taskService.getValueFromFormData(formData, "description");

            if (id == null || id.isEmpty()) {
                return badRequest("Missing or empty 'id' field");
            }
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

            if (pdfFile != null) {
                List<String> imageIds = taskService.convertPDFToImages(pdfFile);
                existingTask.setImageIds(imageIds);
            }

            existingTask.setName(newName);
            existingTask.setDescription(newDescription);

            taskService.updateTask(existingTask);

            return ok(Json.toJson(convertToTaskDTO(existingTask)));
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
                taskDTOs.add(convertToTaskDTO(task));
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
                return ok(Json.toJson(convertToTaskDTO(task)));
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

            Task task = taskService.getTaskById(id);
            if (task == null) {
                return notFound("Task not found with ID: " + id);
            }
            String destinationPath = File.createTempFile(task.getName(), ".zip").getPath();

            File zipFile = taskService.createTaskAsZipFile(task, destinationPath);

            return ok().sendFile(zipFile, false, String.valueOf(true))
                    .withHeader("Content-Disposition", "attachment; filename=" + zipFile.getName());
        } catch (Exception e) {
            log.error("Failed to export task: " + e.getMessage());
            return internalServerError("Failed to export task: " + e.getMessage());
        }
    }

    private boolean checkToken (Http.Request request) {
        String token = request.headers().get("Authorization").orElse(null);
        return  (token == null || !jwtService.verifyToken(token));
    }

    private TaskDTO convertToTaskDTO (Task taskToConvert) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(taskToConvert.getId().toString());
        taskDTO.setName(taskToConvert.getName());
        taskDTO.setDescription(taskToConvert.getDescription());
        taskDTO.setCreatedDate(taskToConvert.getCreatedDate());
        taskDTO.setImageIds(taskToConvert.getImageIds());
        return taskDTO;
    }
}