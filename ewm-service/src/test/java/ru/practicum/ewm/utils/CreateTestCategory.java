package ru.practicum.ewm.utils;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.category.model.Category;

@UtilityClass
public class CreateTestCategory {

    public static Category createNewCategory1() {
        Category category = new Category();
        category.setName("Cinema");
        return category;

    }

    public static Category updateNewCategory1() {
        Category category = new Category();
        category.setName("Party");
        return category;
    }

    public static Category createNewCategory2() {
        Category category = new Category();
        category.setName("Party");
        return category;

    }
}
