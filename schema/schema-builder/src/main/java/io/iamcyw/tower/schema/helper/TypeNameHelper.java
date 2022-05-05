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

    public static String getAnyTypeName(String parametrizedTypeNameExtension, ReferenceType referenceType,
                                        ClassInfo classInfo, Annotations annotationsForThisClass) {
        return classInfo.name().withoutPackagePrefix();
    }

}
