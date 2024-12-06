/*
 * Copyright (c) 2024 Atlassian US, Inc
 */
package net.jqwik.mockito;

import java.util.*;

import org.apiguardian.api.*;
import org.mockito.*;
import org.mockito.internal.configuration.plugins.*;
import org.mockito.internal.session.*;
import org.mockito.junit.jupiter.*;
import org.mockito.quality.*;

import net.jqwik.api.lifecycle.*;

/**
 * Provides {@link net.jqwik.api.lifecycle.LifecycleHook}s to enable usage of Mockito with jqwik.
 * <ul>
 *   <li>Creates mocks for any fields with Mockito annotations such as {@link org.mockito.Mock} or {@link org.mockito.Spy}.</li>
 *   <li>Resets all mocks between each try, whether those mocks were created programmatically (eg: via calls to {@code Mockito.mock()})
 *   or via annotations.</li>
 * </ul>
 *
 * <pre>
 * import net.jqwik.api.lifecycle.AddLifecycleHook;
 * import org.mockito.InjectMocks;
 * import org.mockito.Mock;
 * import org.mockito.Mockito;
 * import org.mockito.Spy;
 * {@literal @}AddLifecycleHook(MockitoLifecycleHooks.class)
 * class OrderServiceTest {
 *     {@literal @}Mock
 *     private OrderRepository orderRepository;
 *     private OrderRepository orderRepository2 = Mockito.mock();
 *     private LoggingService loggingService = Mockito.spy(new ConsoleLoggingServiceImpl());
 *     {@literal @}Spy
 *     private ConsoleLoggingServiceImpl loggingService2;
 *     {@literal @}InjectMock
 *     private OrderService orderService;
 * }
 * </pre>
 *
 * @see net.jqwik.api.lifecycle.AddLifecycleHook
 */
@API(status = API.Status.EXPERIMENTAL, since = "0.0.1")
public class MockitoLifecycleHooks implements AroundPropertyHook, AroundTryHook {
	// type Object[]
	private static final String MOCKS = "net.jqwik.mockito.mocks";

	@Override
	public PropagationMode propagateTo() {
		return PropagationMode.ALL_DESCENDANTS;
	}

	@Override
	public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor propertyExecutor)
		throws Throwable {
		final List<Object> testInstances = context.testInstances();

		// finds all mocked fields within the test instance object
		MockitoSession session = null;
		try {
			final List<Object> mockList = new ArrayList<>();

			final Optional<Strictness> actualStrictness =
				context.findAnnotationsInContainer(MockitoSettings.class).stream()
					   .findFirst()
					   .map(MockitoSettings::strictness);

			session = Mockito.mockitoSession()
							 .initMocks(testInstances.toArray())
							 .strictness(actualStrictness.orElse(null))
							 .logger(new MockitoSessionLoggerAdapter(Plugins.getMockitoLogger()))
							 .startMocking();

			for (final Object testInstance : testInstances) {
				// open all the annotated mocks, keeping track of the handle so that we can close them later
				// mockitoCloseables.add(MockitoAnnotations.openMocks(testInstance));
				// find all of the mocks in each of the test instances and store them in a list, so that we can reset
				// them between tries.
				mockList.addAll(MockFinder.getMocks(testInstance));
			}

			Store.create(MOCKS, Lifespan.PROPERTY, mockList::toArray);

			return propertyExecutor.execute();
		} finally {
			if (session != null) {
				session.finishMocking();
			}
		}
	}

	@Override
	public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor tryExecutor, List<Object> parameters) {
		try {
			return tryExecutor.execute(parameters);
		} finally {
			final Object[] mocks = Store.<Object[]>get(MOCKS).get();
			Mockito.reset(mocks);
		}
	}
}
