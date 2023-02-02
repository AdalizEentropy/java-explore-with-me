package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryReqDto;
import ru.practicum.ewm.category.dto.CategoryRespDto;
import ru.practicum.ewm.common.PageParam;

import java.util.List;

public interface CategoryService {

    CategoryRespDto addCategory(CategoryReqDto categoryReqDto);

    void removeCategory(Integer catId);

    CategoryRespDto updateCategory(Integer catId, CategoryReqDto categoryReqDto);

    List<CategoryRespDto> getCategories(PageParam pageParam);

    CategoryRespDto getCategory(Integer catId);
}
