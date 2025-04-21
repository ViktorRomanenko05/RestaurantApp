package de.ait.restaurantapp.services;

import de.ait.restaurantapp.model.FileEntity;
import de.ait.restaurantapp.repositories.FileRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FileService {

    private final FileRepo fileRepo;

    public FileService(FileRepo fileRepo) {
        this.fileRepo = fileRepo;
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
