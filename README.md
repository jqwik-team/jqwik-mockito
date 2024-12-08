# Mockito Support for jqwik

Provides `net.jqwik.api.lifecycle.LifecycleHook`s to enable usage of Mockito
with jqwik.

- Creates mocks for any fields with Mockito annotations such as
  `org.mockito.Mock` or `org.mockito.Spy`.
- Resets all mocks between each try, whether those mocks were created
  programmatically (eg: via calls to `Mockito.mock()`}) or via annotations.

## How to use

### Maven and Gradle configuration

Maven:

```xml
<dependency>
    <group>net.jqwik</group>
    <artifactId>jqwik-mockito</artifactId>
    <version>$LATEST_VERSION</version>
    <scope>test</scope>
</dependency>
```

Gradle:

```
testImplementation("net.jqwik:jqwik-mockito:$LATEST_VERSION")
```

### Usage in Tests

```java
import net.jqwik.api.lifecycle.AddLifecycleHook;
import net.jqwik.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import static org.mockito.Mockito.*;

@AddLifecycleHook(MockitoLifecycleHooks.class)
class OrderServiceTest {
    // Either @Mock or Mockito.mock() can be used.
    @Mock
    private OrderRepository orderRepository;
    private OrderRepository orderRepository2 = Mockito.mock();

    // Either @Spy or Mockito.spy() can be used.
    @Spy
    private ConsoleLoggingServiceImpl loggingService;
    private LoggingService loggingService2 = Mockito.spy(new ConsoleLoggingServiceImpl());

    @InjectMock
    private OrderService orderService;

    @Group
    class BasicExamples {
        @Example
        void testBasicExample() {
            when(orderRepository.getOrder(any())).thenReturn(null);
            // testing code
        }
    }

    @Group
    class SomeProperties {
        @Property
        void testProperty() {
            when(orderRepository.getOrder(any())).thenReturn("orderId");
            // testing code
        }
    }
}
```

## Compatibility

This is built against Mockito v4, and should support for all versions. 
