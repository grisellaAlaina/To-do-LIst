package services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.util.Date;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import models.Task;
import org.mongodb.morphia.Datastore;

@RunWith(MockitoJUnitRunner.class)
public class TaskServiceTest {

    @Mock
    private Datastore datastore;

    @InjectMocks
    private TaskService taskService;

    private Task task;

    @Before
    public void setUp() {
        task = new Task();
        task.setId(new ObjectId());
        task.setName("Test Task");
        task.setDescription("Test Description");
        task.setCreatedDate(new Date());
    }

    @Test
    public void testCreateTask() {
        taskService.createTask(task);
        verify(datastore).save(task);
    }


    @Test
    public void testGetTaskById() {
        when(datastore.get(Task.class, task.getId())).thenReturn(task);

        Task retrievedTask = taskService.getTaskById(task.getId().toString());

        assertEquals(task, retrievedTask);
    }

    @Test
    public void testUpdateTask() {
        taskService.updateTask(task);
        verify(datastore).save(task);
    }

    @Test
    public void testDeleteTask() {
        taskService.deleteTask(task.getId().toString());
        verify(datastore).delete(Task.class, task.getId());
    }
}