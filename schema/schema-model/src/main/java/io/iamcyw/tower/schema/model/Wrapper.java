package io.iamcyw.tower.schema.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * If the type is wrapped in a generics bucket or in an array, keep the info here.
 */
public class Wrapper implements Serializable {

    private String wrapperClassName;

    private WrapperType wrapperType = WrapperType.EMPTY;

    private Wrapper wrapper = null;

    public Wrapper() {
    }

    public Wrapper(WrapperType wrapperType, String wrapperClassName) {
        this.wrapperType = wrapperType;
        this.wrapperClassName = wrapperClassName;
    }


    public WrapperType getWrapperType() {
        return wrapperType;
    }

    public void setWrapperType(WrapperType wrapperType) {
        this.wrapperType = wrapperType;
    }

    public String getWrapperClassName() {
        return wrapperClassName;
    }

    public void setWrapperClassName(String wrapperClassName) {
        this.wrapperClassName = wrapperClassName;
    }

    public boolean isNotEmpty() {
        return !wrapperType.equals(WrapperType.EMPTY);
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

    public boolean isCollectionOrArrayOrMap() {
        return isCollection() || isArray() || isMap();
    }

    public boolean isCollection() {
        return wrapperType.equals(WrapperType.COLLECTION);
    }

    public boolean isMap() {
        return wrapperType.equals(WrapperType.MAP);
    }

    public boolean isArray() {
        return wrapperType.equals(WrapperType.ARRAY);
    }

    public boolean isOptional() {
        return wrapperType.equals(WrapperType.OPTIONAL);
    }

    public boolean isUnknown() {
        return wrapperType.equals(WrapperType.UNKNOWN);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.wrapperClassName);
        hash = 59 * hash + Objects.hashCode(this.wrapperType);
        hash = 59 * hash + Objects.hashCode(this.wrapper);
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
        final Wrapper other = (Wrapper) obj;
        if (!Objects.equals(this.wrapperClassName, other.wrapperClassName)) {
            return false;
        }
        if (this.wrapperType != other.wrapperType) {
            return false;
        }
        if (!Objects.equals(this.wrapper, other.wrapper)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Wrapper{" + "wrapperClassName=" + wrapperClassName + ", wrapperType=" + wrapperType + ", wrapper=" +
                wrapper + '}';
    }

}
