package controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import play.mvc.Http;
import play.mvc.Result;
import services.JwtService;
import services.TaskService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TaskControllerTest {

    @Mock
    private TaskService mockTaskService;

    @Mock
    private JwtService mockJwtService;

    @InjectMocks
    private TaskController taskController;

    private Http.RequestBuilder fakeRequestBuilder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        fakeRequestBuilder = new Http.RequestBuilder();
        fakeRequestBuilder.header("Authorization", "validToken");
    }

    @Test
    public void getAllTasksTest() {
        Http.Request fakeRequest = fakeRequestBuilder.build();

        when(mockJwtService.verifyToken("validToken")).thenReturn(true);

        Result result = taskController.getAllTasks(fakeRequest);
        assertEquals(200, result.status());
    }

    @Test
    public void deleteTaskTest() {
        Http.Request fakeRequest = fakeRequestBuilder.build();

        when(mockJwtService.verifyToken("validToken")).thenReturn(true);
        String validId = UUID.randomUUID().toString();

        doNothing().when(mockTaskService).deleteTask(validId);

        Result result = taskController.deleteTask(validId, fakeRequest);
        assertEquals(200, result.status());
    }
}