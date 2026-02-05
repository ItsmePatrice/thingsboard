/**
 * Copyright © 2016-2025 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.thingsboard.server.service.tracking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class FunctionCallCounterService {

    private final ConcurrentHashMap<String, AtomicInteger> counters = new ConcurrentHashMap<>();

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
    
    public void clearCounters() {
        counters.clear();
    }

    public void validateFunctionName(String functionName) {
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


    public int getCallCount(String functionName) {
        if (functionName == null) {
            return 0;
        }
        AtomicInteger count = counters.get(functionName);
        return count != null ? count.get() : 0;
    }
}
