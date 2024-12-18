# Mockito Support for jqwik

Provides `net.jqwik.api.lifecycle.LifecycleHook`s to enable usage of Mockito
with jqwik.

- Creates mocks for any fields with Mockito annotations such as
  `org.mockito.Mock` or `org.mockito.Spy`.
- Resets all mocks between each try, whether those mocks were created
  programmatically (eg: via calls to `Mockito.mock()`}) or via annotations.

## How to use

### Maven and Gradle configuration

Keep in mind that you have to explicitly add the Mockito dependencies to your project.

Currently, there is also a dependency on `mockito-junit-jupiter`. 
This allows the use of the `@MockitoSettings` annotation.

#### Maven

```xml
<dependency>
    <group>org.mockito</group>
    <artifactId>mockito-core</artifactId>
    <version>$MOCKITO_VERSION</version>
    <scope>test</scope>
</dependency>
<dependency>
    <group>org.mockito</group>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>$MOCKITO_VERSION</version>
    <scope>test</scope>
</dependency>
<dependency>
    <group>net.jqwik</group>
    <artifactId>jqwik-mockito</artifactId>
    <version>$LATEST_VERSION</version>
    <scope>test</scope>
</dependency>
```

#### Gradle

```
testImplementation("org.mockito:mockito-core:$MOCKITO_VERSION")
testImplementation("org.mockito:mockito-junit-jupiter:$MOCKITO_VERSION")
testImplementation("net.jqwik:jqwik-mockito:$LATEST_VERSION")
```

### Usage in Tests

```java
import net.jqwik.api.lifecycle.AddLifecycleHook;
import net.jqwik.api.*;
import net.jqwik.mockito.MockitoLifecycleHooks;
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

This is built against Mockito v4, but _should_ support all versions. 


## Release Notes

### 1.0.0

- Uses jqwik 1.9.2
- Tested with Mockito 4.11.0