/*
 * Copyright (c) 2024 Atlassian US, Inc
 */
package net.jqwik.mockito;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import org.mockito.*;
import org.mockito.internal.configuration.plugins.*;
import org.mockito.plugins.*;

/**
 * Helper class which extracts mocked fields from a given test instance.
 */
class MockFinder {
	/**
	 * The set of annotations which indicate that a field is a Mockito mock.
	 */
	private static final Set<Class<? extends Annotation>> MOCKING_ANNOTATION_TYPES;

	static {
		MOCKING_ANNOTATION_TYPES = new HashSet<>();
		MOCKING_ANNOTATION_TYPES.add(Mock.class);
		MOCKING_ANNOTATION_TYPES.add(Spy.class);
	}

	private static final MemberAccessor MEMBER_ACCESSOR = Plugins.getMemberAccessor();

	/**
	 * Retrieve mocking fields for a given test instance object.
	 *
	 * @param testInstance The test instance which contains the mocked fields
	 * @return The list of mock objects which have been injected into the test instance
	 * @throws IllegalAccessException If it is not possible to get access to the fields in the test instance
	 */
	static List<Object> getMocks(Object testInstance) throws IllegalAccessException {
		final List<Object> mocks = new ArrayList<>();
		for (final Field field : testInstance.getClass().getDeclaredFields()) {
			if (isFieldMockitoAnnotated(field)) {
				mocks.add(MEMBER_ACCESSOR.get(field, testInstance));
			} else {
				final Object fieldValue = MEMBER_ACCESSOR.get(field, testInstance);
				final MockingDetails mockingDetails = Mockito.mockingDetails(fieldValue);
				if (mockingDetails.isMock()) {
					mocks.add(fieldValue);
				}
			}
		}
		return mocks;
	}

	private static boolean isFieldMockitoAnnotated(Field field) {
		for (Annotation annotation : field.getAnnotations()) {
			if (MOCKING_ANNOTATION_TYPES.contains(annotation.annotationType())) {
				return true;
			}
		}
		return false;
	}
}
