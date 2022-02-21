package io.iamcyw.tower.quarkus.deployment;

import io.iamcyw.tower.messaging.handle.MethodInvoker;
import io.iamcyw.tower.quarkus.runtime.MessageRecorder;
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
import java.util.function.Supplier;

public class MethodInvokerFactory {

    private final BuildProducer<GeneratedClassBuildItem> generatedClassBuildItemBuildProducer;

    private final MessageRecorder recorder;

    public MethodInvokerFactory(BuildProducer<GeneratedClassBuildItem> generatedClassBuildItemBuildProducer,
                                MessageRecorder recorder) {
        this.generatedClassBuildItemBuildProducer = generatedClassBuildItemBuildProducer;
        this.recorder = recorder;
    }

    public Supplier<MethodInvoker> create(ClassInfo currentClassInfo, MethodInfo info) {

        StringBuilder sigBuilder = new StringBuilder();

        String handleName = info.parameters().get(0).name().toString();
        sigBuilder.append(info.name());
        sigBuilder.append(handleName).append(info.returnType().name().toString());

        for (Type parameter : info.parameters()) {
            sigBuilder.append(parameter.name().toString());
        }
        String baseName = currentClassInfo.name() + "$quarkustowerinvoker$" + handleName + "_" +
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
        return recorder.invoker(baseName);
    }

}
