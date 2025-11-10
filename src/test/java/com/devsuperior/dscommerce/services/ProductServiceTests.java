package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.dto.ProductMinDTO;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.factories.ProductFactory;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.DatabaseException;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    private Long existingProductId,
            nonExistingProductId,
            existingProductIdIntegrityViolation;

    private String name;

    private Product product;

    private ProductDTO productDTO;

    private Page<Product> productPage;

    @BeforeEach
    void setup() {

        existingProductId = 1L;
        nonExistingProductId = 2L;
        existingProductIdIntegrityViolation = 3L;

        product = ProductFactory.createProductCategory();

        productDTO = new ProductDTO(product);

        productPage = new PageImpl<>(List.of(product));

        Mockito.when(productRepository.findById(existingProductId)).thenReturn(Optional.of(product));
        Mockito.when(productRepository.findById(nonExistingProductId)).thenReturn(Optional.empty());

        Mockito.when(productRepository.findProductsByName(Mockito.eq(name), Mockito.any(Pageable.class))).thenReturn(productPage);

        Mockito.when(productRepository.save(Mockito.any())).thenReturn(product);

        Mockito.when(productRepository.getReferenceById(existingProductId)).thenReturn(product);
        Mockito.when(productRepository.getReferenceById(nonExistingProductId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(productRepository.existsById(existingProductId)).thenReturn(true);
        Mockito.when(productRepository.existsById(existingProductIdIntegrityViolation)).thenReturn(true);
        Mockito.when(productRepository.existsById(nonExistingProductId)).thenReturn(false);

        Mockito.doNothing().when(productRepository).deleteById(existingProductId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(existingProductIdIntegrityViolation);

    }

    @Test
    void findByIdShouldReturnProductDTOWhenIdExists() {
        ProductDTO result = productService.findById(existingProductId);
        Assertions.assertEquals(result.getId(), product.getId());
        Assertions.assertEquals(result.getName(), product.getName());
        Assertions.assertEquals(result.getPrice(), product.getPrice());
        Assertions.assertEquals(result.getDescription(), product.getDescription());
    }

    @Test
    void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.findById(nonExistingProductId);
        });
    }

    @Test
    void findAllShouldReturnPaginatedProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductMinDTO> result = productService.findAll(name, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getSize());
        Assertions.assertEquals(result.iterator().next().getName(), product.getName());
    }

    @Test
    void insertShouldReturnProductDTOWhenIdExists() {
        ProductDTO result = productService.insert(productDTO);

        Assertions.assertNotNull(result);

        Assertions.assertEquals(result.getId(), productDTO.getId());
        Assertions.assertEquals(result.getName(), productDTO.getName());
        Assertions.assertEquals(result.getPrice(), productDTO.getPrice());
        Assertions.assertEquals(result.getDescription(), productDTO.getDescription());

        Assertions.assertEquals(result.getCategories().getFirst().getId(), productDTO.getCategories().getFirst().getId());
        Assertions.assertEquals(result.getCategories().getFirst().getName(), productDTO.getCategories().getFirst().getName());

    }

    @Test
    void updateShouldReturnProductDTOWhenIdExists() {
        ProductDTO result = productService.update(existingProductId, productDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), productDTO.getId());
        Assertions.assertEquals(result.getName(), productDTO.getName());
        Assertions.assertEquals(result.getPrice(), productDTO.getPrice());
        Assertions.assertEquals(result.getDescription(), productDTO.getDescription());
    }

    @Test
    void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
        Throwable exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.update(nonExistingProductId, productDTO);
        });
        Assertions.assertEquals("Recurso no encontrado", exception.getMessage());
    }

    @Test
    void deleteShouldDoNothingWhenIdExists() {
        Assertions.assertDoesNotThrow(() -> {
            productService.delete(existingProductId);
        });
        Mockito.verify(productRepository, Mockito.times(1))
                .deleteById(existingProductId);
    }

    @Test
    void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.delete(nonExistingProductId);
        });
    }

    @Test
    void deleteShouldThrowDataIntegrityViolationExceptionWhenIdIsDependent() {
        Assertions.assertThrows(DatabaseException.class, () -> {
            productService.delete(existingProductIdIntegrityViolation);
        });
    }
}
