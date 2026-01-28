package org.thingsboard.server.service.tracking;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
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

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForNull() {
        functionCallCounterService.validateFunctionName(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForEmptyString() {
        functionCallCounterService.validateFunctionName("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForFunctionNameWithSpaces() {
        functionCallCounterService.validateFunctionName("invalid function name");
    }

    @Test()
    public void shouldAcceptValidFunctionName() {
        functionCallCounterService.validateFunctionName("validFunctionName");
    }

}

