/*
 * Copyright (c) 2024 Atlassian US, Inc
 */
package net.jqwik.mockito.testcase;

public class AddingService {
    private final CountingService countingService;

    public AddingService(final CountingService countingService) {
        this.countingService = countingService;
    }

    public long addLengths(final String string1, final String string2) {
        return countingService.stringLength(string1) + countingService.stringLength(string2);
    }
}
