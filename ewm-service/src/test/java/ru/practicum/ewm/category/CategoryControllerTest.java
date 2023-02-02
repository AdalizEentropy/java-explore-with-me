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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.ewm.mapper.TestMapper.toCategoryReqDto;
import static ru.practicum.ewm.utils.CreateTestCategory.createNewCategory1;
import static ru.practicum.ewm.utils.CreateTestCategory.createNewCategory2;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("testdb")
class CategoryControllerTest {
    private static final String ADMIN_URL = "/admin/categories";
    private static final String URL = "/categories";
    private final CategoryReqDto category = toCategoryReqDto(createNewCategory1());
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;

    @Test
    @SneakyThrows
    void getCategories() {
        mvc.perform(post(ADMIN_URL)
                        .content(objectMapper.writeValueAsString(category))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mvc.perform(get(URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").value(category.getName()));
    }

    @Test
    @SneakyThrows
    void getCategories_emptyList() {
        mvc.perform(get(URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @SneakyThrows
    void getCategory() {
        String response = mvc.perform(post(ADMIN_URL)
                        .content(objectMapper.writeValueAsString(category))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Integer id = JsonPath.read(response, "$.id");

        mvc.perform(get(URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    @SneakyThrows
    void getCategory_whenIdIncorrect() {
        mvc.perform(get(URL + "/-100")
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
    void getCategories_whenFromAndSizeExist_returnCategory() {
        var secondCategory = toCategoryReqDto(createNewCategory2());

        mvc.perform(post(ADMIN_URL)
                        .content(objectMapper.writeValueAsString(category))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mvc.perform(post(ADMIN_URL)
                        .content(objectMapper.writeValueAsString(secondCategory))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mvc.perform(get(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("from", "0")
                        .queryParam("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(secondCategory.getName()))
                .andExpect(jsonPath("$[1].name").doesNotExist());
    }
}