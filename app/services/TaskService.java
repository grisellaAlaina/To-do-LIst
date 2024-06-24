package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import models.Task;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import com.mongodb.MongoClient;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

public class TaskService {
    private static final Logger log = Logger.getLogger(TaskService.class);

    private final Datastore datastore;
    private final GridFS gridFS;

    public TaskService() {
        log.info("Initialized TaskService with default settings");
        Morphia morphia = new Morphia();
        morphia.mapPackage("models");
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        datastore = morphia.createDatastore(mongoClient, "tasks_db");
        datastore.ensureIndexes();
        gridFS = new GridFS(this.datastore.getDB());
    }

    public TaskService(Datastore datastore, GridFS gridFS) {
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

    public List<String> convertPDFToImages(File pdfFile) {
        List<String> imageIds = new ArrayList<>();
        try (final PDDocument document = Loader.loadPDF(pdfFile)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            for (int page = 0; page < document.getNumberOfPages(); ++page) {
                try {
                    BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(bim, "jpg", baos);
                    byte[] imageInByte = baos.toByteArray();
                    String imageId = saveImageToMongo(imageInByte, "page_" + page + ".jpg");
                    imageIds.add(imageId);

                } catch (IOException e) {
                    log.error("Error rendering image or writing to byte array: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            log.error("Error loading PDF document: " + e.getMessage());
        }

        return imageIds;
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

    public Task createTaskWithPDF(String name, String description, File pdfFile) {
        Task task = new Task();
        task.setName(name);
        task.setDescription(description);
        task.setCreatedDate(new Date());

        if (pdfFile != null) {
            List<String> imageIds = this.convertPDFToImages(pdfFile);
            task.setImageIds(imageIds);
        }

        this.createTask(task);
        return task;
    }

    public String getValueFromFormData(Map<String, String[]> formData, String key) {
        String[] valueArr = formData.get(key);
        return (valueArr != null && valueArr.length > 0) ? valueArr[0] : null;
    }

    public File createTaskAsZipFile(Task task, String destinationPath) {
        try {
            File zipFile = new File(destinationPath);
            try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
                ZipEntry taskEntry = new ZipEntry("task.json");
                zos.putNextEntry(taskEntry);
                ObjectMapper objectMapper = new ObjectMapper();
                String taskJson = objectMapper.writeValueAsString(task);
                zos.write(taskJson.getBytes(StandardCharsets.UTF_8));
                zos.closeEntry();

                for (String imageId : task.getImageIds()) {
                    byte[] imageBytes = getImageByImageId(imageId);
                    if (imageBytes != null) {
                        String imageFileName = "images/" + imageId + ".jpg";
                        ZipEntry imageEntry = new ZipEntry(imageFileName);
                        zos.putNextEntry(imageEntry);
                        zos.write(imageBytes);
                        zos.closeEntry();
                    }
                }
            }
            log.info("Created task as a zip file at " + destinationPath);
            return zipFile;
        } catch (IOException e) {
            log.error("An issue occurred while creating the zip file.", e);
            throw new RuntimeException("An issue occurred while creating the zip file.", e);
        }
    }
}