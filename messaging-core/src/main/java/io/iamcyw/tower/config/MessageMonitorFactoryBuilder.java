/*
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.iamcyw.tower.config;


import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.monitoring.MessageMonitor;
import io.iamcyw.tower.monitoring.NoOpMessageMonitor;
import io.iamcyw.tower.utils.Assert;
import io.iamcyw.tower.utils.i18n.I18ns;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Class used by {@link DefaultConfigure} to maintain the configuration for Message Monitors and create the function
 * used in the {@link Component}.
 */
class MessageMonitorFactoryBuilder {

    // Comparator to ensure the keys are processed in a reasonably stable order in #getFactoryForType
    // Added comparing on the class' classloader as a class can be loaded multiple times by different classloaders
    private final Comparator<Class<?>> classComparator = Comparator
            .comparing((Function<Class<?>, String>) Class::getName)
            .thenComparingInt((Class<?> c) -> c.getClassLoader().hashCode());

    private final Map<String, SortedMap<Class<?>, MessageMonitorFactory>> forNameFactories = new HashMap<>();

    private final SortedMap<Class<?>, MessageMonitorFactory> forTypeFactories = new TreeMap<>(classComparator);

    private MessageMonitorFactory defaultFactory = (configuration, type, name) -> NoOpMessageMonitor.instance();

    private boolean built = false;

    MessageMonitorFactoryBuilder add(Class<?> componentType, String componentName,
                                     MessageMonitorFactory messageMonitorFactory) {
        assertNotBuilt();
        Assert.nonNull(componentType, I18ns.create().args("componentType").apply());
        Assert.nonNull(componentName, I18ns.create().args("componentName").apply());
        Assert.nonNull(messageMonitorFactory, I18ns.create().args("messageMonitorFactory").apply());
        Map<Class<?>, MessageMonitorFactory> mapByType = forNameFactories
                .computeIfAbsent(componentName, (name) -> new TreeMap<>(classComparator));
        mapByType.put(componentType, messageMonitorFactory);
        return this;
    }

    MessageMonitorFactoryBuilder add(Class<?> componentType, MessageMonitorFactory messageMonitorFactory) {
        assertNotBuilt();
        Assert.nonNull(componentType, I18ns.create().args("componentType").apply());
        Assert.nonNull(messageMonitorFactory, I18ns.create().args("messageMonitorFactory").apply());
        forTypeFactories.put(componentType, messageMonitorFactory);
        return this;
    }

    MessageMonitorFactoryBuilder add(MessageMonitorFactory defaultFactory) {
        assertNotBuilt();
        Assert.nonNull(defaultFactory, I18ns.create().args("defaultFactory").apply());
        this.defaultFactory = defaultFactory;
        return this;
    }

    BiFunction<Class<?>, String, MessageMonitor<Message<?>>> build(Configuration configuration) {
        assertNotBuilt();
        built = true;
        return (type, name) -> {
            MessageMonitorFactory factory = getFactoryForType(forNameFactories.get(name), type);

            if (factory == null) {
                factory = getFactoryForType(forTypeFactories, type);
            }

            if (factory == null) {
                factory = defaultFactory;
            }

            return factory.create(configuration, type, name);
        };
    }

    /**
     * Iterates through the keys in the given map, and returns the factory associated with the type that matches the
     * given type, or null if no key matches. The given type matches a key if the given type is assignable to the key.
     * If multiple keys match, and the matching classes derive from each other, the factory associated with the most
     * derived class is returned. If the matching classes do not derive from each other, the result is unspecified.
     */
    private MessageMonitorFactory getFactoryForType(SortedMap<Class<?>, MessageMonitorFactory> map, Class<?> type) {
        if (map == null) {
            return null;
        }

        Class<?> match = null;
        for (Class<?> key : map.keySet()) {
            if (key.isAssignableFrom(type)) {
                if (match != null) {
                    // check if 'key' is a better match than current 'match'
                    if (match.isAssignableFrom(key)) {
                        match = key;
                    }
                } else {
                    match = key;
                }
            }
        }

        if (match == null) {
            return null;
        }
        return map.get(match);
    }

    private void assertNotBuilt() {
        Assert.isFalse(built, "this builder has already been built");
    }

}
