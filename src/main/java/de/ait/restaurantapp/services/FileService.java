package de.ait.restaurantapp.services;

import de.ait.restaurantapp.model.FileEntity;
import de.ait.restaurantapp.repositories.FileRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FileService {

    @Value("${file.upload}")
    private String uploadDirectory;

    private final FileRepo fileRepo;

    public FileService(FileRepo fileRepo) {
        this.fileRepo = fileRepo;
    }

    // сохранение файла в menus
    public FileEntity saveFile(MultipartFile file){
        String fileName = file.getOriginalFilename();
        String fileType = file.getContentType();
        File directory = new File(uploadDirectory);

        if(!directory.exists()) {
            log.debug("Creating directory for file menu: {}", uploadDirectory);
            directory.mkdirs();
        }

        Path destination = Paths.get(uploadDirectory).resolve(fileName);

        try {
            file.transferTo(destination);

            return new FileEntity(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes()
            );
        } catch (IOException e) {
            log.error("Error saving file {}: {}", file.getOriginalFilename(), e.getMessage());
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
