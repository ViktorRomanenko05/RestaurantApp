package de.ait.restaurantapp.services;

import de.ait.restaurantapp.model.FileEntity;
import de.ait.restaurantapp.repositories.FileRepo;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class FileServiceTests {

    @Autowired
    FileRepo fileRepo;

    @Autowired
    FileService fileService;

    MockMultipartFile mockFile1;
    MockMultipartFile mockFile2;

    @BeforeEach
    void setUp (){
        fileRepo.deleteAll();
        String testContent1 = "Test file for loading number one";
        String testContent2 = "Test file for loading number two";
        mockFile1 = new MockMultipartFile("file", "test.txt", "text/plain", testContent1.getBytes());
        mockFile2 = new MockMultipartFile("file", "test.txt", "text/plain", testContent2.getBytes());
    }

    @Test
    @DisplayName("Upload file")
    void fileUploadTest () {
        FileEntity fileEntity1 = fileService.saveFile(mockFile1);
        FileEntity fileEntity2 = fileService.saveFile(mockFile2);
        assertEquals(2, fileRepo.findAll().size());
        Optional<FileEntity> foundFile1 = fileRepo.findById(fileEntity1.getId());
        Optional<FileEntity> foundFile2 = fileRepo.findById(fileEntity2.getId());
        assertFalse(foundFile1.isEmpty());
        assertFalse(foundFile2.isEmpty());
    }

    @Test
    @DisplayName("Get all files")
    void getAllFilesTest () {
        FileEntity fileEntity1 = fileService.saveFile(mockFile1);
        fileService.saveFile(mockFile2);
        List<FileEntity> allFiles = fileService.getAllFiles();
        assertEquals(2, fileRepo.findAll().size());
        List<Long> fileIDs = allFiles.stream().map(FileEntity::getId).toList();
        List<Long>foundId = fileIDs.stream().filter(id-> Objects.equals(id, fileEntity1.getId())).toList();
        assertEquals(1, foundId.size());
        assertEquals(fileEntity1.getId(), foundId.get(0));
    }

    @Test
    @DisplayName("Delete file by id")
    void deleteFileByIdTest () {
        FileEntity fileEntity1 = fileService.saveFile(mockFile1);
        FileEntity fileEntity2 = fileService.saveFile(mockFile2);
        assertEquals(2, fileRepo.findAll().size());
        assertTrue(fileService.deleteById(fileEntity2.getId()));
        assertEquals(1, fileRepo.findAll().size());
        assertEquals(fileEntity1.getId(), fileRepo.findAll().get(0).getId());
    }

    @Test
    @DisplayName("Delete file by id")
    void deleteFileByIdNegativeTest () {
        FileEntity fileEntity1 = fileService.saveFile(mockFile1);
        FileEntity fileEntity2 = fileService.saveFile(mockFile2);
        assertFalse(fileService.deleteById(877L));
        assertEquals(2, fileRepo.findAll().size());
        Optional<FileEntity> foundFile1 = fileRepo.findById(fileEntity1.getId());
        Optional<FileEntity> foundFile2 = fileRepo.findById(fileEntity2.getId());
        assertFalse(foundFile1.isEmpty());
        assertFalse(foundFile2.isEmpty());
    }

    @Test
    @DisplayName("Find file by id")
    void findFilebyIdTest () {
        fileService.saveFile(mockFile1);
        FileEntity fileEntity2 = fileService.saveFile(mockFile2);
        Optional<FileEntity> foundFile = fileService.getFile(fileEntity2.getId());
        assertEquals(fileEntity2.getId(), foundFile.get().getId());
    }

    @Test
    @DisplayName("Find file by id - negative")
    void findFilebyIdNegativeTest () {
        fileService.saveFile(mockFile1);
        fileService.saveFile(mockFile2);
        Optional<FileEntity> foundFile = fileService.getFile(831L);
        assertTrue(foundFile.isEmpty());
    }
}
