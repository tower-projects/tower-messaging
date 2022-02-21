package io.iamcyw.tower.quarkus.deployment;

import io.iamcyw.tower.quarkus.runtime.MessageRecorder;
import io.iamcyw.tower.quarkus.runtime.producer.MessageProducer;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanContainerBuildItem;
import io.quarkus.arc.deployment.UnremovableBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.GeneratedClassBuildItem;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class MessageQuarkusProcessor {

    private static final String FEATURE = "tower-quarkus";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void scanResources(CombinedIndexBuildItem combinedIndexBuildItem,
                       BuildProducer<MessageClassBuildItem> messageBeanBuildItemBuildProducer) {
        Collection<ClassInfo> knowClasses = combinedIndexBuildItem.getComputingIndex().getKnownClasses();

        List<ClassInfo> messageClasses = knowClasses.stream().filter(classInfo -> {
            Set<DotName> annotations = classInfo.annotations().keySet();
            return annotations.contains(MessageDotNames.COMMANDHANDLER) ||
                    annotations.contains(MessageDotNames.QUERYHANDLER) ||
                    annotations.contains(MessageDotNames.PREDICATEHANDLE);
        }).collect(Collectors.toList());

        messageBeanBuildItemBuildProducer.produce(new MessageClassBuildItem(messageClasses));
    }

    @BuildStep
    public void unremoveableBeans(Optional<MessageClassBuildItem> handlerScanningResultBuildItem,
                                  BuildProducer<UnremovableBeanBuildItem> unremoveableBeans) {
        if (!handlerScanningResultBuildItem.isPresent()) {
            return;
        }
        Set<String> beanParams = handlerScanningResultBuildItem.get().messageClasses.stream().map(ClassInfo::name)
                                                                                    .map(DotName::toString)
                                                                                    .collect(Collectors.toSet());
        unremoveableBeans.produce(UnremovableBeanBuildItem.beanClassNames(beanParams));
    }

    @BuildStep
    AdditionalBeanBuildItem additionalReactorConfigureBeans() {
        return new AdditionalBeanBuildItem(MessageProducer.class);
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    void scanMethod(BuildProducer<GeneratedClassBuildItem> generatedClassBuildItemBuildProducer,
                    BeanContainerBuildItem beanContainer, MessageClassBuildItem messageBeanBuildItem,
                    MessageRecorder recorder) {
        MessageBeanFactory messageBeanFactory = new MessageBeanFactory(messageBeanBuildItem.messageClasses,
                                                                       new MethodInvokerFactory(
                                                                               generatedClassBuildItemBuildProducer,
                                                                               recorder),
                                                                       (name) -> recorder.factory(name,
                                                                                                  beanContainer.getValue()));
        messageBeanFactory.scan();


        recorder.config(beanContainer.getValue(), messageBeanFactory.queryGroups(), messageBeanFactory.commandGroups(),
                        messageBeanFactory.getDomainNameMapping());
    }

}
