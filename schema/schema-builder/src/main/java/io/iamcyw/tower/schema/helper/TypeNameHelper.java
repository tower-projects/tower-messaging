package io.iamcyw.tower.schema.helper;

import io.iamcyw.tower.schema.Annotations;
import io.iamcyw.tower.schema.model.Reference;
import io.iamcyw.tower.schema.model.ReferenceType;
import org.jboss.jandex.ClassInfo;

import java.util.Map;

public class TypeNameHelper {

    private static final String UNDERSCORE = "_";

    public static String createParametrizedTypeNameExtension(
            Map<String, Reference> parametrizedTypeArgumentsReferences) {
        if (parametrizedTypeArgumentsReferences == null || parametrizedTypeArgumentsReferences.isEmpty())
            return null;
        StringBuilder sb = new StringBuilder();
        for (Reference gp : parametrizedTypeArgumentsReferences.values()) {
            appendParametrizedArgumet(sb, gp);
        }
        return sb.toString();
    }

    private static final void appendParametrizedArgumet(StringBuilder sb, Reference gp) {
        sb.append(UNDERSCORE);
        sb.append(gp.getName());
    }

    // public static String getAnyTypeName(String parametrizedTypeNameExtension, ReferenceType referenceType,
    //                                     ClassInfo classInfo, Annotations annotationsForThisClass) {
    //     return classInfo.name().withoutPackagePrefix();
    // }

    public static String createParametrizedTypeNameExtension(Reference reference) {
        if (!reference.isAddParametrizedTypeNameExtension() || reference.getParametrizedTypeArguments() == null ||
                reference.getParametrizedTypeArguments().isEmpty())
            return null;
        StringBuilder sb = new StringBuilder();
        for (Reference gp : reference.getParametrizedTypeArguments().values()) {
            sb.append("_");
            sb.append(gp.getName());
        }
        return sb.toString();
    }

    public static String getAnyTypeName(Reference reference, ReferenceType referenceType, ClassInfo classInfo,
                                        Annotations annotationsForThisClass) {
        String parametrizedTypeNameExtension = createParametrizedTypeNameExtension(reference);
        return getAnyTypeName(parametrizedTypeNameExtension, referenceType, classInfo, annotationsForThisClass);
    }

    public static String getAnyTypeName(String parametrizedTypeNameExtension, ReferenceType referenceType,
                                        ClassInfo classInfo, Annotations annotationsForThisClass) {
        // if (Classes.isEnum(classInfo)) {
        //     return getNameForClassType(classInfo, annotationsForThisClass, Annotations.ENUM,
        //     parametrizedTypeNameExtension);
        // } else if (Classes.isInterface(classInfo)) {
        //     return getNameForClassType(classInfo, annotationsForThisClass, Annotations.INTERFACE,
        //     parametrizedTypeNameExtension);
        // } else if (referenceType.equals(ReferenceType.TYPE)) {
        //     return getNameForClassType(classInfo, annotationsForThisClass, Annotations.TYPE,
        //     parametrizedTypeNameExtension);
        // } else if (referenceType.equals(INPUT)) {
        //     return getNameForClassType(classInfo, annotationsForThisClass, Annotations.INPUT,
        //     parametrizedTypeNameExtension,
        //                                INPUT);
        // } else if (referenceType.equals(ReferenceType.SCALAR)) {
        //     return classInfo.name().withoutPackagePrefix();
        // } else {
        //     LOG.warn("Using default name for " + classInfo.simpleName() + " [" + referenceType.name() + "]");
        return classInfo.name().withoutPackagePrefix();
        // }
    }

}
