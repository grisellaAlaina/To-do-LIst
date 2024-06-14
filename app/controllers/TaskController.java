package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
            if (checkToken(request)) {
                return unauthorized("Invalid or missing JWT token");
            }

            Http.MultipartFormData<Files.TemporaryFile> body = request.body().asMultipartFormData();
            Map<String, String[]> formData = body.asFormUrlEncoded();
            Http.MultipartFormData.FilePart<Files.TemporaryFile> pdfFilePart =
                    body.getFile("pdfFile");
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

            return ok(Json.toJson(task));
        } catch (Exception e) {
            return internalServerError("Failed to create task: " + e.getMessage());
        }
    }

    public Result updateTask(Http.Request request) {
        try {
            if (checkToken(request)) {
                return unauthorized("Invalid or missing JWT token");
            }

            Http.MultipartFormData<Files.TemporaryFile> body = request.body().asMultipartFormData();
            Map<String, String[]> formData = body.asFormUrlEncoded();
            Http.MultipartFormData.FilePart<Files.TemporaryFile> pdfFilePart =
                    body.getFile("pdfFile");
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

            return ok(Json.toJson(existingTask));
        } catch (Exception e) {
            return internalServerError("Failed to update task: " + e.getMessage());
        }
    }

    public Result getAllTasks(Http.Request request) {
        try {
            if (checkToken(request)) {
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
            if (checkToken(request)) {
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
            if (checkToken(request)) {
                return unauthorized("Invalid or missing JWT token");
            }

            taskService.deleteTask(id);
            return ok("Task deleted successfully");
        } catch (Exception e) {
            return internalServerError("Failed to delete task: " + e.getMessage());
        }
    }

    public Result exportTaskAsZip(String id, Http.Request request) {
        try {
            if (checkToken(request)) {
                return unauthorized("Invalid or missing JWT token");
            }

            Task task = taskService.getTaskById(id);
            if (task == null) {
                return notFound("Task not found");
            }

            File zipFile = File.createTempFile(task.getName(), ".zip");
            try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
                ZipEntry taskEntry = new ZipEntry("task.json");
                zos.putNextEntry(taskEntry);
                ObjectMapper objectMapper = new ObjectMapper();
                String taskJson = objectMapper.writeValueAsString(task);
                zos.write(taskJson.getBytes());
                zos.closeEntry();

                for (String imageId : task.getImageIds()) {
                    byte[] imageBytes = taskService.getImageByImageId(imageId);
                    if (imageBytes != null) {
                        ZipEntry imageEntry = new ZipEntry("images/" + imageId + ".jpg");
                        zos.putNextEntry(imageEntry);
                        zos.write(imageBytes);
                        zos.closeEntry();
                    }
                }
            }

            return ok().sendFile(zipFile, false)
                    .withHeader("Content-Disposition",
                            "attachment; filename=" + zipFile.getName());
        } catch (Exception e) {
            return internalServerError("Failed to export task: " + e.getMessage());
        }
    }

    private boolean checkToken (Http.Request request) {
        String token = request.headers().get("Authorization").orElse(null);
        return  (token == null || !jwtService.verifyToken(token));
    }
}