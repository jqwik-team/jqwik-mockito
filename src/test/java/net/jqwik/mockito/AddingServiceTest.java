/*
 * Copyright (c) 2024 Atlassian US, Inc
 */
package net.jqwik.mockito;

import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Group;
import net.jqwik.api.Property;
import net.jqwik.api.lifecycle.AddLifecycleHook;
import net.jqwik.mockito.testcase.AddingService;
import net.jqwik.mockito.testcase.CountingService;
import org.mockito.InjectMocks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@AddLifecycleHook(MockitoLifecycleHooks.class)
class AddingServiceTest {
    private final CountingService countingService = mock();

    @InjectMocks
    private AddingService addingService;

    @Group
    class BasicExamples {
        @Example
        void checkSingleValue() {
            final String string1 = "a";
            final String string2 = "bb";

            when(countingService.stringLength(string1)).thenReturn(1);
            when(countingService.stringLength(string2)).thenReturn(2);

            final long result = addingService.addLengths(string1, string2);
            assertThat(result).isEqualTo(3L);
            verify(countingService).stringLength(string1);
            verify(countingService).stringLength(string2);
        }
    }

    @Group
    class PropertyTests {
        @Property
        void checkTotalLength2Args(@ForAll final String string1, @ForAll final String string2) {
            when(countingService.stringLength(string1)).thenReturn(string1.length());
            when(countingService.stringLength(string2)).thenReturn(string2.length());

            final long result = addingService.addLengths(string1, string2);
            assertThat(result).isEqualTo((long) string1.length() + (long) string2.length());

            verify(countingService, times(2)).stringLength(anyString());
        }

        @Property
        void checkTotalLength4Args(
                @ForAll final String string1,
                @ForAll final String string2,
                @ForAll final String string3,
                @ForAll final String string4) {
            when(countingService.stringLength(string1)).thenReturn(string1.length());
            when(countingService.stringLength(string2)).thenReturn(string2.length());
            when(countingService.stringLength(string3)).thenReturn(string3.length());
            when(countingService.stringLength(string4)).thenReturn(string4.length());

            final long result1 = addingService.addLengths(string1, string2);
            final long result2 = addingService.addLengths(string3, string4);
            assertThat(result1).isEqualTo((long) string1.length() + (long) string2.length());
            assertThat(result2).isEqualTo((long) string3.length() + (long) string4.length());

            verify(countingService, times(4)).stringLength(anyString());
        }
    }
}
