package de.ait.restaurantapp.services;

import de.ait.restaurantapp.model.FileEntity;
import de.ait.restaurantapp.repositories.FileRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FileService {
    private static final String MENU_FILENAME = "current_menu.pdf";

    @Value("${file.upload}")
    private String uploadDirectory;

    private final FileRepo fileRepo;

    public FileService(FileRepo fileRepo) {
        this.fileRepo = fileRepo;
    }

    // saving file in project directory - menus
    public void saveMenuInProjectDir(MultipartFile file) throws IOException {
        if (!"application/pdf".equals(file.getContentType())) {
            log.error("Only PDF files are allowed");
            throw new IllegalArgumentException("Only PDF files are allowed");
        }

        Path uploadPath = Paths.get(uploadDirectory);
        Path destination = uploadPath.resolve(MENU_FILENAME);

        // Create directory if it doesn't exist
        if(!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            log.info("Created menu directory: {}", uploadPath);
        }

        // Delete any existing menu file
        Path oldMenu = uploadPath.resolve(MENU_FILENAME);
        if(Files.exists(oldMenu)) {
            Files.delete(oldMenu);
            log.debug("Deleted old menu file");
        }

        try(InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
            log.info("New menu was successfully saved.");
        }
    }

    // Fetching file from project dir
    public Optional<FileEntity> getMenu() {
        try {
            Path filePath = Paths.get(uploadDirectory).resolve(MENU_FILENAME);

            if(Files.exists(filePath)) {
                byte[] data = Files.readAllBytes(filePath);
                return Optional.of(new FileEntity(MENU_FILENAME, "application/pdf", data));
            } else {
                return Optional.empty();
            }
        } catch (IOException e) {
            log.error("Could not read menu file", e);
            return Optional.empty();
        }
    }


    // сохранение файла в бд
    public FileEntity saveFile(MultipartFile file){
        String fileName = file.getOriginalFilename();
        String fileType = file.getContentType();

        try {
            byte[] data = file.getBytes();
            FileEntity fileEntity = new FileEntity(fileName, fileType, data);
            log.debug("Saving file: {}", fileEntity);
            return fileRepo.save(fileEntity);
        } catch (IOException exception){
            log.warn("Error while saving file: {}", exception.getMessage());
            return null;
        }
    }

    //список всез файлов
    public List<FileEntity> getAllFiles(){
        log.debug("Fetching all files.");
        List files = fileRepo.findAll();
        log.debug("Found {} files.", files.size());
        return files;
    }

    //метод для удаления файла
    public boolean deleteById(Long id) {
        if(fileRepo.existsById(id)) {
            fileRepo.deleteById(id);
            log.debug("File deleted | id: {}", id);
            return true;
        }
        log.warn("Delete file failed - file not found | id: {}", id);
        return false;
    }

    //Нужен ли нам метод для поиска файла? В данном случае у нас будет только один файл(меню)
    public Optional<FileEntity> getFile(Long id){
        Optional<FileEntity> file = fileRepo.findById(id);
        file.ifPresentOrElse(f -> log.info("File {} found, id: {}", file, id),
                () -> {log.warn("No file found by id: {}", id);});
        return file;
    }

}
