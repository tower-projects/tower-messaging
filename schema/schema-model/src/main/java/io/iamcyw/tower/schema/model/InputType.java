package io.iamcyw.tower.schema.model;

public class InputType extends Reference {

    private String description;

    public InputType() {
    }

    public InputType(String className, String name, String description) {
        super(className, name, ReferenceType.INPUT);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "InputType{" + "description='" + description + '\'' + '}';
    }

}
