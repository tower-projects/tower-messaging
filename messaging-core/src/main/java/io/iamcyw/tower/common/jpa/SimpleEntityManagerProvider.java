/*
 * Copyright (c) 2010-2014. Axon Framework
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

package io.iamcyw.tower.common.jpa;

import io.iamcyw.tower.utils.Assert;
import io.iamcyw.tower.utils.i18n.I18ns;

import javax.persistence.EntityManager;

/**
 * Simple implementation of the EntityManagerProvider that returns the EntityManager instance provided at construction
 * time.
 */
public class SimpleEntityManagerProvider implements EntityManagerProvider {

    private final EntityManager entityManager;

    /**
     * Initializes an instance that always returns the given {@code entityManager}. This class can be used for
     * testing, or when using a ContainerManaged EntityManager.
     *
     * @param entityManager the EntityManager to return on {@link #getEntityManager()}
     */
    public SimpleEntityManagerProvider(EntityManager entityManager) {
        Assert.nonNull(entityManager, I18ns.create().content("entityManager should not be null").apply());
        this.entityManager = entityManager;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

}
