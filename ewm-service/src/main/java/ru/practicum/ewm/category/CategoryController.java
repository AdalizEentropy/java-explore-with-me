package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.category.dto.CategoryRespDto;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.common.PageParam;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping()
    public List<CategoryRespDto> getCategories(@Valid PageParam pageParam) {
        return categoryService.getCategories(pageParam);
    }

    @GetMapping("/{catId}")
    public CategoryRespDto getCategory(@PathVariable Integer catId) {
        return categoryService.getCategory(catId);
    }
}
