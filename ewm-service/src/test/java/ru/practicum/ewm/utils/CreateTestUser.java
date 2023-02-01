package ru.practicum.ewm.utils;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.user.model.User;

@UtilityClass
public class CreateTestUser {

    public static User createNewUser1() {
        return new User()
                .setName("Nick Name")
                .setEmail("mail@mail.ru");
    }

    public static User createNewUser2() {
        return new User()
                .setName("Mike Nick")
                .setEmail("mail@google.com");
    }
}
