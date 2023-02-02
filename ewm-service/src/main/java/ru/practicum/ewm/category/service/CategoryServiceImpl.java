package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dao.CategoryRepository;
import ru.practicum.ewm.category.dto.CategoryReqDto;
import ru.practicum.ewm.category.dto.CategoryRespDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.exception.DataValidationException;

import javax.persistence.EntityNotFoundException;
import java.sql.SQLException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public CategoryRespDto addCategory(CategoryReqDto categoryDto) {
        Category createdCategory;

        try {
            createdCategory = categoryRepository.save(categoryMapper.toCategory(categoryDto));
            log.debug("Saved: {}", createdCategory);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Save category error", ex);
            throw new DataValidationException(String.format("Category with name %s already exist",
                    categoryDto.getName()));
        }

        return categoryMapper.toCategoryRespDto(createdCategory);
    }

    @Transactional(rollbackFor = SQLException.class, isolation = Isolation.SERIALIZABLE)
    public void removeCategory(Integer catId) {
        findCategoryById(catId);

        try {
            categoryRepository.deleteById(catId);

            //TODO change exception
        } catch (Exception ex) {
            log.warn("Delete category error", ex);
            throw new DataValidationException("The category is not empty");
        }
        log.debug("Category with id {} was deleted", catId);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public CategoryRespDto updateCategory(Integer catId, CategoryReqDto categoryReqDto) {
        Category category = findCategoryById(catId);
        categoryMapper.updateCategoryFromDto(categoryReqDto, category);
        Category updatedCategory;

        try {
            updatedCategory = categoryRepository.save(categoryMapper.toCategory(categoryReqDto));
            log.debug("Updated: {}", updatedCategory);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Update category error", ex);
            throw new DataValidationException(String.format("Category with name %s already exist",
                    categoryReqDto.getName()));
        }

        return categoryMapper.toCategoryRespDto(updatedCategory);
    }

    private Category findCategoryById(Integer catId) {
        var category = categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("CategoryID %s does not exist", catId)));

        log.debug("Category with id {} was found", catId);
        return category;
    }
}
