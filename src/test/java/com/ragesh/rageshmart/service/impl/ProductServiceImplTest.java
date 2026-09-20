package com.ragesh.rageshmart.service.impl;

import com.ragesh.rageshmart.dao.ProductDAO;
import com.ragesh.rageshmart.dto.ProductRequestDTO;
import com.ragesh.rageshmart.exception.AuthException;
import com.ragesh.rageshmart.exception.NotFoundException;
import com.ragesh.rageshmart.exception.ValidationException;
import com.ragesh.rageshmart.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductDAO productDAO;

    private ProductServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ProductServiceImpl(productDAO);
    }

    @Test
    void create_rejectsBlankName() {
        ProductRequestDTO request = ProductRequestDTO.builder()
                .name("  ")
                .price(new BigDecimal("10.00"))
                .stockQty(5)
                .build();

        ValidationException ex = assertThrows(ValidationException.class, () -> service.create(1L, request));
        assertEquals("name", ex.getField());
        verifyNoInteractions(productDAO);
    }

    @Test
    void create_rejectsNonPositivePrice() {
        ProductRequestDTO request = ProductRequestDTO.builder()
                .name("Widget")
                .price(BigDecimal.ZERO)
                .stockQty(5)
                .build();

        ValidationException ex = assertThrows(ValidationException.class, () -> service.create(1L, request));
        assertEquals("price", ex.getField());
    }

    @Test
    void create_savesValidProduct() throws ValidationException {
        ProductRequestDTO request = ProductRequestDTO.builder()
                .name("Widget")
                .price(new BigDecimal("19.99"))
                .stockQty(5)
                .category("Tools")
                .build();
        when(productDAO.insert(any(Product.class))).thenAnswer(inv -> {
            Product p = inv.getArgument(0);
            p.setId(42L);
            return p;
        });

        Product result = service.create(7L, request);

        assertEquals(42L, result.getId());
        assertEquals(7L, result.getSellerId());
        verify(productDAO).insert(any(Product.class));
    }

    @Test
    void update_rejectsNonOwner() {
        Product existing = new Product();
        existing.setId(1L);
        existing.setSellerId(99L); // different seller
        when(productDAO.findById(1L)).thenReturn(Optional.of(existing));

        ProductRequestDTO request = ProductRequestDTO.builder()
                .name("Widget")
                .price(new BigDecimal("10.00"))
                .stockQty(1)
                .build();

        assertThrows(AuthException.class, () -> service.update(1L, 7L, request));
        verify(productDAO, never()).update(any());
    }

    @Test
    void delete_throwsNotFoundWhenMissing() {
        when(productDAO.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.delete(1L, 7L));
    }
}
