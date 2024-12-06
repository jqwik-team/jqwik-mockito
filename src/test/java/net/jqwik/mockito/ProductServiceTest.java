/*
 * Copyright (c) 2024 Atlassian US, Inc
 */
package net.jqwik.mockito;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.lifecycle.AddLifecycleHook;
import net.jqwik.mockito.testcase.LoggingService;
import net.jqwik.mockito.testcase.Product;
import net.jqwik.mockito.testcase.ProductRepository;
import net.jqwik.mockito.testcase.ProductService;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@AddLifecycleHook(MockitoLifecycleHooks.class)
public class ProductServiceTest {
    private static class ConsoleLoggingService implements LoggingService {
        @Override
        public void log(String x) {
            System.out.println(x);
        }
    }

    @Mock
    private ProductRepository productRepository;

    private final LoggingService loggingService = spy(new ConsoleLoggingService());

    @Captor
    private ArgumentCaptor<String> loggingMessageCaptor;

    @InjectMocks
    private ProductService productService;

    @Property
    void verifyFindProduct(@ForAll int productId) {
        final Product product = new Product(productId, "Product " + productId);
        when(productRepository.getById(productId)).thenReturn(product);

        final Product result = productService.getProductById(productId);

        assertThat(result).isEqualTo(product);
        verify(loggingService).log(loggingMessageCaptor.capture());
        verify(loggingService, times(1)).log(anyString());
        final String loggingMessageCaptorValue = loggingMessageCaptor.getValue();
        assertThat(loggingMessageCaptorValue).startsWith("Retrieved product for ID ");
        assertThat(loggingMessageCaptorValue).endsWith(product.toString());
    }
}
