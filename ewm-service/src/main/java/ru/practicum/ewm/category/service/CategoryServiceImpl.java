package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dao.CategoryRepository;
import ru.practicum.ewm.category.dto.CategoryReqDto;
import ru.practicum.ewm.category.dto.CategoryRespDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.common.PageParam;
import ru.practicum.ewm.exception.DataValidationException;

import javax.persistence.EntityNotFoundException;
import java.sql.SQLException;
import java.util.List;

import static ru.practicum.ewm.common.PageParam.pageRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private static final Sort SORT_TYPE = Sort.by("id").descending();
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
        categoryRepository.deleteById(catId);
        log.debug("Category Id {} was removed", catId);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public CategoryRespDto updateCategory(Integer catId, CategoryReqDto categoryReqDto) {
        var category = findCategoryById(catId);
        categoryMapper.updateCategoryFromDto(categoryReqDto, category);

        log.debug("Category was updated: {}", category);
        return categoryMapper.toCategoryRespDto(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryRespDto> getCategories(PageParam pageParam) {
        List<Category> foundCategories = categoryRepository.findAll(pageRequest(pageParam, SORT_TYPE)).toList();

        log.debug("Categories found: {}", foundCategories);
        return categoryMapper.toCategoriesRespDto(foundCategories);
    }

    @Transactional(readOnly = true)
    public CategoryRespDto getCategory(Integer catId) {
        return categoryMapper.toCategoryRespDto(findCategoryById(catId));
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Integer catId) {
        return findCategoryById(catId);
    }

    private Category findCategoryById(Integer catId) {
        var category = categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("CategoryID %s does not exist", catId)));

        log.debug("Category with id {} was found", catId);
        return category;
    }
}
