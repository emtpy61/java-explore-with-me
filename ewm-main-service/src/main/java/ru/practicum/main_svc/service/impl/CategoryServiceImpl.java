package ru.practicum.main_svc.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_svc.dto.category.CategoryDto;
import ru.practicum.main_svc.dto.category.NewCategoryDto;
import ru.practicum.main_svc.exception.ewm.CategoryNotEmptyException;
import ru.practicum.main_svc.exception.ewm.NameAlreadyExistException;
import ru.practicum.main_svc.exception.ewm.NotFoundException.CategoryNotFoundException;
import ru.practicum.main_svc.mapper.CategoryMapper;
import ru.practicum.main_svc.model.Category;
import ru.practicum.main_svc.repository.CategoryRepository;
import ru.practicum.main_svc.repository.EventRepository;
import ru.practicum.main_svc.service.CategoryService;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            String errorMessage = String.format("Can't create category. %s already used.",
                    newCategoryDto.getName());
            throw new NameAlreadyExistException(errorMessage);
        }
        Category category = categoryMapper.toModel(newCategoryDto);
        category = categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        return categoryMapper.toDtoList(categoryRepository.findAll(page).toList());
    }

    @Override
    public CategoryDto getCategory(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new CategoryNotFoundException(catId));
        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new CategoryNotFoundException(catId));

        if (!category.getName().equals(categoryDto.getName())) {
            if (categoryRepository.existsByName(categoryDto.getName())) {
                String errorMessage = String.format("Can't update category. %s already used.",
                        categoryDto.getName());
                throw new NameAlreadyExistException(errorMessage);
            }
        }

        category.setName(categoryDto.getName());

        category = categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        if (eventRepository.existsByCategoryId(catId)) {
            throw new CategoryNotEmptyException("The category is not empty");
        }
        categoryRepository.deleteById(catId);
    }
}

