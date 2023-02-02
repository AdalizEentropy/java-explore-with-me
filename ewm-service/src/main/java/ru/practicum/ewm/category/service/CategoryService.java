package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryReqDto;
import ru.practicum.ewm.category.dto.CategoryRespDto;

public interface CategoryService {

    CategoryRespDto addCategory(CategoryReqDto categoryReqDto);

    void removeCategory(Integer catId);

    CategoryRespDto updateCategory(Integer catId, CategoryReqDto categoryReqDto);
}
