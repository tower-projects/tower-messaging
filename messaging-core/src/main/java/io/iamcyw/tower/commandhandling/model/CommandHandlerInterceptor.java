/*
 * Copyright (c) 2010-2018. Axon Framework
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

package io.iamcyw.tower.commandhandling.model;


import io.iamcyw.tower.commandhandling.CommandMessage;
import io.iamcyw.tower.messaging.annotation.MessageHandler;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@MessageHandler(messageType = CommandMessage.class)
public @interface CommandHandlerInterceptor {

    /**
     * Will filter commands which names match this pattern and invoke handler only with those commands.
     *
     * @return pattern used to filter command names
     */
    String commandNamePattern() default ".*";

}
