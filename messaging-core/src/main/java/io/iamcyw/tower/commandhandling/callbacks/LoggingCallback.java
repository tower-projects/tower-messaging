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

package io.iamcyw.tower.commandhandling.callbacks;

import io.iamcyw.tower.commandhandling.CommandCallback;
import io.iamcyw.tower.commandhandling.CommandMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CommandCallback implementation that simply logs the results of a command.
 *
 * @author Allard Buijze
 * @since 2.0
 */
public class LoggingCallback implements CommandCallback<Object, Object> {

    /**
     * The singleton instance for this callback
     */
    public static final LoggingCallback INSTANCE = new LoggingCallback();

    private static final Logger logger = LoggerFactory.getLogger(LoggingCallback.class);

    private LoggingCallback() {
    }

    @Override
    public void onSuccess(CommandMessage message, Object result) {
        logger.info("Command executed successfully: {}", message.getCommandName());
    }

    @Override
    public void onFailure(CommandMessage<?> message, Throwable cause) {
        logger.warn("Command resulted in exception: {}", message.getCommandName(), cause);
    }

}
