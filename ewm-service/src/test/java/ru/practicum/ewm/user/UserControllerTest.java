package ru.practicum.ewm.user;

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
import ru.practicum.ewm.user.dto.NewUserDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.ewm.mapper.TestMapper.toNewUserDto;
import static ru.practicum.ewm.utils.CreateTestUser.createNewUser1;
import static ru.practicum.ewm.utils.CreateTestUser.createNewUser2;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("testdb")
class UserControllerTest {
    private static final String URL = "/admin/users";
    private final NewUserDto newUser = toNewUserDto(createNewUser1());
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;

    @Test
    @SneakyThrows
    void addUser() {
        mvc.perform(post(URL)
                        .content(objectMapper.writeValueAsString(newUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(newUser.getName()))
                .andExpect(jsonPath("$.email").value(newUser.getEmail()));
    }

    @Test
    @SneakyThrows
    void addUser_withEmptyName_returnBadRequest() {
        NewUserDto user = toNewUserDto(createNewUser1());
        user.setName(null);

        mvc.perform(post(URL)
                        .content(objectMapper.writeValueAsString(user))
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
    void addUser_withExistEmail_returnConflict() {
        mvc.perform(post(URL)
                        .content(objectMapper.writeValueAsString(newUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mvc.perform(post(URL)
                        .content(objectMapper.writeValueAsString(newUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("CONFLICT"))
                .andExpect(jsonPath("$.reason").value("Integrity constraint has been violated."))
                .andExpect(jsonPath("$.message")
                        .value(String.format("User with email %s already exist", newUser.getEmail())))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @SneakyThrows
    void getUsers() {
        mvc.perform(post(URL)
                        .content(objectMapper.writeValueAsString(newUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mvc.perform(get(URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").value(newUser.getName()))
                .andExpect(jsonPath("$[0].email").value(newUser.getEmail()));
    }

    @Test
    @SneakyThrows
    void getUsers_emptyList() {
        mvc.perform(get(URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @SneakyThrows
    void getUsers_whenIdsExist_returnUser() {
        String response = mvc.perform(post(URL)
                        .content(objectMapper.writeValueAsString(newUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Integer id = JsonPath.read(response, "$.id");

        mvc.perform(get(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("ids", id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id));
    }

    @Test
    @SneakyThrows
    void getUsers_whenIdsExist_notReturnUser() {
        mvc.perform(get(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("ids", "-100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @SneakyThrows
    void getUsers_whenFromAndSizeExist_returnUser() {
        var secondUser = toNewUserDto(createNewUser2());

        mvc.perform(post(URL)
                        .content(objectMapper.writeValueAsString(newUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mvc.perform(post(URL)
                        .content(objectMapper.writeValueAsString(secondUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mvc.perform(get(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("from", "0")
                        .queryParam("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value(secondUser.getEmail()))
                .andExpect(jsonPath("$[1].email").doesNotExist());
    }

    @Test
    @SneakyThrows
    void getUsers_whenFromIncorrect_returnError() {
        mvc.perform(get(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("from", "-2")
                        .queryParam("size", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.reason").value("Incorrectly made request."))
                .andExpect(jsonPath("$.message")
                        .value("Field: from. Error: must be greater than or equal to 0. Value: -2"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @SneakyThrows
    void deleteUser() {
        String response = mvc.perform(post(URL)
                        .content(objectMapper.writeValueAsString(newUser))
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
    void deleteUser_incorrectId() {
        mvc.perform(post(URL)
                        .content(objectMapper.writeValueAsString(newUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mvc.perform(delete(URL + "/100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.reason").value("The required object was not found."))
                .andExpect(jsonPath("$.message")
                        .value(String.format("UserID %s does not exist", 100)))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}