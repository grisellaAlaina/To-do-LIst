package utils;

import java.nio.file.*;
import java.util.zip.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.models.Task;
import repository.TaskRepository;

import java.nio.charset.StandardCharsets;
import java.io.*;

public class ZipFileUtils {

    public static File createTaskAsZipFile(Task task, String destinationPath, TaskRepository taskRepository) {
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
                    byte[] imageBytes = taskRepository.getImageByImageId(imageId);
                    if (imageBytes != null) {
                        String imageFileName = "images/" + imageId + ".jpg";
                        ZipEntry imageEntry = new ZipEntry(imageFileName);
                        zos.putNextEntry(imageEntry);
                        zos.write(imageBytes);
                        zos.closeEntry();
                    }
                }
            }

            return zipFile;
        } catch (IOException e) {
            throw new RuntimeException("An issue occurred while creating the zip file.", e);
        }
    }
}