package org.thingsboard.server.service.tracking;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest(classes = FunctionCallCounterService.class)
@ActiveProfiles("test")
public class FunctionCallCounterServiceTest {

    @Autowired
    private FunctionCallCounterService functionCallCounterService;

    @BeforeEach
    public void setUp() {
        functionCallCounterService.clearCounters();
    }

    @Test
    public void shouldRecordSingleFunctionCall() {
        String functionName = "testFunction";
        functionCallCounterService.recordCall(functionName);
        assertEquals(1, functionCallCounterService.getCallCount(functionName));
    }

    @Test
    public void shouldRecordMultipleCallsForSameFunction() {
        String functionName = "testFunction";
        functionCallCounterService.recordCall(functionName);
        functionCallCounterService.recordCall(functionName);
        functionCallCounterService.recordCall(functionName);
        assertEquals(3, functionCallCounterService.getCallCount(functionName));
    }

    @Test
    public void shouldTrackMultipleFunctionsIndependently() {
        String function1 = "getUserProfile";
        String function2 = "logout";
        String function3 = "changePassword";

        functionCallCounterService.recordCall(function1);
        functionCallCounterService.recordCall(function2);
        functionCallCounterService.recordCall(function1);
        functionCallCounterService.recordCall(function3);
        functionCallCounterService.recordCall(function2);

        assertEquals(2, functionCallCounterService.getCallCount(function1));
        assertEquals(2, functionCallCounterService.getCallCount(function2));
        assertEquals(1, functionCallCounterService.getCallCount(function3));
    }

    @Test
    public void shouldIgnoreNullFunctionName() {
        functionCallCounterService.recordCall(null);
        assertEquals(0, functionCallCounterService.getCallCount(null));
    }

    @Test
    public void shouldIgnoreEmptyFunctionName() {
        functionCallCounterService.recordCall("");
        assertEquals(0, functionCallCounterService.getCallCount(""));
    }

    @Test
    public void shouldIgnoreFunctionNameWithSpaces() {
        String invalidFunctionName = "invalid function";
        functionCallCounterService.recordCall(invalidFunctionName);
        assertEquals(0, functionCallCounterService.getCallCount(invalidFunctionName));
    }

    @Test
    public void shouldReturnZeroForFunctionNeverCalled() {
        String functionName = "neverCalledFunction";
        assertEquals(0, functionCallCounterService.getCallCount(functionName));
    }

    @Test
    public void shouldThrowExceptionForNull() {
        assertThrows(IllegalArgumentException.class, () -> functionCallCounterService.validateFunctionName(null));
    }

    @Test
    public void shouldThrowExceptionForEmptyString() {
        assertThrows(IllegalArgumentException.class, () -> functionCallCounterService.validateFunctionName(""));
    }

    @Test
    public void shouldThrowExceptionForFunctionNameWithSpaces() {
        assertThrows(IllegalArgumentException.class, () -> functionCallCounterService.validateFunctionName("invalid function name"));
    }

    @Test()
    public void shouldAcceptValidFunctionName() {
        functionCallCounterService.validateFunctionName("validFunctionName");
    }

}

