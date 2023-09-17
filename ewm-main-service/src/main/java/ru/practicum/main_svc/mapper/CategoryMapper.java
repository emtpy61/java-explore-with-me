package ru.practicum.main_svc.mapper;

import org.mapstruct.Mapper;
import ru.practicum.main_svc.dto.category.CategoryDto;
import ru.practicum.main_svc.dto.category.NewCategoryDto;
import ru.practicum.main_svc.model.Category;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toModel(NewCategoryDto newCategoryDto);

    CategoryDto toDto(Category category);

    List<CategoryDto> toDtoList(List<Category> categoryList);
}
