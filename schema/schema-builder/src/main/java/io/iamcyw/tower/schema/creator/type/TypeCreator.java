package io.iamcyw.tower.schema.creator.type;

import io.iamcyw.tower.schema.Classes;
import io.iamcyw.tower.schema.creator.FieldCreator;
import io.iamcyw.tower.schema.creator.OperationCreator;
import io.iamcyw.tower.schema.creator.ReferenceCreator;
import io.iamcyw.tower.schema.helper.Direction;
import io.iamcyw.tower.schema.helper.MethodHelper;
import io.iamcyw.tower.schema.model.Reference;
import io.iamcyw.tower.schema.model.ReferenceType;
import io.iamcyw.tower.schema.model.Type;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.MethodInfo;

import java.util.*;

/**
 * This creates a type object.
 * <p>
 * The type object has fields that might reference other types that should still be created. It might also implement
 * some interfaces that should be created. It might also have some operations that reference other types that should
 * still be created.
 */
public class TypeCreator extends AbstractCreator {

    private final FieldCreator fieldCreator;

    public TypeCreator(ReferenceCreator referenceCreator, FieldCreator fieldCreator,
                       OperationCreator operationCreator) {
        super(operationCreator, referenceCreator);
        this.fieldCreator = fieldCreator;
    }

    protected ReferenceType referenceType() {
        return ReferenceType.TYPE;
    }

    @Override
    protected void addFields(Type type, ClassInfo classInfo, Reference reference) {
        // Fields
        List<MethodInfo> allMethods = new ArrayList<>();
        Map<String, FieldInfo> allFields = new HashMap<>();

        // Find all methods and properties up the tree
        // for (ClassInfo c = classInfo; c != null; c = ScanningContext.getIndex().getClassByName(c.superName())) {
        //     if (InterfaceCreator.canAddInterfaceIntoScheme(c.toString())) { // Not java objects
        //         allMethods.addAll(c.methods());
        //         for (FieldInfo fieldInfo : c.fields()) {
        //             allFields.putIfAbsent(fieldInfo.name(), fieldInfo);
        //         }
        //     }
        // }

        for (MethodInfo methodInfo : allMethods) {
            if (MethodHelper.isPropertyMethod(Direction.OUT, methodInfo)) {
                String fieldName = MethodHelper.getPropertyName(Direction.OUT, methodInfo.name());
                FieldInfo fieldInfo = allFields.remove(fieldName);
                fieldCreator.createFieldForPojo(Direction.OUT, fieldInfo, methodInfo, reference)
                            .ifPresent(type::addField);
            }
        }

        if (Objects.equals(classInfo.superName(), Classes.RECORD)) {
            // Each record component has an accessor method
            // We check these after getters, so that getters are preferred, e.g. if they have been inherited by an
            // interface
            for (FieldInfo fieldInfo : allFields.values()) {
                MethodInfo methodInfo = classInfo.method(fieldInfo.name());
                fieldCreator.createFieldForPojo(Direction.OUT, fieldInfo, methodInfo, reference)
                            .ifPresent(type::addField);
            }
        } else {
            // See what fields are left (this is fields without methods)
            for (FieldInfo fieldInfo : allFields.values()) {
                fieldCreator.createFieldForPojo(Direction.OUT, fieldInfo, reference).ifPresent(type::addField);
            }
        }
    }

}
