package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryReqDto;
import ru.practicum.ewm.category.dto.CategoryRespDto;
import ru.practicum.ewm.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@Slf4j
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping()
    @ResponseStatus(code = HttpStatus.CREATED)
    public CategoryRespDto addCategory(@Valid @NotNull @RequestBody CategoryReqDto categoryReqDto) {
        return categoryService.addCategory(categoryReqDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void removeCategory(@PathVariable Integer catId) {
        categoryService.removeCategory(catId);
        log.debug("Category with id {} was deleted", catId);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(code = HttpStatus.OK)
    public CategoryRespDto updateCategory(@PathVariable Integer catId,
                                          @Valid @NotNull @RequestBody CategoryReqDto categoryReqDto) {
        CategoryRespDto updatedCategory = categoryService.updateCategory(catId, categoryReqDto);
        log.debug("Updated: {}", updatedCategory);
        return updatedCategory;
    }
}
