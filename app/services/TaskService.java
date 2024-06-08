package services;

import models.Task;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import com.mongodb.MongoClient;

import java.util.List;

public class TaskService {
    private Datastore datastore;

    public TaskService() {
        Morphia morphia = new Morphia();
        morphia.mapPackage("models");
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        datastore = morphia.createDatastore(mongoClient, "tasks_db");
        datastore.ensureIndexes();
    }

    public void createTask(Task task) {
        datastore.save(task);
    }

    public List<Task> getAllTasks() {
        return datastore.find(Task.class).asList();
    }

    public Task getTaskById(String taskId) {
        return datastore.get(Task.class, new org.bson.types.ObjectId(taskId));
    }

    public void updateTask(Task updatedTask) {
        datastore.save(updatedTask);
    }

    public void deleteTask(String taskId) {
        datastore.delete(Task.class, new org.bson.types.ObjectId(taskId));
    }
}
