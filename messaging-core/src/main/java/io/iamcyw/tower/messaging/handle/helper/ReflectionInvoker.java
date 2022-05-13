package io.iamcyw.tower.messaging.handle.helper;

import io.iamcyw.tower.messaging.spi.ClassloadingService;
import io.iamcyw.tower.messaging.spi.LookupService;
import io.iamcyw.tower.messaging.spi.ManagedInstance;
import io.iamcyw.tower.messaging.spi.MethodInvokeService;
import io.iamcyw.tower.schema.model.Operation;

import static io.iamcyw.tower.messaging.TowerMessageServerMessages.msg;

public class ReflectionInvoker {

    private final LookupService lookupService = LookupService.get();

    private final MethodInvokeService methodInvokeService = MethodInvokeService.get();

    private final ClassloadingService classloadingService = ClassloadingService.load();

    private final Operation operation;

    private MethodInvoker methodInvoker;

    private Class<?> operationClass;

    public ReflectionInvoker(Operation operation) {
        this.operation = operation;
        this.methodInvoker = methodInvokeService.get(operation);
        this.operationClass = classloadingService.loadClass(operation.getClassName());
    }

    protected <R> R invoke(Object[] arguments) {
        try {
            ManagedInstance<?> operationInstance = lookupService.getInstance(operationClass);
            Object operationInstance1 = operationInstance.get();
            return (R) methodInvoker.invoke(operationInstance1, arguments);
        } catch (Throwable throwable) {
            if (throwable instanceof Error) {
                throw (Error) throwable;
            } else {
                throw msg.generalMessageHandleException(operation.getName(), throwable);
            }
        }
    }

}
