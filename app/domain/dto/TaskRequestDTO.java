package domain.dto;

import java.io.File;

public class TaskRequestDTO {

    private String id;
    private String name;
    private String description;
    private File pdfFile;

    public TaskRequestDTO() {
    }

    public TaskRequestDTO(String name, String description, File pdfFile) {
        this.name = name;
        this.description = description;
        this.pdfFile = pdfFile;
    }

    public TaskRequestDTO(String id, String name, String description, File pdfFile) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.pdfFile = pdfFile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public File getPdfFile() {
        return pdfFile;
    }

    public void setPdfFile(File pdfFile) {
        this.pdfFile = pdfFile;
    }
}
