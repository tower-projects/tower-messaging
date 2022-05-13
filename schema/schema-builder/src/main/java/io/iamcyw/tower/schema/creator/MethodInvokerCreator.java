package io.iamcyw.tower.schema.creator;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.MethodInfo;

@FunctionalInterface
public interface MethodInvokerCreator {

    String create(ClassInfo classInfo, MethodInfo info);

}
