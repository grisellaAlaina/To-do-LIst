package mappers;

import dto.TaskDTO;
import exceptions.EmptyFieldException;
import exceptions.MissingFieldException;
import dto.TaskRequestDTO;
import models.Task;
import play.libs.Files;
import play.mvc.Http;

import java.io.File;
import java.util.Map;

public class TaskMapper {

    public TaskRequestDTO toTaskRequestDTO(Http.Request request, boolean update) throws EmptyFieldException, MissingFieldException {
        Http.MultipartFormData<Files.TemporaryFile> body = request.body().asMultipartFormData();
        Map<String, String[]> formData = body.asFormUrlEncoded();
        Http.MultipartFormData.FilePart<Files.TemporaryFile> pdfFilePart = body.getFile("pdfFile");
        File pdfFile = pdfFilePart != null ? pdfFilePart.getRef().path().toFile() : null;
        String id = getValueFromFormData(formData, "id");
        String name = getValueFromFormData(formData, "name");
        String description = getValueFromFormData(formData, "description");

        if (name == null || name.isEmpty()) {
            throw new MissingFieldException("Missing or empty 'name' field");
        }
        if (description == null || description.isEmpty()) {
            throw new MissingFieldException("Missing or empty 'description' field");
        }

        if (update) {
            if (id == null || id.isEmpty()) {
                throw new MissingFieldException("Missing or empty 'id' field");
            }
        }

        return new TaskRequestDTO(id, name, description, pdfFile);
    }

    private String getValueFromFormData(Map<String, String[]> formData, String key) {
        String[] values = formData.get(key);
        return (values != null && values.length > 0) ? values[0] : null;
    }

    public TaskDTO convertToTaskDTO(Task taskToConvert) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(taskToConvert.getId().toString());
        taskDTO.setName(taskToConvert.getName());
        taskDTO.setDescription(taskToConvert.getDescription());
        taskDTO.setCreatedDate(taskToConvert.getCreatedDate());
        taskDTO.setImageIds(taskToConvert.getImageIds());
        return taskDTO;
    }
}