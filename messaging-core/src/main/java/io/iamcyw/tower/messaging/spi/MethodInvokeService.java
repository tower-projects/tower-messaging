package io.iamcyw.tower.messaging.spi;

import io.iamcyw.tower.messaging.handle.helper.MethodInvoker;
import io.iamcyw.tower.schema.model.Field;
import io.iamcyw.tower.schema.model.Operation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

import static io.iamcyw.tower.messaging.TowerMessageServerLogging.log;
import static io.iamcyw.tower.messaging.TowerMessageServerMessages.msg;

public interface MethodInvokeService {

    ServiceLoader<MethodInvokeService> methodInvokeServices = ServiceLoader.load(MethodInvokeService.class);

    MethodInvokeService methodInvokeService = load();

    static MethodInvokeService get() {
        return methodInvokeService;
    }

    static MethodInvokeService load() {
        MethodInvokeService cls;
        try {
            cls = methodInvokeServices.iterator().next();
        } catch (Exception ex) {
            cls = new DefaultMethodInvokeService();
        }
        log.usingMethodInvokeService(cls.getName());
        return cls;
    }

    String getName();

    MethodInvoker get(Operation operation);

    class DefaultMethodInvokeService implements MethodInvokeService {
        private final ClassloadingService classloadingService = ClassloadingService.load();

        @Override
        public String getName() {
            return "Default";
        }

        @Override
        public MethodInvoker get(Operation operation) {
            try {
                if (operation.getInvoke() == null) {
                    Class<?> clazz = classloadingService.loadClass(operation.getClassName());
                    return new DefaultMethodInvoker(clazz, getParameterClasses(getParameterClasses(operation)),
                                                    operation);
                }
                return (MethodInvoker) classloadingService.loadClass(operation.getInvoke()).getDeclaredConstructor()
                                                          .newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private Class<?>[] getParameterClasses(List<String> parameterClasses) {
            if (parameterClasses != null && !parameterClasses.isEmpty()) {
                List<Class<?>> cl = new LinkedList<>();
                for (String className : parameterClasses) {
                    cl.add(classloadingService.loadClass(className));
                }

                return cl.toArray(new Class[]{});
            }
            return null;
        }

        private List<String> getParameterClasses(Operation operation) {
            if (operation.hasArguments()) {
                List<String> cl = new LinkedList<>();
                for (Field argument : operation.getArguments()) {
                    if (argument.hasWrapper()) {
                        cl.add(argument.getWrapper().getWrapperClassName());
                    } else {
                        cl.add(argument.getReference().getClassName());
                    }
                }
                return cl;
            }
            return null;
        }

    }

    class DefaultMethodInvoker implements MethodInvoker {

        private Method method;

        public DefaultMethodInvoker(Class<?> operationClass, Class<?>[] parameterClasses, Operation operation) {
            this.method = lookupMethod(operationClass, operation.getMethodName(), parameterClasses);
        }

        private Method lookupMethod(Class<?> operationClass, String methodName, Class<?>[] parameterClasses) {
            try {
                return operationClass.getMethod(methodName, parameterClasses);
            } catch (NoSuchMethodException e) {
                throw msg.generalMessageHandleException(operationClass.getName() + ": " + methodName, e);
            }
        }

        @Override
        public <R> R invoke(Object instance, Object[] args) {
            try {
                return (R) method.invoke(instance, args);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
