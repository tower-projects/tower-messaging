package io.iamcyw.tower.schema.model;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a reference to some other type (type/input/enum/interface) This so that, as we are scanning, we can refer
 * to a type that might not exist yet. All types extends this.
 */
public class Reference implements Serializable {

    private String className;

    private String name;

    private ReferenceType type;


    private Map<String, Reference> parametrizedTypeArguments;

    private boolean addParametrizedTypeNameExtension;

    /**
     * If this is wrapped in generics or an array, this contain the info, examples are arrays, collections, async,
     * optional or
     * just plain generic.
     */
    private Wrapper wrapper = null;

    public Reference() {
    }

    public Reference(String className, String name, ReferenceType type,
                     Map<String, Reference> parametrizedTypeArguments, boolean addParametrizedTypeNameExtension,
                     Wrapper wrapper) {
        this.className = className;
        this.name = name;
        this.type = type;
        this.parametrizedTypeArguments = parametrizedTypeArguments;
        this.addParametrizedTypeNameExtension = addParametrizedTypeNameExtension;
        this.wrapper = wrapper;
    }

    public Reference(String className, String name, ReferenceType referenceType,
                     Map<String, Reference> parametrizedTypeArgumentsReferences,
                     boolean addParametrizedTypeNameExtension) {
        this(className, name, referenceType, parametrizedTypeArgumentsReferences, addParametrizedTypeNameExtension,
             null);
    }

    /**
     * This represent the Java Class Name
     *
     * @return String full class name
     */
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * This represents the GraphQL Name
     *
     * @return String name
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * This represent the GraphQL Type
     */
    public ReferenceType getType() {
        return type;
    }

    public void setType(ReferenceType type) {
        this.type = type;
    }


    public Map<String, Reference> getParametrizedTypeArguments() {
        return parametrizedTypeArguments;
    }

    public void setParametrizedTypeArguments(Map<String, Reference> parametrizedTypeArguments) {
        this.parametrizedTypeArguments = parametrizedTypeArguments;
    }

    public boolean isAddParametrizedTypeNameExtension() {
        return addParametrizedTypeNameExtension;
    }

    public void setAddParametrizedTypeNameExtension(boolean addParametrizedTypeNameExtension) {
        this.addParametrizedTypeNameExtension = addParametrizedTypeNameExtension;
    }

    public Wrapper getWrapper() {
        return wrapper;
    }

    public void setWrapper(Wrapper wrapper) {
        this.wrapper = wrapper;
    }

    public boolean hasWrapper() {
        return this.wrapper != null;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.className);
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + Objects.hashCode(this.type);
        hash = 97 * hash + Objects.hashCode(this.parametrizedTypeArguments);
        hash = 97 * hash + (this.addParametrizedTypeNameExtension ? 1 : 0);
        // hash = 97 * hash + Objects.hashCode(this.directiveInstances);
        hash = 97 * hash + Objects.hashCode(this.wrapper);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Reference other = (Reference) obj;
        if (this.addParametrizedTypeNameExtension != other.addParametrizedTypeNameExtension) {
            return false;
        }
        if (!Objects.equals(this.className, other.className)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if (!Objects.equals(this.parametrizedTypeArguments, other.parametrizedTypeArguments)) {
            return false;
        }
        if (!Objects.equals(this.wrapper, other.wrapper)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Reference{" + "className='" + className + '\'' + ", name='" + name + '\'' + ", type=" + type +
                ", parametrizedTypeArguments=" + parametrizedTypeArguments + ", addParametrizedTypeNameExtension=" +
                addParametrizedTypeNameExtension + ", wrapper=" + wrapper + '}';
    }

}
