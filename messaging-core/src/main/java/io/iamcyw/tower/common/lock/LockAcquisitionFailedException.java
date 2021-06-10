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

package io.iamcyw.tower.common.lock;


import io.iamcyw.tower.common.SystemTransientException;

/**
 * Exception indicating that a lock could not be obtained. Typically, operations failing with this exception can be
 * retried without any intervention.
 *
 */
public class LockAcquisitionFailedException extends SystemTransientException {

    private static final long serialVersionUID = 4453369833513201587L;

    /**
     * Initialize the exception with given {@code message} and {@code cause}
     *
     * @param message The message describing the exception
     * @param cause   The underlying cause of the exception
     */
    public LockAcquisitionFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Initialize the exception with given {@code message}.
     *
     * @param message The message describing the exception
     */
    public LockAcquisitionFailedException(String message) {
        super(message);
    }
}
