package io.iamcyw.tower.quarkus.deployment;

import io.iamcyw.tower.messaging.cdi.producer.MessageProducer;
import io.iamcyw.tower.messaging.spi.LookupService;
import io.iamcyw.tower.quarkus.runtime.MessageRecorder;
import io.iamcyw.tower.schema.Annotations;
import io.iamcyw.tower.schema.ScanningContext;
import io.iamcyw.tower.schema.SchemaBuilder;
import io.iamcyw.tower.schema.creator.ArgumentCreator;
import io.iamcyw.tower.schema.creator.OperationCreator;
import io.iamcyw.tower.schema.creator.ReferenceCreator;
import io.iamcyw.tower.schema.model.Argument;
import io.iamcyw.tower.schema.model.Operation;
import io.iamcyw.tower.schema.model.Reference;
import io.iamcyw.tower.schema.model.Schema;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanContainerBuildItem;
import io.quarkus.arc.deployment.BeanDefiningAnnotationBuildItem;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.annotations.*;
import io.quarkus.deployment.builditem.*;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveHierarchyBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ServiceProviderBuildItem;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.RuntimeValue;
import org.jboss.jandex.Indexer;
import org.jboss.logging.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class MessageQuarkusProcessor {

    private static final Logger LOG = Logger.getLogger(MessageQuarkusProcessor.class);

    private static final String FEATURE = "tower";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void additionalBeanDefiningAnnotation(
            BuildProducer<BeanDefiningAnnotationBuildItem> beanDefiningAnnotationProducer) {
        // Make ArC discover the beans marked with the @GraphQlApi qualifier
        beanDefiningAnnotationProducer.produce(new BeanDefiningAnnotationBuildItem(Annotations.USECASE));
    }

    @BuildStep
    void additionalBean(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {
        additionalBeanProducer.produce(
                AdditionalBeanBuildItem.builder().addBeanClass(MessageProducer.class).setUnremovable().build());
    }

    @BuildStep
    void registerNativeImageResources(BuildProducer<ServiceProviderBuildItem> serviceProvider) {
        // Lookup Service (We use the one from the CDI Module)
        serviceProvider.produce(ServiceProviderBuildItem.allProvidersFromClassPath(LookupService.class.getName()));
    }

    @BuildStep
    TowerMessageIndexBuildItem createIndex(TransformedClassesBuildItem transformedClassesBuildItem) {
        Map<String, byte[]> modifiedClasses = new HashMap<>();
        Map<Path, Set<TransformedClassesBuildItem.TransformedClass>> transformedClassesByJar =
                transformedClassesBuildItem.getTransformedClassesByJar();
        for (Map.Entry<Path, Set<TransformedClassesBuildItem.TransformedClass>> transformedClassesByJarEntrySet :
                transformedClassesByJar.entrySet()) {

            Set<TransformedClassesBuildItem.TransformedClass> transformedClasses =
                    transformedClassesByJarEntrySet.getValue();
            for (TransformedClassesBuildItem.TransformedClass transformedClass : transformedClasses) {
                modifiedClasses.put(transformedClass.getClassName(), transformedClass.getData());
            }
        }
        return new TowerMessageIndexBuildItem(modifiedClasses);
    }

    @Record(ExecutionTime.STATIC_INIT)
    @BuildStep
    void buildExecutionService(BuildProducer<ReflectiveClassBuildItem> reflectiveClassProducer,
                               BuildProducer<GeneratedClassBuildItem> generatedClassBuildItemBuildProducer,
                               BuildProducer<ReflectiveHierarchyBuildItem> reflectiveHierarchyProducer,
                               BuildProducer<TowerMessageSchemaBuildItem> towerMessageSchemaBuildItemBuildProducer,
                               MessageRecorder recorder, TowerMessageIndexBuildItem towerMessageIndexBuildItem,
                               BeanContainerBuildItem beanContainer, CombinedIndexBuildItem combinedIndex) {

        Indexer indexer = new Indexer();
        Map<String, byte[]> modifiedClasses = towerMessageIndexBuildItem.getModifiedClases();

        for (Map.Entry<String, byte[]> kv : modifiedClasses.entrySet()) {
            if (kv.getKey() != null && kv.getValue() != null) {
                try (ByteArrayInputStream bais = new ByteArrayInputStream(kv.getValue())) {
                    indexer.index(bais);
                } catch (IOException ex) {
                    LOG.warn("Could not index [" + kv.getKey() + "] - " + ex.getMessage());
                }
            }
        }

        OverridableIndex overridableIndex = OverridableIndex.create(combinedIndex.getIndex(), indexer.complete());

        ReferenceCreator referenceCreator = new ReferenceCreator();
        ArgumentCreator argumentCreator = new ArgumentCreator(referenceCreator);
        MethodInvokerFactory methodInvokerFactory = new MethodInvokerFactory(generatedClassBuildItemBuildProducer);
        OperationCreator operationCreator = new OperationCreator(referenceCreator, argumentCreator,
                                                                 methodInvokerFactory::create);

        SchemaBuilder schemaBuilder = new SchemaBuilder(referenceCreator,operationCreator);
        ScanningContext.register(overridableIndex);
        Schema schema = schemaBuilder.generateSchema();

        RuntimeValue<Boolean> initialized = recorder.createMessageService(beanContainer.getValue(), schema);

        towerMessageSchemaBuildItemBuildProducer.produce(new TowerMessageSchemaBuildItem(schema));

        // Make sure the complex object from the application can work in native mode
        reflectiveClassProducer.produce(new ReflectiveClassBuildItem(true, true, getSchemaJavaClasses(schema)));
    }

    @Record(ExecutionTime.RUNTIME_INIT)
    @BuildStep
    @Consume(BeanContainerBuildItem.class)
    void buildExecutionEndpoint(MessageRecorder recorder, ShutdownContextBuildItem shutdownContext,
                                LaunchModeBuildItem launchMode) {

        /*
         * <em>Ugly Hack</em>
         * In dev mode, we pass a classloader to use in the CDI Loader.
         * This hack is required because using the TCCL would get an outdated version - the initial one.
         * This is because the worker thread on which the handler is called captures the TCCL at creation time
         * and does not allow updating it.
         *
         * In non dev mode, the TCCL is used.
         */
        if (launchMode.getLaunchMode() == LaunchMode.DEVELOPMENT) {
            recorder.setupClDevMode(shutdownContext);
        }
    }

    private String[] getSchemaJavaClasses(Schema schema) {
        // Unique list of classes we need to do reflection on
        Set<String> classes = new HashSet<>();

        classes.addAll(getOperationClassNames(schema.getQueries()));

        classes.addAll(getOperationClassNames(schema.getCommands()));

        return classes.toArray(new String[]{});
    }

    private Set<String> getOperationClassNames(Set<Operation> operations) {
        Set<String> classes = new HashSet<>();
        for (Operation operation : operations) {
            classes.add(operation.getClassName());
            for (Argument argument : operation.getArguments()) {
                classes.addAll(getAllReferenceClasses(argument.getReference()));
            }
            classes.addAll(getAllReferenceClasses(operation.getReference()));
        }
        return classes;
    }

    private Set<String> getAllReferenceClasses(Reference reference) {
        Set<String> classes = new HashSet<>();
        if (reference.getClassName().equals("void")) {
            return classes;
        }
        classes.add(reference.getClassName());
        if (reference.getParametrizedTypeArguments() != null && !reference.getParametrizedTypeArguments().isEmpty()) {

            Collection<Reference> parametrized = reference.getParametrizedTypeArguments().values();
            for (Reference r : parametrized) {
                classes.addAll(getAllReferenceClasses(r));
            }
        }
        return classes;
    }


}
