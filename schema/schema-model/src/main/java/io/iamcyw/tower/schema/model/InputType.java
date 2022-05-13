package io.iamcyw.tower.schema.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InputType extends Reference {

    private String description;

    private Map<String, Field> fields = new LinkedHashMap<>();

    /**
     * All fields which are required by the creator method (e.g. {@code @JsonbCreator}).
     */
    private List<Field> creatorParameters = new ArrayList<>();

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

    public Map<String, Field> getFields() {
        return fields;
    }

    public void setFields(Map<String, Field> fields) {
        this.fields = fields;
    }

    public void addField(Field field) {
        this.fields.put(field.getName(), field);
    }

    public boolean hasFields() {
        return !this.fields.isEmpty();
    }

    public boolean hasField(String fieldName) {
        return this.fields.containsKey(fieldName);
    }

    public List<Field> getCreatorParameters() {
        return creatorParameters;
    }

    public void setCreatorParameters(List<Field> creatorParameters) {
        this.creatorParameters = creatorParameters;
    }

    public void addCreatorParameter(Field creatorParameter) {
        this.creatorParameters.add(creatorParameter);
    }

    @Override
    public String toString() {
        return "InputType{" + "description=" + description + ", fields=" + fields + ", creatorParameters=" +
                creatorParameters + '}';
    }

}
