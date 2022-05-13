package io.iamcyw.tower.schema.creator.type;

import io.iamcyw.tower.schema.Annotations;
import io.iamcyw.tower.schema.creator.OperationCreator;
import io.iamcyw.tower.schema.creator.ReferenceCreator;
import io.iamcyw.tower.schema.helper.DescriptionHelper;
import io.iamcyw.tower.schema.helper.TypeNameHelper;
import io.iamcyw.tower.schema.model.Reference;
import io.iamcyw.tower.schema.model.ReferenceType;
import io.iamcyw.tower.schema.model.Type;
import org.jboss.jandex.ClassInfo;
import org.jboss.logging.Logger;

abstract class AbstractCreator implements Creator<Type> {
    private static final Logger LOG = Logger.getLogger(AbstractCreator.class.getName());

    private final OperationCreator operationCreator;

    private final ReferenceCreator referenceCreator;

    protected AbstractCreator(OperationCreator operationCreator, ReferenceCreator referenceCreator) {
        this.operationCreator = operationCreator;
        this.referenceCreator = referenceCreator;
    }


    protected abstract ReferenceType referenceType();

    @Override
    public Type create(ClassInfo classInfo, Reference reference) {
        LOG.debug("Creating from " + classInfo.name().toString());

        Annotations annotations = Annotations.getAnnotationsForClass(classInfo);

        // Name
        String name = TypeNameHelper.getAnyTypeName(reference, referenceType(), classInfo, annotations);

        // Description
        String description = DescriptionHelper.getDescriptionForType(annotations).orElse(null);

        Type type = new Type(classInfo.name().toString(), name, description);
        // type.setIsInterface(referenceType() == ReferenceType.INTERFACE);

        // Fields
        addFields(type, classInfo, reference);

        // Interfaces
        // addInterfaces(type, classInfo, reference);

        // Operations
        // addOperations(type, classInfo);

        // Directives
        // addDirectives(type, classInfo);

        return type;
    }

    protected abstract void addFields(Type type, ClassInfo classInfo, Reference reference);

}
