/*
 * Copyright (c) 2024 Atlassian US, Inc
 */
package net.jqwik.mockito;

import org.mockito.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.mockito.testcase.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
