package org.thingsboard.server.service.tracking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class FunctionCallCounterService {

    private final ConcurrentHashMap<String, AtomicInteger> counters = new ConcurrentHashMap<>();

    private void validateFunctionName(String functionName) {
        if (functionName == null) {
            throw new IllegalArgumentException("Function name cannot be null");
        }
        if (functionName.isEmpty()) {
            throw new IllegalArgumentException("Function name cannot be empty");
        }
        if (functionName.contains(" ")) {
            throw new IllegalArgumentException("Function name cannot contain spaces");
        }
    }

    public void recordCall(String functionName) {
        try {
            validateFunctionName(functionName);
            counters
                .computeIfAbsent(functionName, k -> new AtomicInteger(0))
                .incrementAndGet();
        } catch (IllegalArgumentException e) {
            log.error("Failed to record function call: ", e.getMessage());
        }
    }

    public int getCallCount(String functionName) {
        try {
            validateFunctionName(functionName);
            AtomicInteger count = counters.get(functionName);
            return count != null ? count.get() : 0;
        } catch (IllegalArgumentException e) {
            log.error("Failed to get call count: ", e.getMessage());
            return 0;
        }
    }
}
