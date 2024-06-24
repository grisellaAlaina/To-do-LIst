package controllers;

import domain.dto.TaskDTO;
import domain.dto.TaskRequestDTO;
import domain.mappers.TaskMapper;
import domain.models.Task;
import exceptions.EmptyFieldException;
import exceptions.MissingFieldException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import play.mvc.Http;
import play.mvc.Result;
import services.JwtService;
import services.TaskService;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TaskControllerTest {

    @Mock
    private TaskService mockTaskService;

    @Mock
    private TaskMapper mockTaskMapper;

    @Mock
    private JwtService mockJwtService;

    @InjectMocks
    private TaskController taskController;

    private Http.Request request;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        request = mock(Http.Request.class);
        Http.Headers headersMock = mock(Http.Headers.class);

        when(request.headers()).thenReturn(headersMock);
        when(headersMock.get(eq("Authorization"))).thenReturn(Optional.of("validToken"));
        when(mockJwtService.verifyToken(anyString())).thenReturn(true);
    }

    @Test
    public void createTaskTest() throws EmptyFieldException, MissingFieldException, IOException {
        TaskDTO mockTaskDTO = new TaskDTO();
        Task mockTask = new Task();
        TaskRequestDTO mockTaskDTORequest = new TaskRequestDTO();

        when(mockTaskMapper.toTaskRequestDTO(any(), anyBoolean())).thenReturn(mockTaskDTORequest);
        when(mockTaskService.createTaskWithPDF(any(), any(), any())).thenReturn(mockTask);
        when(mockTaskMapper.convertToTaskDTO(any())).thenReturn(mockTaskDTO);

        Result result = taskController.createTask(request);

        assertEquals(200, result.status());
    }

    @Test
    public void updateTaskTest() throws EmptyFieldException, MissingFieldException, IOException {
        TaskDTO mockTaskDTO = new TaskDTO();
        Task mockTask = new Task();
        TaskRequestDTO mockTaskDTORequest = new TaskRequestDTO();

        when(mockTaskMapper.toTaskRequestDTO(any(), anyBoolean())).thenReturn(mockTaskDTORequest);
        when(mockTaskService.getTaskById(any())).thenReturn(mockTask);
        when(mockTaskMapper.convertToTaskDTO(any())).thenReturn(mockTaskDTO);

        Result result = taskController.updateTask(request);

        assertEquals(200, result.status());
    }

    @Test
    public void getAllTasksTest() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task());
        tasks.add(new Task());

        when(mockTaskService.getAllTasks()).thenReturn(tasks);

        Result result = taskController.getAllTasks(request);

        assertEquals(200, result.status());
    }

    @Test
    public void getTaskByIdTest() {
        Task task = new Task();
        when(mockTaskService.getTaskById(any())).thenReturn(task);

        Result result = taskController.getTaskById("someTaskId", request);

        assertEquals(200, result.status());
    }

    @Test
    public void deleteTaskTest() {
        doNothing().when(mockTaskService).deleteTask(any());

        Result result = taskController.deleteTask("someTaskId", request);

        assertEquals(200, result.status());
    }

    @Test
    public void exportTaskAsZipTest() throws IOException {
        File file = File.createTempFile("temp", "zip");
        when(mockTaskService.exportTaskAsZipFile(any())).thenReturn(file);

        Result result = taskController.exportTaskAsZip("someTaskId", request);

        assertEquals(200, result.status());
    }

}