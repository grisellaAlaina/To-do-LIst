package services;

import domain.models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.TaskRepository;
import services.TaskService;
import utils.PDFProcessing;
import utils.ZipFileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceTest {
    TaskService taskService;
    TaskRepository taskRepository;
    PDFProcessing pdfProcessing;
    ZipFileUtils zipFileUtils;
    File tempFile;

    @BeforeEach
    public void setUp() {
        taskRepository = mock(TaskRepository.class);
        pdfProcessing = mock(PDFProcessing.class);
        zipFileUtils = mock(ZipFileUtils.class);
        taskService = new TaskService(taskRepository, pdfProcessing, zipFileUtils);
        tempFile = mock(File.class);
    }

    @Test
    public void createTask() {
        Task task = new Task();
        taskService.createTask(task);
        verify(taskRepository, times(1)).createTask(task);
    }

    @Test
    public void getAllTasks() {
        taskService.getAllTasks();
        verify(taskRepository, times(1)).getAllTasks();
    }

    @Test
    public void getTaskById() {
        taskService.getTaskById("id");
        verify(taskRepository, times(1)).getTaskById("id");
    }

    @Test
    public void updateTask() {
        Task task = new Task();
        taskService.updateTask(task);
        verify(taskRepository, times(1)).updateTask(task);
    }

    @Test
    public void deleteTask() {
        taskService.deleteTask("id");
        verify(taskRepository, times(1)).deleteTask("id");
    }

    @Test
    public void createTaskWithPDF() throws IOException {
        when(tempFile.exists()).thenReturn(true);
        when(pdfProcessing.convertPDFToImages(any(File.class), any(TaskRepository.class))).thenReturn(Arrays.asList("1", "2"));
        Task task = taskService.createTaskWithPDF("name", "description", tempFile);
        verify(pdfProcessing, times(1)).convertPDFToImages(any(File.class), any(TaskRepository.class));
        verify(taskRepository, times(1)).createTask(task);
    }
}