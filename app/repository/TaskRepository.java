package repository;

import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.mongodb.MongoClient;
import domain.models.Task;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class TaskRepository {
    private static final Logger log = Logger.getLogger(TaskRepository.class);

    private final Datastore datastore;
    private final GridFS gridFS;

    public TaskRepository() {
        log.info("Initialized TaskService with default settings");
        Morphia morphia = new Morphia();
        morphia.mapPackage("models");
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        datastore = morphia.createDatastore(mongoClient, "tasks_db");
        datastore.ensureIndexes();
        gridFS = new GridFS(this.datastore.getDB());
    }

    public TaskRepository(Datastore datastore, GridFS gridFS) {
        log.info("Initialized TaskService with custom settings");
        this.datastore = datastore;
        this.gridFS = gridFS;
    }

    public void createTask(Task task) {
        datastore.save(task);
        log.info("Task created: " + task.getId());
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = datastore.find(Task.class).asList();
        log.info("Retrieved " + tasks.size() + " tasks");
        return tasks;
    }

    public Task getTaskById(String taskId) {
        Task task = datastore.get(Task.class, new org.bson.types.ObjectId(taskId));
        if (task != null) {
            log.info("Retrieved task with ID: " + taskId);
        } else {
            log.warn("Task not found with ID: " + taskId);
        }
        return task;
    }

    public void updateTask(Task updatedTask) {
        datastore.save(updatedTask);
        log.info("Updated task: " + updatedTask.getId());
    }

    public void deleteTask(String taskId) {
        datastore.delete(Task.class, new org.bson.types.ObjectId(taskId));
        log.info("Deleted task with ID: " + taskId);
    }

    public String saveImageToMongo(byte[] imageBytes, String filename) {
        GridFSInputFile gfsFile = gridFS.createFile(imageBytes);
        gfsFile.setFilename(filename);
        gfsFile.setContentType("image/jpg");
        gfsFile.save();
        log.info("Saved " + filename + " to MongoDB");
        return gfsFile.getId().toString();
    }

    public byte[] getImageByImageId(String imageId) {
        try {
            GridFSDBFile imageFile = gridFS.find(new ObjectId(imageId));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            imageFile.writeTo(outputStream);
            log.info("Loaded image with ID: " + imageId);
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error loading image: " + e.getMessage());
            return null;
        }
    }
}