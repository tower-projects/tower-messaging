package io.iamcyw.tower.schema.creator.type;

import io.iamcyw.tower.schema.model.Reference;
import org.jboss.jandex.ClassInfo;

/**
 * Something that can create object types on the schema
 *
 * @param <T> the created type
 */
public interface Creator<T> {

    T create(ClassInfo classInfo, Reference reference);

}
