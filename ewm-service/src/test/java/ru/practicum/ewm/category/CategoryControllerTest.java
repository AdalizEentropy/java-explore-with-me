package ru.practicum.ewm.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryReqDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.ewm.mapper.TestMapper.toCategoryReqDto;
import static ru.practicum.ewm.utils.CreateTestCategory.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("testdb")
class CategoryControllerTest {
    private static final String URL = "/admin/categories";
    private final CategoryReqDto category = toCategoryReqDto(createNewCategory1());
    private final CategoryReqDto updateCat = toCategoryReqDto(updateNewCategory1());
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;

    @Test
    @SneakyThrows
    void addCategory() {
        mvc.perform(post(URL)
                        .content(objectMapper.writeValueAsString(category))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(category.getName()));
    }

    @Test
    @SneakyThrows
    void addCategory_withEmptyName_returnBadRequest() {
        CategoryReqDto cat = toCategoryReqDto(createNewCategory1());
        cat.setName(null);

        mvc.perform(post(URL)
                        .content(objectMapper.writeValueAsString(cat))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.reason").value("Incorrectly made request."))
                .andExpect(jsonPath("$.message")
                        .value("Field: name. Error: must not be blank. Value: null"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @SneakyThrows
    void addCategory_withExistName_returnConflict() {
        mvc.perform(post(URL)
                        .content(objectMapper.writeValueAsString(category))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mvc.perform(post(URL)
                        .content(objectMapper.writeValueAsString(category))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("CONFLICT"))
                .andExpect(jsonPath("$.reason").value("Integrity constraint has been violated."))
                .andExpect(jsonPath("$.message")
                        .value(String.format("Category with name %s already exist", category.getName())))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @SneakyThrows
    void deleteCategory() {
        String response = mvc.perform(post(URL)
                        .content(objectMapper.writeValueAsString(category))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Integer id = JsonPath.read(response, "$.id");

        mvc.perform(delete(URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @SneakyThrows
    void deleteCategory_incorrectId() {
        mvc.perform(post(URL)
                        .content(objectMapper.writeValueAsString(category))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mvc.perform(delete(URL + "/-100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.reason").value("The required object was not found."))
                .andExpect(jsonPath("$.message")
                        .value(String.format("CategoryID %s does not exist", -100)))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @SneakyThrows
    void updateCategory() {
        String response = mvc.perform(post(URL)
                        .content(objectMapper.writeValueAsString(category))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Integer id = JsonPath.read(response, "$.id");

        mvc.perform(patch(URL + "/" + id)
                        .content(objectMapper.writeValueAsString(updateCat))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(updateCat.getName()));
    }

    @Test
    @SneakyThrows
    void updateCategory_incorrectId() {
        mvc.perform(patch(URL + "/-100")
                        .content(objectMapper.writeValueAsString(updateCat))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.reason").value("The required object was not found."))
                .andExpect(jsonPath("$.message")
                        .value(String.format("CategoryID %s does not exist", -100)))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @SneakyThrows
    void updateCategory_withExistName_returnConflict() {
        mvc.perform(post(URL)
                        .content(objectMapper.writeValueAsString(toCategoryReqDto(createNewCategory2())))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        String response = mvc.perform(post(URL)
                        .content(objectMapper.writeValueAsString(category))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Integer id = JsonPath.read(response, "$.id");

        mvc.perform(patch(URL + "/" + id)
                        .content(objectMapper.writeValueAsString(updateCat))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("CONFLICT"))
                .andExpect(jsonPath("$.reason").value("Integrity constraint has been violated."))
                .andExpect(jsonPath("$.message")
                        .value(String.format("Category with name %s already exist", updateCat.getName())))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}