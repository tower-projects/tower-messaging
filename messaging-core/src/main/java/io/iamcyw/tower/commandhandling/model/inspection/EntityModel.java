/*
 * Copyright (c) 2010-2017. Axon Framework
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

package io.iamcyw.tower.commandhandling.model.inspection;

import io.iamcyw.tower.commandhandling.CommandMessage;
import io.iamcyw.tower.commandhandling.NoHandlerForCommandException;
import io.iamcyw.tower.messaging.annotation.MessageHandlingMember;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;

/**
 * Interface of an entity model that describes the properties and capabilities of an entity of type {@code T}. The
 * entity may be child entity or an aggregate root.
 *
 * @param <T> The type of entity described by this model
 */
public interface EntityModel<T> {

    /**
     * Get the name of the routing key property on commands and events that provides the identifier that should be used
     * to target entities of this kind.
     *
     * @return The name of the routing key that holds the identifier used to target this sort of entity
     */
    String routingKey();

    /**
     * Get a mapping of {@link MessageHandlingMember} to command name (obtained via {@link
     * CommandMessage#getCommandName()}).
     *
     * @return Map of message handler to command name
     */
    Map<String, MessageHandlingMember<? super T>> commandHandlers();

    /**
     * Gets a list of command handler interceptors for this entity.
     *
     * @return list of command handler interceptors
     */
    List<MessageHandlingMember<? super T>> commandHandlerInterceptors();

    /**
     * Get the {@link MessageHandlingMember} capable of handling commands with given {@code commandName} (see {@link
     * CommandMessage#getCommandName()}). If the entity is not capable of handling
     * such commands an exception is raised.
     *
     * @param commandName The name of the command
     * @return The handler for the command
     * @throws NoHandlerForCommandException In case the entity is not capable of handling commands of given name
     */
    default MessageHandlingMember<? super T> commandHandler(String commandName) {
        MessageHandlingMember<? super T> handler = commandHandlers().get(commandName);
        if (handler == null) {
            throw new NoHandlerForCommandException(format("No handler available to handle command [%s]", commandName));
        }
        return handler;
    }

    /**
     * Returns the class this model describes
     *
     * @return the class this model describes
     */
    Class<? extends T> entityClass();
}
