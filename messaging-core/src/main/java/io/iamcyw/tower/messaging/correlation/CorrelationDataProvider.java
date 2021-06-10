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

package io.iamcyw.tower.messaging.correlation;

import io.iamcyw.tower.messaging.Message;

import java.util.Map;

/**
 * 定义来自消息的数据的对象,
 * 该消息应作为关联数据附加到由于处理该消息而生成的消息上
 */
@FunctionalInterface
public interface CorrelationDataProvider {

    /**
     * Provides a map with the entries to attach as correlation data to generated messages while processing given
     * {@code message}.
     * <p/>
     * This method should not return {@code null}.
     *
     * @param message The message to define correlation data for
     * @return the data to attach as correlation data to generated messages
     */
    Map<String, ?> correlationDataFor(Message<?> message);

}
