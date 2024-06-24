package repository;

import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import domain.models.Task;
import org.junit.*;
import org.mockito.*;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TaskRepositoryTest {

    @Mock
    private Datastore datastore;

    @Mock
    private GridFS gridFS;

    @Mock
    private GridFSDBFile gridFSDBFile;

    @Mock
    private GridFSInputFile gridFSInputFile;

    @InjectMocks
    private TaskRepository taskRepository;

    @Mock
    private Query<Task> query;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateTask() {
        Task task = new Task();
        taskRepository.createTask(task);
        verify(datastore, times(1)).save(task);
    }

    @Test
    public void testGetAllTask() {
        when(datastore.find(Task.class)).thenReturn(query);
        when(query.asList()).thenReturn(Arrays.asList(new Task(), new Task()));
        List<Task> tasks = taskRepository.getAllTasks();
        assertEquals(2, tasks.size());
        verify(datastore, times(1)).find(Task.class);
    }

    @Test
    public void testGetTaskById() {
        ObjectId id = new ObjectId();
        Task task = new Task();

        when(datastore.get(Task.class, id)).thenReturn(task);
        Task result = taskRepository.getTaskById(id.toString());

        assertEquals(task, result);
        verify(datastore, times(1)).get(Task.class, id);
    }

    @Test
    public void testUpdateTask() {
        Task task = new Task();
        taskRepository.updateTask(task);
        verify(datastore, times(1)).save(task);
    }

    @Test
    public void testDeleteTask() {
        ObjectId id = new ObjectId();
        taskRepository.deleteTask(id.toString());
        verify(datastore, times(1)).delete(Task.class, id);
    }

    @Test
    public void testSaveImageToMongo() {
        byte[] imageBytes = new byte[]{1, 2, 3, 4, 5};
        String filename = "somefile.jpg";

        when(gridFS.createFile(imageBytes)).thenReturn(gridFSInputFile);
        when(gridFSInputFile.getId()).thenReturn("SomeId");

        String resultId = taskRepository.saveImageToMongo(imageBytes, filename);

        verify(gridFS, times(1)).createFile(imageBytes);
        verify(gridFSInputFile, times(1)).setFilename(filename);
        verify(gridFSInputFile, times(1)).setContentType("image/jpg");
        verify(gridFSInputFile, times(1)).save();

        assertEquals("SomeId", resultId);
    }

    @Test
    public void testGetImageByImageId() throws Exception {
        ObjectId imageId = new ObjectId();
        byte[] imageBytes = new byte[]{1, 2, 3, 4, 5};

        when(gridFS.find(imageId)).thenReturn(gridFSDBFile);
        when(gridFSDBFile.writeTo(any(ByteArrayOutputStream.class))).then(invocation -> {
            ByteArrayOutputStream outputStream = invocation.getArgument(0);
            outputStream.write(imageBytes);
            return null;
        });

        byte[] result = taskRepository.getImageByImageId(imageId.toString());

        assertArrayEquals(imageBytes, result);
        verify(gridFS, times(1)).find(imageId);
    }
}