/*
 * Copyright (c) 2024 Atlassian US, Inc
 */
package net.jqwik.mockito.testcase;

public interface ProductRepository {
    Product getById(int productId);
}
