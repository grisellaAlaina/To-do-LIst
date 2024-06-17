package services;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import models.Task;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

@RunWith(MockitoJUnitRunner.class)
public class TaskServiceTest {

    @Mock
    private Datastore datastore;

    @Mock
    private GridFS gridFS;

    @Mock
    private GridFSInputFile gridFSInputFile;

    @Mock
    private Query<Task> query;

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

    @Test
    public void testGetAllTasks() {
        List<Task> mockTasks = Arrays.asList(task, new Task());
        when(datastore.find(Task.class)).thenReturn(query);
        when(query.asList()).thenReturn(mockTasks);

        List<Task> returnedTasks = taskService.getAllTasks();
        assertEquals(returnedTasks, mockTasks);
    }

    @Test
    public void saveImageToMongo() {
        byte[] mockBytes = "test-image".getBytes();
        String filename = "test.jpg";
        ObjectId mockObjectId = new ObjectId();

        when(gridFS.createFile(mockBytes)).thenReturn(gridFSInputFile);
        when(gridFSInputFile.getId()).thenReturn(mockObjectId);

        String returnedObjectId = taskService.saveImageToMongo(mockBytes, filename);

        assertEquals(returnedObjectId, mockObjectId.toString());
        verify(gridFSInputFile).setContentType("image/jpg");
        verify(gridFSInputFile).setFilename(filename);
        verify(gridFSInputFile).save();
    }

    @Test
    public void testGetImageByImageId_Success() throws IOException {
        String imageId = "507f191e810c19729de860ea";
        GridFSDBFile imageFile = Mockito.mock(GridFSDBFile.class);
        Mockito.when(gridFS.find(new ObjectId(imageId))).thenReturn(imageFile);

        Mockito.doAnswer(invocation -> {
            ByteArrayOutputStream outputStream = invocation.getArgument(0);
            return 0;
        }).when(imageFile).writeTo(any(ByteArrayOutputStream.class));

        taskService.getImageByImageId(imageId);

        Mockito.verify(gridFS).find(new ObjectId(imageId));
    }



    @Test
    public void testGetImageByImageId_GridFSError() throws IOException {
        String imageId = "invalid-image-id";

        byte[] result = taskService.getImageByImageId(imageId);

        assertNull(result);
    }


}