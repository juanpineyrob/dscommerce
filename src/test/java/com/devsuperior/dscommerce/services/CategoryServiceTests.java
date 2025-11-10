package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.CategoryDTO;
import com.devsuperior.dscommerce.entities.Category;
import com.devsuperior.dscommerce.factories.CategoryFactory;
import com.devsuperior.dscommerce.repositories.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
public class CategoryServiceTests {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    private Category category;
    private List<Category> categoryList;

    @BeforeEach
    void setup() throws Exception {
        category = CategoryFactory.createCategory();
        categoryList = new ArrayList<>(List.of(category));

        Mockito.when(categoryRepository.findAll()).thenReturn(categoryList);
    }

    @Test
    void findAllShouldReturnListCategoryDTO() {
        List<CategoryDTO> categoryDTOList = categoryService.findAll();

        Assertions.assertEquals(1, categoryDTOList.size());
        Assertions.assertEquals(categoryDTOList.getFirst().getId(), category.getId());
        Assertions.assertEquals(categoryDTOList.getFirst().getName(), category.getName());
        Assertions.assertEquals(categoryDTOList.getFirst().getId(), category.getId());
    }
}
