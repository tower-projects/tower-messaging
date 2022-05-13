package io.iamcyw.tower.quarkus.deployment;

import io.iamcyw.tower.messaging.handle.helper.MethodInvoker;
import io.quarkus.deployment.GeneratedClassGizmoAdaptor;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.builditem.GeneratedClassBuildItem;
import io.quarkus.gizmo.ClassCreator;
import io.quarkus.gizmo.MethodCreator;
import io.quarkus.gizmo.ResultHandle;
import io.quarkus.runtime.util.HashUtil;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.Type;

import java.lang.reflect.Modifier;

public class MethodInvokerFactory {

    private final BuildProducer<GeneratedClassBuildItem> generatedClassBuildItemBuildProducer;


    public MethodInvokerFactory(BuildProducer<GeneratedClassBuildItem> generatedClassBuildItemBuildProducer) {
        this.generatedClassBuildItemBuildProducer = generatedClassBuildItemBuildProducer;
    }

    public String create(ClassInfo currentClassInfo, MethodInfo info) {

        StringBuilder sigBuilder = new StringBuilder();

        String methodName = info.name();
        String commandName = info.parameters().get(0).name().withoutPackagePrefix();
        sigBuilder.append(methodName).append(info.returnType().name().withoutPackagePrefix());

        for (Type parameter : info.parameters()) {
            sigBuilder.append(parameter.name().toString());
        }
        String baseName = currentClassInfo.name() + "$" + methodName + "$" + commandName + "_" +
                HashUtil.sha1(sigBuilder.toString());
        try (ClassCreator classCreator = new ClassCreator(
                new GeneratedClassGizmoAdaptor(generatedClassBuildItemBuildProducer, true), baseName, null,
                Object.class.getName(), MethodInvoker.class.getName())) {
            MethodCreator mc = classCreator.getMethodCreator("invoke", Object.class, Object.class, Object[].class);
            ResultHandle[] args = new ResultHandle[info.parameters().size()];
            ResultHandle message = mc.getMethodParam(1);
            for (int i = 0; i < info.parameters().size(); ++i) {
                args[i] = mc.readArrayValue(message, i);
            }
            ResultHandle res;
            if (Modifier.isInterface(currentClassInfo.flags())) {
                res = mc.invokeInterfaceMethod(info, mc.getMethodParam(0), args);
            } else {
                res = mc.invokeVirtualMethod(info, mc.getMethodParam(0), args);
            }
            if (info.returnType().kind() == Type.Kind.VOID) {
                mc.returnValue(mc.loadNull());
            } else {
                mc.returnValue(res);
            }
        }
        return baseName;
    }

}
