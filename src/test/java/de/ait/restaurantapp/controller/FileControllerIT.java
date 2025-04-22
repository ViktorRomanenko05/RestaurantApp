package de.ait.restaurantapp.controller;

import de.ait.restaurantapp.model.FileEntity;
import de.ait.restaurantapp.repositories.FileRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FileControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileRepo fileRepo;

    @BeforeEach
    void setUp () {
        fileRepo.deleteAll();
    }

    @Test
    @DisplayName("Успешная загрузка и скачивание файла")
    void testFileUploadAndDownload() throws Exception {
        String content = "Test file.txt";
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "hello.txt", MediaType.TEXT_PLAIN_VALUE, content.getBytes()
        );
        mockMvc.perform(multipart("/file/upload")
                        .file(mockFile)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("upload-form"))
                .andExpect(model().attribute("message", containsString("Файл успешно загружен")))
                .andExpect(model().attributeExists("fileId"));
        assertEquals(1, fileRepo.findAll().size());
        FileEntity saved = fileRepo.findAll().get(0);
        assertArrayEquals(content.getBytes(), saved.getData());

        mockMvc.perform(get("/file/" + saved.getId())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().bytes(content.getBytes()));
    }

    @Test
    @DisplayName("Валидация при пустом файле")
    void testUploadEmptyFile() throws Exception {
        MockMultipartFile empty = new MockMultipartFile(
                "file", "", MediaType.APPLICATION_OCTET_STREAM_VALUE, new byte[0]
        );

        mockMvc.perform(multipart("/file/upload")
                        .file(empty)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("upload-form"))
                .andExpect(model().attribute("message", "Файл для загрузки не выбран"));
    }

    @Test
    @DisplayName("Скачивание несуществующего файла")
    void testDownloadNotFound() throws Exception {
        mockMvc.perform(get("/file/999")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Удаление существующего файла")
    void testDeleteExistingFile() throws Exception {
        FileEntity file = fileRepo.save(new FileEntity(
                "toDelete.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "testFile".getBytes()
        ));
        assertTrue(fileRepo.existsById(file.getId()));

        mockMvc.perform(post("/file/delete-file/" + file.getId())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(redirectedUrlPattern("/file/list?*message=*"));
        assertFalse(fileRepo.existsById(file.getId()));
    }

    @Test
    @DisplayName("Удаление несуществующего файла")
    void testDeleteNonexistentFile() throws Exception {
        mockMvc.perform(post("/file/delete-file/12345")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/file/list?*message=*"));
        assertTrue(fileRepo.findAll().isEmpty());
    }
}