package io.iamcyw.tower.schema.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * 字段，也可能是方法的最终字段
 */
public class Field implements Serializable {

    /**
     * This is the java method name (getter/setter/operation)
     */
    private String methodName;

    /**
     * This is the java property name (i.e without get/set/is)
     */
    private String propertyName;

    /**
     * This is the Command Name in the schema
     */
    private String name;

    /**
     * This is the description in the GraphQL Schema
     */
    private String description;

    /**
     * The type of this field.
     */
    private Reference reference;

    /**
     * If this is wrapped in generics or an array, this contain the info, examples are arrays, collections, async,
     * optional or
     * just plain generic.
     */
    private Wrapper wrapper = null;

    private String defaultValue = null;

    private boolean notNull = false;

    public Field() {
    }

    public Field(String methodName, String propertyName, String name, Reference reference) {
        this.methodName = methodName;
        this.propertyName = propertyName;
        this.name = name;
        this.reference = reference;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Reference getReference() {
        return reference;
    }

    public void setReference(Reference reference) {
        this.reference = reference;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public Wrapper getWrapper() {
        return wrapper;
    }

    public void setWrapper(Wrapper wrapper) {
        this.wrapper = wrapper;
    }

    public boolean hasWrapper() {
        return this.wrapper != null && this.wrapper.isNotEmpty();
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean hasDefaultValue() {
        return this.defaultValue != null;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.methodName);
        hash = 73 * hash + Objects.hashCode(this.propertyName);
        hash = 73 * hash + Objects.hashCode(this.name);
        hash = 73 * hash + Objects.hashCode(this.description);
        hash = 73 * hash + Objects.hashCode(this.reference);
        hash = 73 * hash + Objects.hashCode(this.wrapper);
        hash = 73 * hash + Objects.hashCode(this.defaultValue);
        hash = 73 * hash + (this.notNull ? 1 : 0);
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
        final Field other = (Field) obj;
        if (this.notNull != other.notNull) {
            return false;
        }
        if (!Objects.equals(this.methodName, other.methodName)) {
            return false;
        }
        if (!Objects.equals(this.propertyName, other.propertyName)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.defaultValue, other.defaultValue)) {
            return false;
        }
        if (!Objects.equals(this.reference, other.reference)) {
            return false;
        }
        if (!Objects.equals(this.wrapper, other.wrapper)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Field{" + "methodName='" + methodName + '\'' + ", propertyName='" + propertyName + '\'' + ", name='" +
                name + '\'' + ", description='" + description + '\'' + ", reference=" + reference + ", wrapper=" +
                wrapper + ", defaultValue='" + defaultValue + '\'' + ", notNull=" + notNull + '}';
    }

}
