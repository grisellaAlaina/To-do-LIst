package services;

import repository.TaskRepository;
import domain.models.Task;
import utils.PDFProcessing;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import utils.ZipFileUtils;

public class TaskService {
    private static final Logger log = Logger.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final PDFProcessing pdfProcessing;
    private final ZipFileUtils zipFileUtils;

    public TaskService() {
        this.taskRepository = new TaskRepository();
        this.pdfProcessing = new PDFProcessing();
        this.zipFileUtils = new ZipFileUtils();
    }

    public TaskService(TaskRepository taskRepository, PDFProcessing pdfProcessing, ZipFileUtils zipFileUtils) {
        this.taskRepository = taskRepository;
        this.pdfProcessing = pdfProcessing;
        this.zipFileUtils = zipFileUtils;
    }

    public void createTask(Task task) {
        taskRepository.createTask(task);
        log.info("Task created: " + task.getId());
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = taskRepository.getAllTasks();
        log.info("Retrieved " + tasks.size() + " tasks");
        return tasks;
    }

    public Task getTaskById(String taskId) {
        Task task = taskRepository.getTaskById(taskId);
        if (task != null) {
            log.info("Retrieved task with ID: " + taskId);
        } else {
            log.warn("Task not found with ID: " + taskId);
        }
        return task;
    }

    public void updateTask(Task updatedTask) {
        taskRepository.updateTask(updatedTask);
        log.info("Updated task: " + updatedTask.getId());
    }

    public void deleteTask(String taskId) {
        taskRepository.deleteTask(taskId);
        log.info("Deleted task with ID: " + taskId);
    }

    public Task createTaskWithPDF(String name, String description, File pdfFile) {
        Task task = new Task();
        task.setName(name);
        task.setDescription(description);
        task.setCreatedDate(new Date());

        if (pdfFile != null) {
            List<String> imageIds = pdfProcessing.convertPDFToImages(pdfFile, taskRepository);
            task.setImageIds(imageIds);
        }

        this.createTask(task);
        return task;
    }

    public File exportTaskAsZipFile(String taskId) throws IOException {
        Task task = taskRepository.getTaskById(taskId);
        if (task == null) {
            return null;
        }
        String destinationPath = File.createTempFile(task.getName(), ".zip").getPath();
        return zipFileUtils.createTaskAsZipFile(task, destinationPath, taskRepository);
    }



}