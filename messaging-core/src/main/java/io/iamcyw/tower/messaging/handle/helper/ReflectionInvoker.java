package io.iamcyw.tower.messaging.handle.helper;

import io.iamcyw.tower.messaging.spi.ClassloadingService;
import io.iamcyw.tower.messaging.spi.LookupService;
import io.iamcyw.tower.messaging.spi.ManagedInstance;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import static io.iamcyw.tower.messaging.TowerMessageServerMessages.msg;

public class ReflectionInvoker {

    private final LookupService lookupService = LookupService.get();

    private final ClassloadingService classloadingService = ClassloadingService.get();

    private final Class<?> operationClass;

    private Method method;

    public ReflectionInvoker(String className) {
        this.operationClass = classloadingService.loadClass(className);
    }

    public ReflectionInvoker(String className, String methodName, List<String> parameterClasses) {
        this.operationClass = classloadingService.loadClass(className);
        this.setMethod(methodName, parameterClasses);
    }

    public void setMethod(String methodName, List<String> parameterClasses) {
        this.method = lookupMethod(operationClass, methodName, parameterClasses);
    }

    protected <R> R invoke(Object... arguments) {
        try {
            ManagedInstance<?> operationInstance = lookupService.getInstance(operationClass);
            Object operationInstance1 = operationInstance.get();
            return (R) method.invoke(operationInstance1, arguments);
        } catch (InvocationTargetException ex) {
            //Invoked method has thrown something, unwrap
            Throwable throwable = ex.getCause();

            if (throwable instanceof Error) {
                throw (Error) throwable;
            } else {
                throw msg.generalMessageHandleException(operationClass.getName() + ": " + method.getName(), throwable);
            }
        } catch (Throwable throwable) {
            if (throwable instanceof Error) {
                throw (Error) throwable;
            } else {
                throw msg.generalMessageHandleException(operationClass.getName() + ": " + method.getName(), throwable);
            }
        }
    }

    private Method lookupMethod(Class<?> operationClass, String methodName, List<String> parameterClasses) {
        try {
            return operationClass.getMethod(methodName, getParameterClasses(parameterClasses));
        } catch (NoSuchMethodException e) {
            throw msg.generalMessageHandleException(operationClass.getName() + ": " + methodName, e);
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

}
