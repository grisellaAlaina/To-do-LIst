package utils;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.log4j.Logger;
import repository.TaskRepository;

import javax.imageio.ImageIO;

public class PDFProcessing {
    private static final Logger log = Logger.getLogger(PDFProcessing.class);

    public List<String> convertPDFToImages(File pdfFile, TaskRepository taskRepository) {
        List<String> imageIds = new ArrayList<>();
        try (final PDDocument document = Loader.loadPDF(pdfFile)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            for (int page = 0; page < document.getNumberOfPages(); ++page) {
                try {
                    BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(bim, "jpg", baos);
                    byte[] imageInByte = baos.toByteArray();
                    String imageId = taskRepository.saveImageToMongo(imageInByte, "page_" + page + ".jpg");
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
}
