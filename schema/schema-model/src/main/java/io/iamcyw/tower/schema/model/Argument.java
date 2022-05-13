package io.iamcyw.tower.schema.model;

public class Argument extends Field {

    private String methodArgumentName; // This is the java method argument name

    private boolean domainArgument = false; // Flag if this is a domain argument

    private boolean metaArgument = false;

    private boolean parameterArgument = false;

    private boolean metaValueArgument = false;

    public Argument() {
    }

    public Argument(String methodArgumentName, String methodName, String propertyName, String name,
                    Reference reference) {
        super(methodName, propertyName, name, reference);
        this.methodArgumentName = methodArgumentName;
    }

    public String getMethodArgumentName() {
        return methodArgumentName;
    }

    public void setMethodArgumentName(String methodArgumentName) {
        this.methodArgumentName = methodArgumentName;
    }

    public boolean isDomainArgument() {
        return domainArgument;
    }

    public void setDomainArgument(boolean domainArgument) {
        this.domainArgument = domainArgument;
    }

    public boolean isParameterArgument() {
        return parameterArgument;
    }

    public void setParameterArgument(boolean parameterArgument) {
        this.parameterArgument = parameterArgument;
    }

    public boolean isMetaArgument() {
        return metaArgument;
    }

    public void setMetaArgument(boolean metaArgument) {
        this.metaArgument = metaArgument;
    }

    public boolean isMetaValueArgument() {
        return metaValueArgument;
    }

    public void setMetaValueArgument(boolean metaValueArgument) {
        this.metaValueArgument = metaValueArgument;
    }

    @Override
    public String toString() {
        return "Argument{" + "methodArgumentName='" + methodArgumentName + '\'' + ", domainArgument=" + domainArgument +
                ", metaArgument=" + metaArgument + ", metaValueArgument=" + metaValueArgument + '}';
    }

}
