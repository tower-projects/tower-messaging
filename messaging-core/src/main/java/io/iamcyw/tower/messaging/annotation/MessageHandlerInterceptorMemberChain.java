/*
 * Copyright (c) 2010-2020. Axon Framework
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

package io.iamcyw.tower.messaging.annotation;


import io.iamcyw.tower.messaging.Message;

/**
 * Interface to interact with a MessageHandlingMember instance through a chain of interceptors, which were
 * used to build up this chain. Unlike regular handlers, interceptors have the ability to act on messages on their
 * way to the regular handler, and have the ability to block these messages.
 *
 * @param <T> The type that declares the handlers in this chain
 */
public interface MessageHandlerInterceptorMemberChain<T> {

    /**
     * Handle the given {@code message} by passing it through the interceptors and ultimately to the given
     * {@code handler} on the given {@code target} instance. The result of this invocation is the result as given by the
     * {@code handler}, possibly modified by any of the interceptors in this chain.
     *
     * @param message The message to pass through the interceptor chain
     * @param target  The target instance to invoke the interceptors and handlers on
     * @param handler The actual handler to invoke once all interceptors have received the message
     * @return the result as returned by the handlers or interceptors
     * @throws Exception any exception thrown by the handler or any of the interceptors
     */
    Object handle(Message<?> message, T target, MessageHandlingMember<? super T> handler) throws Exception;

}
