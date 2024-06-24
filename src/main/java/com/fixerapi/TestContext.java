package com.fixerapi;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class TestContext {
    private final ThreadLocal<Map<String, Object>> map = ThreadLocal.withInitial(HashMap::new);

    public <T> void remember(String key, T object) {
        map.get().put(key, object);
    }

    public <T> T recall(String key) {
        @SuppressWarnings("unchecked")
        T value = (T) map.get().get(key);
        return value;
    }

    public <T> T recallAndForget(String key) {
        @SuppressWarnings("unchecked")
        T value = (T) map.get().remove(key);
        return value;
    }

    public void forgetAll() {
        map.get().clear();
    }
}
