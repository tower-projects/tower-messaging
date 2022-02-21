package io.iamcyw.tower.quarkus.deployment;

import io.iamcyw.tower.messaging.BeanFactory;
import io.iamcyw.tower.messaging.MessageBean;
import io.iamcyw.tower.messaging.handle.DefaultMessageHandle;
import io.iamcyw.tower.messaging.handle.MessageHandle;
import io.iamcyw.tower.messaging.handle.MessageHandlerGroup;
import io.iamcyw.tower.messaging.handle.MessageHandlerGroups;
import io.iamcyw.tower.messaging.handle.resolve.*;
import io.iamcyw.tower.messaging.predicate.DefaultMessageHandlePredicate;
import io.iamcyw.tower.messaging.predicate.DefaultMessageHandlePredicateWrapper;
import io.iamcyw.tower.messaging.predicate.MessageHandlePredicate;
import io.iamcyw.tower.messaging.predicate.TrueMessageHandlePredicate;
import io.iamcyw.tower.quarkus.runtime.MessageRecorder;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.Type;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MessageBeanFactory {

    private final List<ClassInfo> classInfos;

    private final MethodInvokerFactory invokerFactory;

    private final Map<String, MessageBean> messageBeans;

    private final Map<String, List<MessageHandle>> queries = new HashMap<>();

    private final Map<String, List<MessageHandle>> commands = new HashMap<>();

    private final Map<String, Map<String, MessageHandlePredicate>> predicateMap = new HashMap<>();

    private final Map<String, MessageHandlePredicate> globalPredicate = new HashMap<>();

    private final Function<String, BeanFactory<?>> beanFactoryCreator;

    private final Map<String, Class<?>> domainNameMapping = new HashMap<>();

    public MessageBeanFactory(List<ClassInfo> classInfos, MethodInvokerFactory invokerFactory,
                              Function<String, BeanFactory<?>> beanFactoryCreator) {
        this.classInfos = classInfos;
        this.messageBeans = new HashMap<>(classInfos.size());
        this.invokerFactory = invokerFactory;
        this.beanFactoryCreator = beanFactoryCreator;
    }

    public void scan() {
        scanPredicate();

        scanCommand();

        scanQuery();
    }

    private void scanQuery() {
        classInfos.stream()
                  .filter(classInfo -> classInfo.annotations().containsKey(MessageDotNames.QUERYHANDLER))
                  .forEach(this::scanQuery);
    }

    private MessageBean getMessageBean(String name) {
        return messageBeans.computeIfAbsent(name, (key) -> new MessageBean(key, beanFactoryCreator.apply(key)));
    }

    private void scanQuery(ClassInfo classInfo) {
        MessageBean messageBean = getMessageBean(classInfo.name().toString());

        List<MessageHandle> handles = classInfo.annotations()
                                               .get(MessageDotNames.QUERYHANDLER)
                                               .stream()
                                               .map(instance -> scanHandle(instance, messageBean))
                                               .toList();

        handles.forEach(messageHandle -> {
            List<MessageHandle> messageHandles = queries.getOrDefault(messageHandle.handleTarget, new ArrayList<>());
            messageHandles.add(messageHandle);
            queries.put(messageHandle.handleTarget, messageHandles);
        });

    }

    private MessageHandle scanHandle(AnnotationInstance instance, MessageBean messageBean) {
        MethodInfo methodInfo = instance.target().asMethod();

        String handleTarget = methodInfo.parameters().get(0).name().toString();

        domainNameMapping.computeIfAbsent(handleTarget, (key) -> MessageRecorder.loadClass(handleTarget));

        List<AnnotationInstance> predicateInstance = methodInfo.annotations(MessageDotNames.MESSAGE_PREDICATE);
        AnnotationInstance predicates = methodInfo.annotation(MessageDotNames.MESSAGE_PREDICATES);
        if (predicates != null) {
            predicateInstance.addAll(Arrays.stream(predicates.value("value").asNestedArray()).toList());
        }

        return new DefaultMessageHandle(handleTarget, buildPredicate(predicateInstance, handleTarget),
                                        invokerFactory.create(methodInfo.declaringClass(), methodInfo), messageBean,
                                        createResolver(methodInfo));
    }

    private MessageHandlePredicate buildPredicate(List<AnnotationInstance> predicateInstance, String handleTarget) {
        MessageHandlePredicate predicate = new TrueMessageHandlePredicate();

        Map<String, MessageHandlePredicate> messageHandlePredicates = predicateMap.getOrDefault(handleTarget,
                                                                                                new HashMap<>());
        if (!predicateInstance.isEmpty()) {
            for (AnnotationInstance instance : predicateInstance) {
                String name = instance.value("value").asString();
                String[] parameter = instance.value("parameter") != null ? instance.value("parameter")
                                                                                   .asStringArray() : new String[]{};

                MessageHandlePredicate handlePredicate = messageHandlePredicates.get(name);
                if (handlePredicate == null) {
                    throw new IllegalArgumentException(
                            "The Command: {" + handleTarget + "} with Predicate: {" + name + "} not found Handle");
                }
                predicate = predicate.and(new DefaultMessageHandlePredicateWrapper(handlePredicate, parameter));
            }
        }

        return predicate;
    }

    private void scanCommand() {
        classInfos.stream()
                  .filter(classInfo -> classInfo.annotations().containsKey(MessageDotNames.COMMANDHANDLER))
                  .forEach(this::scanCommand);
    }

    private void scanCommand(ClassInfo classInfo) {
        MessageBean messageBean = getMessageBean(classInfo.name().toString());

        List<MessageHandle> handles = classInfo.annotations()
                                               .get(MessageDotNames.COMMANDHANDLER)
                                               .stream()
                                               .map(instance -> scanHandle(instance, messageBean))
                                               .toList();

        handles.forEach(messageHandle -> {
            List<MessageHandle> messageHandles = commands.getOrDefault(messageHandle.handleTarget, new ArrayList<>());
            messageHandles.add(messageHandle);
            commands.put(messageHandle.handleTarget, messageHandles);
        });

    }

    private void scanPredicate() {
        classInfos.stream()
                  .filter(classInfo -> classInfo.annotations().containsKey(MessageDotNames.PREDICATEHANDLE))
                  .forEach(this::scanPredicate);
    }

    private void scanPredicate(ClassInfo classInfo) {
        MessageBean messageBean = getMessageBean(classInfo.name().toString());

        classInfo.annotations()
                 .get(MessageDotNames.PREDICATEHANDLE)
                 .forEach(annotationInstance -> scanPredicate(annotationInstance, messageBean));

    }

    private MessageHandlePredicate scanPredicate(AnnotationInstance instance, MessageBean messageBean) {
        MethodInfo methodInfo = instance.target().asMethod();

        String name = instance.value().asString();
        String predicateTarget = methodInfo.parameters().get(0).name().toString();

        MessageHandlePredicate messageHandlePredicate = new DefaultMessageHandlePredicate(name, invokerFactory.create(
                methodInfo.declaringClass(), methodInfo), messageBean, createPredicateResolver(methodInfo));

        Map<String, MessageHandlePredicate> predicates = predicateMap.computeIfAbsent(predicateTarget,
                                                                                      (key) -> new HashMap<>());
        predicates.put(name, messageHandlePredicate);

        return messageHandlePredicate;
    }

    private ParameterResolver<?>[] createPredicateResolver(MethodInfo methodInfo) {
        List<Type> parameters = methodInfo.parameters();
        ParameterResolver<?>[] resolver = new ParameterResolver<?>[parameters.size()];
        for (int i = 0; i < parameters.size(); i++) {
            Type parameter = parameters.get(i);
            if (i == 0) {
                resolver[i] = new PayloadParameterResolver(MessageRecorder.loadClass(parameter.name().toString()));
            }
            if (i == 1) {
                resolver[i] = new PredicateParameterResolver(MessageRecorder.loadClass(parameter.name().toString()));
            }
            if (parameter.hasAnnotation(MessageDotNames.METADATAVALUE)) {
                AnnotationInstance metaDataValue = parameter.annotation(MessageDotNames.METADATAVALUE);
                Class<?> payloadType = MessageRecorder.loadClass(parameter.name().toString());
                resolver[i] = new MetaDataValueParameterResolver<>(metaDataValue.value().asString(), payloadType);
            }
            if (parameter.asClassType().name().equals(MessageDotNames.METADATA)) {
                resolver[i] = new MetaDataParameterResolver();
            }
        }
        return resolver;
    }

    private ParameterResolver<?>[] createResolver(MethodInfo methodInfo) {
        List<Type> parameters = methodInfo.parameters();
        ParameterResolver<?>[] resolver = new ParameterResolver<?>[parameters.size()];
        for (int i = 0; i < parameters.size(); i++) {
            Type parameter = parameters.get(i);
            if (i == 0) {
                resolver[i] = new PayloadParameterResolver(MessageRecorder.loadClass(parameter.name().toString()));
            }
            if (parameter.hasAnnotation(MessageDotNames.METADATAVALUE)) {
                AnnotationInstance metaDataValue = parameter.annotation(MessageDotNames.METADATAVALUE);
                Class<?> payloadType = MessageRecorder.loadClass(parameter.name().toString());
                resolver[i] = new MetaDataValueParameterResolver<>(metaDataValue.value().asString(), payloadType);
            }
            if (parameter.asClassType().name().equals(MessageDotNames.METADATA)) {
                resolver[i] = new MetaDataParameterResolver();
            }
        }
        return resolver;
    }

    public Map<String, Class<?>> getDomainNameMapping() {
        return domainNameMapping;
    }

    public MessageHandlerGroups queryGroups() {
        List<MessageHandlerGroup> groups = new ArrayList<>(queries.size());
        queries.forEach((key, handles) -> groups.add(new MessageHandlerGroup(key, handles)));
        return new MessageHandlerGroups(groups);
    }

    public MessageHandlerGroups commandGroups() {
        List<MessageHandlerGroup> groups = new ArrayList<>(commands.size());
        commands.forEach((key, handles) -> groups.add(new MessageHandlerGroup(key, handles)));
        return new MessageHandlerGroups(groups);
    }

}
